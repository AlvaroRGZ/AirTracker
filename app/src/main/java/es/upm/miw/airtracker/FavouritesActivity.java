package es.upm.miw.airtracker;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import es.upm.miw.airtracker.view.WeatherListAdapter;
import es.upm.miw.airtracker.view.WeatherViewModel;


public class FavouritesActivity extends AppCompatActivity {
    // private ScoreViewModel mScoreViewModel;
    private static final String TAG = "DATA";

    private WeatherViewModel weatherViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final WeatherListAdapter adapter = new WeatherListAdapter(new WeatherListAdapter.WeatherDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
        weatherViewModel.getAllFavourites().observe(this, favourites -> {
            adapter.submitList(favourites);
        });

        Button atras = findViewById(R.id.btnFinish);
        atras.setOnClickListener(view -> finish());
    }
}
