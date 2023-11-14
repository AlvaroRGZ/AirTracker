package es.upm.miw.airtracker.firebase;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import es.upm.miw.airtracker.model.Weather;

public class FirebaseClient {

    private static final String TAG = "FIREBASE";

    private static final String FIREBASE_URL = "https://airtracker-d4e8e-default-rtdb.europe-west1.firebasedatabase.app";

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    public FirebaseClient() {
        this.database = FirebaseDatabase.getInstance(FIREBASE_URL);
        databaseReference = this.database.getReference();
    }

    public DatabaseReference getDatabaseRootReference() {
        return databaseReference;
    }

    public DatabaseReference getDatabaseReference(String entity) {
        return FirebaseDatabase.getInstance(FIREBASE_URL).getReference(entity);
    }

    public LiveData<List<Weather>> getAllFavourites() {
        MutableLiveData<List<Weather>> weathersLiveData = new MutableLiveData<>();
        List<String> favourites = Arrays.asList("Madrid", "Salamanca", "Tenerife");
        List<Weather> allWeathers = new ArrayList<>();

        for (String favourite : favourites) {
            databaseReference.child("weather").child(favourite).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Weather> weathers = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        weathers.add(child.getValue(Weather.class));
                        // Ordena los registros por orden cronológico
                        Collections.sort(weathers, Comparator.comparing(w -> w.getCurrent().getLastUpdated()));
                    }
                    // Elige el más reciente
                    allWeathers.add(weathers.get(weathers.size() - 1));

                    // Check if this is the last favorite before setting the value
                    if (favourites.indexOf(favourite) == favourites.size() - 1) {
                        weathersLiveData.setValue(allWeathers);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handling onCancelled
                    Log.w(TAG, "Error retrieving favourite -> ", databaseError.toException());
                }
            });
        }
        return weathersLiveData;
    }

    public void writeNewWeather(Weather weather) {
        databaseReference
            .child("weather")
                .child(weather.getLocation().getName())
                    .child(weather.getCurrent().getLastUpdated())
                        .setValue(weather);
    }
}
