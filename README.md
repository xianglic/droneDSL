# droneDSL Documentation



## Introduction

- This project develops a domain specific language for describing drone flight behavior.
- The project involves three modules:
  - pre-process: Extract the way points from KML file.
  - command line interface: Converts the written droneDSL to mission flight script.
  - post-process: Run the mission flight script using Olympe SDK

## Design

Steel Eagle is separated into three distinct parts: the local commander client, the cloudlet server, and the onboard software. The commander client is intended to run on a personal computer close to the PIC (Pilot in Command) with an internet connection. It gives an interface to receive telemetry and upload an MS to the drone. It also provides tools to assume manual control of the drone while it is in-flight (*kill* command). The cloudlet server is the bridge between the onboard drone software and the commander client. It relays messages between the two and also publicly hosts flight scripts. Additionally, the server runs compute engines for the drone which will be executed on the offloaded sensor data/video stream. Finally, the onboard software consists of an app that runs on the drone-mounted Android device. This app relays telemetry and offloads sensor data/video frames to the cloudlet server. It also is responsible for interpreting an MS as DSI which are then sent to the drone to execute. Note: once an MS is sent to a drone, it is downloaded onto the onboard app. This means that in the event of a disconnection, the drone can continue executing its mission. See the **Architecture** section for a detailed system diagram.

## Architecture

![image-20231130130713108](/home/xianglic/.config/Typora/typora-user-images/image-20231130130713108.png)

## Nomenclature

**Mission** - A mission is an automated drone flight which consists of one or more tasks that is designed by a mission planner.

**Task** - A task is a unit of work that should be completed during a mission which consists of a scope and actions. Initially tasks can be simple self-contained units or, in the future, can have dependencies on other tasks. Task ordering will be done by the mission plan generator.

**KML/KMZ** - Keyhole Markup Language is an XML document that is used to define the scope of a mission’s tasks. A KMZ file is a zipped version of a KML document that includes any additional styling resources needed for the KML document.

**DSI** - Drone-Specific Instructions are commands which are written using the API for a specific drone (e.g. Parrot GroundSDK commands for Parrot Drones).

**FPS** -Flight plan structure contains the a list of Task object, where each task object has its own task type(detect, track, object avoidance). Each task type contains different set of attribute informations needed for executing that task required for steel-eagle pipeline.

**Mission Runner** - define all the tasks, start the first task, and manage the task transition based on the decision made from Task Controller.

 **Task Controller** - Manage a event queue. Once recieved an event in the event queue, make decision on what task should be run next based on the current task, and event message. Notify the Mission Runner to transit to the next task.

**TaskDefs** - This is the implementation of specific tasks including detect task, track task, and object avoidance task. These tasks are reponsible for sending the triggered event message to the task controller

## Workflow

### My maps



### Pre-process

This module requires user to use My maps to draw the drone's flight route. After drawing the flight route, user needs to download the flight route as KML file.

The preprocess module takes KML file as an input and extract the waypoints for takeoff, and following tasks.

cmd: ./gradlew :preprocess:run --args="{KML file path}"

```
Parsed result example:
takeoff
[(-79.9504726, 40.4156235, 25.0)]
task 1
[(-79.9502696, 40.4156737, 25.0),(-79.9502655, 40.4154588, 25.0),(-79.9499142, 40.4154567, 25.0),(-79.9499128, 40.4156753, 25.0),(-79.9502696, 40.4156737, 25.0)]
task 2
[(-79.9499065, 40.4152976, 25.0),(-79.9502364, 40.4152976, 25.0),(-79.950054, 40.4151098, 25.0),(-79.9499065, 40.4152976, 25.0)]

```





### Command Line Interface

User generates its own DSL based on the below template: 

```
        Task {
            ^TaskType^ ^TaskID^ {
                ^TaskAttribute_1^: ^attribute value^, 
                ...
       			^TaskAttribute_n^: ^attribute value^,
            }
        }

        Mission {
            Start { ^firstTask^ }
            Transition (^triggeredEvent_1^(^trigger value^)) ^TaskID^ -> ^TaskID^
            .....
            Transition (^triggeredEvent_n^(^trigger value^)) ^TaskID^ -> ^TaskID^
        }

```



The ^ symbol include what type of information needs to be filled in the template. 

TaskType:

​	Detect, Track, and Object Avoidance

TaskID:

​	name of the task

TaskAttribute_*:

​	Gimbal_pitch, Hover_delay, Model, rotation

AttributeValue:

​	number, or string

TriggeredEvent:

​	Timeup

​	Detected

Triggered Value:

​	seconds,

​	colors



parse the DSL to low level AST tree

convert low level AST to Concrete structure

translate to state machine written in python or java

example:

```
Task {
    Detect task1 {
        way_points: [(-79.9503492, 40.4155806, 25.0),(-79.9491717, 40.4155826, 25.0)],
        gimbal_pitch: -45.0,
        drone_rotation: 0.0,
        sample_rate: 2,
        hover_delay: 5
        model: none
    }

    Detect task2 {
        way_points: [(-79.9497296, 40.415505, 25.0),(-79.9497001, 40.41507, 25.0)],
        gimbal_pitch: -45.0,
        drone_rotation: 0.0,
        sample_rate: 2,
        hover_delay: 5
        model: none
    }
}

Mission {
    Start { task1 }
    Transition (timeout(40)) task1 -> task2
}
```



### Post-process

- Simulation Environment setup:

  - Parrot Simulation Environment:
    - Sphinx drone
    - parrot unity engine 
    - Omlype SDK  environment

  - Steel-eagle Pipeline:
    - onion router
    - cognitive engine backend
    - commander
    - superviosr

- cmd:

  - ```
    env:
        sphinx "/opt/parrot-sphinx/usr/share/sphinx/drones/anafi.drone"::firmware="https://firmware.parrot.com/Versions/anafi/pc/%23latest/images/anafi-pc.ext2.zip"
    
        parrot-ue4-sphx-tests -level=main -list-paths
        == Available Paths ===========================================
        Path name : DefaultPath
        Path name : LongPath
        Path name : SquarePath
        ==============================================================
    
        parrot-ue4-sphx-tests -gps-json='{"lat_deg":40.4156235, "lng_deg":-79.9504726 , "elevation":1.5}' -ams-path="DefaultPath,Pickup:*" -ams-path=SquarePath,Jasper
    
        sphinx-cli param -m world actors pause false
    Task:
        90 second: task1 complete -> task 2 complete
    ```



Example:

```python
import threading
from dependencies.Task import Task
import time
import ast

class DetectTask(Task):

    def __init__(self, drone, task_id, event_queue,**kwargs):
        super().__init__(drone, task_id, **kwargs)
        self.event_queue = event_queue

    def trigger_event(self, event):
        print(f"**************Detect Task {self.task_id}: triggered event! {event}**************\n")
        self.event_queue.put((self.task_id,  event))

    def run(self):
        # triggered event
        if (self.task_id == "task1"):
            # construct the timer with 90 seconds
            timer = threading.Timer(90, self.trigger_event, ["timeout"])
            timer.daemon = True
            # Start the timer
            timer.start()
        try:
            print(f"**************Detect Task {self.task_id}: hi this is detect task {self.task_id}**************\n")
            coords = ast.literal_eval(self.kwargs["coords"])
            self.drone.setGimbalPose(0.0, float(self.kwargs["gimbal_pitch"]), 0.0)
            hover_delay = int(self.kwargs["hover_delay"])
            for dest in coords:
                lng = dest["lng"]
                lat = dest["lat"]
                alt = dest["alt"]
                print(f"**************Detect Task {self.task_id}: move to {lat}, {lng}, {alt}**************")
                self.drone.moveTo(lat, lng, alt)
                time.sleep(hover_delay)

            print(f"**************Detect Task {self.task_id}: Done**************\n")
            self.trigger_event("done")
        except Exception as e:
            print(e)



```

 ![img](https://documents.lucid.app/documents/036d65a8-1197-41e7-9e98-4f0be76c5665/pages/0_0?a=3833&x=2686&y=1284&w=1167&h=773&store=1&accept=image%2F*&auth=LCA%206f845b95e4708a274f9b232449aa8261fc407a956e3bbabc7715e7dc9446febc-ts%3D1701359926)






