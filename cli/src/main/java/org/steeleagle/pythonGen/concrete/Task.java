package org.steeleagle.pythonGen.concrete;

import kala.collection.immutable.ImmutableSeq;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class Task {
  public ImmutableSeq<Point> wayPoints;

  public String taskID;

  public Task(String taskID, ImmutableSeq<Point> wayPoints) {
    this.taskID = taskID;
    this.wayPoints = wayPoints;
    this.transitions = new ArrayList<>();
  }

  public List<Transition> transitions;



  public record Point(float x, float y, float z) {
    public String toJson() {
      return String.format("{'lng': %s, 'lat': %s, 'alt': %s}", x, y, z);
    }
  }


  public record Transition(
      String condID,
      @Nullable String condArg,
      String currentTaskID,
      String nextTaskID
  ){}

  public @NotNull StringBuilder wayPointsString() {
    var wayPointsStr = new StringBuilder();
    wayPointsStr.append("[");
    wayPoints.joinTo(wayPointsStr, ", ", Task.Point::toJson);
    wayPointsStr.append("]");
    return wayPointsStr;
  }

  public abstract void debugPrint();
  public abstract String generateDefineTaskCode(boolean isSteelEagle);
}
