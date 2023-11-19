package org.steeleagle.concrete;

import kala.collection.immutable.ImmutableMap;
import kala.text.StringSlice;

public record AST(ImmutableMap<StringSlice, Task> taskList, Mission mission) {
}
