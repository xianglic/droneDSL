
from ..transition_defs.ObjectDetectionTransition import ObjectDetectionTransition
from ..transition_defs.TimerTransition import TimerTransition
from ..transition_defs.HSVDetectionTransition import HSVDetectionTransition
from interface.Task import Task
import asyncio
import ast
import logging
from gabriel_protocol import gabriel_pb2


logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)

class DetectTask(Task):

    def __init__(self, control, data, task_id, trigger_event_queue, task_args):
        super().__init__(control, data, task_id, trigger_event_queue, task_args)


    def create_transition(self):

        logger.info(f"**************Detect Task {self.task_id}: create transition! **************\n")
        logger.info(self.transitions_attributes)
        args = {
            'task_id': self.task_id,
            'trans_active': self.trans_active,
            'trans_active_lock': self.trans_active_lock,
            'trigger_event_queue': self.trigger_event_queue
        }

        # triggered event
        if ("timeout" in self.transitions_attributes):
            logger.info(f"**************Detect Task {self.task_id}:  timer transition! **************\n")
            timer = TimerTransition(args, self.transitions_attributes["timeout"])
            timer.daemon = True
            timer.start()

        if ("object_detection" in self.transitions_attributes):
            logger.info(f"**************Detect Task {self.task_id}:  object detection transition! **************\n")
            object_trans = ObjectDetectionTransition(args, self.transitions_attributes["object_detection"], self.data)
            object_trans.daemon = True
            object_trans.start()

        if ("hsv_detection" in self.transitions_attributes):
            logger.info(f"**************Detect Task {self.task_id}:  hsv detection transition! **************\n")
            hsv = HSVDetectionTransition(args, self.transitions_attributes["hsv_detection"], self.data)
            hsv.daemon = True
            hsv.start()

    async def report(self, msg):
        reply = await self.control['report'].send_notification(msg)
        self.running_flag = reply['status']
        self.patrol_areas = reply['patrol_areas']
        self.altitude = reply['altitude']

    @Task.call_after_exit
    async def run(self):
        # init the data
        model = self.task_attributes["model"]
        lower_bound = self.task_attributes["lower_bound"]
        upper_bound = self.task_attributes["upper_bound"]
        await self.control['ctrl'].configure_compute(model, lower_bound, upper_bound)
        logger.info("Finished configuring compute")
        self.create_transition()
        logger.info(f"Done creating transition; {self.task_attributes}")
        # try:
        logger.info(f"**************Detect Task {self.task_id}: hi this is detect task {self.task_id}**************\n")

        logger.info("Sending notification")
        await self.report("start")

        logger.info(f"**************Detect Task {self.task_id}: running_flag: {self.running_flag}**************\n")
        while self.running_flag == "running":
            await self.control['ctrl'].set_gimbal_pose(float(self.task_attributes["gimbal_pitch"]), 0.0, 0.0)
            for  area in self.patrol_areas:
                logger.info(f"**************Detect Task {self.task_id}: patrol area: {area}**************\n")
                coords = await self.control['report'].get_waypoints(area)
                for dest in coords:
                    lng = dest["lng"]
                    lat = dest["lat"]
                    alt = self.altitude
                    logger.info(f"**************Detect Task {self.task_id}: move to {lat}, {lng}, {alt}**************\n")
                    await self.control['ctrl'].set_gps_location(lat, lng, alt)
                    await asyncio.sleep(1)

            await self.report("finish")

        logger.info(f"**************Detect Task {self.task_id}: Done**************\n")


