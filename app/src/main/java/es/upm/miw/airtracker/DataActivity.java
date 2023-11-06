package es.upm.miw.airtracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import es.upm.miw.airtracker.api.AirQualityRESTAPIService;
import es.upm.miw.airtracker.model.Result;
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
    private static final String API_BASE_URL = "https://api.weatherapi.com/";
    private static final String k = "0ed93a21f84e47b1a38203517230511";
    private static final String aqi = "no";
    private TextView tvResultado;
    private AirQualityRESTAPIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        this.tvResultado = findViewById(R.id.tvResult);

        // RecyclerView recyclerView = findViewById(R.id.recyclerview);
        // final ScoreListAdapter adapter = new ScoreListAdapter(new ScoreListAdapter.ScoreDiff());
        // recyclerView.setAdapter(adapter);
        // recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // mScoreViewModel = new ViewModelProvider(this).get(ScoreViewModel.class);
        // mScoreViewModel.getAllScores().observe(this, scores -> {
        //     adapter.submitList(scores);
        // });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(AirQualityRESTAPIService.class);

        Button atras = findViewById(R.id.btnFinish);
        atras.setOnClickListener(view -> finish());

        Button borrar = findViewById(R.id.btnClearData);
        borrar.setOnClickListener(view -> tvResultado.setText("Nada por ahora"));

        Button getData = findViewById(R.id.btnGetData);
        getData.setOnClickListener(view -> {
            
            EditText etNombreZona = findViewById(R.id.etNombreZona);

            Call<Result> call_async = apiService.getZoneLocation(k, etNombreZona.getText().toString(), aqi);

            // Asíncrona
            call_async.enqueue(new Callback<Result>() {

                /**
                 * Invoked for a received HTTP response.
                 * <p>
                 * Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
                 * Call {@link Response#isSuccessful()} to determine if the response indicates success.
                 */
                @Override
                public void onResponse(Call<Result> call, Response<Result> response) {
                    Result result = response.body();
                    if (null != result) {
                        tvResultado.append(result.getLocation().getName() + ", " + result.getLocation().getCountry() +
                                           "\n    Last: " + result.getCurrent().getLastUpdated());

                    } else {
                        tvResultado.setText("No se ha recibido nada");
                    }
                }

                /**
                 * Invoked when a network exception occurred talking to the server or when an unexpected
                 * exception occurred creating the request or processing the response.
                 */
                @Override
                public void onFailure(Call<Result> call, Throwable t) {
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
}
