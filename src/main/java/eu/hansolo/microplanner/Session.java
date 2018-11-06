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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class Session {
    private String        title;
    private String        abstrakt;
    private long          seconds;
    private List<Speaker> speakers;
    private List<String>  tags;


    // ******************** Constructors **************************************
    public Session(final String title, final String abstrakt, final Speaker... speakers) {
        this.title    = title;
        this.abstrakt = abstrakt;
        this.seconds  = 2700;
        this.speakers = new ArrayList<>(Arrays.asList(speakers));
        this.tags     = new ArrayList<>();
    }


    // ******************** Methods *******************************************
    public String getTitle() { return title; }
    public void setTitle(final String title) { this.title = title; }

    public String getAbstrakt() { return abstrakt; }
    public void setAbstrakt(final String abstrakt) { this.abstrakt = abstrakt; }

    public long getSeconds() { return seconds; }
    public void setSeconds(final long seconds) { this.seconds = seconds; }

    public List<Speaker> getSpeakers() { return speakers; }

    public List<String> getTags() { return tags; }

    @Override public String toString() {
        StringBuilder str = new StringBuilder().append("{")
                                  .append("\"title\":\"").append(title).append("\",")
                                  .append("\"abstract\":\"").append(abstrakt).append("\",")
                                  .append("\"seconds\":").append(seconds).append(",")
                                  .append("[");
        speakers.forEach(speaker -> str.append(speaker.toString()).append(","));
        str.setLength(str.length() - 1);
        str.append("],")
           .append("[");
        tags.forEach(tag -> str.append("\"").append(tag).append("\","));
        str.setLength(str.length() - 1);
        return str.append("}").toString();
    }
}
