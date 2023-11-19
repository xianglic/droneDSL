package org.steeleagle.concrete;

import java.util.ArrayList;

public class Mission {
  public static class Transition {

    public String triggeredEvent;
    public String currentTaskID;
    public String nextTaskID;

    public Transition(String triggeredEvent, String currentTaskID, String nextTaskID) {
      this.triggeredEvent = triggeredEvent;
      this.currentTaskID = currentTaskID;
      this.nextTaskID = nextTaskID;
    }
  }

  public String startTaskID;
  public ArrayList<Transition> transitionList;

  public Mission(String startTaskID, ArrayList<Transition> transitionList) {
    this.startTaskID = startTaskID;
    this.transitionList = transitionList;
  }

}
