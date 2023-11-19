package org.steeleagle.pythonGen.taskswitch;

import org.aya.intellij.GenericNode;
import org.steeleagle.concrete.Preparse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CodeGenerator_Python {
  public static void generateCode(GenericNode<? extends GenericNode<?>> node) {
    var task = Preparse.createTask(node);

    var code1 = new StringBuilder();
    code1.append("  def move() :\n");
    for (var ele : task.waypoints()) {
      code1.append(String.format("    self.drone.moveTo(%s, %s, %s)\n", ele.x(), ele.y(), ele.z()));
      code1.append(String.format("    time.sleep(%s)\n", task.hoverDelay()));
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
                print(e)""", task.model(), task.gimbalPitch());
    try {
      Files.writeString(Paths.get("DetectTask.py"), code);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
