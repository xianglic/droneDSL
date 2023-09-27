package org.steeleagle;

import kala.collection.immutable.ImmutableSeq;
import kala.text.StringSlice;
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

    List<ImmutableSeq<StringSlice>> waypoints = new ArrayList<>();
    var waypoint_attr = node.child(TASK).child(TASK_DECL).child(TASK_BODY).child(COMMA_SEP).childrenOfType(ATTRIBUTE).toImmutableSeq().get(0);
    var way_points = waypoint_attr.child(COLONED).child(ATTRIBUTE_EXPR).child(SQUARE_BRACKED).child(COMMA_SEP).childrenOfType(PAREN).toSeq(); // childrenOfType(WAYPOINT).toSeq();
    for (var point : way_points) {
      var waypoint = point.child(WAYPOINT);
      waypoints.add(waypoint.childrenOfType(NUMBER).map(GenericNode::tokenText).toImmutableSeq());
    }

//    String gimbalPitch = params.get("gimbal_pitch");
//    String coords = params.get("coords");
    var code1 = new StringBuilder();
    code1.append("  public void move() {\n");

    for (var ele : waypoints) {
      code1.append(String.format("    drone.moveTo(%s, %s, %s);\n", ele.get(0), ele.get(1), ele.get(2)));
    }
    code1.append("  }\n");

    String code = String.format("""
        // SPDX-FileCopyrightText: 2023 Carnegie Mellon University - Satyalab
        //
        // SPDX-License-Identifier: GPL-2.0-only

        package edu.cmu.cs.dronebrain;

        import edu.cmu.cs.dronebrain.interfaces.DroneItf;
        import edu.cmu.cs.dronebrain.interfaces.CloudletItf;
        import edu.cmu.cs.dronebrain.interfaces.Task;
        import java.util.HashMap;
        import org.json.JSONArray;
        import org.json.JSONObject;
        import android.util.Log;

        public class DetectTask extends Task {
          double gimbal;
          public DetectTask(DroneItf d, CloudletItf c, HashMap<String, String> k) {
            super(d, c, k);
            gimbal = %s;
          }
        """ +
        code1 + """
          @Override public void run() {
            try {
              drone.setGimbalPose(0.0, gimbal, 0.0);
              cloudlet.switchModel("coco");
              Log.d("DetectTask", "Parsing coordinates...");
              move();
            } catch (Exception e) {
              Log.e("DetectTask", e.toString());
            }
          }

          @Override public void pause() {}

          @Override public void resume() {}
        }""", gimbal_pitch);

    try (var writer = new BufferedWriter(new FileWriter("DetectTask.java"))) {
      writer.write(code);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
