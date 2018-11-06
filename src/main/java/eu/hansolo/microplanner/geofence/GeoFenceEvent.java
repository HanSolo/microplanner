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

import eu.hansolo.microplanner.location.Location;


public class GeoFenceEvent {
    public enum GeoFenceEventType { ENTERED_FENCE, INSIDE_FENCE, LEFT_FENCE, OUTSIDE_FENCE }

    private final GeoFenceEventType type;
    private final Object            source;
    private final Location          location;



    // ******************** Constructors **************************************
    public GeoFenceEvent(final Object source, final Location location, final GeoFenceEventType type) {
        this.source   = source;
        this.location = location;
        this.type     = type;
    }


    // ******************** Methods *******************************************
    public Object getSource() { return source; }

    public Location getLocation() { return location; }

    public GeoFenceEventType getType() { return type; }
}
