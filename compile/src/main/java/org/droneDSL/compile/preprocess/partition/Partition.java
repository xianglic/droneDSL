package org.droneDSL.compile.preprocess.partition;

import org.droneDSL.compile.preprocess.waypoints.GeoPoints;
import org.locationtech.jts.geom.*;
import java.util.List;

public abstract class Partition {

  public Partition() {
  }

  public abstract List<GeoPoints> generatePartitionedGeoPoints(Polygon polygon);
}
