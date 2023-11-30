from interfaces.FlightScript import FlightScript
# Import derived tasks
from task_defs.DetectTask import DetectTask

class MS(FlightScript):

    def __init__(self, drone, cloudlet):
        super().__init__(drone, cloudlet)

    def run(self):
        try:
            kwargs = {}
            # Detect/DetectTask START
            # TASKtask1
            kwargs.clear()
            kwargs["gimbal_pitch"] = "-45.0"
            kwargs["drone_rotation"] = "0.0"
            kwargs["sample_rate"] = "2"
            kwargs["hover_delay"] = "5"
            kwargs["coords"] = "[{'lng': -79.95035, 'lat': 40.41558, 'alt': 25.0}, {'lng': -79.94917, 'lat': 40.41558, 'alt': 25.0}]"
            kwargs["model"] = "none"
            t = DetectTask(self.drone, self.cloudlet, **kwargs)
            self.taskQueue.put(t)
            print("Added task DetectTask to the queue")

            self.drone.takeOff()
            self._execLoop()
        except Exception as e:
            print(e)
