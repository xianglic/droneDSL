package org.steeleagle.concrete;

import kala.collection.immutable.ImmutableSeq;

public class DetectTask extends Task {
  public float gimbalPitch;
  public float droneRotation;
  public int sampleRate;
  public float hoverDelay;
  public String model;

  public DetectTask(ImmutableSeq<Point> wayPoints, float gimbalPitch, float droneRotation, int sampleRate, float hoverDelay, String model) {
    super(wayPoints);
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
  public String initCode(String key) {
    return """
            # TASK%s
            kwargs.clear()
            kwargs["gimbal_pitch"] = %s
            kwargs["drone_rotation"] = %s
            kwargs["sample_rate"] = %s
            kwargs["hover_delay"] = %s
            kwargs["coords"] = %s
            self.%s = DetectTask(self.drone, %s, event_queue, **kwargs)
            self.taskMap[%s] = self.%s
        """.formatted(key, gimbalPitch, droneRotation, sampleRate, hoverDelay, wayPointsString(), key, key, key, key);
  }
}
