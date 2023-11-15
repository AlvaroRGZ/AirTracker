package es.upm.miw.airtracker.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import javax.annotation.Generated;

public class Favourite {

    private String name;
    private String country;
    private String date;
    private String temperature;

    private String image;

    public Favourite() {
        // Default constructor required for calls to DataSnapshot.getValue(Weather.class)
    }

    public Favourite(String name, String country, String date, String temperature, String image) {
        this.name = name;
        this.country = country;
        this.date = date;
        this.temperature = temperature;
        this.image = image;
    }

    public Favourite(Weather weather) {
        this.name = weather.getLocation().getName();
        this.country = weather.getLocation().getCountry();
        this.date = weather.getCurrent().getLastUpdated();
        this.temperature = weather.getCurrent().getTempC().toString() + "º";
        this.image = weather.getCurrent().getCondition().getIcon();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Favourite favourite = (Favourite) o;
        return Objects.equals(name, favourite.name) && Objects.equals(country, favourite.country) && Objects.equals(date, favourite.date) && Objects.equals(temperature, favourite.temperature) && Objects.equals(image, favourite.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, country, date, temperature, image);
    }

    @Override
    public String toString() {
        return "Favourite{" +
                "name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", date='" + date + '\'' +
                ", temperature='" + temperature + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
