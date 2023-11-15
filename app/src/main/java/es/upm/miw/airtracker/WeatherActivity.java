package es.upm.miw.airtracker;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
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
import es.upm.miw.airtracker.model.Weather;
import es.upm.miw.airtracker.view.weather.WeatherListAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class WeatherActivity extends AppCompatActivity {
    private static final String TAG = "DATA";

    private FirebaseClient firebaseClient;

    private static final String API_BASE_URL = "https://api.weatherapi.com/";
    private static final String k = "0ed93a21f84e47b1a38203517230511";
    private static final String aqi = "no";

    private AirQualityRESTAPIService apiService;

    private String zoneName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //Obtenemos el nombre de la zona que se ha pulsado
        Bundle bundle = getIntent().getExtras();
        zoneName = bundle.getString("zone", "Madrid");


        firebaseClient = new FirebaseClient();
        firebaseClient.setCurrentUserUID(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(AirQualityRESTAPIService.class);

        refreshFavouriteData();
        createRecycler();

        TextView tvZona = findViewById(R.id.tvTextoSaludo);
        tvZona.setText(zoneName);


        Button refresh = findViewById(R.id.btnFefresh);
        refresh.setOnClickListener(view -> {
            refreshFavouriteData();
            createRecycler();
        });
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {
            finish();
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
        final WeatherListAdapter adapter = new WeatherListAdapter(new WeatherListAdapter.WeatherDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Obtenemos todos los registros de la zona especificada ordenados cronologicamente
        firebaseClient.getDatabaseReference("weather").child(zoneName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Weather> weathers = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    weathers.add(child.getValue(Weather.class));
                }
                // Ordena los registros por orden cronológico
                Collections.sort(weathers, Comparator.comparing(w -> w.getCurrent().getLastUpdated(), Collections.reverseOrder()));
                adapter.submitList(weathers);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("f", "Error retrieving favourite -> ", databaseError.toException());
            }
        });
    }

    public void refreshFavouriteData() {
        // Buscamos una nueva actualizacion de la zona
        Call<Weather> call_async = apiService.getZoneLocation(k, zoneName, aqi);
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
                Log.i("q", t.getMessage());
            }
        });
    }
}
