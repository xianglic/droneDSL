package org.steeleagle.pythonGen.taskswitch;

import kala.collection.immutable.ImmutableSeq;
import kala.text.StringSlice;
import org.aya.intellij.GenericNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.steeleagle.parser.BotPsiElementTypes.*;

public class CodeGenerator_Python {
  public static void generateCode(GenericNode<? extends GenericNode<?>> node) {
    var task = node.child(TASK).child(TASK_DECL);
    var attr = task.child(TASK_BODY).childrenOfType(ATTRIBUTE).toImmutableSeq();
    var gimbal_attr = attr.get(1);
    var gimbal_pitch = gimbal_attr.child(ATTRIBUTE_EXPR).child(NUMBER).tokenText();
    var model_attr = attr.get(2);
//        var model = "coco";

    var model = model_attr.child(ATTRIBUTE_EXPR).child(NAME).tokenText();
    var hover_delay_attr = attr.get(3);
    var hover_delay = hover_delay_attr.child(ATTRIBUTE_EXPR).child(NUMBER).tokenText();

    System.out.println("gimbal_pitch :" + gimbal_pitch);
    System.out.println("model :" + model);
    System.out.println("hover_delay :" + hover_delay);

    List<ImmutableSeq<StringSlice>> waypoints = new ArrayList<>();
    var waypoint_attr = attr.get(0);
    var way_points = waypoint_attr.child(ATTRIBUTE_EXPR).child(SQUARE_BRACKED).childrenOfType(PAREN).toSeq(); // childrenOfType(WAYPOINT).toSeq();
    for (var point : way_points) {
      waypoints.add(point.child(WAYPOINT).childrenOfType(NUMBER).map(GenericNode::tokenText).toImmutableSeq());
    }

    var code1 = new StringBuilder();
    code1.append("  def move() :\n");
    for (var ele : waypoints) {
      code1.append(String.format("    self.drone.moveTo(%s, %s, %s)\n", ele.get(0), ele.get(1), ele.get(2)));
      code1.append(String.format("    time.sleep(%s)\n", hover_delay));
    }

    String code = String.format("""
        from interfaces.Task import Task
        import time
        import ast
                    
        class DetectTask(Task):
                    
        def __init__(self, drone, cloudlet, **kwargs):
            super().__init__(drone, cloudlet, **kwargs)
        """ + code1 + """
        def run(self):
            try:
                self.cloudlet.switchModel("%s")
                self.drone.setGimbalPose(0.0, %s, 0.0)
                move()
            except Exception as e:
                print(e)""", model, gimbal_pitch);
    try {
      Files.writeString(Paths.get("DetectTask.py"), code);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
