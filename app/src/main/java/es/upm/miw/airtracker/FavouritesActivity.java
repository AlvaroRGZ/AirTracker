package es.upm.miw.airtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
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
import es.upm.miw.airtracker.model.User;
import es.upm.miw.airtracker.model.Weather;
import es.upm.miw.airtracker.view.WeatherListAdapter;
import es.upm.miw.airtracker.view.WeatherViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class FavouritesActivity extends AppCompatActivity {
    // private ScoreViewModel mScoreViewModel;
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
        nombreUsuario.setText(FirebaseAuth.getInstance().getCurrentUser().getUid());

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
        createRecycler();
    }

    protected void createRecycler() {
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final WeatherListAdapter adapter = new WeatherListAdapter(new WeatherListAdapter.WeatherDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseClient.getDatabaseReference("user").child(userUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    List<String> favourites = user.getFavouriteZones();

                    MutableLiveData<List<Weather>> weathersLiveData = new MutableLiveData<>();
                    List<Weather> allWeathers = new ArrayList<>();
                    for (String favourite : favourites) {
                        firebaseClient.getDatabaseReference("weather").child(favourite).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                List<Weather> weathers = new ArrayList<>();
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    weathers.add(child.getValue(Weather.class));
                                }
                                // Ordena los registros por orden cronológico
                                Collections.sort(weathers, Comparator.comparing(w -> w.getCurrent().getLastUpdated()));
                                // Elige el más reciente
                                allWeathers.add(weathers.get(weathers.size() - 1));

                                // Check if this is the last favorite before setting the value
                                if (favourites.indexOf(favourite) == favourites.size() - 1) {
                                    adapter.submitList(allWeathers);
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Handling onCancelled
                                Log.w("f", "Error retrieving favourite -> ", databaseError.toException());
                            }
                        });
                    }
                    adapter.submitList(weathersLiveData.getValue());
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

                    for (String favourite : favourites) {
                        Call<Weather> call_async = apiService.getZoneLocation(k, favourite, aqi);

                        call_async.enqueue(new Callback<Weather>() {
                            @Override
                            public void onResponse(Call<Weather> call, Response<Weather> response) {
                                Weather weather = response.body();
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

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handling onCancelled
                Log.w("f", "Error retrieving user -> ", databaseError.toException());
            }
        });
    }

}
