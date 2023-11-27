package org.steeleagle.pythonGen;

import org.steeleagle.pythonGen.concrete.AST;

import java.io.IOException;

public class CodeGeneratorPython {
  public static void generateCode(AST ast) {
    try {

      ast.codeGenPython();


    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
