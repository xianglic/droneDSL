package org.steeleagle.concrete;

import kala.collection.immutable.ImmutableSeq;
import org.jetbrains.annotations.NotNull;

public abstract class Task {
  public ImmutableSeq<Point> wayPoints;

  public String taskID;

  public Task(String taskID, ImmutableSeq<Point> wayPoints) {
    this.taskID = taskID;
    this.wayPoints = wayPoints;
  }

  public record Point(float x, float y, float z) {
    public String toJson() {
      return String.format("{'lng': %s, 'lat': %s, 'alt': %s}", x, y, z);
    }
  }

  public @NotNull StringBuilder wayPointsString() {
    var wayPointsStr = new StringBuilder();
    wayPointsStr.append("[");
    wayPoints.joinTo(wayPointsStr, ", ", Task.Point::toJson);
    wayPointsStr.append("]");
    return wayPointsStr;
  }

  public abstract void debugPrint();
  public abstract StringBuilder codeGenPython(@NotNull StringBuilder builder);
}
