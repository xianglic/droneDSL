package org.steeleagle;

import org.aya.intellij.GenericNode;
import org.jetbrains.annotations.NotNull;
import org.steeleagle.psi.DslParserImpl;
import org.steeleagle.psi.StreamReporter;

import java.util.ArrayList;
import java.util.List;

import static org.steeleagle.parser.BotPsiElementTypes.*;

public class Main {
  public static int translate(GenericNode<? extends GenericNode<?>> node) {


    return 114514;
  }

  public static void main(String[] args) {
    var node = parser().parseNode("""
        Task{
            Detect task1 {
                waypoints: [(100, 200, 300), (200, 200, 300), (300, 200, 300), (400, 200, 300)],
                gimbal_pitch: 20,
                model: coco
            }
        }
        
        Mission {
            Start { task1 }
        }
        """);
    System.out.println(node.toDebugString());
//    traverseAST(node);
    CodeGenerator.generateCode(node);
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
