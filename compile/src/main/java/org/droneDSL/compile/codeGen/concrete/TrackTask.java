package org.droneDSL.compile.codeGen.concrete;

public class TrackTask extends Task {
  public float gimbalPitch;
  public String target_class;
  public String model;

  public HSV lowerBound;
  public HSV upperBound;

  public float altitude;
  public float descent_speed;
  public float orbit_speed;
  public float follow_speed;
  public float yaw_speed;
  public float gimbal_offset;

  public TrackTask(
      String taskID,
      float gimbalPitch,
      String target_class,
      String model,
      HSV lower_bound,
      HSV upper_bound,
      float altitude,
      float descent_speed,
      float orbit_speed,
      float follow_speed,
      float yaw_speed,
      float gimbal_offset
  ) {
    super(taskID);
    this.gimbalPitch = gimbalPitch;
    this.model = model;
    this.target_class = target_class;
    this.lowerBound = lower_bound;
    this.upperBound = upper_bound;
    this.altitude = altitude;
    this.descent_speed = descent_speed;
    this.orbit_speed = orbit_speed;
    this.follow_speed = follow_speed;
    this.yaw_speed = yaw_speed;
    this.gimbal_offset = gimbal_offset;
  }

  @Override
  public void debugPrint() {
    System.out.println("gimbal_pitch :" + gimbalPitch);
    System.out.println("model :" + model);
    System.out.println("target_class :" + target_class);
    System.out.println("hsv (lower_bound/upper_bound) :" + lowerBound + " / " + upperBound);
    System.out.println("altitude :" + altitude);
    System.out.println("descent_speed :" + descent_speed);
    System.out.println("orbit_speed :" + orbit_speed);
    System.out.println("follow_speed :" + follow_speed);
    System.out.println("yaw_speed :" + yaw_speed);
    System.out.println("gimbal_offset :" + gimbal_offset);
  }

  @Override
  public String generateDefineTaskCode() {
    return """
                #TASK%s
                task_attr_%s = {}
                task_attr_%s["model"] = "%s"
                task_attr_%s["class"] = "%s"
                task_attr_%s["gimbal_pitch"] = "%s"
                task_attr_%s["upper_bound"] = %s
                task_attr_%s["lower_bound"] = %s
                task_attr_%s["altitude"] = %s
                task_attr_%s["descent_speed"] = %s
                task_attr_%s["orbit_speed"] = %s
                task_attr_%s["follow_speed"] = %s
                task_attr_%s["yaw_speed"] = %s
                task_attr_%s["gimbal_offset"] = %s
        """.formatted(
        taskID, taskID,
        taskID, model,
        taskID, target_class,
        taskID, gimbalPitch,
        taskID, upperBound.toString(),
        taskID, lowerBound.toString(),
        taskID, altitude,
        taskID, descent_speed,
        taskID, orbit_speed,
        taskID, follow_speed,
        taskID, yaw_speed,
        taskID, gimbal_offset
    ) + this.generateTaskTransCode() + """
                task_arg_map["%s"] = TaskArguments(TaskType.Track, transition_attr_%s, task_attr_%s)
        """.formatted(taskID, taskID, taskID);
  }
}
