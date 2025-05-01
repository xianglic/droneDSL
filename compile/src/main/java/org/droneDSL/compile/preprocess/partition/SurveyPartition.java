package org.droneDSL.compile.preprocess.partition;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

public class SurveyPartition extends Partition {
  private final double triggerDistance;

  public SurveyPartition(double spacing, double angleDegrees, double triggerDistance) {
    super(spacing, angleDegrees);
    this.triggerDistance = triggerDistance;
  }

  @Override
  public List<Point2D> generateTransectsAndPoints(Polygon polygon) {
    Rectangle2D bounds = polygon.getBounds2D();
    double maxLength = Math.max(bounds.getWidth(), bounds.getHeight()) + 100;
    List<Point2D> allPoints = new ArrayList<>();
    double angleRadians = Math.toRadians(angleDegrees);
    Point2D center = new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());

    double offset = bounds.getMinX() - maxLength;
    double endOffset = bounds.getMaxX() + maxLength;

    while (offset <= endOffset) {
      double x = offset;
      Line2D line = new Line2D.Double(
          PartitionUtils.rotatePoint(x, bounds.getMinY() - maxLength, center, angleRadians),
          PartitionUtils.rotatePoint(x, bounds.getMaxY() + maxLength, center, angleRadians)
      );
      List<Point2D> intersections = PartitionUtils.getLinePolygonIntersections(line, polygon);
      if (intersections.size() >= 2) {
        Point2D start = intersections.get(0);
        Point2D end = intersections.get(1);

        double totalDistance = start.distance(end);
        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();
        double lineAngle = Math.atan2(dy, dx);

        int numPoints = (int) (totalDistance / triggerDistance);
        for (int i = 0; i <= numPoints; i++) {
          double dist = i * triggerDistance;
          if (dist > totalDistance) break;
          double px = start.getX() + dist * Math.cos(lineAngle);
          double py = start.getY() + dist * Math.sin(lineAngle);
          Point2D pt = new Point2D.Double(px, py);
          if (polygon.contains(pt)) {
            allPoints.add(PartitionUtils.roundPoint(pt));
          }
        }
      }
      offset += spacing;
    }
    return allPoints;
  }
}
