package org.droneDSL.compile.preprocess.partition;

public class EdgePartition {

//  private void parseWaypointsForDrones(List<String> droneList, String areaName, String currAltitude, boolean firstTime, List<String> areaWaypoints, Map<String, Map<String, List<Compiler.Pt>>> droneWaypointsDict){
//    int startIdx = 0;
//    int endIdx = 0;
//    int edgesPerDrone = (areaWaypoints.size() -1) / droneList.size();
//    // sub waypoints for each drone
//    for (int droneIdx = 0; droneIdx < droneList.size(); droneIdx++) {
//      // increment 3 meters for each drone's flight for ATC
//      currAltitude = String.valueOf(Double.parseDouble(currAltitude) + 3);
//      startIdx = (droneIdx == 0) ? 0: endIdx -1;
//      endIdx = (droneIdx == droneList.size() - 1) ? areaWaypoints.size() : startIdx + edgesPerDrone + 1;
//
//      List<String> subWaypoints = areaWaypoints.subList(startIdx, endIdx);
//      // get the waypointMap
//      String droneID = droneList.get(droneIdx);
//      Map<String, List<Compiler.Pt>> waypointsMap;
//
//      if (firstTime) { // first time and create the map
//        waypointsMap = new HashMap<>();
//        droneWaypointsDict.put(droneID, waypointsMap);
//      } else {
//        waypointsMap = droneWaypointsDict.get(droneID);
//      }
//
//      // update the waypointMap
//      updateWaypointsMap(areaName, subWaypoints, waypointsMap, currAltitude);
//    }
//  }
}
