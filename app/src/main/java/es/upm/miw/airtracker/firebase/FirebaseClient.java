package es.upm.miw.airtracker.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.upm.miw.airtracker.model.Weather;

public class FirebaseClient {

    private static final String TAG = "FIREBASE";

    private static final String FIREBASE_URL = "https://airtracker-d4e8e-default-rtdb.europe-west1.firebasedatabase.app";

    private DatabaseReference database;

    public FirebaseClient() {
        this.database = FirebaseDatabase.getInstance(FIREBASE_URL).getReference();

        // Register all methods
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Weather weather = dataSnapshot.getValue(Weather.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        database.addValueEventListener(postListener);
    }

    public DatabaseReference getDatabaseReference() {
        return database;
    }

    public DatabaseReference getDatabaseReference(String entity) {
        return FirebaseDatabase.getInstance(FIREBASE_URL).getReference(entity);
    }

    public void writeNewWeather(Weather weather) {
        database.child("weather").child(weather.getCurrent().getLastUpdated()).setValue(weather);
    }
}
