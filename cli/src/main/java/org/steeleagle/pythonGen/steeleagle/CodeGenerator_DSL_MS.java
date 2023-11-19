package org.steeleagle.pythonGen.steeleagle;

import org.aya.intellij.GenericNode;
import org.steeleagle.concrete.DetectTask;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.steeleagle.concrete.Preparse.createTask;

public class CodeGenerator_DSL_MS {
  public static void generateCode(GenericNode<? extends GenericNode<?>> node) {
    var task = createTask(node);
    var wayPointsStr = new StringBuilder();
    wayPointsStr.append("[");
    task.wayPoints().joinTo(wayPointsStr, ", ", DetectTask.Point::toJson);
    wayPointsStr.append("]");

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
            """, task.gimbalPitch(), task.droneRotation(),
        task.sampleRate(), task.hoverDelay(), task.model(),
        wayPointsStr);
    try {
      Files.writeString(Paths.get("DSL-MS.py"), code);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
