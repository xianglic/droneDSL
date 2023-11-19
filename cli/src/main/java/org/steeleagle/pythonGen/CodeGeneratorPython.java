package org.steeleagle.pythonGen;

import org.aya.intellij.GenericNode;
import org.steeleagle.concrete.Preparse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CodeGeneratorPython {
  public static void generateCode(GenericNode<? extends GenericNode<?>> node) {
    var task = Preparse.createTask(node);

    try {
      Files.writeString(Paths.get("DetectTask.py"), task.codeGenPython(new StringBuilder()));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
