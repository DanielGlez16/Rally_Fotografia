package com.dani.rally_fotografa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MisFotosAdapter extends FirestoreRecyclerAdapter<Foto, MisFotosAdapter.FotoViewHolder> {

    public MisFotosAdapter(@NonNull FirestoreRecyclerOptions<Foto> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull FotoViewHolder holder, int position, @NonNull Foto model) {
        Glide.with(holder.imageFoto.getContext())
                .load(model.getUrl())
                .into(holder.imageFoto);

        holder.textEstado.setText("Estado: " + model.getEstado());

        boolean esPendiente = "pendiente".equals(model.getEstado());
        holder.btnEliminar.setVisibility(esPendiente ? View.VISIBLE : View.GONE);

        if (esPendiente) {
            DocumentSnapshot snapshot = getSnapshots().getSnapshot(position);
            String docId = snapshot.getId();

            holder.btnEliminar.setOnClickListener(v -> {
                FirebaseFirestore.getInstance()
                        .collection("fotos")
                        .document(docId)
                        .delete();
            });
        }
    }

    @NonNull
    @Override
    public FotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mi_foto, parent, false);
        return new FotoViewHolder(view);
    }

    public static class FotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageFoto;
        TextView textEstado;
        Button btnEliminar;

        public FotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageFoto = itemView.findViewById(R.id.imageFoto);
            textEstado = itemView.findViewById(R.id.textEstado);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}
