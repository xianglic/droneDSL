package org.steeleagle;

import org.aya.intellij.GenericNode;
import org.jetbrains.annotations.NotNull;
import org.steeleagle.psi.DslParserImpl;
import org.steeleagle.psi.StreamReporter;
import org.steeleagle.pythonGen.steeleagle.CodeGenerator_DSL_MS;

import static org.steeleagle.parser.BotPsiElementTypes.*;

public class Main {
  public static void main(String[] args) {
    var node = parser().parseNode("""
        Task {
            Detect task1 {
                waypoints: [(-80.0076661, 40.4534506, 15.0), (-80.0075856, 40.4526669, 15.0), (-80.0061211, 40.4526995, 15.0), (-80.0057885, 40.4536384, 15.0), (-80.0076661, 40.4534506, 15.0)],
                gimbal_pitch: -45.0,
                drone_rotation: 0.0,
                sample_rate: 2,
                hover_delay: 5,
                model: coco
            }
        }
              
        Mission {
            Start { task1 }
        }
        """);
    System.out.println(node.toDebugString());
    CodeGenerator_DSL_MS.generateCode(node);
    ////    traverseAST(node);

  }

  //   Define a method to traverse the AST recursively
  public static void traverseAST(GenericNode<? extends GenericNode<?>> node) {
    // Check if the current node is null
    if (node == null) {
      return;
    }

    // Process the current node
    // You can access node properties and perform actions here

//    var gimbal_attr = node.child(TASK).child(TASK_DECL).child(TASK_BODY).child(COMMA_SEP).childrenOfType(ATTRIBUTE).toImmutableSeq().get(1);
//    var gimbal_pitch = gimbal_attr.child(COLONED).child(ATTRIBUTE_EXPR).child(NUMBER).tokenText();
////    double gimbal_pitch = gimbal_attr.child(COLONED).child(ATTRIBUTE_EXPR).child(NUMBER).tokenText().toDouble();
//    System.out.println("gimbal_pitch :" + gimbal_pitch);
//
//    List<String> waypoints = new ArrayList<>();
//    var waypoint_attr = node.child(TASK).child(TASK_DECL).child(TASK_BODY).child(COMMA_SEP).childrenOfType(ATTRIBUTE).toImmutableSeq().get(0);
//    var way_points = waypoint_attr.child(COLONED).child(ATTRIBUTE_EXPR).child(SQUARE_BRACKED).child(COMMA_SEP).childrenOfType(PAREN).toSeq(); // childrenOfType(WAYPOINT).toSeq();
//    for (var point : way_points){
//      waypoints.add(point.child(WAYPOINT).child(LATITUDE).tokenText().toString());
//      waypoints.add(point.child(WAYPOINT).child(LONGITUDE).tokenText().toString());
//      waypoints.add(point.child(WAYPOINT).child(ALTITUDE).tokenText().toString());
//    }
//    System.out.println("waypoints :" + way_points);
//    System.out.println("waypoints :" + waypoints);
//    System.out.println("waypoints :" + waypoints.size());


    System.out.println("Mission Type: " + node.child(MISSION).child(MISSION_CONTENT).child(MISSION_START_DECL));

    System.out.println("Node Type: " + node.child(TASK).child(TASK_DECL));
    System.out.println("Mission Type: " + node.child(MISSION).child(MISSION_CONTENT).child(MISSION_START_DECL));

//    System.out.println("Task Type: " + node.elementType());
//
//    // Recursively traverse child nodes
//    for (var child : node.childrenView()) {
//      traverseAST(child);
//    }
  }

  @NotNull
  private static DslParserImpl parser() {
    return new DslParserImpl(new StreamReporter(System.out));
  }
}
