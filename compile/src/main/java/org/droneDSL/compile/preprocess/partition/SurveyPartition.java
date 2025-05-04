package org.droneDSL.compile.preprocess.partition;

import org.droneDSL.compile.preprocess.waypoints.GeoPoints;
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
  public List<GeoPoints> generatePartitionedGeoPoints(Polygon polygon) {

    List<GeoPoints> result = new ArrayList<>();
    List<LineSegment> transects = PartitionUtils.generateTransects(polygon, spacing, angleDegrees);

    for (LineSegment line : transects) {
      List<Coordinate> intersections = PartitionUtils.getLinePolygonIntersections(line, polygon);
      if (intersections.size() >= 2) {
        GeoPoints output = new GeoPoints();
        Coordinate start = intersections.get(0);
        Coordinate end = intersections.get(1);
        double totalDistance = start.distance(end);
        double dx = end.x - start.x;
        double dy = end.y - start.y;
        double angle = Math.atan2(dy, dx);

        int numPoints = (int) (totalDistance / triggerDistance);
        for (int i = 0; i <= numPoints; i++) {
          double dist = i * triggerDistance;
          double px = start.x + dist * Math.cos(angle);
          double py = start.y + dist * Math.sin(angle);
          Coordinate pt = new Coordinate(px, py);

          if (polygon.covers(new GeometryFactory().createPoint(pt))) {
            output.add(PartitionUtils.roundPoint(pt));
          }
        }
        result.add(output);
      }
    }

    return result;
  }
}
