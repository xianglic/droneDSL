package org.steeleagle;

import org.aya.intellij.GenericNode;
import org.jetbrains.annotations.NotNull;
import org.steeleagle.psi.DslParserImpl;
import org.steeleagle.psi.StreamReporter;

public class Main {
  public static int bro(GenericNode<? extends GenericNode<?>> node) {
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
        
            Track task2 {
                waypoints: [(100, 200, 300), (200, 200, 300), (300, 200, 300), (400, 200, 300)],
                gimbal_pitch: 20,
                model: robomaster
            }
        }
        
        Mission {
            Start { task1 }
        }
        """);
    System.out.println(node.toDebugString());
  }

  @NotNull
  private static DslParserImpl parser() {
    return new DslParserImpl(new StreamReporter(System.out));
  }
}
