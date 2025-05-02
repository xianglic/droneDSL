package org.droneDSL.compile.preprocess.waypoints;

import org.locationtech.jts.geom.*;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.*;

public class WaypointsUtils {
  public static Map<String, GeoPoints> parseKMLFile(String kmlPath) {
    Map<String, GeoPoints> geoMap = new HashMap<>();
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(kmlPath);
      NodeList placemarks = document.getElementsByTagName("Placemark");

      for (int i = 0; i < placemarks.getLength(); i++) {
        Node placemark = placemarks.item(i);
        if (placemark.getNodeType() == Node.ELEMENT_NODE) {
          Element elem = (Element) placemark;
          String areaName = elem.getElementsByTagName("name").item(0).getTextContent();
          String coordText = elem.getElementsByTagName("coordinates").item(0).getTextContent().trim();
          GeoPoints points = parseKMLCoordinatesToGeoPoints(coordText);
          geoMap.put(areaName, points);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return geoMap;
  }

  public static GeoPoints parseKMLCoordinatesToGeoPoints(String kmlCoordinates) {
    GeoPoints geoPoints = new GeoPoints();
    String[] lines = kmlCoordinates.trim().split("\\s+");
    for (String line : lines) {
      String[] parts = line.split(",");
      if (parts.length >= 2) {
        double lon = Double.parseDouble(parts[0]);
        double lat = Double.parseDouble(parts[1]);
        geoPoints.add(new Coordinate(lon, lat));
      }
    }

    return geoPoints;
  }
}
