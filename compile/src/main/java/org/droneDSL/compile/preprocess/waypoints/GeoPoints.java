package org.droneDSL.compile.preprocess.waypoints;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GeoPoints extends ArrayList<GeoPoints.GeoPoint> {

  public record GeoPoint(@NotNull String longitude, @NotNull String latitude) {
  }

  public GeoPoints() {
    super();
  }

  public GeoPoints(List<GeoPoint> geoPoints) {
    super(geoPoints);
  }
}
