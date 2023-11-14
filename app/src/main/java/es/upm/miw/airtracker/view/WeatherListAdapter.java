package es.upm.miw.airtracker.view;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import es.upm.miw.airtracker.model.Weather;

public class WeatherListAdapter extends ListAdapter<Weather, WeatherViewHolder> {

    public WeatherListAdapter(@NonNull DiffUtil.ItemCallback<Weather> diffCallback) {
        super(diffCallback);
    }

    @Override
    public WeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return WeatherViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(WeatherViewHolder holder, int position) {
        Weather weather = getItem(position);
        holder.bind(weather);
    }

    public static class WeatherDiff extends DiffUtil.ItemCallback<Weather> {

        @Override
        public boolean areItemsTheSame(@NonNull Weather oldItem, @NonNull Weather newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Weather oldItem, @NonNull Weather newItem) {
            return oldItem.equals(newItem);
        }
    }
}
