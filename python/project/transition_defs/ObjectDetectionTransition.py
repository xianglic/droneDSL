import asyncio
import json
import logging
from interface.Transition import Transition, auto_register_unregister
import dataplane_pb2 as dataplane

logger = logging.getLogger(__name__)

class ObjectDetectionTransition(Transition):
    def __init__(self, args, target, data):
        super().__init__(args)
        self.target = target
        self.data = data

    @auto_register_unregister
    async def run(self):
        await self._register()
        
        # Non-blocking startup delay
        await asyncio.sleep(4)

        while not self._stop_event.is_set():
            result = await self.data.get_compute_result("openscout-object")
            if len(result) > 0:
                logger.info(f"Task {self.task_id}: Detected payload: {result}")

                try:
                    # Extract JSON result string
                    dets = result[0]
                    json_data = json.loads(dets)

                    class_attribute = json_data[0].get('class')
                    logger.info(f"Detected class: {class_attribute}")

                    if class_attribute == self.target:
                        logger.info(f"Task {self.task_id}: Target matched! {class_attribute}")
                        await self._trigger_event("object_detection")
                        break

                except json.JSONDecodeError as e:
                    logger.error(f"JSON decode error: {e}")
                except Exception as e:
                    logger.error(f"Unexpected error in detection: {e}")

            await asyncio.sleep(0.1)  # Yield to event loop

        await self._unregister()
