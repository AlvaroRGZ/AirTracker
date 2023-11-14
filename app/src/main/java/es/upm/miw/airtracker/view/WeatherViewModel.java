package es.upm.miw.airtracker.view;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import es.upm.miw.airtracker.firebase.FirebaseClient;
import es.upm.miw.airtracker.model.Weather;

public class WeatherViewModel extends AndroidViewModel {

    private final FirebaseClient firebaseClient;

    private final LiveData<List<Weather>> favourites;

    public WeatherViewModel(Application application) {
        super(application);
        firebaseClient = new FirebaseClient();
        favourites = firebaseClient.getAllFavourites();
    }

    public LiveData<List<Weather>> getAllFavourites() {
        return favourites;
    }
}