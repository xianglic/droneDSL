package org.steeleagle.concrete;

import kala.collection.immutable.ImmutableSeq;

public class AST {
  public ImmutableSeq<Task> taskList;

  public AST(ImmutableSeq<Task> taskList){
    this.taskList = taskList;
  }
}
