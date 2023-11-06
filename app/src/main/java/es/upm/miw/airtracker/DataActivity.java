package es.upm.miw.airtracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
// import androidx.recyclerview.widget.LinearLayoutManager;
// import androidx.recyclerview.widget.RecyclerView;

// import es.upm.miw.bantumi.dialog.DeleteScoresDialog;
// import es.upm.miw.bantumi.view.ScoreListAdapter;
// import es.upm.miw.bantumi.view.ScoreViewModel;

public class DataActivity extends AppCompatActivity {
    // private ScoreViewModel mScoreViewModel;
    private TextView tvResultado;

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

        Button atras = findViewById(R.id.btnFinish);
        atras.setOnClickListener(view -> finish());

        Button borrar = findViewById(R.id.btnClearData);
        borrar.setOnClickListener(view -> tvResultado.setText("Nada por ahora"));

        Button getData = findViewById(R.id.btnGetData);
        getData.setOnClickListener(view -> {
            tvResultado.setText("Busqueda");
        });
    }
}
