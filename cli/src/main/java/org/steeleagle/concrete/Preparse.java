package org.steeleagle.concrete;

import kala.collection.mutable.MutableMap;
import kala.text.StringSlice;
import org.aya.intellij.GenericNode;
import org.jetbrains.annotations.NotNull;

import static org.steeleagle.parser.BotPsiElementTypes.*;

public interface Preparse {
  class IDEA {
    MutableMap<StringSlice, GenericNode<? extends GenericNode<?>>> bro = MutableMap.create();

    public GenericNode<? extends GenericNode<?>> get(String name) {
      return bro.get(StringSlice.of(name));
    }
  }

  @NotNull
  static DetectTask createTask(GenericNode<? extends GenericNode<?>> node) {
    var task = node.child(TASK).child(TASK_DECL);
    var bro = new IDEA();
    task.child(TASK_BODY).childrenOfType(ATTRIBUTE)
        .forEach(attr -> bro.bro.put(attr.child(ID).tokenText(), attr.child(ATTRIBUTE_EXPR)));
    var gimbal_pitch = bro.get("gimbal_pitch").child(NUMBER).tokenText();
    var drone_rotation = bro.get("drone_rotation").child(NUMBER).tokenText();
    var sample_rate = bro.get("sample_rate").child(NUMBER).tokenText();
    var hover_delay = bro.get("hover_delay").child(NUMBER).tokenText();
    var model = bro.get("model").child(NAME).tokenText();

    var wayPoints = bro.get("way_points").child(SQUARE_BRACKED).childrenOfType(PAREN).
        map(point -> {
          var nums = point.child(WAYPOINT).childrenOfType(NUMBER)
              .map(t -> t.tokenText().toFloat());
          return new DetectTask.Point(nums.get(0), nums.get(1), nums.get(2));
        })
        .toImmutableSeq();
    var detectTask = new DetectTask(
        wayPoints,
        gimbal_pitch.toFloat(),
        drone_rotation.toFloat(),
        sample_rate.toInt(),
        hover_delay.toFloat(),
        model.toString()
    );
    detectTask.debugPrint();
    return detectTask;
  }
}
