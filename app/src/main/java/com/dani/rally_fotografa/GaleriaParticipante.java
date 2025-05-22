package com.dani.rally_fotografa;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

public class GaleriaParticipante extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirestoreRecyclerAdapter<Foto, FotoViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria_participante);

        recyclerView = findViewById(R.id.recyclerViewGaleria);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        String userId = auth.getCurrentUser().getUid();

        Query query = db.collection("fotos")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Foto> options =
                new FirestoreRecyclerOptions.Builder<Foto>()
                        .setQuery(query, Foto.class)
                        .build();

        adapter = new FirestoreRecyclerAdapter<Foto, FotoViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FotoViewHolder holder, int position, @NonNull Foto model) {
                Glide.with(holder.imageView.getContext())
                        .load(model.getUrl())
                        .into(holder.imageView);
                holder.estadoText.setText("Estado: " + model.getEstado());

                holder.btnEliminar.setOnClickListener(v -> {
                    new AlertDialog.Builder(GaleriaParticipante.this)
                            .setTitle("Eliminar foto")
                            .setMessage("¿Seguro que quieres eliminar esta foto?")
                            .setPositiveButton("Sí", (dialog, which) -> {
                                String url = model.getUrl();
                                String path = "photos/" + model.getUid() + "/" + obtenerNombreDeArchivo(url);

                                // 1. Eliminar de Storage
                                FirebaseStorage.getInstance().getReference().child(path).delete()
                                        .addOnSuccessListener(unused -> {
                                            // 2. Eliminar documento Firestore
                                            getSnapshots().getSnapshot(position).getReference().delete()
                                                    .addOnSuccessListener(unused2 ->
                                                            Toast.makeText(GaleriaParticipante.this, "Foto eliminada", Toast.LENGTH_SHORT).show())
                                                    .addOnFailureListener(e ->
                                                            Toast.makeText(GaleriaParticipante.this, "Error al borrar Firestore", Toast.LENGTH_SHORT).show());
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(GaleriaParticipante.this, "Error al borrar imagen", Toast.LENGTH_SHORT).show());
                            })
                            .setNegativeButton("Cancelar", null)
                            .show();
                });
            }

            @NonNull
            @Override
            public FotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_foto_participante, parent, false);
                return new FotoViewHolder(view);
            }
        };

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
        ImageView imageView;
        TextView estadoText;
        Button btnEliminar;

        public FotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewFoto);
            estadoText = itemView.findViewById(R.id.textEstado);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }

    private String obtenerNombreDeArchivo(String url) {
        if (url == null || !url.contains("/")) return "";
        int lastSlash = url.lastIndexOf("/");
        int questionMark = url.indexOf("?", lastSlash);
        return url.substring(lastSlash + 1, questionMark > 0 ? questionMark : url.length());
    }
}
