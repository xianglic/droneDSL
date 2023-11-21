package org.steeleagle.pythonGen;

import org.steeleagle.concrete.AST;
import org.steeleagle.concrete.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CodeGeneratorPython {
  public static void generateCode(AST ast) {
    try {

      ast.codeGenPython();


    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
