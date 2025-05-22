package com.dani.rally_fotografa;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class GaleriaPublica extends AppCompatActivity {

    private RecyclerView recyclerGaleria;
    private FotoPublicaAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria_publica);

        recyclerGaleria = findViewById(R.id.recyclerGaleria);
        recyclerGaleria.setLayoutManager(new LinearLayoutManager(this));

        Query query = FirebaseFirestore.getInstance()
                .collection("fotos")
                .whereEqualTo("estado", "admitida")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Foto> options =
                new FirestoreRecyclerOptions.Builder<Foto>()
                        .setQuery(query, Foto.class)
                        .build();

        adapter = new FotoPublicaAdapter(options, this);
        recyclerGaleria.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
