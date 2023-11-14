package es.upm.miw.airtracker.view;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import es.upm.miw.airtracker.R;
import es.upm.miw.airtracker.model.Weather;

public class WeatherViewHolder extends RecyclerView.ViewHolder {
    private  TextView zone;
    private  TextView country;
    private  TextView lastUpdated;
    private  TextView temperature;

    private WeatherViewHolder(View itemView) {
        super(itemView);
        zone = itemView.findViewById(R.id.item_zone);
        country = itemView.findViewById(R.id.item_country);
        lastUpdated = itemView.findViewById(R.id.item_last_updated);
        temperature = itemView.findViewById(R.id.item_temperature);
    }

    public void bind(Weather weather) {
        zone.setText(weather.getLocation().getName());
        country.setText(weather.getLocation().getCountry());
        lastUpdated.setText(weather.getCurrent().getLastUpdated());
        temperature.setText(weather.getCurrent().getTempC().toString() + "º");
    }

    static WeatherViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_zone_data, parent, false);
        return new WeatherViewHolder(view);
    }
}