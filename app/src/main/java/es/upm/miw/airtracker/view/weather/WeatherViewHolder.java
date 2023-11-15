package es.upm.miw.airtracker.view.weather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import es.upm.miw.airtracker.R;
import es.upm.miw.airtracker.model.Weather;

public class WeatherViewHolder extends RecyclerView.ViewHolder {
    private  TextView zone;
    private  TextView country;
    private  TextView lastUpdated;
    private  TextView temperature;
    private Button navigateButton;

    private WeatherViewHolder(View itemView) {
        super(itemView);
        zone = itemView.findViewById(R.id.item_zone);
        country = itemView.findViewById(R.id.item_country);
        lastUpdated = itemView.findViewById(R.id.item_last_updated);
        temperature = itemView.findViewById(R.id.item_temperature);
        navigateButton= itemView.findViewById(R.id.btnIconNav);
    }

    public void bind(Weather weather) {
        zone.setText(weather.getLocation().getName());
        country.setText(weather.getLocation().getCountry());
        lastUpdated.setText(formatDateString(weather.getCurrent().getLastUpdated()));
        temperature.setText(weather.getCurrent().getTempC().toString() + "º");
    }

    static WeatherViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_weather, parent, false);
        return new WeatherViewHolder(view);
    }

    public static String formatDateString(String inputDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        try {
            Date date = sdf.parse(inputDate);
            Date currentDate = new Date();

            long diffInMillis = currentDate.getTime() - date.getTime();
            long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            if (diffInDays == 0) {
                return "Hoy, " + timeFormat.format(date);
            } else if (diffInDays == 1) {

                return "Ayer, " + timeFormat.format(date);
            } else {
                return "Hace " + diffInDays + " dias, " + timeFormat.format(date);
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return "Error al procesar la fecha";
        }
    }
}