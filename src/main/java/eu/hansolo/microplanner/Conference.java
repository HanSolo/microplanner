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

import eu.hansolo.microplanner.location.Location;


public class Conference {
    private String   name;
    private long     startDate;
    private long     endDate;
    private String   city;
    private Location location;
    private String   country;
    private Schedule schedule;


    // ******************** Construcors ***************************************
    public Conference(final String name, final String city, final long startDate, final long endDate) {
        this.name      = name;
        this.city      = city;
        this.startDate = startDate;
        this.endDate   = endDate;
        this.location  = new Location();
        this.country   = "";
        this.schedule  = new Schedule();
    }


    // ******************** Methods *******************************************
    public String getName() { return name; }
    public void setName(final String name) { this.name = name; }

    public long getStartDate() { return startDate; }
    public void setStartDate(final long startDate) {
        this.startDate = startDate;
        if (this.endDate < this.startDate) { endDate = this.startDate + 1; }
    }

    public long getEndDate() { return endDate; }
    public void setEndDate(final long endDate) {
        if (endDate < startDate) { throw new IllegalArgumentException("conference cannot end before it has started"); }
        this.endDate = endDate;
    }

    public String getCity() { return city; }
    public void setCity(final String city) { this.city = city; }

    public Location getLocation() { return location; }
    public void setLocation(final Location location) { this.location = location; }

    public String getCountry() { return country; }
    public void setCountry(final String country) { this.country = country; }

    public Schedule getSchedule() { return schedule; }
    public void setSchedule(final Schedule schedule) { this.schedule = schedule; }
}
