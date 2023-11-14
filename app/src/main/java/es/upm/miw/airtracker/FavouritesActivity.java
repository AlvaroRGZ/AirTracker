package es.upm.miw.airtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import es.upm.miw.airtracker.firebase.FirebaseClient;
import es.upm.miw.airtracker.view.WeatherListAdapter;
import es.upm.miw.airtracker.view.WeatherViewModel;


public class FavouritesActivity extends AppCompatActivity {
    // private ScoreViewModel mScoreViewModel;
    private static final String TAG = "DATA";

    private WeatherViewModel weatherViewModel;

    private FirebaseClient firebaseClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        firebaseClient = new FirebaseClient();
        firebaseClient.setCurrentUserUID(FirebaseAuth.getInstance().getCurrentUser().getUid());

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final WeatherListAdapter adapter = new WeatherListAdapter(new WeatherListAdapter.WeatherDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
        weatherViewModel.getAllFavourites().observe(this, favourites -> {
            adapter.submitList(favourites);
        });

        TextView nombreUsuario = findViewById(R.id.tvNombreUsuario);
        nombreUsuario.setText(firebaseClient.getActiveUser(FirebaseAuth.getInstance().getCurrentUser().getUid()).getName());

        Button añadirFavoritos = findViewById(R.id.btnAddFavourites);
        añadirFavoritos.setOnClickListener(view -> {
            startActivity(new Intent(FavouritesActivity.this, DataActivity.class));
            Log.i(TAG, "[=>] Pantalla de datos");
        });

        Button atras = findViewById(R.id.btnFinish);
        atras.setOnClickListener(view -> finish());
    }
}
