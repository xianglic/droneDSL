package org.droneDSL.cli.codeGen.concrete;

import kala.collection.immutable.ImmutableSeq;

public class DetectTask extends Task {
  public float gimbalPitch;
  public float droneRotation;
  public int sampleRate;
  public int hoverDelay;
  public String model;

  public DetectTask(String taskID, ImmutableSeq<Point> wayPoints, float gimbalPitch, float droneRotation, int sampleRate, int hoverDelay, String model) {
    super(taskID, wayPoints);
    this.gimbalPitch = gimbalPitch;
    this.droneRotation = droneRotation;
    this.sampleRate = sampleRate;
    this.hoverDelay = hoverDelay;
    this.model = model;
  }

  public void debugPrint() {
    System.out.println("gimbal_pitch :" + gimbalPitch);
    System.out.println("drone_rotation :" + droneRotation);
    System.out.println("sample_rate :" + sampleRate);
    System.out.println("hover_delay :" + hoverDelay);
    System.out.println("model :" + model);
  }

  @Override
  public String generateDefineTaskCode(boolean isSteelEagle) {
    var waypointsStr = wayPoints.joinToString(",", "[", "]", Point::toJson);
    if (!isSteelEagle) {
      return """
                  # TASK%s
                  kwargs.clear()
                  kwargs["gimbal_pitch"] = "%s"
                  kwargs["drone_rotation"] = "%s"
                  kwargs["sample_rate"] = "%s"
                  kwargs["hover_delay"] = "%s"
                  kwargs["coords"] = "%s"
                  self.%s = DetectTask(self.drone, "%s", event_queue, **kwargs)
                  self.taskMap["%s"] = self.%s
          """.formatted(taskID, gimbalPitch, droneRotation, sampleRate, hoverDelay, waypointsStr, taskID, taskID, taskID, taskID);
    } else {
      return """
                  # TASK%s
                  kwargs.clear()
                  kwargs["gimbal_pitch"] = "%s"
                  kwargs["drone_rotation"] = "%s"
                  kwargs["sample_rate"] = "%s"
                  kwargs["hover_delay"] = "%s"
                  kwargs["coords"] = "%s"
                  kwargs["model"] = "%s"
                  %s = DetectTask(self.drone, self.cloudlet, "%s", self.trigger_event_queue, transition_args_%s, **kwargs)
                  self.taskMap["%s"] = %s
          """.formatted(taskID, gimbalPitch, droneRotation, sampleRate, hoverDelay, waypointsStr, model, taskID, taskID, taskID, taskID, taskID);
    }

  }


}
