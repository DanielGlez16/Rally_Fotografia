package com.dani.rally_fotografa;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MisFotos extends AppCompatActivity {

    private RecyclerView recyclerMisFotos;
    private MisFotosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_fotos);

        recyclerMisFotos = findViewById(R.id.recyclerMisFotos);
        recyclerMisFotos.setLayoutManager(new LinearLayoutManager(this));

        String uid = FirebaseUtils.getCurrentUserId();

        Query query = FirebaseFirestore.getInstance()
                .collection("fotos")
                .whereEqualTo("userId", uid)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Foto> options =
                new FirestoreRecyclerOptions.Builder<Foto>()
                        .setQuery(query, Foto.class)
                        .setLifecycleOwner(this)
                        .build();

        adapter = new MisFotosAdapter(options);
        recyclerMisFotos.setAdapter(adapter);
    }
}
