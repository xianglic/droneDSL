package org.steeleagle.concrete;

import kala.collection.immutable.ImmutableMap;
import kala.text.StringSlice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.steeleagle.concrete.Mission.*;

public record AST(ImmutableMap<StringSlice, Task> taskList, Mission mission) {

  public CharSequence codeGenPython(StringBuilder builder) throws IOException {
    String missionRunnerPath = "./output/MissionRunner.py";
    Files.writeString(Paths.get(missionRunnerPath), missionRunnerContent());

    String taskControllerPath = "./output/TaskController.py";
    Files.writeString(Paths.get(taskControllerPath), taskControllerContent());

    String detectTaskPath = "./output/DetectTask.py";
    Files.writeString(Paths.get(detectTaskPath), detectTaskContent());

    return builder;
  }



  private String detectTaskContent() {

    StringBuilder triggerEvent = new StringBuilder("# triggered event\n");
    mission.transitionList().forEach(trans ->{
      if (trans.condId().equals("timeup")) {
        triggerEvent.append(String.format("""
                    if (self.task_id == "%s"):
                        # construct the timer with %s seconds
                        timer = threading.Timer(%s, self.trigger_event, ["timeup"])
                        # Start the timer
                        timer.start()
            """, trans.currentTaskID(), trans.condArg(), trans.condArg()));
      }
    } );

    return String.format("""
        import threading
        from threadlevel.Task import Task
        import time
        import ast
                
        class DetectTask(Task):
                
            def __init__(self, drone, task_id, event_queue,**kwargs):
                super().__init__(drone, task_id, **kwargs)
                self.event_queue = event_queue
           
            def trigger_event(self, event):
                print(f"Detect Task: triggered event! {event}\\n")
                self.event_queue.put((self.task_id,  event))
                
            def run(self):
            """ + triggerEvent + """
                try:
                    print(f"Detect Task: hi this is detect task {self.task_id}\\n")
                    coords = ast.literal_eval(self.kwargs["coords"])
                    self.drone.setGimbalPose(0.0, float(self.kwargs["gimbal_pitch"]), 0.0)
                    hover_delay = int(self.kwargs["hover_delay"])
                    for dest in coords:
                        lng = dest["lng"]
                        lat = dest["lat"]
                        alt = dest["alt"]
                        print(f"Detect Task: move to {lat}, {lng}, {alt}")
                        self.drone.moveTo(lat, lng, alt)
                        time.sleep(hover_delay)

                    print("Detect Task: Done\\n")
                    self.trigger_event("done")
                except Exception as e:
                    print(e)
                
                
        """);
  }

  private String taskControllerContent() {


    StringBuilder transitionMap = new StringBuilder("""
                self.transitMap = {
                    "t1": self.task_1_transit,
                    "t2": self.task_2_transit
                }
               
            @staticmethod
            def task_1_transit(triggered_event):
                if (triggered_event == "done"):
                    return "terminate"
                if (triggered_event == "timeup"):
                    return "t2"
            @staticmethod
            def task_2_transit(triggered_event):
                if (triggered_event == "done"):
                    return "terminate"
        """);

    taskList.forEach((key, val) ->{

    } );

    return String.format("""
        import threading
                
                
        class TaskController(threading.Thread):
              
           
            def __init__(self, mr):
                super().__init__()
                self.mr = mr
               
                self.event_queue = mr.event_queue
                """+ transitionMap + """
        
               

                    
            @staticmethod
            def default_transit(triggered_event):
                print(f"no matched up transition, triggered event {triggered_event}\\n", triggered_event)
            def next_task(self, triggered_event):
                current_task_id = self.mr.get_current_task()
                next_task_id  = self.transitMap.get(current_task_id, self.default_transit)(triggered_event)
                self.mr.transit_to(next_task_id)
                return next_task_id
            def run(self):
                print("hi start the controller\\n")
                # check the triggered event
                while True:
                    item = self.event_queue.get()
                    if item is not None:
                        print(f"Controller: Trigger one event {item} \\n")
                        print(f"Controller: Task id  {item[0]} \\n")
                        print(f"Controller: event   {item[1]} \\n")
                        if (item[0] == self.mr.get_current_task()):
                            next_task_id = self.next_task(item[1])
                            if (next_task_id == 0):
                                print(f"Controller: the current task is done, terminate the controller \\n")
                                break
                                      
        """);
  }


  private String missionRunnerContent() {

    StringBuilder initContent = new StringBuilder("""
        def __init__(self, drone):
            super().__init__(drone)
            self.curr_task_id = None
            self.taskMap = {}
            self.event_queue = queue.Queue()
        """);

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

    taskList.forEach((key, task) -> {
      // Example of what you might want to append
      initContent.append(String.format("    self.%s\n", key));
      defineTaskContent.append(task.generateDefineTaskCode(key.toString()));
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
