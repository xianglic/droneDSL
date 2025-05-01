package org.droneDSL.compile.preprocess.waypoints;

import org.droneDSL.compile.Compiler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.*;

public class WaypointsUtils {

  public static Map<String, GeoPoints> parseKMLFile(String KMLFilePath) {
    Map<String, GeoPoints> geoWayPointsMap = new HashMap<>();
    try {
      // get scanner
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Scanner scanner = new Scanner(System.in);

      // Replace this with your KML file path or an InputStream
      Document document = builder.parse(KMLFilePath);
      NodeList placemarks = document.getElementsByTagName("Placemark");

      // build the map
      for (int i = 0; i < placemarks.getLength(); i++) {
        // parse placemark section in KML
        Node placemark = placemarks.item(i);
        if (placemark.getNodeType() == Node.ELEMENT_NODE) {
          // get area name
          Element placemarkElement = (Element) placemark;
          String areaName = placemarkElement.getElementsByTagName("name").item(0).getTextContent();
          // get waypoints
          List<String> KMLWaypoints =
              List.of(placemarkElement.
                  getElementsByTagName("coordinates").item(0).getTextContent().trim().split("\\s+"));
          GeoPoints geoWayPoints = convertKMLToGeoPoints(KMLWaypoints);
          geoWayPointsMap.put(areaName, geoWayPoints);
        }
      }
      scanner.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return geoWayPointsMap;
  }
  public static GeoPoints convertKMLToGeoPoints(List<String> areaWaypoints) {
    GeoPoints coords = new GeoPoints();
    for (String waypoint : areaWaypoints) {
      String[] coordinate_ele = waypoint.split(",");
      if (coordinate_ele.length >= 2) { // Making sure there are at least longitude and latitude
        String longitude = coordinate_ele[0];
        String latitude = coordinate_ele[1];
        var pt = new GeoPoints.GeoPoint(longitude, latitude);
        coords.add(pt);
      }
    }
    return coords;
  }
}
