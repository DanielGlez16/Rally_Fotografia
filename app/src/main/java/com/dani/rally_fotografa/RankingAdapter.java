package com.dani.rally_fotografa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.FotoViewHolder> {

    private List<Foto> listaFotos;

    public RankingAdapter(List<Foto> listaFotos) {
        this.listaFotos = listaFotos;
    }

    @NonNull
    @Override
    public FotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_foto_ranking, parent, false);
        return new FotoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FotoViewHolder holder, int position) {
        Foto foto = listaFotos.get(position);
        Glide.with(holder.imageFoto.getContext())
                .load(foto.getUrl())
                .into(holder.imageFoto);

        holder.textVotos.setText("Votos: " + foto.getVotos());
    }

    @Override
    public int getItemCount() {
        return listaFotos.size();
    }

    public static class FotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageFoto;
        TextView textVotos;

        public FotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageFoto = itemView.findViewById(R.id.imageFoto);
            textVotos = itemView.findViewById(R.id.textVotos);
        }
    }
}
