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

package eu.hansolo.microplanner.location;

import eu.hansolo.microplanner.location.LocationEvent.LocationEventType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;


public class Location {
    public enum CardinalDirection {
        N("North", 348.75, 11.25),
        NNE("North North-East", 11.25, 33.75),
        NE("North-East", 33.75, 56.25),
        ENE("East North-East", 56.25, 78.75),
        E("East", 78.75, 101.25),
        ESE("East South-East", 101.25, 123.75),
        SE("South-East", 123.75, 146.25),
        SSE("South South-East", 146.25, 168.75),
        S("South", 168.75, 191.25),
        SSW("South South-West", 191.25, 213.75),
        SW("South-West", 213.75, 236.25),
        WSW("West South-West", 236.25, 258.75),
        W("West", 258.75, 281.25),
        WNW("West North-West", 281.25, 303.75),
        NW("North-West", 303.75, 326.25),
        NNW("North North-West", 326.25, 348.75);

        public String direction;
        public double from;
        public double to;

        private CardinalDirection(final String DIRECTION, final double FROM, final double TO) {
            direction = DIRECTION;
            from      = FROM;
            to        = TO;
        }
    }

    private String                  name;

    // Location related information
    private Instant                 timestamp;
    private double                  latitude;
    private double                  longitude;
    private double                  altitude;
    private double                  accuracy;

    // Additional information
    private String                  info;
    private String                  fence;
    private String                  color;

    private String                  city;
    private String                  postcode;
    private String                  street;
    private String                  houseNumber;

    private List<LocationObserver>  observers;
    private Consumer<LocationEvent> locationChangedConsumer;


    // ******************** Constructors **************************************
    public Location() {
        this(0, 0, 0, Instant.now(), "Loc #" + new Random().nextInt(), "");
    }
    public Location(final double latitude, final double longitude, final String name) {
        this(latitude, longitude, 0, Instant.now() ,name, "");
    }
    public Location(final double latitude, final double longitude, final double altitude, final String name) {
        this(latitude, longitude, altitude, Instant.now(), name, "");
    }
    public Location(final double latitude, final double longitude, final double altitude, final Instant timestamp, final String name) {
        this(latitude, longitude, altitude, timestamp, name, "");
    }
    public Location(final double latitude, final double longitude, final double altitude, final Instant timestamp, final String name, final String info) {
        this.name      = name;
        this.latitude  = latitude;
        this.longitude = longitude;
        this.altitude  = altitude;
        this.timestamp = timestamp;
        accuracy       = 20;
        this.info      = info;
        fence          = "";
        color          = "#000000";
        city           = "";
        postcode       = "";
        street         = "";
        houseNumber    = "";
        observers      = new CopyOnWriteArrayList<>();
    }


    // ******************** Methods *******************************************
    public String getName() { return name; }
    public void setName(final String name) { this.name = name; }

    public Instant getTimestamp() { return timestamp; }
    public long getTimestampInSeconds() { return timestamp.getEpochSecond(); }
    public void setTimestamp(final Instant timestamp) { this.timestamp = timestamp; }

    public double getLatitude() { return latitude; }
    public void setLatitude(final double latitude) {
        this.latitude = latitude;
        fireLocationEvent(new LocationEvent(Location.this, LocationEventType.LOCATION_CHANGED));
    }

    public double getLongitude() { return longitude; }
    public void setLongitude(final double longitude) {
        this.longitude = longitude;
        fireLocationEvent(new LocationEvent(Location.this, LocationEventType.LOCATION_CHANGED));
    }

    public double getAltitude() { return altitude; }
    public void setAltitude(final double ALTITUDE) {
        altitude = ALTITUDE;
        fireLocationEvent(new LocationEvent(Location.this, LocationEventType.LOCATION_CHANGED));
    }

    public double getAccuracy() { return accuracy; }
    public void setAccuracy(final double accuracy) { this.accuracy = accuracy; }

    public String getInfo() { return info; }
    public void setInfo(final String info) { this.info = info; }

    public String getFence() { return fence; }
    public void setFence(final String fence) { this.fence = fence; }

    public String getColor() { return color; }
    public void setColor(final String COLOR) { color = COLOR; }

    public String getCity() { return city; }
    public void setCity(final String city) { this.city = city; }

    public String getPostcode() { return postcode; }
    public void setPostcode(final String postcode) { this.postcode = postcode; }

    public String getStreet() { return street; }
    public void setStreet(final String STREET) { street = STREET; }

    public String getHouseNumber() { return houseNumber; }
    public void setHouseNumber(final String houseNumber) { this.houseNumber = houseNumber; }

    public LocalDateTime getLocaleDateTime() { return getLocalDateTime(ZoneId.systemDefault()); }
    public LocalDateTime getLocalDateTime(final ZoneId zoneId) { return LocalDateTime.ofInstant(timestamp, zoneId); }

    public void update(final double latitude, final double longitude) { set(latitude, longitude); }

    public void set(final double latitude, final double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        timestamp = Instant.now();
        fireLocationEvent(new LocationEvent(Location.this, LocationEventType.LOCATION_CHANGED));
    }
    public void set(final double latitude, final double longitude, final double altitude, final Instant timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.timestamp = timestamp;
        fireLocationEvent(new LocationEvent(Location.this, LocationEventType.LOCATION_CHANGED));
    }
    public void set(final double latitude, final double longitude, final double altitude, final Instant timestamp, final double accuracy, final String info) {
        this.latitude  = latitude;
        this.longitude = longitude;
        this.altitude  = altitude;
        this.timestamp = timestamp;
        this.accuracy  = accuracy;
        this.info      = info;
        fireLocationEvent(new LocationEvent(Location.this, LocationEventType.LOCATION_CHANGED));
    }
    public void set(final Location location) {
        name      = location.getName();
        latitude  = location.getLatitude();
        longitude = location.getLongitude();
        altitude  = location.getAltitude();
        timestamp = location.getTimestamp();
        accuracy  = location.getAccuracy();
        info      = location.info;
        city      = location.getCity();
        postcode  = location.getPostcode();
        street    = location.getStreet();
        houseNumber = location.getHouseNumber();
        fireLocationEvent(new LocationEvent(Location.this, LocationEventType.LOCATION_CHANGED));
    }

    public double getDistanceTo(final Location location) { return calcDistanceInMeter(this, location); }

    public boolean isWithinRangeOf(final Location location, final double meters) { return getDistanceTo(location) < meters; }

    public double calcDistanceInMeter(final Location p1, final Location p2) {
        return calcDistanceInMeter(p1.getLatitude(), p1.getLongitude(), p2.getLatitude(), p2.getLongitude());
    }
    public double calcDistanceInKilometer(final Location p1, final Location p2) {
        return calcDistanceInMeter(p1, p2) / 1000.0;
    }
    public double calcDistanceInMeter(final double lat1, final double lon1, final double lat2, final double lon2) {
        final double EARTH_RADIUS      = 6_371_000; // m
        final double LAT_1_RADIANS     = Math.toRadians(lat1);
        final double LAT_2_RADIANS     = Math.toRadians(lat2);
        final double DELTA_LAT_RADIANS = Math.toRadians(lat2-lat1);
        final double DELTA_LON_RADIANS = Math.toRadians(lon2-lon1);

        final double A = Math.sin(DELTA_LAT_RADIANS * 0.5) * Math.sin(DELTA_LAT_RADIANS * 0.5) + Math.cos(LAT_1_RADIANS) * Math.cos(LAT_2_RADIANS) * Math.sin(DELTA_LON_RADIANS * 0.5) * Math.sin(DELTA_LON_RADIANS * 0.5);
        final double C = 2 * Math.atan2(Math.sqrt(A), Math.sqrt(1-A));

        final double DISTANCE = EARTH_RADIUS * C;

        return DISTANCE;
    }

    public double getAltitudeDifferenceInMeter(final Location location) { return (altitude - location.getAltitude()); }

    public double getBearingTo(final Location location) {
        return calcBearingInDegree(getLatitude(), getLongitude(), location.getLatitude(), location.getLongitude());
    }
    public double getBearingTo(final double latitude, final double longitude) {
        return calcBearingInDegree(getLatitude(), getLongitude(), latitude, longitude);
    }

    public boolean isZero() { return Double.compare(latitude, 0d) == 0 && Double.compare(longitude, 0d) == 0; }

    public double calcBearingInDegree(final double lat1, final double lon1, final double lat2, final double lon2) {
        double latitude1  = Math.toRadians(lat1);
        double longitude1 = Math.toRadians(lon1);
        double latitude2  = Math.toRadians(lat2);
        double longitude2 = Math.toRadians(lon2);
        double deltaLon   = longitude2 - longitude1;
        double deltaPhi   = Math.log(Math.tan(latitude2 * 0.5 + Math.PI * 0.25) / Math.tan(latitude1 * 0.5 + Math.PI * 0.25));
        if (Math.abs(deltaLon) > Math.PI) {
            if (deltaLon > 0) {
                deltaLon = -(2.0 * Math.PI - deltaLon);
            } else {
                deltaLon = (2.0 * Math.PI + deltaLon);
            }
        }
        double bearing = (Math.toDegrees(Math.atan2(deltaLon, deltaPhi)) + 360.0) % 360.0;
        return bearing;
    }

    public String getCardinalDirectionFromBearing(final double bearing) {
        double correctedBearing = bearing % 360.0;
        for (CardinalDirection cardinalDirection : CardinalDirection.values()) {
            if (Double.compare(correctedBearing, cardinalDirection.from) >= 0 && Double.compare(correctedBearing, cardinalDirection.to) < 0) {
                return cardinalDirection.direction;
            }
        }
        return "";
    }


    // ******************** Event handling ************************************
    public void addLocationObserver(final LocationObserver observer) { if (!observers.contains(observer)) { observers.add(observer); }}
    public void removeLocationObserver(final LocationObserver observer) { if (observers.contains(observer)) { observers.remove(observer); }}
    public void removeAllObservers() { observers.clear(); }

    public Consumer<LocationEvent> getOnLocationChanged() { return locationChangedConsumer; }
    public void setOnLocationChanged(final Consumer<LocationEvent> consumer) { locationChangedConsumer = consumer; }


    public void fireLocationEvent(final LocationEvent evt) {
        for (LocationObserver observer : observers) { observer.onLocationEvent(evt); }

        final LocationEventType type = evt.getType();
        switch(type) {
            case LOCATION_CHANGED: if (null == locationChangedConsumer) { break; } else { locationChangedConsumer.accept(evt); break; }
        }
    }


    // ******************** Misc **********************************************
    @Override public boolean equals(final Object OBJECT) {
        if (OBJECT instanceof Location) {
            final Location LOCATION = (Location) OBJECT;
            return (Double.compare(latitude, LOCATION.latitude) == 0 &&
                    Double.compare(longitude, LOCATION.longitude) == 0 &&
                    Double.compare(altitude, LOCATION.altitude) == 0);
        } else {
            return false;
        }
    }

    @Override public String toString() {
        return new StringBuilder().append("{")
                                  .append("\"name\":\"").append(name).append("\",")
                                  .append("\"timestamp\":").append(timestamp).append(",")
                                  .append("\"latitude\":").append(latitude).append(",")
                                  .append("\"longitude\":").append(longitude).append(",")
                                  .append("\"altitude\":").append(altitude).append(",")
                                  .append("\"accuracy\":").append(accuracy).append(",")
                                  .append("\"info\":\"").append(info).append("\",")
                                  .append("\"fence\":\"").append(fence).append("\",")
                                  .append("\"color\":\"").append(color).append("\"")
                                  .append("}")
                                  .toString();
    }

    @Override public int hashCode() {
        int result;
        long temp;
        result = name != null ? name.hashCode() : 0;
        temp   = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp   = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp   = Double.doubleToLongBits(altitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
