package org.steeleagle;

import kala.collection.immutable.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.steeleagle.pythonGen.concrete.AST;
import org.steeleagle.pythonGen.concrete.Preparse;
import org.steeleagle.pythonGen.concrete.Task;
import org.steeleagle.psi.DslParserImpl;
import org.steeleagle.psi.StreamReporter;
import org.steeleagle.pythonGen.CodeGeneratorPython;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.steeleagle.parser.BotPsiElementTypes.*;

public class Main {
  public static void main(String[] args) {
    String filePath = args[0];
    boolean isSteelEagle = Objects.equals(args[1], "--steeleagle");

    String fileContent;

    try {
      // Read the content of the file
      fileContent = Files.readString(Paths.get(filePath));
    } catch (IOException e) {
      System.err.println("Error reading the file: " + e.getMessage());
      return; // Exit if there's an error reading the file
    }

    var node = parser().parseNode(fileContent);
    System.out.println(node.toDebugString());

    ImmutableMap<String, Task> taskMap = ImmutableMap.from(node.child(TASK).childrenOfType(TASK_DECL).map(Preparse::createTask));
    var startTaskID = Preparse.createMission(node.child(MISSION).child(MISSION_CONTENT), taskMap);

    var ast = new AST(startTaskID, taskMap, isSteelEagle);

    CodeGeneratorPython.generateCode(ast);
  }

  @NotNull
  private static DslParserImpl parser() {
    return new DslParserImpl(new StreamReporter(System.out));
  }
}
