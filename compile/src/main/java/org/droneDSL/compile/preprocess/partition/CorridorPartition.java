package org.droneDSL.compile.preprocess.partition;

import org.droneDSL.compile.preprocess.waypoints.GeoPoints;
import org.locationtech.jts.geom.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CorridorPartition extends Partition {

  public CorridorPartition(double spacing, double angleDegrees) {
    super(spacing, angleDegrees);
  }

  @Override
  public List<GeoPoints> generatePartitionedGeoPoints(Polygon polygon) {
    List<GeoPoints> result = new ArrayList<>();

    List<LineSegment> transects = PartitionUtils.generateTransects(polygon, spacing, angleDegrees);

    for (LineSegment line : transects) {
      List<Coordinate> intersections = PartitionUtils.getLinePolygonIntersections(line, polygon);
      if (intersections.size() >= 2) {
        GeoPoints output = new GeoPoints();
        intersections.sort(Comparator.comparingDouble(pt ->
            PartitionUtils.projectOntoLine(pt, line.p0, line.p1)));
        output.add(intersections.get(0));
        output.add(intersections.get(intersections.size() - 1));
        result.add(output);
      }
    }
    return result;
  }
}