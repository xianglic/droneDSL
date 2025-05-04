package org.droneDSL.compile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.droneDSL.compile.codeGen.concrete.MissionPlan;
import org.droneDSL.compile.codeGen.concrete.Parse;
import org.droneDSL.compile.preprocess.partition.Partition;
import org.droneDSL.compile.preprocess.partition.CorridorPartition;
import org.droneDSL.compile.preprocess.partition.SurveyPartition;
import org.droneDSL.compile.preprocess.waypoints.GeoPoints;
import org.droneDSL.compile.preprocess.waypoints.WaypointsUtils;
import org.jetbrains.annotations.NotNull;
import org.droneDSL.compile.psi.DslParserImpl;
import org.droneDSL.compile.psi.StreamReporter;
import picocli.CommandLine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.locationtech.jts.geom.*;


@CommandLine.Command(name = "DroneDSL Compiler", version = "DroneDSL Compiler 2.0",
    mixinStandardHelpOptions = true)

public class Compiler implements Runnable {
  private static class PartitionConfig {
    @CommandLine.Option(names = {"-p", "--PartitionType"}, defaultValue = "corridor",
        description = "Type of partitioning algorithm (corridor or survey)")
    String type;

    @CommandLine.Option(names = "--angle", description = "Rotation angle for partition lines",
        defaultValue = "90")
    double angleDegrees;

    @CommandLine.Option(names = "--spacing", description = "Spacing between transects",
        defaultValue = "10")
    double spacing;

    @CommandLine.Option(names = "--trigger", description = "Trigger distance for survey mode",
        defaultValue = "5")
    double triggerDistance;
  }

  @CommandLine.Option(names = {"-k", "--KMLFilePath"}, paramLabel = "<KMLFilePath>",
      defaultValue = "null",
      description = "File Path of the KML file")
  String KMLFilePath;
  @CommandLine.Option(names = {"-w", "--WayPointsMapPath"}, paramLabel = "<WayPointsMapPath>",
      defaultValue = "./way_points_map.json",
      description = "File Path of the KML file")
  String WayPointsMapPath;
  @CommandLine.Option(names = {"-s", "--DSLScriptPath"}, paramLabel = "<DSLScriptPath>",
      defaultValue = "null",
      description = "File Path of the DSL script")
  String DSLScriptPath;
  @CommandLine.Option(names = {"-o", "--OutputFilePath"}, paramLabel = "<OutputFilePath>",
      defaultValue =
          "./flightplan", description = "output file path")
  String OutputFilePath = "./flightplan";
  @CommandLine.Option(names = {"-l", "--Language"}, paramLabel = "<Language>", defaultValue =
      "python/project",
      description = "compiled code platform")
  String Platform = "python/project";
  @CommandLine.ArgGroup(exclusive = false, heading = "Partitioning Options%n")
  PartitionConfig PartitionConfig;


  @Override
  public void run() {
    // preprocess - partition waypoints
    Partition partitionAlgo = getPartitionAlgo(PartitionConfig);
    Map<String, GeoPoints> rawGeoPointsMap = WaypointsUtils.parseKMLFile(KMLFilePath);
    var wayPointsMap = getPartitionedGeoPointsMap(partitionAlgo, rawGeoPointsMap);
    try {
      writeToJsonFile(wayPointsMap, WayPointsMapPath);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // parse DSL
    String fileContent;
    try {
      fileContent = Files.readString(Paths.get(DSLScriptPath));
    } catch (IOException e) {
      System.err.println("Error reading the file: " + e.getMessage());
      return;
    }
    var node = parser().parseNode(fileContent);
    System.out.println(node.toDebugString());
    // get the concrete Flight plan structure
    var taskMap = Parse.createTaskMap(node);
    var startTaskID = Parse.createTransition(node, taskMap);
    var mission = new MissionPlan(startTaskID, taskMap);

    // compile
    compile(mission);
  }

  private void compile(MissionPlan mission) {

    //code gen
    var platformPath = Platform;
    try {
      mission.codeGenPython(platformPath);
    } catch (IOException e) {
      e.printStackTrace();
    }

    // build file generate
    try {
      ProcessBuilder builder = new ProcessBuilder();
      var cmd = String.format("cd %s && pipreqs . --force", Platform);
      builder.command("bash", "-c", cmd);
      builder.start().waitFor(); // Wait for the command to complete
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }

    // zip
    try {
      FileOutputStream fos = new FileOutputStream(String.format(OutputFilePath + ".ms"));
      ZipOutputStream zos = new ZipOutputStream(fos);
      // add to the zip file
      addToZipFile(platformPath, "", zos);
      zos.close();
      fos.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @NotNull
  private Partition getPartitionAlgo(PartitionConfig partitionConfig) {
    Partition partitionAlgo;
    switch (partitionConfig.type.toLowerCase()) {
      case "survey" -> partitionAlgo = new SurveyPartition(
          partitionConfig.spacing,
          partitionConfig.angleDegrees,
          partitionConfig.triggerDistance
      );
      case "corridor" -> partitionAlgo = new CorridorPartition(
          partitionConfig.spacing,
          partitionConfig.angleDegrees
      );
      default ->
          throw new IllegalArgumentException("Unknown partition type: " + partitionConfig.type);
    }
    return partitionAlgo;
  }

  public static Map<String, List<GeoPoints>> getPartitionedGeoPointsMap(Partition partitionAlgo,
                                                                        Map<String, GeoPoints> rawGeoPointsMap) {
    Map<String, List<GeoPoints>> partitionedGeoWayPointsMap = new HashMap<>();
    for (String area : rawGeoPointsMap.keySet()) {
      // create a polygon
      GeoPoints geoPoints = rawGeoPointsMap.get(area);
      Polygon projectedPolygon = geoPoints.toProjectedPolygon();
      List<GeoPoints> partitionedGeoPoints =
          partitionAlgo.generatePartitionedGeoPoints(projectedPolygon);

      List<GeoPoints> finalGeoPoints = new ArrayList<>();
      for (GeoPoints subPartitionedGeoPoints : partitionedGeoPoints) {
        finalGeoPoints.add(subPartitionedGeoPoints.inverseProjectFrom(geoPoints.getCentroid()));
      }

      partitionedGeoWayPointsMap.put(area, finalGeoPoints);
    }
    return partitionedGeoWayPointsMap;
  }

  public static void writeToJsonFile(Map<String, List<GeoPoints>> map, String filePath) throws IOException {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    Map<String, List<List<double[]>>> jsonCompatibleMap = new HashMap<>();

    for (Map.Entry<String, List<GeoPoints>> entry : map.entrySet()) {
      List<List<double[]>> geoList = new ArrayList<>();

      for (GeoPoints geo : entry.getValue()) {
        List<double[]> coords = new ArrayList<>();
        for (Coordinate c : geo) {
          coords.add(new double[]{c.x, c.y});
        }
        geoList.add(coords);
      }

      jsonCompatibleMap.put(entry.getKey(), geoList);
    }

    try (FileWriter writer = new FileWriter(filePath)) {
      gson.toJson(jsonCompatibleMap, writer);
    }
  }


  private static void addToZipFile(String sourceDir, String insideZipDir, ZipOutputStream zos) throws IOException {
    File dir = new File(sourceDir);
    File[] files = dir.listFiles();
    // Check if the directory exists and contains files
    if (files != null) {
      for (File file : files) {
        // If it's a directory, recursively process the files inside this directory
        if (file.isDirectory()) {
          // Create the corresponding directory inside the ZIP file
          addToZipFile(file.getAbsolutePath(), insideZipDir + "/" + file.getName(), zos);
        } else {
          // If it's a file, add it to the ZIP file
          zos.putNextEntry(new ZipEntry(insideZipDir + "/" + file.getName()));
          Files.copy(file.toPath(), zos);
          zos.closeEntry();
        }
      }
    }
  }

  @NotNull
  private static DslParserImpl parser() {
    return new DslParserImpl(new StreamReporter(System.out));
  }

  public static void main(String[] args) {
    int exitCode = new CommandLine(new Compiler()).execute(args);
    System.exit(exitCode);
  }
}
