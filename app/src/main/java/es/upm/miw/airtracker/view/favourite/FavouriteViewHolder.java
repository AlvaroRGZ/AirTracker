package es.upm.miw.airtracker.view.favourite;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.upm.miw.airtracker.WeatherActivity;
import es.upm.miw.airtracker.R;
import es.upm.miw.airtracker.model.Favourite;

public class FavouriteViewHolder extends RecyclerView.ViewHolder {
    private  TextView zone;
    private  TextView country;
    private  TextView lastUpdated;
    private  TextView temperature;
    private ImageButton navigateButton;

    private FavouriteViewHolder(View itemView) {
        super(itemView);
        zone = itemView.findViewById(R.id.item_zone);
        country = itemView.findViewById(R.id.item_country);
        lastUpdated = itemView.findViewById(R.id.item_last_updated);
        temperature = itemView.findViewById(R.id.item_temperature);
        navigateButton= itemView.findViewById(R.id.btnIconNav);
    }

    public void bind(Favourite favourite) {
        zone.setText(favourite.getName());
        country.setText(favourite.getCountry());
        lastUpdated.setText(formatDateString(favourite.getDate()));
        temperature.setText(favourite.getTemperature());
        String iconUrl = favourite.getImage();
        if (iconUrl != null && !iconUrl.isEmpty()) {
            Picasso.get().load("https:" +  iconUrl)
                    .resize(64, 64)
                    .centerCrop()
                    .into(navigateButton);
        }

        navigateButton.setOnClickListener(view -> {
            Intent detalles = new Intent(itemView.getContext(), WeatherActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("zone", favourite.getName());
            bundle.putString("country", favourite.getCountry());
            detalles.putExtras(bundle);
            itemView.getContext().startActivity(detalles);
            Log.i("VH", "[=>] Pantalla de detalles");
        });
    }

    static FavouriteViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_favourite, parent, false);
        return new FavouriteViewHolder(view);
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