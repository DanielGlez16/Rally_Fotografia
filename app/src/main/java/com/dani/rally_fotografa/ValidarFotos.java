package com.dani.rally_fotografa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.Map;

public class ValidarFotos extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter<Foto, FotoViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validar_fotos);

        recyclerView = findViewById(R.id.recyclerView);
        db = FirebaseFirestore.getInstance();

        Query query = db.collection("fotos")
                .whereEqualTo("estado", "pendiente"); // Solo fotos sin validar

        FirestoreRecyclerOptions<Foto> options =
                new FirestoreRecyclerOptions.Builder<Foto>()
                        .setQuery(query, Foto.class)
                        .build();

        adapter = new FirestoreRecyclerAdapter<Foto, FotoViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FotoViewHolder holder, int position, @NonNull Foto model) {
                Glide.with(holder.imageFoto.getContext())
                        .load(model.getUrl())
                        .into(holder.imageFoto);

                holder.btnAceptar.setOnClickListener(v -> {
                    getSnapshots().getSnapshot(position).getReference()
                            .update("estado", "admitida")
                            .addOnSuccessListener(unused ->
                                    Toast.makeText(ValidarFotos.this, "Foto aceptada", Toast.LENGTH_SHORT).show());
                });

                holder.btnRechazar.setOnClickListener(v -> {
                    getSnapshots().getSnapshot(position).getReference()
                            .update("estado", "rechazada")
                            .addOnSuccessListener(unused ->
                                    Toast.makeText(ValidarFotos.this, "Foto rechazada", Toast.LENGTH_SHORT).show());
                });
            }

            @NonNull
            @Override
            public FotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.activity_item_foto, parent, false);
                return new FotoViewHolder(view);
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
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

    private static class FotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageFoto;
        Button btnAceptar, btnRechazar;

        public FotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageFoto = itemView.findViewById(R.id.imageFoto);
            btnAceptar = itemView.findViewById(R.id.btnAceptar);
            btnRechazar = itemView.findViewById(R.id.btnRechazar);
        }
    }
}