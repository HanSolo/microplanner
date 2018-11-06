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

package eu.hansolo.microplanner.geofence;

import eu.hansolo.microplanner.geofence.GeoFenceEvent.GeoFenceEventType;
import eu.hansolo.microplanner.location.Location;
import eu.hansolo.microplanner.tools.Helper;
import eu.hansolo.microplanner.tools.Point;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import static eu.hansolo.microplanner.geofence.GeoFenceEvent.GeoFenceEventType.ENTERED_FENCE;
import static eu.hansolo.microplanner.geofence.GeoFenceEvent.GeoFenceEventType.INSIDE_FENCE;
import static eu.hansolo.microplanner.geofence.GeoFenceEvent.GeoFenceEventType.LEFT_FENCE;
import static eu.hansolo.microplanner.geofence.GeoFenceEvent.GeoFenceEventType.OUTSIDE_FENCE;


public class GeoFence {
    private String                  name;           // Name of the GeoFence
    private String                  category;       // Category can be used to organize the fences
    private List<Point>             points;         // List of points that defines the Polygon that represents the GeoFence
    private String                  info;           // Contains additional information of the GeoFence
    private boolean                 active;         // Indicates if the GeoFence is active or not
    private boolean                 timeBased;      // Indicates if the GeoFence is only active at specified times
    private LocalTime               startTime;      // Start time of fence when it will trigger events if activated
    private LocalTime               endTime;        // End time of fence when it will trigger events if activated
    private ZoneId                  zoneId;         // ZoneId of the fence
    private Set<String>             tags;           // List of tags
    private Set<DayOfWeek>          days;           // Contains the days in a week where the fence will trigger events if activated
    private Map<String, Location>   objectsInFence; // Contains all Location-Objects that are inside the fence at the moment
    private List<GeoFenceObserver>  observers;      // List of observers
    private Consumer<GeoFenceEvent> enteredFenceConsumer;
    private Consumer<GeoFenceEvent> insideFenceConsumer;
    private Consumer<GeoFenceEvent> leftFenceConsumer;
    private Consumer<GeoFenceEvent> outsideFenceConsumer;


    // ******************** Constructors **************************************
    public GeoFence(final String name) {
        init(name, "public", new ArrayList<>(), "");
    }
    public GeoFence(final String name, final String category) {
        init(name, category, new ArrayList<>(), "");
    }
    public GeoFence(final String name, final String category, final double latitude, final double longitude, final double radius) {
        final List<Point> points = new ArrayList<>();
        for (int i = 0 ; i < 64 ; i++) {
            double theta = Math.PI * (i / (64 / 2));
            double x = longitude + (radius * Math.cos(theta));
            double y = latitude + (radius * Math.sin(theta));
            points.add(new Point(x, y));
        }
        init(name, category, points, "");
    }
    public GeoFence(final String name, final String category, final String xyList) {
        final String[]     data   = xyList.split(",");
        final List<Point> points = new ArrayList<>();
        for (int i = 0, size = data.length ; i < (size - 2) ; i += 2) {
            points.add(new Point(Double.parseDouble(data[i]), Double.parseDouble(data[i+1])));
        }
        init(name, category, points, "");
    }
    public GeoFence(final String name, final String category, final Location... locations) {
        final List<Point> points = new ArrayList<>();
        for (Location location : locations) {
            points.add(new Point(location.getLatitude(), location.getLongitude()));
        }
        init(name, category, points, "");
    }
    public GeoFence(final String name, final String category, final List<Point> points) {
        init(name, category, points, "");
    }


    // ******************** Initialization ************************************
    private void init(final String name, final String category, final List<Point> points, final String info) {
        this.name      = name;
        this.category  = category;
        this.points    = new ArrayList<>(points);
        this.info      = info;
        objectsInFence = new HashMap<>(8);
        active         = true;
        timeBased      = false;
        startTime      = LocalTime.MIN;
        endTime        = LocalTime.MAX;
        zoneId         = ZoneId.systemDefault();
        tags           = new HashSet<>(2);
        days           = new HashSet<>(7);
        observers      = new CopyOnWriteArrayList<>();
    }


    // ******************** Methods *******************************************
    public String getName() { return name; }
    public void setName(final String NAME) { name = NAME; }

    public String getCategory() { return category; }
    public void setCategory(final String category) { this.category = category; }

    public List<Point> getPoints()  { return points; }
    public void setPoints(final List<Point> points) {
        this.points.clear();
        this.points.addAll(points);
    }

    public String getInfo() { return info; }
    public void setInfo(final String info) { this.info = info; }

    public List<Location> getObjectsInFence() { return new ArrayList<>(objectsInFence.values()); }

    public boolean isActive() { return active; }
    public void setActive(final boolean active) { this.active = active; }

    public boolean isTimeBased() { return timeBased; }
    public void setTimeBased(final boolean timeBased) { this.timeBased = timeBased; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(final LocalTime START_TIME) { startTime = clamp(LocalTime.MIN, endTime, START_TIME); }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(final LocalTime endTime) { this.endTime = clamp(startTime, LocalTime.MAX, endTime); }

    public Set<String> getTags() { return tags; }
    public void setTags(final String... tags) { setTags(Arrays.asList(tags)); }
    public void setTags(final List<String> tags) { tags.forEach(tag -> this.tags.add(tag)); }
    public void setTags(final Set<String> tags) { tags.forEach(tag -> this.tags.add(tag)); }
    public void addTag(final String tag) { tags.add(tag); }
    public void removeTag(final String tag) { if (tags.contains(tag)) tags.remove(tag); }
    public void clearTags() { tags.clear(); }

    public Set<DayOfWeek> getDays() { return days; }
    public void setDays(final DayOfWeek... days) { setDays(Arrays.asList(days)); }
    public void setDays(final List<DayOfWeek> days) { days.forEach(day -> this.days.add(day)); }
    public void setDays(final Set<DayOfWeek> days) { days.forEach(day -> this.days.add(day)); }
    public void addDay(final DayOfWeek day) { days.add(day); }
    public void removeDay(final DayOfWeek day) { if (days.contains(day)) days.remove(day); }
    public void clearDays() { days.clear(); }

    public ZoneId getZoneId() { return zoneId; }
    public void setZoneId(final ZoneId zoneId) { this.zoneId = zoneId; }

    public boolean containsLocation(final Location location) {
        if (!active) return false;

        if (timeBased) {
            LocalDateTime now = LocalDateTime.now(zoneId);
            if (!(now.toLocalTime().isAfter(startTime) && now.toLocalTime().isBefore(endTime))) return false;
            if (!days.isEmpty()) {
                if (!days.contains(now.getDayOfWeek())) return false;
            }
        }

        final String  NAME            = location.getName();
        final boolean IS_KNOWN        = objectsInFence.containsKey(NAME);
        final boolean IS_INSIDE_FENCE = Helper.isInPolygon(location.getLatitude(), location.getLongitude(), points);
        if (IS_KNOWN) {
            if (IS_INSIDE_FENCE) {
                fireFenceEvent(new GeoFenceEvent(GeoFence.this, location, INSIDE_FENCE));
            } else {
                fireFenceEvent(new GeoFenceEvent(GeoFence.this, location, LEFT_FENCE));
                objectsInFence.remove(NAME);
            }
        } else {
            if (IS_INSIDE_FENCE) {
                fireFenceEvent(new GeoFenceEvent(GeoFence.this, location,  ENTERED_FENCE));
                objectsInFence.put(NAME, location);
            } else {
                fireFenceEvent(new GeoFenceEvent(GeoFence.this, location,  OUTSIDE_FENCE));
            }
        }
        return IS_INSIDE_FENCE;
    }

    private LocalTime clamp(final LocalTime MIN, final LocalTime MAX, final LocalTime TIME) {
        if (TIME.isBefore(MIN)) return MIN;
        if (TIME.isAfter(MAX)) return MAX;
        return TIME;
    }


    // ******************** Event handling ************************************
    public void addGeoFenceObserver(final GeoFenceObserver observer) { if (!observers.contains(observer)) { observers.add(observer); }}
    public void removeGeoFenceObserver(final GeoFenceObserver observer) { if (observers.contains(observer)) { observers.remove(observer); }}
    public void removeAllObservers() { observers.clear(); }

    public Consumer<GeoFenceEvent> getOnEnteredFence() { return enteredFenceConsumer; }
    public void setOnEnteredFence(final Consumer<GeoFenceEvent> consumer) { enteredFenceConsumer = consumer; }

    public Consumer<GeoFenceEvent> getOnInsideFence() { return insideFenceConsumer; }
    public void setOnInsideFence(final Consumer<GeoFenceEvent> consumer)  { insideFenceConsumer  = consumer; }

    public Consumer<GeoFenceEvent> getOnLeftFence() { return leftFenceConsumer; }
    public void setOnLeftFence(final Consumer<GeoFenceEvent> consumer)    { leftFenceConsumer    = consumer; }

    public Consumer<GeoFenceEvent> getOnOutsideFence() { return outsideFenceConsumer; }
    public void setOnOutsideFence(final Consumer<GeoFenceEvent> consumer) { outsideFenceConsumer = consumer; }

    public void fireFenceEvent(final GeoFenceEvent evt) {
        for (GeoFenceObserver observer : observers) { observer.onGeoFenceEvent(evt); }

        final GeoFenceEventType type = evt.getType();
        switch (type) {
            case ENTERED_FENCE: if (null == enteredFenceConsumer) { break; } else { enteredFenceConsumer.accept(evt); break; }
            case INSIDE_FENCE : if (null == insideFenceConsumer)  { break; } else { insideFenceConsumer.accept(evt); break; }
            case LEFT_FENCE   : if (null == leftFenceConsumer)    { break; } else { leftFenceConsumer.accept(evt); break; }
            case OUTSIDE_FENCE: if (null == outsideFenceConsumer) { break; } else { outsideFenceConsumer.accept(evt); break; }
        }
    }
}
