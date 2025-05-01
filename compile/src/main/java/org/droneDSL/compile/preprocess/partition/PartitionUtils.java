package org.droneDSL.compile.preprocess.partition;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

public class PartitionUtils {

  public static Point2D rotatePoint(double x, double y, Point2D center, double angleRad) {
    double dx = x - center.getX();
    double dy = y - center.getY();
    double cos = Math.cos(angleRad);
    double sin = Math.sin(angleRad);
    double rx = center.getX() + dx * cos - dy * sin;
    double ry = center.getY() + dx * sin + dy * cos;
    return new Point2D.Double(rx, ry);
  }

  public static Point2D roundPoint(Point2D pt) {
    double x = Math.round(pt.getX() * 1000.0) / 1000.0;
    double y = Math.round(pt.getY() * 1000.0) / 1000.0;
    return new Point2D.Double(x, y);
  }

  public static Point2D getLineIntersection(Line2D l1, Line2D l2) {
    double x1 = l1.getX1(), y1 = l1.getY1();
    double x2 = l1.getX2(), y2 = l1.getY2();
    double x3 = l2.getX1(), y3 = l2.getY1();
    double x4 = l2.getX2(), y4 = l2.getY2();

    double denominator = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
    if (denominator == 0) return null;

    double px = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)) / denominator;
    double py = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4)) / denominator;

    return new Point2D.Double(px, py);
  }

  public static List<Point2D> getLinePolygonIntersections(Line2D line, Polygon polygon) {
    List<Point2D> points = new ArrayList<>();
    for (int i = 0; i < polygon.npoints; i++) {
      int next = (i + 1) % polygon.npoints;
      Line2D edge = new Line2D.Double(
          polygon.xpoints[i], polygon.ypoints[i],
          polygon.xpoints[next], polygon.ypoints[next]
      );
      if (line.intersectsLine(edge)) {
        Point2D intersection = getLineIntersection(line, edge);
        if (intersection != null) {
          points.add(intersection);
        }
      }
    }
    return points;
  }

  public static double projectOntoLine(Point2D pt, Point2D lineStart, Point2D lineEnd) {
    double dx = lineEnd.getX() - lineStart.getX();
    double dy = lineEnd.getY() - lineStart.getY();
    double lengthSquared = dx * dx + dy * dy;
    if (lengthSquared == 0) return 0;
    double t = ((pt.getX() - lineStart.getX()) * dx + (pt.getY() - lineStart.getY()) * dy) / lengthSquared;
    return t;
  }
}
