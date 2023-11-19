package org.steeleagle.concrete;

import kala.collection.immutable.ImmutableSeq;
import org.jetbrains.annotations.NotNull;

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
  public StringBuilder codeGenPython(@NotNull StringBuilder builder) {

    return builder;
  }
}
