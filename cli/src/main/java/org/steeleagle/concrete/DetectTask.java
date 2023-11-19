package org.steeleagle.concrete;

import kala.collection.immutable.ImmutableSeq;
import org.jetbrains.annotations.NotNull;

public record DetectTask(
    @NotNull ImmutableSeq<Point> waypoints,
    float gimbalPitch,
    float droneRotation,
    int sampleRate,
    float hoverDelay,
    String model
) {
  record Point(int x, int y, int z) {
  }
}
