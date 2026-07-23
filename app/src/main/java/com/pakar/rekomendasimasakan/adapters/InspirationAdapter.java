package com.pakar.rekomendasimasakan.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pakar.rekomendasimasakan.R;
import com.pakar.rekomendasimasakan.models.Masakan;
import java.io.File;
import java.util.List;

public class InspirationAdapter extends RecyclerView.Adapter<InspirationAdapter.ViewHolder> {

    private List<Masakan> listMasakan;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Masakan masakan);
    }

    public InspirationAdapter(List<Masakan> listMasakan, OnItemClickListener listener) {
        this.listMasakan = listMasakan;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inspirasi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Masakan masakan = listMasakan.get(position);
        holder.tvInspirasi.setText(masakan.getNama());

        if (masakan.getImagePath() != null && !masakan.getImagePath().isEmpty()) {
            File imgFile = new File(masakan.getImagePath());
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.imgInspirasi.setImageBitmap(myBitmap);
            } else {
                holder.imgInspirasi.setImageResource(R.drawable.img_splash);
            }
        } else {
            holder.imgInspirasi.setImageResource(R.drawable.img_splash);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(masakan));
    }

    @Override
    public int getItemCount() {
        return listMasakan.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgInspirasi;
        TextView tvInspirasi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgInspirasi = itemView.findViewById(R.id.imgInspirasi);
            tvInspirasi = itemView.findViewById(R.id.tvInspirasi);
        }
    }
}