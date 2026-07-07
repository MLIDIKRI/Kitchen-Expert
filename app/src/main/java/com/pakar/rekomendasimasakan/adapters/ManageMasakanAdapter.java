package com.pakar.rekomendasimasakan.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pakar.rekomendasimasakan.databinding.ItemManageMasakanBinding;
import com.pakar.rekomendasimasakan.models.Masakan;
import java.util.List;

public class ManageMasakanAdapter extends RecyclerView.Adapter<ManageMasakanAdapter.ViewHolder> {

    private List<Masakan> list;
    private OnActionClickListener listener;

    public interface OnActionClickListener {
        void onDelete(int id);
        void onEdit(Masakan item);
    }

    public ManageMasakanAdapter(List<Masakan> list, OnActionClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemManageMasakanBinding binding = ItemManageMasakanBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Masakan item = list.get(position);
        holder.binding.tvNama.setText(item.getNama());
        holder.binding.tvKategori.setText(item.getKategori());
        
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

        holder.binding.btnDelete.setOnClickListener(v -> listener.onDelete(item.getId()));
        holder.itemView.setOnClickListener(v -> listener.onEdit(item));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemManageMasakanBinding binding;
        public ViewHolder(ItemManageMasakanBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}