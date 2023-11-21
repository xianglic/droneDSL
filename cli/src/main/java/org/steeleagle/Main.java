package org.steeleagle;

import kala.collection.immutable.ImmutableMap;
import kala.text.StringSlice;
import org.jetbrains.annotations.NotNull;
import org.steeleagle.concrete.AST;
import org.steeleagle.concrete.Preparse;
import org.steeleagle.concrete.Task;
import org.steeleagle.psi.DslParserImpl;
import org.steeleagle.psi.StreamReporter;
import org.steeleagle.pythonGen.CodeGeneratorPython;

import static org.steeleagle.parser.BotPsiElementTypes.*;

public class Main {
  public static void main(String[] args) {
    var node = parser().parseNode("""
        Task {
            Detect task1 {
                way_points: [(-79.9503613, 40.4156274, 50.0), (-79.9503231, 40.415537, 50.0), (-79.950185, 40.415561, 50.0), (-79.9502218, 40.4156524, 50.0), (-79.9503613, 40.4156274, 50.0)],
                gimbal_pitch: -45.0,
                drone_rotation: 0.0,
                sample_rate: 2,
                hover_delay: 5
                model: coco
            }
        }

        Mission {
            Start { task1 }
        }
        """);
    System.out.println(node.toDebugString());

    ImmutableMap<String, Task> taskMap = ImmutableMap.from(node.child(TASK).childrenOfType(TASK_DECL).map(Preparse::createTask));
    var startTaskID = Preparse.createMission(node.child(MISSION).child(MISSION_CONTENT), taskMap);
    int isSteelEagle = 1;
    var ast = new AST(startTaskID, taskMap, isSteelEagle);

    CodeGeneratorPython.generateCode(ast);
    // System.out.println(node.toDebugString());
  }

  @NotNull
  private static DslParserImpl parser() {
    return new DslParserImpl(new StreamReporter(System.out));
  }
}
