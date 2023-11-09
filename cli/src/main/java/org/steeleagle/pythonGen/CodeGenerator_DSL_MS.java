package org.steeleagle.pythonGen;

import kala.collection.immutable.ImmutableSeq;
import kala.text.StringSlice;
import org.aya.intellij.GenericNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.steeleagle.parser.BotPsiElementTypes.*;

public class CodeGenerator_DSL_MS {
    public static void generateCode(GenericNode<? extends GenericNode<?>> node) {
        var task = node.child(TASK).child(TASK_DECL);
        var attr = task.child(TASK_BODY).childrenOfType(ATTRIBUTE).toImmutableSeq();
        var gimbal_attr = attr.get(1);
        var gimbal_pitch = gimbal_attr.child(ATTRIBUTE_EXPR).child(NUMBER).tokenText();
        var drone_rotation_attr = attr.get(2);
        var drone_rotation = drone_rotation_attr.child(ATTRIBUTE_EXPR).child(NUMBER).tokenText();
        var sample_rate_attr = attr.get(3);
        var sample_rate = sample_rate_attr.child(ATTRIBUTE_EXPR).child(NUMBER).tokenText();
        var hover_delay_attr = attr.get(4);
        var hover_delay = hover_delay_attr.child(ATTRIBUTE_EXPR).child(NUMBER).tokenText();
        var model_attr = attr.get(5);
        var model = model_attr.child(ATTRIBUTE_EXPR).child(NAME).tokenText();

        System.out.println("gimbal_pitch :" + gimbal_pitch);
        System.out.println("drone_rotation :" + drone_rotation);
        System.out.println("sample_rate :" + sample_rate);
        System.out.println("hover_delay :" + hover_delay);
        System.out.println("model :" + model);

        List<ImmutableSeq<StringSlice>> waypoints = new ArrayList<>();
        var waypoint_attr = attr.get(0);
        var way_points = waypoint_attr.child(ATTRIBUTE_EXPR).child(SQUARE_BRACKED).childrenOfType(PAREN).toSeq(); // childrenOfType(WAYPOINT).toSeq();
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
                """, gimbal_pitch, drone_rotation, sample_rate,hover_delay,model, way_points_str);
        try {
            Files.writeString(Paths.get("DSL-MS.py"), code);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
