package org.droneDSL.compile;

import kala.collection.immutable.ImmutableMap;
import org.droneDSL.compile.codeGen.concrete.MissionPlan;
import org.droneDSL.compile.codeGen.concrete.Parse;
import org.droneDSL.compile.codeGen.concrete.Task;
import org.droneDSL.compile.parser.BotPsiElementTypes;
import org.droneDSL.compile.preprocess.partition.Partition;
import org.droneDSL.compile.preprocess.partition.CorridorPartition;
import org.droneDSL.compile.preprocess.partition.SurveyPartition;
import org.droneDSL.compile.preprocess.waypoints.GeoPoints;
import org.droneDSL.compile.preprocess.waypoints.WaypointsUtils;
import org.jetbrains.annotations.NotNull;
import org.droneDSL.compile.psi.DslParserImpl;
import org.droneDSL.compile.psi.StreamReporter;
import picocli.CommandLine;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;







@CommandLine.Command(name = "DroneDSL Compiler", version = "DroneDSL Compiler 1.5",
    mixinStandardHelpOptions = true)
public class Compiler implements Runnable {
   private static class PartitionConfig {
    @CommandLine.Option(names = {"-p", "--PartitionType"}, defaultValue = "corridor",
        description = "Type of partitioning algorithm (corridor or survey)")
    String type;

    @CommandLine.Option(names = "--angle", description = "Rotation angle for partition lines", defaultValue = "90")
    double angleDegrees;

    @CommandLine.Option(names = "--spacing", description = "Spacing between transects", defaultValue = "10")
    double spacing;

    @CommandLine.Option(names = "--trigger", description = "Trigger distance for survey mode", defaultValue = "5")
    double triggerDistance;
  }

  @CommandLine.Option(names = {"-k", "--KMLFilePath"}, paramLabel = "<KMLFilePath>",
      defaultValue = "null",
      description = "File Path of the KML file")
  String KMLFilePath;

  @CommandLine.Option(names = {"-s", "--DSLScriptPath"}, paramLabel = "<DSLScriptPath>",
      defaultValue = "null",
      description = "File Path of the DSL script")
  String DSLScriptPath;

  @CommandLine.Option(names = {"-o", "--OutputFilePath"}, paramLabel = "<OutputFilePath>",
      defaultValue =
          "./flightplan_", description = "output file path")
  String OutputFilePath = "./flightplan_";

  @CommandLine.Option(names = {"-a", "--Altitude"}, paramLabel = "<Altitude>", defaultValue = "15"
      , description =
      "altitude of the waypoints specified")
  String Altitude = "12";

  @CommandLine.Option(names = {"-l", "--Language"}, paramLabel = "<Language>", defaultValue =
      "python/project",
      description = "compiled code platform")
  String Platform = "python/project";

  @CommandLine.ArgGroup(exclusive = false, heading = "Partitioning Options%n")
  PartitionConfig partitionConfig;




  @CommandLine.Option(names = {"-p", "--PartitionType"}, paramLabel = "<PartitionType>", defaultValue =
      "corridor",
      description = "waypoint partition algorithm")
  String PartitionType = "corridor";

  @Override
  public void run() {
    // preprocess
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
      default -> throw new IllegalArgumentException("Unknown partition type: " + partitionConfig.type);
    }

    var droneWaypointsDict = partition(partitionAlgo);

    // get the DSL script file
    String fileContent;
    try {
      fileContent = Files.readString(Paths.get(DSLScriptPath));
    } catch (IOException e) {
      System.err.println("Error reading the file: " + e.getMessage());
      return;
    }

    // get the ast
    var node = parser().parseNode(fileContent);
    System.out.println(node.toDebugString());

    for (var areaName : droneWaypointsDict.keySet()) {
      var waypointsMap = droneWaypointsDict.get(areaName);
      // get the concrete Flight plan structure
      ImmutableMap<String, Task> taskMap =
          ImmutableMap.from(node.child(BotPsiElementTypes.TASK).
              childrenOfType(BotPsiElementTypes.TASK_DECL).map(task -> Parse.createTask(task, waypointsMap)));
      var startTaskID =
          Parse.createMission(node.child(BotPsiElementTypes.MISSION).child(BotPsiElementTypes.MISSION_CONTENT),
              taskMap);
      var ast = new MissionPlan(startTaskID, taskMap);

      // code generate
      var platformPath = Platform;
      try {
        ast.codeGenPython(platformPath);
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
        FileOutputStream fos = new FileOutputStream(String.format(OutputFilePath + droneID + ".ms"
        ));
        ZipOutputStream zos = new ZipOutputStream(fos);
        // add to the zip file
        addToZipFile(platformPath, "", zos);
        zos.close();
        fos.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }


  private Map<String, List<GeoPoints>> partition(Partition partitionAlgo){
    Map<String, List<GeoPoints>> partitionedGEOWayPointsMap = new HashMap<>();
    Map<String, GeoPoints> geoWayPointsMap = WaypointsUtils.parseKMLFile(KMLFilePath);
    for (String area : geoWayPointsMap.keySet()){

      // create a polygon
      Polygon polygon = new Polygon();

      partitionAlgo.generateTransectsAndPoints(polygon);
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
