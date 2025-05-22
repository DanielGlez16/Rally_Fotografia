package com.dani.rally_fotografa;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RankingActivity extends AppCompatActivity {

    private RecyclerView recyclerRanking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        recyclerRanking = findViewById(R.id.recyclerRanking);
        recyclerRanking.setLayoutManager(new LinearLayoutManager(this));

        // Aquí cargaremos los datos ordenados por votos (cuando integremos Firebase)
    }
}
