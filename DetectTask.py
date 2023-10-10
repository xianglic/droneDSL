from interfaces.Task import Task
import time
import ast

class DetectTask(Task):

def __init__(self, drone, cloudlet, **kwargs):
    super().__init__(drone, cloudlet, **kwargs)
  def move() :
    self.drone.moveTo(100, 200, 300)
    time.sleep(2)
    self.drone.moveTo(200, 200, 300)
    time.sleep(2)
    self.drone.moveTo(300, 200, 300)
    time.sleep(2)
    self.drone.moveTo(400, 200, 300)
    time.sleep(2)
def run(self):
    try:
        self.cloudlet.switchModel("coco")
        self.drone.setGimbalPose(0.0, 20, 0.0)
        move()
    except Exception as e:
        print(e)