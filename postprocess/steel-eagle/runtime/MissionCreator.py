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
        if (triggered_event == "timeout"):
            return "task2"
        if (triggered_event == "object_detection"):
            return "task3"
        if (triggered_event == "done"):
            return "terminate"

    @staticmethod
    def task2_transit(triggered_event):
        if (triggered_event == "timeout"):
            return "task1"
        if (triggered_event == "object_detection"):
            return "task3"
        if (triggered_event == "done"):
            return "terminate"

    @staticmethod
    def task3_transit(triggered_event):
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
        transitMap["start"] = MissionCreator.start_transit
        transitMap["task1"]= MissionCreator.task1_transit
        transitMap["task2"]= MissionCreator.task2_transit
        transitMap["task3"]= MissionCreator.task3_transit
        transitMap["default"]= MissionCreator.default_transit
        # define task
        logger.info("MissionController: define the tasks\n")
        # TASKtask1
        task_attr_task1 = {}
        task_attr_task1["gimbal_pitch"] = "-20.0"
        task_attr_task1["drone_rotation"] = "0.0"
        task_attr_task1["sample_rate"] = "2"
        task_attr_task1["hover_delay"] = "0"
        task_attr_task1["coords"] = "[{'lng': -79.9500665, 'lat': 40.4155987, 'alt': 10.0},{'lng': -79.9503535, 'lat': 40.415514, 'alt': 10.0},{'lng': -79.9502368, 'lat': 40.4152965, 'alt': 10.0},{'lng': -79.9499431, 'lat': 40.4153965, 'alt': 10.0},{'lng': -79.9500665, 'lat': 40.4155987, 'alt': 10.0}]"
        task_attr_task1["model"] = "coco"
        transition_attr_task1 = {}
        transition_attr_task1["timeout"] = 120.0
        transition_attr_task1["object_detection"] = "car"
        task_arg_map["task1"] = TaskArguments(TaskType.Detect, transition_attr_task1, task_attr_task1)
        # TASKtask2
        task_attr_task2 = {}
        task_attr_task2["gimbal_pitch"] = "-45.0"
        task_attr_task2["drone_rotation"] = "0.0"
        task_attr_task2["sample_rate"] = "2"
        task_attr_task2["hover_delay"] = "0"
        task_attr_task2["coords"] = "[{'lng': -79.9498717, 'lat': 40.415161, 'alt': 10.0},{'lng': -79.9496799, 'lat': 40.4150793, 'alt': 10.0},{'lng': -79.9492923, 'lat': 40.4151651, 'alt': 10.0},{'lng': -79.949236, 'lat': 40.4153285, 'alt': 10.0},{'lng': -79.9494264, 'lat': 40.4153969, 'alt': 10.0},{'lng': -79.9497858, 'lat': 40.415305, 'alt': 10.0},{'lng': -79.9498717, 'lat': 40.415161, 'alt': 10.0}]"
        task_attr_task2["model"] = "coco"
        transition_attr_task2 = {}
        transition_attr_task2["timeout"] = 120.0
        transition_attr_task2["object_detection"] = "car"
        task_arg_map["task2"] = TaskArguments(TaskType.Detect, transition_attr_task2, task_attr_task2)
        #TASKtask3
        task_attr_task3 = {}
        task_attr_task3["model"] = "coco"
        task_attr_task3["class"] = "car"
        task_attr_task3["gimbal_pitch"] = "-30.0"
        transition_attr_task3 = {}
        transition_attr_task3["timeout"] = 60.0
        task_arg_map["task3"] = TaskArguments(TaskType.Track, transition_attr_task3, task_attr_task3)
