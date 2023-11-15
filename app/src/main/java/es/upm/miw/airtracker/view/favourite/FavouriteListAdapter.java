package es.upm.miw.airtracker.view.favourite;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import es.upm.miw.airtracker.model.Favourite;

public class FavouriteListAdapter extends ListAdapter<Favourite, FavouriteViewHolder> {

    public FavouriteListAdapter(@NonNull DiffUtil.ItemCallback<Favourite> diffCallback) {
        super(diffCallback);
    }

    @Override
    public FavouriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return FavouriteViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteViewHolder holder, int position) {
        Favourite favourite = getItem(position);
        holder.bind(favourite);
    }

    public static class FavouriteDiff extends DiffUtil.ItemCallback<Favourite> {

        @Override
        public boolean areItemsTheSame(@NonNull Favourite oldItem, @NonNull Favourite newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Favourite oldItem, @NonNull Favourite newItem) {
            return oldItem.equals(newItem);
        }
    }
}
