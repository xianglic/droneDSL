package org.steeleagle;

import org.aya.intellij.GenericNode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.steeleagle.parser.BotPsiElementTypes.*;


public class CodeGenerator {

//  public static void main(String[] args) {
//    HashMap<String, String> params = new HashMap<>();
//    params.put("gimbal_pitch", "45.0");
//    params.put("coords", "[{\"lat\": 40.4406, \"lng\": -79.9959, \"alt\": 300.0}]");
//
//    generateCode(params);
//  }

  public static void generateCode(GenericNode<? extends GenericNode<?>> node) {

    var gimbal_attr = node.child(TASK).child(TASK_DECL).child(TASK_BODY).child(COMMA_SEP).childrenOfType(ATTRIBUTE).toImmutableSeq().get(1);
    var gimbal_pitch = gimbal_attr.child(COLONED).child(ATTRIBUTE_EXPR).child(NUMBER).tokenText();

    System.out.println("gimbal_pitch :" + gimbal_pitch);

    List<List<String>> waypoints = new ArrayList<>();
    var waypoint_attr = node.child(TASK).child(TASK_DECL).child(TASK_BODY).child(COMMA_SEP).childrenOfType(ATTRIBUTE).toImmutableSeq().get(0);
    var way_points = waypoint_attr.child(COLONED).child(ATTRIBUTE_EXPR).child(SQUARE_BRACKED).child(COMMA_SEP).childrenOfType(PAREN).toSeq(); // childrenOfType(WAYPOINT).toSeq();
    for (var point : way_points) {
      List<String> ele = new ArrayList<>();
      ele.add(point.child(WAYPOINT).child(LATITUDE).tokenText().toString());
      ele.add(point.child(WAYPOINT).child(LONGITUDE).tokenText().toString());
      ele.add(point.child(WAYPOINT).child(ALTITUDE).tokenText().toString());
      waypoints.add(ele);
    }

//    String gimbalPitch = params.get("gimbal_pitch");
//    String coords = params.get("coords");
    var code1 = new StringBuilder();
    code1.append("    public void move() {\n");

    for (var ele : waypoints) {
      code1.append(String.format("        drone.moveTo(%s, %s, %s);\n", ele.get(0), ele.get(1), ele.get(2)));
    }
    code1.append("    }\n");

    String code = String.format("// SPDX-FileCopyrightText: 2023 Carnegie Mellon University - Satyalab\n" +
        "//\n" +
        "// SPDX-License-Identifier: GPL-2.0-only\n" +
        "\n" +
        "package edu.cmu.cs.dronebrain;\n" +
        "\n" +
        "import edu.cmu.cs.dronebrain.interfaces.DroneItf;\n" +
        "import edu.cmu.cs.dronebrain.interfaces.CloudletItf;\n" +
        "import edu.cmu.cs.dronebrain.interfaces.Task;\n" +
        "import java.lang.Float;\n" +
        "import java.util.HashMap;\n" +
        "import org.json.JSONArray;\n" +
        "import org.json.JSONObject;\n" +
        "import android.util.Log;\n" +
        "\n" +
        "public class DetectTask extends Task {\n" +
        "    \n" +
        "    Double gimbal;\n" +
        "    public DetectTask(DroneItf d, CloudletItf c, HashMap<String, String> k) {\n" +
        "        super(d, c, k);\n" +
        "        gimbal = %s;\n" +
        "    }\n" +
        code1 +
        "    \n" +
        "    @Override\n" +
        "    public void run() {\n" +
        "        try {\n" +
        "            drone.setGimbalPose(0.0, gimbal, 0.0);\n" +
        "            cloudlet.switchModel(\"coco\");\n" +
        "            Log.d(\"DetectTask\", \"Parsing coordinates...\");\n" +
        "            move(); \n" +
        "        } catch (Exception e) {\n" +
        "            Log.e(\"DetectTask\", e.toString());\n" +
        "        }\n" +
        "    }\n" +
        "\n" +
        "    @Override\n" +
        "    public void pause() {}\n" +
        "\n" +
        "    @Override\n" +
        "    public void resume() {}\n" +
        "}", gimbal_pitch);

    try (var writer = new BufferedWriter(new FileWriter("DetectTask.java"))) {
      writer.write(code);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
