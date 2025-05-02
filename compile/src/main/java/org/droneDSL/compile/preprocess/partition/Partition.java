package org.droneDSL.compile.preprocess.partition;

import org.droneDSL.compile.preprocess.waypoints.GeoPoints;
import org.locationtech.jts.geom.*;
import java.util.List;

public abstract class Partition {
  protected double spacing;
  protected double angleDegrees;

  public Partition(double spacing, double angleDegrees) {
    this.spacing = spacing;
    this.angleDegrees = angleDegrees;
  }

  public abstract GeoPoints generateTransectsAndPoints(Polygon polygon);
}
