package es.upm.miw.airtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;

import es.upm.miw.airtracker.api.AirQualityRESTAPIService;
import es.upm.miw.airtracker.firebase.FirebaseClient;
import es.upm.miw.airtracker.model.Weather;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class DataActivity extends AppCompatActivity {
    private static final String TAG = "DATA";
    private static final String API_BASE_URL = "https://api.weatherapi.com/";
    private static final String k = "0ed93a21f84e47b1a38203517230511";
    private static final String aqi = "no";
    private EditText etNombreZona;
    private TextView tvResultado;
    private TextView tvSaved;
    private AirQualityRESTAPIService apiService;
    private FirebaseClient database;
    private Boolean aprobedZoneNameToSave;
    private String lastNameCalled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        aprobedZoneNameToSave = false;

        etNombreZona = findViewById(R.id.etNombreZona);
        listenChanguesOnEditText();

        this.tvResultado = findViewById(R.id.tvResult);

        database = new FirebaseClient();
        database.setCurrentUserUID(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(AirQualityRESTAPIService.class);

        Button atras = findViewById(R.id.btnFinish);
        atras.setOnClickListener(view -> finish());

        Button favoritos = findViewById(R.id.btnSaveAsFavourite);
        favoritos.setOnClickListener(view -> {
            String toastMessage = "";
            if (!aprobedZoneNameToSave) {
                if (etNombreZona.getText().toString() == "") {
                    toastMessage = "Indica una zona";
                } else {
                    toastMessage = "Debes buscar la zona";
                }
            } else {
                database.addFavouriteToUser(FirebaseAuth.getInstance().getCurrentUser().getUid(), lastNameCalled);
                toastMessage = lastNameCalled + " añadido a favoritos";
            }
            Toast.makeText(
                    getApplicationContext(),
                    toastMessage,
                    Toast.LENGTH_SHORT
            ).show();
            Log.i(TAG, toastMessage);
        });

        Button getData = findViewById(R.id.btnGetData);
        getData.setOnClickListener(view -> {

            Call<Weather> call_async = apiService.getZoneLocation(k, etNombreZona.getText().toString(), aqi);

            call_async.enqueue(new Callback<Weather>() {
                @Override
                public void onResponse(Call<Weather> call, Response<Weather> response) {
                    Weather weather = response.body();
                    if (null != weather) {
                        tvResultado.setText(weather.getLocation().getName() + ", " + weather.getLocation().getCountry() +
                                "\n    Last: " + weather.getCurrent().getLastUpdated());

                        database.writeNewWeather(weather);
                        registerListeners(weather);
                        aprobedZoneNameToSave = true;
                        lastNameCalled = weather.getLocation().getName();
                    } else {
                        tvResultado.setText("Pais no encontrado");
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
        });
    }

    public void registerListeners(Weather weather) {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, dataSnapshot.toString());
                Weather weather = dataSnapshot.getValue(Weather.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
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

    public void listenChanguesOnEditText() {
        etNombreZona.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No se usa
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                aprobedZoneNameToSave = false;
            }
            @Override
            public void afterTextChanged(Editable s) {
                aprobedZoneNameToSave = false;
            }
        });
    }
}
