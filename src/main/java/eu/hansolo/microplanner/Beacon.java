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

public class Beacon {
    private final String uuid;
    private final String minor;
    private final String major;
    private       String name;


    // ******************** Constructors **************************************
    public Beacon(final String uuid, final String minor, final String major) {
        this(uuid, minor, major, "");
    }
    public Beacon(final String uuid, final String minor, final String major, final String name) {
        this.uuid  = uuid;
        this.minor = minor;
        this.major = major;
        this.name  = name;
    }


    // ******************** Methods *******************************************
    public String getUUID() { return uuid; }

    public String getMinor() { return minor; }

    public String getMajor() { return major; }

    public String getName() { return name; }
    public void setName(final String name) { this.name = name; }

    @Override public String toString() {
        return new StringBuilder().append("{")
                                  .append("\"uuid\":\"").append(uuid).append("\",")
                                  .append("\"minor\":\"").append(minor).append("\",")
                                  .append("\"major\":\"").append(major).append("\",")
                                  .append("\"name\":\"").append(name).append("\",")
                                  .append("}")
                                  .toString();
    }
}
