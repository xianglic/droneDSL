package org.steeleagle.concrete;

import kala.collection.immutable.ImmutableSeq;
import org.jetbrains.annotations.NotNull;

public record DetectTask(
    @NotNull ImmutableSeq<Point> wayPoints,
    float gimbalPitch,
    float droneRotation,
    int sampleRate,
    float hoverDelay,
    String model
) {
  public record Point(float x, float y, float z) {
    public String toJson() {
      return String.format("{'lng': %s, 'lat': %s, 'alt': %s}", x, y, z);
    }
  }

  public void debugPrint() {
    System.out.println("gimbal_pitch :" + gimbalPitch);
    System.out.println("drone_rotation :" + droneRotation);
    System.out.println("sample_rate :" + sampleRate);
    System.out.println("hover_delay :" + hoverDelay);
    System.out.println("model :" + model);
  }
}
