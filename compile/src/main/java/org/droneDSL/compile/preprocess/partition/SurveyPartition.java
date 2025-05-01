package org.droneDSL.compile.preprocess.partition;

import org.locationtech.jts.geom.*;
import java.util.ArrayList;
import java.util.List;

public class SurveyPartition extends Partition {
  private final double triggerDistance;

  public SurveyPartition(double spacing, double angleDegrees, double triggerDistance) {
    super(spacing, angleDegrees);
    this.triggerDistance = triggerDistance;
  }

  @Override
  public List<Coordinate> generateTransectsAndPoints(Polygon polygon) {
    Envelope bounds = polygon.getEnvelopeInternal();
    double maxLength = Math.max(bounds.getWidth(), bounds.getHeight()) + 100;
    List<Coordinate> allPoints = new ArrayList<>();
    double angleRadians = Math.toRadians(angleDegrees);
    Coordinate center = bounds.centre();

    double offset = bounds.getMinX() - maxLength;
    double endOffset = bounds.getMaxX() + maxLength;

    while (offset <= endOffset) {
      double x = offset;
      Coordinate p1 = PartitionUtils.rotatePoint(x, bounds.getMinY() - maxLength, center, angleRadians);
      Coordinate p2 = PartitionUtils.rotatePoint(x, bounds.getMaxY() + maxLength, center, angleRadians);
      LineSegment line = new LineSegment(PartitionUtils.roundPoint(p1), PartitionUtils.roundPoint(p2));

      List<Coordinate> intersections = PartitionUtils.getLinePolygonIntersections(line, polygon);
      if (intersections.size() >= 2) {
        Coordinate start = intersections.get(0);
        Coordinate end = intersections.get(1);

        double totalDistance = start.distance(end);
        double dx = end.x - start.x;
        double dy = end.y - start.y;
        double lineAngle = Math.atan2(dy, dx);

        int numPoints = (int) (totalDistance / triggerDistance);
        for (int i = 0; i <= numPoints; i++) {
          double dist = i * triggerDistance;
          if (dist > totalDistance) break;
          double px = start.x + dist * Math.cos(lineAngle);
          double py = start.y + dist * Math.sin(lineAngle);
          Coordinate pt = new Coordinate(px, py);
          if (polygon.contains(new GeometryFactory().createPoint(pt))) {
            allPoints.add(pt);
          }
        }
      }
      offset += spacing;
    }
    return allPoints;
  }
}
