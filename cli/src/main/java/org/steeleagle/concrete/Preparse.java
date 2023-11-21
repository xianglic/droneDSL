package org.steeleagle.concrete;

import kala.collection.immutable.ImmutableMap;
import kala.collection.mutable.MutableMap;
import kala.text.StringSlice;
import kala.tuple.Tuple;
import kala.tuple.Tuple2;
import org.aya.intellij.GenericNode;
import org.jetbrains.annotations.NotNull;
import org.steeleagle.concrete.Task.Transition;

import java.util.ArrayList;
import java.util.List;

import static org.steeleagle.parser.BotPsiElementTypes.*;

public interface Preparse {
  enum TaskKind {
    Detect,
    Track
  }

  class AttributeMap {
    MutableMap<StringSlice, GenericNode<? extends GenericNode<?>>> content = MutableMap.create();
    TaskKind kind;

    public GenericNode<? extends GenericNode<?>> get(String name) {
      return content.get(StringSlice.of(name));
    }
  }

  @NotNull
  static Tuple2<String, Task> createTask(GenericNode<? extends GenericNode<?>> task) {
    var attrMap = createMap(task);
    var taskID = task.child(TASK_NAME).tokenText().toString();
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

        var detectTask = new DetectTask(
            taskID,
            wayPoints,
            gimbal_pitch.toFloat(),
            drone_rotation.toFloat(),
            sample_rate.toInt(),
            hover_delay.toInt(),
            model.toString()
        );
        yield Tuple.of(taskID, detectTask);
      }
      case Track -> throw new UnsupportedOperationException();
    };
  }

  static String createMission(GenericNode<? extends GenericNode<?>> missionContent, ImmutableMap<String, Task> taskMap) {

    var startTaskID = missionContent.child(MISSION_START_DECL).child(TASK_NAME).tokenText().toString();

    for (var transition : missionContent.childrenOfType(MISSION_TRANSITION)) {
      var cond = transition.child(PAREN).child(COND);
      var condId = cond.child(ID).tokenText().toString();
      var argNode = cond.peekChild(PAREN);
      var arg = argNode != null ? argNode.child(NUMBER).tokenText().toString() : null;

      var taskPair = transition.childrenOfType(TASK_NAME)
          .map(GenericNode::tokenText)
          .map(StringSlice::toString)
          .toImmutableSeq();
      var curr_task = taskPair.get(0);
      var next_task = taskPair.get(1);

      var tran = new Transition(condId, arg, curr_task, next_task);
      taskMap.get(curr_task).transitions.add(tran);
    }

    return startTaskID;
  }

  @NotNull
  private static AttributeMap createMap(GenericNode<? extends GenericNode<?>> task) {
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
