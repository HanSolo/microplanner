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
import java.time.LocalDate;


public class Person {
    private String    name;
    private String    firstName;
    private Gender    gender;
    private LocalDate birthDate;


    // ******************** Constructors **************************************
    public Person(final String name, final String firstName, final Gender gender) {
        this(name, firstName, gender, null);
    }
    public Person(final String name, final String firstName, final Gender gender, final LocalDate birthDate) {
        this.name      = name;
        this.firstName = firstName;
        this.gender    = gender;
        this.birthDate = birthDate;
    }


    // ******************** Methods *******************************************
    public String getName() { return name; }
    public void setName(final String name) { this.name = name; }

    public String getFirstName() { return firstName; }
    public void setFirstName(final String firstName) { this.firstName = firstName; }

    public Gender getGender() { return gender; }
    public void setGender(final Gender gender) { this.gender = gender; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(final LocalDate birthDate) { this.birthDate = birthDate; }

    public long getBirthDateEpochSeconds() {
        if (null == birthDate) { return 0; }
        return Instant.from(birthDate).getEpochSecond();
    }
    public void setBirthDateFromEpochSeconds(final long epochSeconds) {
        if (epochSeconds < 0) { throw new IllegalArgumentException("Seconds cannot be smaller than 0"); }
        setBirthDate(LocalDate.from(Instant.ofEpochSecond(epochSeconds)));
    }

    @Override public String toString() {
        return new StringBuilder().append("{")
                                  .append("\"name\":\"").append(name).append("\",")
                                  .append("\"firstName\":\"").append(firstName).append("\",")
                                  .append("\"gender\":\"").append(gender.name().toLowerCase()).append("\",")
                                  .append("\"birthDate\":").append(getBirthDateEpochSeconds())
                                  .append("}").toString();
    }
}
