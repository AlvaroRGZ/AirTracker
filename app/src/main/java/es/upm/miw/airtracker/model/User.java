package es.upm.miw.airtracker.model;

import java.util.Arrays;
import java.util.List;

public class User {
    private String uid;
    private String name;
    private String email;

    private List<String> favouriteZones;

    public User () {

    }

    public User(String uid, String name, String email, List<String> favouriteZones) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.favouriteZones = favouriteZones;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getFavouriteZones() {
        return favouriteZones;
    }

    public void setFavouriteZones(List<String> favouriteZones) {
        this.favouriteZones = favouriteZones;
    }

    public void addFavouriteZone(String favouriteZone) {
        if (!this.favouriteZones.contains(favouriteZone)) {
            this.favouriteZones.add(favouriteZone);
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", favouriteZones=" + favouriteZones +
                '}';
    }
}
