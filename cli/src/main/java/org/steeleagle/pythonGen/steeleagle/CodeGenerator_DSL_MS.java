package org.steeleagle.pythonGen.steeleagle;

import kala.collection.immutable.ImmutableSeq;
import kala.collection.mutable.MutableMap;
import kala.text.StringSlice;
import org.aya.intellij.GenericNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.steeleagle.parser.BotPsiElementTypes.*;

public class CodeGenerator_DSL_MS {
  private static class IDEA {
    MutableMap<StringSlice, GenericNode<? extends GenericNode<?>>> bro = MutableMap.create();

    public GenericNode<? extends GenericNode<?>> get(String name) {
      return bro.get(StringSlice.of(name));
    }
  }

  public static void generateCode(GenericNode<? extends GenericNode<?>> node) {
    var task = node.child(TASK).child(TASK_DECL);
    var attrs = task.child(TASK_BODY).childrenOfType(ATTRIBUTE).toImmutableSeq();
    var bro = new IDEA();
    attrs.forEach(attr -> bro.bro.put(attr.child(ID).tokenText(), attr.child(ATTRIBUTE_EXPR)));
    var gimbal_pitch = bro.get("gimbal_pitch").child(NUMBER).tokenText();
    var drone_rotation = bro.get("drone_rotation").child(NUMBER).tokenText();
    var sample_rate = bro.get("sample_rate").child(NUMBER).tokenText();
    var hover_delay = bro.get("hover_delay").child(NUMBER).tokenText();
    var model = bro.get("model").child(NAME).tokenText();

    System.out.println("gimbal_pitch :" + gimbal_pitch);
    System.out.println("drone_rotation :" + drone_rotation);
    System.out.println("sample_rate :" + sample_rate);
    System.out.println("hover_delay :" + hover_delay);
    System.out.println("model :" + model);

    List<ImmutableSeq<StringSlice>> waypoints = new ArrayList<>();
    var way_points = bro.get("way_points").child(SQUARE_BRACKED).childrenOfType(PAREN).toSeq(); // childrenOfType(WAYPOINT).toSeq();
    for (var point : way_points) {
      waypoints.add(point.child(WAYPOINT).childrenOfType(NUMBER).map(GenericNode::tokenText).toImmutableSeq());
    }
    var way_points_str = new StringBuilder();
    way_points_str.append("[");
    for (var ele : waypoints) {
      if (way_points_str.length() > 1) {
        way_points_str.append(", ");
      }
      way_points_str.append(String.format("{'lng': %s, 'lat': %s, 'alt': %s}", ele.get(0), ele.get(1), ele.get(2)));
    }
    way_points_str.append("]");

    String code = String.format("""
        from interfaces.FlightScript import FlightScript
        # Import derived tasks
        from task_defs.DetectTask import DetectTask
                
        class MS(FlightScript):
          \s
            def __init__(self, drone, cloudlet):
                super().__init__(drone, cloudlet)
        \s
            def run(self):
                try:
                    kwargs = {}
                    # Detect/DetectTask START
                    kwargs.clear()
                    kwargs["gimbal_pitch"] = "%s"
                    kwargs["drone_rotation"] = "%s"
                    kwargs["sample_rate"] = "%s"
                    kwargs["hover_delay"] = "%s"
                    kwargs["model"] = "%s"
                    kwargs["coords"] = "%s"
                    t = DetectTask(self.drone, self.cloudlet, **kwargs)
                    self.taskQueue.put(t)
                    print("Added task DetectTask to the queue")
                   \s
                    self.drone.takeOff()
                    self._execLoop()
                except Exception as e:
                    print(e)
            """, gimbal_pitch, drone_rotation, sample_rate, hover_delay, model, way_points_str);
    try {
      Files.writeString(Paths.get("DSL-MS.py"), code);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
