import logging
logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)
from interfaces.Task import TaskArguments, TaskType


class MissionCreator:

    # transition
    @staticmethod
    def start_transit(triggered_event):
        logger.info("start_transit\n")
        return "task1"
    @staticmethod
    def task1_transit(triggered_event):
        if (triggered_event == "object_detection"):
            return "task2"
        if (triggered_event == "done"):
            return "terminate"

    @staticmethod
    def task2_transit(triggered_event):
        if (triggered_event == "timeout"):
            return "task1"
        if (triggered_event == "done"):
            return "terminate"

    @staticmethod
    def default_transit(triggered_event):
        logger.info(f"MissionController: no matched up transition, triggered event {triggered_event}\n", triggered_event)
    #task
    @staticmethod
    def define_mission(transitMap, task_arg_map):
        #define transition
        logger.info("MissionController: define the transitMap\n")

        transitMap["task1"]= task1_transit
        transitMap["task2"]= task2_transit
        transitMap["default"]= default_transit
        # define task
        logger.info("MissionController: define the tasks\n")
        # TASKtask1
        task_attr_task1 = {}
        task_attr_task1["gimbal_pitch"] = "-20.0"
        task_attr_task1["drone_rotation"] = "0.0"
        task_attr_task1["sample_rate"] = "2"
        task_attr_task1["hover_delay"] = "0"
        task_attr_task1["coords"] = "[{'lng': -79.9499065, 'lat': 40.4152976, 'alt': 8.0},{'lng': -79.9502364, 'lat': 40.4152976, 'alt': 8.0},{'lng': -79.950054, 'lat': 40.4151098, 'alt': 8.0},{'lng': -79.9499065, 'lat': 40.4152976, 'alt': 8.0}]"
        task_attr_task1["model"] = "coco"
        transition_attr_task1 = {}
        transition_attr_task1["object_detection"] = "car"
        task_arg_map["task1"] = TaskArguments(TaskType.Detect, transition_attr_task1, task_attr_task1)
        # TASKtask2
        task_attr_task2 = {}
        task_attr_task2["gimbal_pitch"] = "-45.0"
        task_attr_task2["drone_rotation"] = "0.0"
        task_attr_task2["sample_rate"] = "2"
        task_attr_task2["hover_delay"] = "0"
        task_attr_task2["coords"] = "[{'lng': -79.9502696, 'lat': 40.4156737, 'alt': 35.0},{'lng': -79.9502655, 'lat': 40.4154588, 'alt': 35.0},{'lng': -79.9499142, 'lat': 40.4154567, 'alt': 35.0},{'lng': -79.9499128, 'lat': 40.4156753, 'alt': 35.0},{'lng': -79.9502696, 'lat': 40.4156737, 'alt': 35.0}]"
        task_attr_task2["model"] = "coco"
        transition_attr_task2 = {}
        transition_attr_task2["timeout"] = 70.0
        task_arg_map["task2"] = TaskArguments(TaskType.Detect, transition_attr_task2, task_attr_task2)
