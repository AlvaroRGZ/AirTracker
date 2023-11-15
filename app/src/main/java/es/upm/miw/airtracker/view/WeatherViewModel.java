package es.upm.miw.airtracker.view;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import es.upm.miw.airtracker.firebase.FirebaseClient;
import es.upm.miw.airtracker.model.Weather;

public class WeatherViewModel extends AndroidViewModel {

    private final FirebaseClient firebaseClient;

    private final LiveData<List<Weather>> favourites;

    public WeatherViewModel(Application application) {
        super(application);
        firebaseClient = new FirebaseClient();
        firebaseClient.setCurrentUserUID(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Log.i("f", "Voy a entrar a getAllFavourites");
        favourites = firebaseClient.getAllFavourites();
        Log.i("f", "Salgo de getAllFavourites");
    }

    public LiveData<List<Weather>> getAllFavourites() {
        return favourites;
    }
}