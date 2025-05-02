package org.droneDSL.compile.preprocess.partition;

import org.locationtech.jts.geom.*;
import java.util.ArrayList;
import java.util.List;

public class PartitionUtils {

  public static List<LineSegment> generateTransects(Polygon polygon, double spacing, double angleDegrees) {
    Envelope bounds = polygon.getEnvelopeInternal();
    double maxLength = Math.max(bounds.getWidth(), bounds.getHeight()) + 100;
    double angleRadians = Math.toRadians(angleDegrees);
    Coordinate center = bounds.centre();

    List<LineSegment> transects = new ArrayList<>();
    double offset = -maxLength;

    while (offset <= maxLength) {
      double x = bounds.getMinX() + offset;

      Coordinate p1 = rotatePoint(x, bounds.getMinY() - maxLength, center, angleRadians);
      Coordinate p2 = rotatePoint(x, bounds.getMaxY() + maxLength, center, angleRadians);

      transects.add(new LineSegment(roundPoint(p1), roundPoint(p2)));
      offset += spacing;
    }

    return transects;
  }

  public static Coordinate rotatePoint(double x, double y, Coordinate center, double angleRad) {
    double dx = x - center.x;
    double dy = y - center.y;
    double cos = Math.cos(angleRad);
    double sin = Math.sin(angleRad);
    double rx = center.x + dx * cos - dy * sin;
    double ry = center.y + dx * sin + dy * cos;
    return new Coordinate(rx, ry);
  }

  public static Coordinate roundPoint(Coordinate c) {
    double scale = 1000.0; // for 3 decimal places
    double rx = Math.round(c.x * scale) / scale;
    double ry = Math.round(c.y * scale) / scale;
    return new Coordinate(rx, ry);
  }

  public static List<Coordinate> getLinePolygonIntersections(LineSegment line, Polygon polygon) {
    List<Coordinate> intersections = new ArrayList<>();
    Coordinate[] coords = polygon.getCoordinates();
    for (int i = 0; i < coords.length - 1; i++) {
      LineSegment edge = new LineSegment(coords[i], coords[i + 1]);
      Coordinate intPt = lineIntersection(line, edge);
      if (intPt != null) {
        intersections.add(intPt);
      }
    }
    return intersections;
  }

  public static Coordinate lineIntersection(LineSegment l1, LineSegment l2) {
    return l1.intersection(l2); // Will be null if no intersection
  }



  public static double projectOntoLine(Coordinate pt, Coordinate start, Coordinate end) {
    double dx = end.x - start.x;
    double dy = end.y - start.y;
    double lengthSquared = dx * dx + dy * dy;
    if (lengthSquared == 0) return 0;
    double t = ((pt.x - start.x) * dx + (pt.y - start.y) * dy) / lengthSquared;
    return t;
  }
}
