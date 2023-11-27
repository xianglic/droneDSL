package org.steeleagle.preprocess;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class WaypointsParser {
  public static void main(String[] args) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();

      // Replace this with your KML file path or an InputStream
      Document document = builder.parse(args[0]);

      NodeList coordinateList = document.getElementsByTagName("coordinates");

      for (int i = 0; i < coordinateList.getLength(); i++) {
        if (i == 0) {
          System.out.println("takeoff");
        } else {
          System.out.println(String.format("task %s", i));
        }

        StringBuilder str = new StringBuilder();
        str.append("[");

        Element element = (Element) coordinateList.item(i);
        String coordinates = element.getTextContent().trim();
        String[] parts = coordinates.split("\\s+"); // Split by whitespace (spaces, newlines, etc.)

        for (String part : parts) {
          String[] coords = part.split(",");
          double longitude = Double.parseDouble(coords[0]);
          double latitude = Double.parseDouble(coords[1]);
//          double altitude = coords.length > 2 ? Double.parseDouble(coords[2]) : 0;
          double altitude = 25;

          str.append(String.format("(%s, %s, %s),", longitude, latitude, altitude));
        }

        str.deleteCharAt(str.length() - 1);
        str.append("]");
        System.out.println(str);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
