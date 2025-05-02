package org.droneDSL.compile.preprocess.partition;

import org.droneDSL.compile.preprocess.waypoints.GeoPoints;
import org.locationtech.jts.geom.*;

import java.util.Comparator;
import java.util.List;

public class CorridorPartition extends Partition {

  public CorridorPartition(double spacing, double angleDegrees) {
    super(spacing, angleDegrees);
  }

  @Override
  public GeoPoints generateTransectsAndPoints(Polygon polygon) {
    GeoPoints output = new GeoPoints();
    List<LineSegment> transects = PartitionUtils.generateTransects(polygon, spacing, angleDegrees);

    for (LineSegment line : transects) {
      List<Coordinate> intersections = PartitionUtils.getLinePolygonIntersections(line, polygon);
      if (intersections.size() >= 2) {
        intersections.sort(Comparator.comparingDouble(pt ->
            PartitionUtils.projectOntoLine(pt, line.p0, line.p1)));
        output.add(intersections.get(0));
        output.add(intersections.get(intersections.size() - 1));
      }
    }

    return output;
  }
}
