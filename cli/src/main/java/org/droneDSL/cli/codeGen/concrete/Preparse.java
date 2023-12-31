package org.droneDSL.cli.codeGen.concrete;

import kala.collection.immutable.ImmutableMap;
import kala.collection.mutable.MutableMap;
import kala.text.StringSlice;
import kala.tuple.Tuple;
import kala.tuple.Tuple2;
import org.aya.intellij.GenericNode;
import org.droneDSL.cli.parser.BotPsiElementTypes;
import org.jetbrains.annotations.NotNull;

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
    var taskID = task.child(BotPsiElementTypes.TASK_NAME).tokenText().toString();
    return switch (attrMap.kind) {
      case Detect -> {
        var gimbal_pitch = attrMap.get("gimbal_pitch").child(BotPsiElementTypes.NUMBER).tokenText();
        var drone_rotation = attrMap.get("drone_rotation").child(BotPsiElementTypes.NUMBER).tokenText();
        var sample_rate = attrMap.get("sample_rate").child(BotPsiElementTypes.NUMBER).tokenText();
        var hover_delay = attrMap.get("hover_delay").child(BotPsiElementTypes.NUMBER).tokenText();
        var model = attrMap.get("model").child(BotPsiElementTypes.NAME).tokenText();



        var wayPoints = attrMap.get("way_points").child(BotPsiElementTypes.SQUARE_BRACKED).childrenOfType(BotPsiElementTypes.PAREN).
            map(point -> {
              var nums = point.child(BotPsiElementTypes.WAYPOINT).childrenOfType(BotPsiElementTypes.NUMBER)
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

    var startTaskID = missionContent.child(BotPsiElementTypes.MISSION_START_DECL).child(BotPsiElementTypes.TASK_NAME).tokenText().toString();

    for (var transition : missionContent.childrenOfType(BotPsiElementTypes.MISSION_TRANSITION)) {
      var cond = transition.child(BotPsiElementTypes.PAREN).child(BotPsiElementTypes.COND);
      var condId = cond.child(BotPsiElementTypes.ID).tokenText().toString();
      var argNode = cond.peekChild(BotPsiElementTypes.PAREN);
      var arg = argNode != null ? argNode.child(BotPsiElementTypes.NUMBER).tokenText().toString() : null;

      var taskPair = transition.childrenOfType(BotPsiElementTypes.TASK_NAME)
          .map(GenericNode::tokenText)
          .map(StringSlice::toString)
          .toImmutableSeq();
      var curr_task = taskPair.get(0);
      var next_task = taskPair.get(1);

      var tran = new Task.Transition(condId, arg, curr_task, next_task);
      taskMap.get(curr_task).transitions.add(tran);
    }

    return startTaskID;
  }

  @NotNull
  private static AttributeMap createMap(GenericNode<? extends GenericNode<?>> task) {
    var isDetect = task.peekChild(BotPsiElementTypes.TASK_DETECT_KW);
    var attrMap = new AttributeMap();
    task.child(BotPsiElementTypes.TASK_BODY).childrenOfType(BotPsiElementTypes.ATTRIBUTE)
        .forEach(attr -> attrMap.content.put(attr.child(BotPsiElementTypes.ID).tokenText(), attr.child(BotPsiElementTypes.ATTRIBUTE_EXPR)));
    if (isDetect != null) {
      attrMap.kind = TaskKind.Detect;
    } else {
      attrMap.kind = TaskKind.Track;
    }
    return attrMap;
  }


}
