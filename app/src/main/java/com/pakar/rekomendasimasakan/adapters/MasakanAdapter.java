package com.pakar.rekomendasimasakan.adapters;

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
import java.util.Locale;

public class MasakanAdapter extends RecyclerView.Adapter<MasakanAdapter.ViewHolder> {

    private List<Masakan> listMasakan;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Masakan masakan);
    }

    public MasakanAdapter(List<Masakan> listMasakan, OnItemClickListener listener) {
        this.listMasakan = listMasakan;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_masakan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Masakan masakan = listMasakan.get(position);
        holder.tvNama.setText(masakan.getNama());
        holder.tvMatch.setText(String.format(Locale.getDefault(), "%.0f%%", masakan.getMatchPercentage()));

        if (masakan.getImagePath() != null && !masakan.getImagePath().isEmpty()) {
            File imgFile = new File(masakan.getImagePath());
            if (imgFile.exists()) {
                holder.imgMasakan.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
            } else {
                holder.imgMasakan.setImageResource(R.drawable.img_splash);
            }
        } else {
            holder.imgMasakan.setImageResource(R.drawable.img_splash);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(masakan));
    }

    @Override
    public int getItemCount() {
        return listMasakan.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMasakan;
        TextView tvNama, tvMatch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMasakan = itemView.findViewById(R.id.imgMasakan);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvMatch = itemView.findViewById(R.id.tvMatch);
        }
    }
}