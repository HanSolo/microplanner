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

public class Organizer extends Person {
    private String mail;
    private String phone;


    // ******************** Constructors **************************************
    public Organizer(final String name, final String firstName, final Gender gender) {
        this(name, firstName, gender, "", "");
    }
    public Organizer(final String name, final String firstName, final Gender gender, final String mail, final String phone) {
        super(name, firstName, gender);
        this.mail  = mail;
        this.phone = phone;
    }


    // ******************** Methods *******************************************
    public String getMail() { return mail; }
    public void setMail(final String mail) { this.mail = mail; }

    public String getPhone() { return phone; }
    public void setPhone(final String phone) { this.phone = phone; }

    @Override public String toString() {
        return new StringBuilder().append("{")
                                  .append("\"name\":\"").append(getName()).append("\",")
                                  .append("\"firstName\":\"").append(getFirstName()).append("\",")
                                  .append("\"gender\":\"").append(getGender().name().toLowerCase()).append("\",")
                                  .append("\"birthDate\":").append(getBirthDateEpochSeconds()).append(",")
                                  .append("\"mail\":\"").append(mail).append("\",")
                                  .append("\"phone\":\"").append(phone).append("\"")
                                  .append("}").toString();
    }
}
