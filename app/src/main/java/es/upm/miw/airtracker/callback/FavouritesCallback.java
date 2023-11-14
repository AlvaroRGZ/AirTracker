package es.upm.miw.airtracker.callback;

import java.util.List;

import es.upm.miw.airtracker.model.Weather;

public interface FavouritesCallback {
    void onFavouritesLoaded(List<Weather> allWeathers);
}
