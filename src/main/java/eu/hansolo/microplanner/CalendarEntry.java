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

import java.time.Instant;
import java.time.ZonedDateTime;


public class CalendarEntry {
    private Session       session;
    private ZonedDateTime dateTime;
    private Room          room;


    // ******************** Constructors **************************************
    public CalendarEntry(final Session session, final ZonedDateTime dateTime, final Room room) {
        this.session  = session;
        this.dateTime = dateTime;
        this.room     = room;
    }


    // ******************** Methods *******************************************
    public Session getSession() { return session; }
    public void setSession(final Session session) { this.session = session; }

    public ZonedDateTime getDateTime() { return dateTime; }
    public void setDateTime(final ZonedDateTime dateTime) { this.dateTime = dateTime; }

    public long getEpochSecond() { return dateTime.toEpochSecond(); }
    public void setEpochSecond(final long epochSecond) { dateTime = ZonedDateTime.from(Instant.ofEpochSecond(epochSecond)); }

    public Room getRoom() { return room; }
    public void setRoom(final Room room) { this.room = room; }

    @Override public String toString() {
        return new StringBuilder().append("{")
                                  .append("\"session\":").append(session.toString()).append(",")
                                  .append("\"timestamp\":").append(getEpochSecond()).append(",")
                                  .append("\"room\":").append(room.toString())
                                  .append("}")
                                  .toString();
    }
}
