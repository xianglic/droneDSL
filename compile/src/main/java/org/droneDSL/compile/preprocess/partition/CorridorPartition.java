package org.droneDSL.compile.preprocess.partition;

import org.locationtech.jts.geom.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CorridorPartition extends Partition {

  public CorridorPartition(double spacing, double angleDegrees) {
    super(spacing, angleDegrees);
  }

  @Override
  public List<Coordinate> generateTransectsAndPoints(Polygon polygon) {
    Envelope bounds = polygon.getEnvelopeInternal();
    double maxLength = Math.max(bounds.getWidth(), bounds.getHeight()) + 100;
    List<Coordinate> points = new ArrayList<>();
    double angleRadians = Math.toRadians(angleDegrees);
    Coordinate center = bounds.centre();

    double offset = -maxLength;
    while (offset <= maxLength) {
      double x = bounds.getMinX() + offset;
      Coordinate p1 = PartitionUtils.rotatePoint(x, bounds.getMinY() - maxLength, center, angleRadians);
      Coordinate p2 = PartitionUtils.rotatePoint(x, bounds.getMaxY() + maxLength, center, angleRadians);
      LineSegment line = new LineSegment(PartitionUtils.roundPoint(p1), PartitionUtils.roundPoint(p2));

      List<Coordinate> intersections = PartitionUtils.getLinePolygonIntersections(line, polygon);
      if (intersections.size() >= 2) {
        intersections.sort(Comparator.comparingDouble(pt -> PartitionUtils.projectOntoLine(pt, p1, p2)));
        points.add(intersections.get(0));
        points.add(intersections.get(intersections.size() - 1));
      }

      offset += spacing;
    }
    return points;
  }
}
