package es.upm.miw.airtracker.model;

import java.util.List;

public class User {
    private String name;
    private String email;

    private List<String> favouriteZones;

    public User(String name, String email, List<String> favouriteZones) {
        this.name = name;
        this.email = email;
        this.favouriteZones = favouriteZones;
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

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", favouriteZones=" + favouriteZones +
                '}';
    }
}
