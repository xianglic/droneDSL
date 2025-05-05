package org.droneDSL.compile.preprocess.partition;

import org.droneDSL.compile.preprocess.waypoints.GeoPoints;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.List;

public class EdgePartition extends Partition {

  public EdgePartition() {
    super(); // spacing and angle are not used here
  }

  public List<GeoPoints> generatePartitionedGeoPoints(Polygon polygon) {
    List<GeoPoints> edgeSegments = new ArrayList<>();

    Coordinate[] coords = polygon.getExteriorRing().getCoordinates();

    // Each edge is defined by two consecutive coordinates
    for (int i = 0; i < coords.length - 1; i++) {
      Coordinate p1 = coords[i];
      Coordinate p2 = coords[i + 1];

      GeoPoints edge = new GeoPoints();
      edge.add(p1);
      edge.add(p2);

      edgeSegments.add(edge);
    }

    return edgeSegments;
  }
}