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

public class Speaker {
    public enum Gender{ MALE, FEMALE }

    private String name;
    private String firstName;
    private Gender gender;
    private String bio;
    private String photoUrl;


    // ******************** Constructors **************************************
    public Speaker(final String name, final String firstName, final Gender gender) {
        this(name, firstName, gender, "");
    }
    public Speaker(final String name, final String firstName, final Gender gender, final String bio) {
        this.name      = name;
        this.firstName = firstName;
        this.gender    = gender;
        this.bio       = bio;
        this.photoUrl  = "";
    }


    // ******************** Methods *******************************************
    public String getName() { return name; }
    public void setName(final String name) { this.name = name; }

    public String getFirstName() { return firstName; }
    public void setFirstName(final String firstName) { this.firstName = firstName; }

    public Gender getGender() { return gender; }
    public void setGender(final Gender gender) { this.gender = gender; }

    public String getBio() { return bio; }
    public void setBio(final String bio) { this.bio = bio; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(final String photoUrl) { this.photoUrl = photoUrl; }

    @Override public String toString() {
        return new StringBuilder().append("{")
                                  .append("\"name\":\"").append(name).append("\",")
                                  .append("\"firstName\":\"").append(firstName).append("\",")
                                  .append("\"gender\":\"").append(gender.name().toLowerCase()).append("\",")
                                  .append("\"bio\":\"").append(bio).append("\",")
                                  .append("\"photoUrl\":\"").append(photoUrl).append("\",")
                                  .append("}").toString();
    }
}
