package org.droneDSL.preprocess;

import com.google.gson.Gson;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.*;


public class Main {
  public record Pt(
      String longitude,
      String latitude,
      String altitude
  ) implements Serializable {
  }

  public static void main(String[] args) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();

      // Replace this with your KML file path or an InputStream
      Document document = builder.parse(args[0]);
      NodeList placemarks = document.getElementsByTagName("Placemark");
      Map<String, List<Pt>> lineCoordinates = new HashMap<>();

      int alt = 35;
      for (int i = 0; i < placemarks.getLength(); i++) {

        Node placemark = placemarks.item(i);
        if (placemark.getNodeType() == Node.ELEMENT_NODE) {
          Element placemarkElement = (Element) placemark;

          String name = placemarkElement.getElementsByTagName("name").item(0).getTextContent();
          NodeList coordinateList = placemarkElement.getElementsByTagName("coordinates");

          String[] coordinates = coordinateList.item(0).getTextContent().trim().split("\\s+");
          List<Pt> coords = new ArrayList<>();
          for (String coordinate : coordinates) {
            String[] coordinate_ele = coordinate.split(",");
            if (coordinate_ele.length >= 2) { // Making sure there are at least longitude and latitude
              String longitude = coordinate_ele[0];
              String latitude = coordinate_ele[1];
              String altitude = coordinate_ele.length > 2 ? coordinate_ele[2] : "Not provided";
              var pt = new Pt(longitude, latitude, altitude);

              coords.add(pt);

            }
            lineCoordinates.put(name, coords);
          }

          alt -= 10;
        }
      }

      // Example of retrieving data
      for (var entry : lineCoordinates.entrySet()) {
        System.out.println("Name: " + entry.getKey() + " - Coordinates: " + entry.getValue());
      }

      // Convert map to JSON
      Gson gson = new Gson();
      String json = gson.toJson(lineCoordinates);

      // Write JSON to file
      try (FileWriter writer = new FileWriter("../shared_dir/coordinates.json")) {
        writer.write(json);
      }


//      NodeList coordinateList = document.getElementsByTagName("coordinates");
//
//      for (int i = 0; i < coordinateList.getLength(); i++) {
//        if (i == 0) {
//          System.out.println("takeoff");
//        } else {
//          System.out.println(String.format("task %s", i));
//        }
//
//        StringBuilder str = new StringBuilder();
//        str.append("[");
//
//        Element element = (Element) coordinateList.item(i);
//        String coordinates = element.getTextContent().trim();
//        String[] parts = coordinates.split("\\s+"); // Split by whitespace (spaces, newlines, etc.)
//
//        for (String part : parts) {
//          String[] coords = part.split(",");
//          double longitude = Double.parseDouble(coords[0]);
//          double latitude = Double.parseDouble(coords[1]);
////          double altitude = coords.length > 2 ? Double.parseDouble(coords[2]) : 0;
//          double altitude = 25;
//
//          str.append(String.format("(%s, %s, %s),", longitude, latitude, altitude));
//        }
//
//        str.deleteCharAt(str.length() - 1);
//        str.append("]");
//        System.out.println(str);
//      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
