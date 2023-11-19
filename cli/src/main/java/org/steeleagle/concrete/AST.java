package org.steeleagle.concrete;

import kala.collection.immutable.ImmutableMap;
import kala.collection.immutable.ImmutableSeq;
import kala.text.StringSlice;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public record AST(ImmutableMap<StringSlice, Task> taskList, Mission mission) {
  public CharSequence codeGenPython(StringBuilder builder) throws IOException {
    String missionRunnerPath = "../MissionRunner.py";
    Files.writeString(Paths.get(missionRunnerPath), missionRunnerContent());

    String taskControllerPath = "../TaskController.py";
    Files.writeString(Paths.get(taskControllerPath), taskControllerContent());

    String detectTaskPath = "../DetectTask.py";
    Files.writeString(Paths.get(detectTaskPath), detectTaskPathContent());


//    taskList.forEach((id, task) -> {
////      task.wayPoints
//      mission.transitionList().forEach(trans -> {
//        trans.currentTaskID().equals(id).
//      });
//
//
//    });
    return builder;
  }

  private String detectTaskPathContent() {
    return """
        """;
  }

  private String taskControllerContent() {
    return """
        """;
  }


  private String missionRunnerContent() {

//    def __init__(self, drone):
//    super().__init__(drone)
//    self.curr_task_id = None
//    self.taskMap = {}
//    self.event_queue = queue.Queue()
//    self.t1 = None
//    self.t2 = None
    StringBuilder initContent = new StringBuilder("""
        def __init__(self, drone):
            super().__init__(drone)
            self.curr_task_id = None
            self.taskMap = {}
            self.event_queue = queue.Queue()
        """);

    //    # TASK1
//    kwargs.clear()
//    kwargs["gimbal_pitch"] = "-45.0"
//    kwargs["drone_rotation"] = "0.0"
//    kwargs["sample_rate"] = "2"
//    kwargs["hover_delay"] = "5"
//    kwargs["coords"] = "[{'lng': -79.9503613, 'lat': 40.4156274, 'alt': 50.0}, {'lng': -79.9503231, 'lat': 40.415537, 'alt': 50.0}, {'lng': -79.950185, 'lat': 40.415561, 'alt': 50.0}, {'lng': -79.9502218, 'lat': 40.4156524, 'alt': 50.0}, {'lng': -79.9503613, 'lat': 40.4156274, 'alt': 50.0}]"
//    self.t1 = DetectTask(self.drone, 1, event_queue, **kwargs)
//    self.taskMap[1] = self.t1
//
//
//    # TASK2
//    kwargs.clear()
//    kwargs["gimbal_pitch"] = "-45.0"
//    kwargs["drone_rotation"] = "0.0"
//    kwargs["sample_rate"] = "2"
//    kwargs["hover_delay"] = "5"
//    kwargs["coords"] = "[{'lng': -79.9504218, 'lat': 40.4155937, 'alt': 50.0}, {'lng': -79.9505123, 'lat': 40.4155293, 'alt': 50.0}, {'lng': -79.9503849, 'lat': 40.4155059, 'alt': 50.0}, {'lng': -79.9504218, 'lat': 40.4155937, 'alt': 50.0}]"
//    self.t2 = DetectTask(self.drone, 2, event_queue, **kwargs)
//    self.taskMap[2] = self.t2

    StringBuilder defineTaskContent = new StringBuilder();

    String startMissionContent = """
        def start_mission(self):
            # set the current task
            task_id = self.%s.get_task_id()
            self.set_current_task(task_id)
            print(f"MR: start mission, current taskid:{task_id}\\n")
            # start
            self.taskQueue.put(self.t1)
            print("MR: taking off")
            self.drone.takeOff()
            self._execLoop()
         """.formatted(mission.startTaskID());
    // Convert the key set to a list

    taskList.forEach((key, task) -> {
      // Example of what you might want to append
      initContent.append(String.format("self.%s\n", key));
      defineTaskContent.append(task.initCode(key.toString()));
    });

    return String.format(
        """
            from threadlevel.FlightScript import FlightScript
            # Import derived tasks
            from DetectTask import DetectTask
            import queue
            import time
                    
                    
            class MissionRunner(FlightScript):
            """ + initContent + """
            def define_task(self, event_queue):
                # Define task
                kwargs = {}
            """ + defineTaskContent + """
            def transit_to(self, task_id):
                print(f"MR: transit to task with task_id: {task_id}, current_task_id: {self.curr_task_id}")
                self.set_current_task(task_id)
                self._kill()
                if (task_id != 0):
                    self._push_task(self.taskMap[task_id])
                    self._execLoop()
                else:
                    self.end_mission()
            """ + startMissionContent + """
            def end_mission(self):
                print("MR: end mission, rth\\n")
                self.drone.rth()
            def set_current_task(self, task_id):
                self.curr_task_id = task_id
            def get_current_task(self):
                return self.curr_task_id
            def run(self):
                try:
                    # define task
                    print("MR: define the tasks\\n")
                    self.define_task(self.event_queue)
                    # start mission
                    print("MR: start the mission!\\n")
                    self.start_mission()
                except Exception as e:
                    print(e)
            """);
  }
}
