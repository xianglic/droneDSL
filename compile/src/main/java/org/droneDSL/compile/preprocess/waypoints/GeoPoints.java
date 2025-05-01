package org.droneDSL.compile.preprocess.waypoints;

import org.jetbrains.annotations.NotNull;
import org.locationtech.jts.geom.*;

import java.util.ArrayList;
import java.util.List;

public class GeoPoints extends ArrayList<GeoPoints.GeoPoint> {

  public record GeoPoint(@NotNull String longitude, @NotNull String latitude) {
  }

  public GeoPoints() {
    super();
  }

  public GeoPoints(List<GeoPoint> geoPoints) {
    super(geoPoints);
  }

  public Polygon toPolygon() {
    GeometryFactory gf = new GeometryFactory();
    int size = this.size();
    if (size < 3) throw new IllegalArgumentException("A polygon must have at least 3 points");

    Coordinate[] coords = new Coordinate[size + 1]; // +1 to close the polygon

    for (int i = 0; i < size; i++) {
      GeoPoint pt = this.get(i);
      double lon = Double.parseDouble(pt.longitude());
      double lat = Double.parseDouble(pt.latitude());
      coords[i] = new Coordinate(lon, lat);
    }

    // Close the polygon
    coords[size] = coords[0];

    return gf.createPolygon(coords);
  }
}
