package es.upm.miw.airtracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

import es.upm.miw.airtracker.api.AirQualityRESTAPIService;
import es.upm.miw.airtracker.firebase.FirebaseClient;
import es.upm.miw.airtracker.model.Condition;
import es.upm.miw.airtracker.model.Weather;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// import androidx.recyclerview.widget.LinearLayoutManager;
// import androidx.recyclerview.widget.RecyclerView;

// import es.upm.miw.bantumi.dialog.DeleteScoresDialog;
// import es.upm.miw.bantumi.view.ScoreListAdapter;
// import es.upm.miw.bantumi.view.ScoreViewModel;

public class DataActivity extends AppCompatActivity {
    // private ScoreViewModel mScoreViewModel;
    private static final String TAG = "DATA";
    private static final String API_BASE_URL = "https://api.weatherapi.com/";
    private static final String k = "0ed93a21f84e47b1a38203517230511";
    private static final String aqi = "no";
    private TextView tvResultado;
    private TextView tvSaved;
    private AirQualityRESTAPIService apiService;
    private static final String FIREBASE_URL = "https://airtracker-d4e8e-default-rtdb.europe-west1.firebasedatabase.app";

    private FirebaseClient database;
    private Integer n = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        this.tvResultado = findViewById(R.id.tvResult);
        this.tvSaved = findViewById(R.id.tvSaved);

        // RecyclerView recyclerView = findViewById(R.id.recyclerview);
        // final ScoreListAdapter adapter = new ScoreListAdapter(new ScoreListAdapter.ScoreDiff());
        // recyclerView.setAdapter(adapter);
        // recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // mScoreViewModel = new ViewModelProvider(this).get(ScoreViewModel.class);
        // mScoreViewModel.getAllScores().observe(this, scores -> {
        //     adapter.submitList(scores);
        // });

        database = new FirebaseClient();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(AirQualityRESTAPIService.class);

        Button atras = findViewById(R.id.btnFinish);
        atras.setOnClickListener(view -> finish());

        Button borrar = findViewById(R.id.btnClearData);
        borrar.setOnClickListener(view -> tvResultado.setText("Nada por ahora"));

        Button favoritos = findViewById(R.id.btnSaveAsFavourite);
        favoritos.setOnClickListener(view -> {
            startActivity(new Intent(DataActivity.this, FavouritesActivity.class));
            Log.i(TAG, "[=>] Pantalla de favoritos");
        });

        Button getData = findViewById(R.id.btnGetData);
        getData.setOnClickListener(view -> {

            EditText etNombreZona = findViewById(R.id.etNombreZona);

            Call<Weather> call_async = apiService.getZoneLocation(k, etNombreZona.getText().toString(), aqi);

            call_async.enqueue(new Callback<Weather>() {
                @Override
                public void onResponse(Call<Weather> call, Response<Weather> response) {
                    Weather weather = response.body();
                    if (null != weather) {
                        tvResultado.setText(weather.getLocation().getName() + ", " + weather.getLocation().getCountry() +
                                "\n    Last: " + weather.getCurrent().getLastUpdated());

                        tvSaved.setText(weather.getLocation().getName() + ", " + weather.getLocation().getCountry() +
                                "\n    Last: " + weather.getCurrent().getLastUpdated());

                        database.writeNewWeather(weather);
                        registerListeners(weather);
                    } else {
                        tvResultado.setText("Pais no encontrado");
                    }
                }

                /**
                 * Invoked when a network exception occurred talking to the server or when an unexpected
                 * exception occurred creating the request or processing the response.
                 */
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
        });
    }

    public void registerListeners(Weather weather) {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, dataSnapshot.toString());
                Weather weather = dataSnapshot.getValue(Weather.class);
                tvSaved.setText(weather.getLocation().getName() + ", " + weather.getLocation().getCountry() +
                        "\n    Last: " + weather.getCurrent().getLastUpdated());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };

        try {
            database.getDatabaseRootReference().child("weather")
                    .child(weather.getLocation().getName())
                    .child(weather.getCurrent().getLastUpdated())
                    .addValueEventListener(postListener);
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }
}
