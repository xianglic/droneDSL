package org.droneDSL.compile.preprocess.waypoints;

import org.locationtech.jts.geom.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeoPoints extends ArrayList<Coordinate> {

  public GeoPoints() {
    super();
  }

  public GeoPoints(List<Coordinate> coordinates) {
    super(coordinates);
  }

  public Coordinate getCentroid() {
    return this.toPolygon().getCentroid().getCoordinate();
  }

  public Polygon toPolygon() {
    GeometryFactory gf = new GeometryFactory();
    int size = this.size();
    if (size < 3) throw new IllegalArgumentException("A polygon must have at least 3 points");

    Coordinate[] coords = new Coordinate[size + 1];
    for (int i = 0; i < size; i++) {
      coords[i] = this.get(i);
    }

    if (!coords[0].equals2D(coords[size - 1])) {
      coords[size] = coords[0];
    } else {
      coords = Arrays.copyOf(coords, size);
    }
    return gf.createPolygon(coords);
  }

  public Polygon toProjectedPolygon() {
    GeometryFactory gf = new GeometryFactory();
    if (this.size() < 3) throw new IllegalArgumentException("Polygon must have at least 3 points");
    GeoPoints projected = this.convertToProjected();

    if (!projected.get(0).equals2D(projected.get(projected.size() - 1))) {
      projected.add(projected.get(0));
    }
    return gf.createPolygon(projected.toArray(new Coordinate[0]));
  }


  public GeoPoints convertToProjected() {
    Coordinate origin = this.getCentroid();
    GeoPoints projected = new GeoPoints();
    for (Coordinate c : this) {
      projected.add(projectToMeters(origin, c));
    }
    return projected;
  }

  public GeoPoints inverseProjectFrom(Coordinate origin) {
    GeoPoints unprojected = new GeoPoints();
    for (Coordinate c : this) {
      unprojected.add(inverseProjectToWGS(origin, c));
    }
    return unprojected;
  }

  private static Coordinate projectToMeters(Coordinate origin, Coordinate wgsCoord) {
    double latRad = Math.toRadians(origin.y);
    double x = (wgsCoord.x - origin.x) * 111320 * Math.cos(latRad);
    double y = (wgsCoord.y - origin.y) * 110540;
    return new Coordinate(x, y);
  }

  private static Coordinate inverseProjectToWGS(Coordinate origin, Coordinate projected) {
    double latRad = Math.toRadians(origin.y);
    double lon = projected.x / (111320 * Math.cos(latRad)) + origin.x;
    double lat = projected.y / 110540 + origin.y;
    return new Coordinate(lon, lat);
  }
}
