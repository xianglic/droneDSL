package org.steeleagle.concrete;

import kala.text.StringSlice;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record Mission(String startTaskID, List<Transition> transitionList) {
  public record Transition(
      StringSlice condId,
      @Nullable String triggeredEvent,
      String currentTaskID,
      String nextTaskID
  ) {
  }
}
