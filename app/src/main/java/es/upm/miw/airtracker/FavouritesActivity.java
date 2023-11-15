package es.upm.miw.airtracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import es.upm.miw.airtracker.api.AirQualityRESTAPIService;
import es.upm.miw.airtracker.firebase.FirebaseClient;
import es.upm.miw.airtracker.model.Favourite;
import es.upm.miw.airtracker.model.User;
import es.upm.miw.airtracker.model.Weather;
import es.upm.miw.airtracker.view.favourite.FavouriteListAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class FavouritesActivity extends AppCompatActivity {
    private static final String TAG = "DATA";

    private FirebaseClient firebaseClient;

    private static final String API_BASE_URL = "https://api.weatherapi.com/";
    private static final String k = "0ed93a21f84e47b1a38203517230511";
    private static final String aqi = "no";

    private AirQualityRESTAPIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        firebaseClient = new FirebaseClient();
        firebaseClient.setCurrentUserUID(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(AirQualityRESTAPIService.class);

        refreshFavouriteData();
        createRecycler();

        TextView nombreUsuario = findViewById(R.id.tvNombreUsuario);
        nombreUsuario.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        Button añadirFavoritos = findViewById(R.id.btnAddFavourites);
        añadirFavoritos.setOnClickListener(view -> {
            startActivity(new Intent(FavouritesActivity.this, DataActivity.class));
            Log.i(TAG, "[=>] Pantalla de datos");
        });

        Button refresh = findViewById(R.id.btnFefresh);
        refresh.setOnClickListener(view -> {
            refreshFavouriteData();
            createRecycler();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshFavouriteData();
        createRecycler();
    }

    protected void createRecycler() {
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final FavouriteListAdapter adapter = new FavouriteListAdapter(new FavouriteListAdapter.FavouriteDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseClient.getDatabaseReference("user").child(userUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    List<String> favourites = user.getFavouriteZones();
                    if (!favourites.isEmpty()) {
                        List<Favourite> allfavourites = new ArrayList<>();
                        for (String favourite : favourites) {
                            firebaseClient.getDatabaseReference("weather").child(favourite).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    List<Favourite> collectedFavourites = new ArrayList<>();
                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                                        collectedFavourites.add(new Favourite(child.getValue(Weather.class)));
                                    }
                                    // Ordena los registros por orden cronológico
                                    Collections.sort(collectedFavourites, Comparator.comparing(w -> w.getDate()));
                                    // Elige el más reciente
                                    allfavourites.add(collectedFavourites.get(collectedFavourites.size() - 1));

                                    // Cuando se han procesado todas las zonas actualiza
                                    if (allfavourites.size() == favourites.size()) {
                                        adapter.submitList(allfavourites);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Handling onCancelled
                                    Log.w("f", "Error retrieving favourite -> ", databaseError.toException());
                                }
                            });
                        }
                    } else {
                        adapter.submitList(new ArrayList<>());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handling onCancelled
                Log.w("f", "Error retrieving user -> ", databaseError.toException());
            }
        });
    }

    public void refreshFavouriteData() {
        String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseClient.getDatabaseReference("user").child(userUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    List<String> favourites = user.getFavouriteZones();
                    if (!favourites.isEmpty()) {
                        for (String favourite : favourites) {
                            Call<Weather> call_async = apiService.getZoneLocation(k, favourite, aqi);
                            call_async.enqueue(new Callback<Weather>() {
                                @Override
                                public void onResponse(Call<Weather> call, Response<Weather> response) {
                                    Weather weather = response.body();
                                    Log.i(TAG, weather.toString());
                                    if (null != weather) {
                                        firebaseClient.writeNewWeather(weather);
                                    }
                                }

                                @Override
                                public void onFailure(Call<Weather> call, Throwable t) {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "ERROR: " + t.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                    Log.i("error", t.getMessage());
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handling onCancelled
                Log.w("f", "Error retrieving user -> ", databaseError.toException());
            }
        });
    }

}
