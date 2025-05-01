package org.droneDSL.compile.preprocess.partition;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CorridorPartition extends Partition {

  public CorridorPartition(double spacing, double angleDegrees) {
    super(spacing, angleDegrees);
  }

  @Override
  public List<Point2D> generateTransectsAndPoints(Polygon polygon) {
    Rectangle2D bounds = polygon.getBounds2D();
    double maxLength = Math.max(bounds.getWidth(), bounds.getHeight()) + 100;
    List<Point2D> points = new ArrayList<>();
    double angleRadians = Math.toRadians(angleDegrees);
    Point2D center = new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());

    double offset = -maxLength;
    while (offset <= maxLength) {
      double x = bounds.getMinX() + offset;
      Line2D line = new Line2D.Double(
          PartitionUtils.rotatePoint(x, bounds.getMinY() - maxLength, center, angleRadians),
          PartitionUtils.rotatePoint(x, bounds.getMaxY() + maxLength, center, angleRadians)
      );
      List<Point2D> intersections = PartitionUtils.getLinePolygonIntersections(line, polygon);
      if (intersections.size() >= 2) {
        intersections.sort(Comparator.comparingDouble(pt -> PartitionUtils.projectOntoLine(pt, line.getP1(), line.getP2())));
        points.add(PartitionUtils.roundPoint(intersections.get(0)));
        points.add(PartitionUtils.roundPoint(intersections.get(intersections.size() - 1)));
      }
      offset += spacing;
    }
    return points;
  }
}
