package com.dani.rally_fotografa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FotoPublicaAdapter extends FirestoreRecyclerAdapter<Foto, FotoPublicaAdapter.FotoViewHolder> {

    private final Context context;
    private long fechaLimiteVotacion = Long.MAX_VALUE;

    public FotoPublicaAdapter(@NonNull FirestoreRecyclerOptions<Foto> options, Context context) {
        super(options);
        this.context = context;

        // Obtener fecha fin votación desde Firestore
        FirebaseFirestore.getInstance()
                .collection("config")
                .document("rally")
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.contains("fechaFinVotacion")) {
                        fechaLimiteVotacion = doc.getLong("fechaFinVotacion");
                    }
                });
    }

    @Override
    protected void onBindViewHolder(@NonNull FotoViewHolder holder, int position, @NonNull Foto model) {
        Glide.with(context).load(model.getUrl()).into(holder.imageFoto);
        holder.textVotos.setText("Votos: " + model.getVotos());

        String fotoId = getSnapshots().getSnapshot(position).getId();
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "anonimo_" + fotoId; // fallback para test sin login

        long ahora = System.currentTimeMillis();

        // Ocultar botón si pasó fecha de votación
        if (ahora > fechaLimiteVotacion) {
            holder.btnVotar.setVisibility(View.GONE);
            return;
        }

        holder.btnVotar.setVisibility(View.VISIBLE);
        holder.btnVotar.setOnClickListener(v -> {
            FirebaseFirestore.getInstance().collection("votos")
                    .document(userId + "_" + fotoId).get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            Toast.makeText(context, "Ya has votado esta foto", Toast.LENGTH_SHORT).show();
                        } else {
                            int nuevosVotos = model.getVotos() + 1;
                            FirebaseFirestore.getInstance().collection("fotos")
                                    .document(fotoId).update("votos", nuevosVotos);

                            FirebaseFirestore.getInstance().collection("votos")
                                    .document(userId + "_" + fotoId)
                                    .set(new java.util.HashMap<>());

                            holder.textVotos.setText("Votos: " + nuevosVotos);
                            holder.btnVotar.setVisibility(View.GONE);
                        }
                    });
        });
    }

    @NonNull
    @Override
    public FotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_foto_publica, parent, false);
        return new FotoViewHolder(view);
    }

    public static class FotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageFoto;
        TextView textVotos;
        Button btnVotar;

        public FotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageFoto = itemView.findViewById(R.id.imageFoto);
            textVotos = itemView.findViewById(R.id.textVotos);
            btnVotar = itemView.findViewById(R.id.btnVotar);
        }
    }
}
