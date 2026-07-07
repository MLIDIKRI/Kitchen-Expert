package com.pakar.rekomendasimasakan.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pakar.rekomendasimasakan.databinding.ItemMasakanBinding;
import com.pakar.rekomendasimasakan.models.Masakan;
import java.util.List;
import java.util.Locale;

public class MasakanAdapter extends RecyclerView.Adapter<MasakanAdapter.ViewHolder> {

    private List<Masakan> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Masakan item);
    }

    public MasakanAdapter(List<Masakan> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMasakanBinding binding = ItemMasakanBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Masakan item = list.get(position);
        holder.binding.tvNama.setText(item.getNama());
        holder.binding.tvKategori.setText(item.getKategori());
        holder.binding.tvMatch.setText(String.format(Locale.getDefault(), "Kecocokan: %.0f%%", item.getMatchPercentage()));
        
        if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            java.io.File imgFile = new java.io.File(item.getImagePath());
            if (imgFile.exists()) {
                holder.binding.imgMasakan.setImageBitmap(android.graphics.BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
                holder.binding.imgMasakan.setColorFilter(null);
            } else {
                holder.binding.imgMasakan.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            holder.binding.imgMasakan.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemMasakanBinding binding;
        public ViewHolder(ItemMasakanBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}