package org.steeleagle.concrete;

import kala.collection.mutable.MutableMap;
import kala.text.StringSlice;
import org.aya.intellij.GenericNode;
import org.jetbrains.annotations.NotNull;

import static org.steeleagle.parser.BotPsiElementTypes.*;

public interface Preparse {
  enum TaskKind {
    Detect,
    Track;
  }

  class AttributeMap {
    MutableMap<StringSlice, GenericNode<? extends GenericNode<?>>> content = MutableMap.create();
    TaskKind kind;

    public GenericNode<? extends GenericNode<?>> get(String name) {
      return content.get(StringSlice.of(name));
    }
  }

  @NotNull
  static Task createTask(GenericNode<? extends GenericNode<?>> node) {
    var attrMap = createMap(node);
    return switch (attrMap.kind) {
      case Detect -> {
        var gimbal_pitch = attrMap.get("gimbal_pitch").child(NUMBER).tokenText();
        var drone_rotation = attrMap.get("drone_rotation").child(NUMBER).tokenText();
        var sample_rate = attrMap.get("sample_rate").child(NUMBER).tokenText();
        var hover_delay = attrMap.get("hover_delay").child(NUMBER).tokenText();
        var model = attrMap.get("model").child(NAME).tokenText();

        var wayPoints = attrMap.get("way_points").child(SQUARE_BRACKED).childrenOfType(PAREN).
            map(point -> {
              var nums = point.child(WAYPOINT).childrenOfType(NUMBER)
                  .map(t -> t.tokenText().toFloat())
                  .toImmutableSeq();
              return new DetectTask.Point(nums.get(0), nums.get(1), nums.get(2));
            })
            .toImmutableSeq();

        yield new DetectTask(
            wayPoints,
            gimbal_pitch.toFloat(),
            drone_rotation.toFloat(),
            sample_rate.toInt(),
            hover_delay.toFloat(),
            model.toString()
        );
      }
      case Track -> throw new UnsupportedOperationException();
    };
  }

  @NotNull
  private static AttributeMap createMap(GenericNode<? extends GenericNode<?>> node) {
    var task = node.child(TASK).child(TASK_DECL);
    var isDetect = task.peekChild(TASK_DETECT_KW);
    var attrMap = new AttributeMap();
    task.child(TASK_BODY).childrenOfType(ATTRIBUTE)
        .forEach(attr -> attrMap.content.put(attr.child(ID).tokenText(), attr.child(ATTRIBUTE_EXPR)));
    if (isDetect != null) {
      attrMap.kind = TaskKind.Detect;
    } else {
      attrMap.kind = TaskKind.Track;
    }
    return attrMap;
  }


}
