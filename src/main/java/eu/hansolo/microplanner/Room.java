/*
 * Copyright (c) 2018 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.microplanner;

import eu.hansolo.microplanner.geofence.GeoFence;
import eu.hansolo.microplanner.location.Location;

import java.util.ArrayList;
import java.util.List;


public class Room {
    private String         name;
    private String         building;
    private int            floor;
    private Location       location;
    private List<Beacon>   beacons;
    private List<GeoFence> geoFences;


    // ******************** Constructors **************************************
    public Room(final String name, final String building, final int floor) {
        this.name      = name;
        this.building  = building;
        this.floor     = floor;
        this.location  = null;
        this.beacons   = new ArrayList<>();
        this.geoFences = new ArrayList<>();
    }



    // ******************** Methods *******************************************
    public String getName() { return name; }
    public void setName(final String name) { this.name = name; }

    public String getBuilding() { return building; }
    public void setBuilding(final String building) { this.building = building; }

    public int getFloor() { return floor; }
    public void setFloor(final int floor) { this.floor = floor; }

    public Location getLocation() { return location; }
    public void setLocation(final Location location) { this.location = location; }

    public List<Beacon> getBeacons() { return beacons; }
    public void setBeacons(final List<Beacon> beacons) {
        this.beacons.clear();
        this.beacons.addAll(beacons);
    }

    public List<GeoFence> getGeoFences() { return geoFences; }
    public void setGeoFences(final List<GeoFence> geoFences) {
        this.geoFences.clear();
        this.geoFences.addAll(geoFences);
    }

    @Override public String toString() {
        return new StringBuilder().append("{")
                                  .append("\"name\":\"").append(name).append("\",")
                                  .append("\"building\":\"").append(building).append("\",")
                                  .append("\"floor\":").append(floor).append(",")
                                  .append("\"location\":").append(location.toString())
                                  .append("}").toString();
    }
}
