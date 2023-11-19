package org.steeleagle.pythonGen;

import org.steeleagle.concrete.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CodeGeneratorPython {
  public static void generateCode(Task task) {
    try {
      Files.writeString(Paths.get("DetectTask.py"), task.codeGenPython(new StringBuilder()));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
