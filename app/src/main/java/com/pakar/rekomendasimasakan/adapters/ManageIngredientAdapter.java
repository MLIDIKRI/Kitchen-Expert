package com.pakar.rekomendasimasakan.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pakar.rekomendasimasakan.databinding.ItemManageIngredientBinding;
import com.pakar.rekomendasimasakan.models.Bahan;
import java.util.List;

public class ManageIngredientAdapter extends RecyclerView.Adapter<ManageIngredientAdapter.ViewHolder> {

    private List<Bahan> list;
    private OnActionClickListener listener;

    public interface OnActionClickListener {
        void onDelete(int id);
        void onEdit(Bahan item);
    }

    public ManageIngredientAdapter(List<Bahan> list, OnActionClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemManageIngredientBinding binding = ItemManageIngredientBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bahan item = list.get(position);
        holder.binding.tvNama.setText(item.getNama());
        holder.binding.btnDelete.setOnClickListener(v -> listener.onDelete(item.getId()));
        holder.itemView.setOnClickListener(v -> listener.onEdit(item));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemManageIngredientBinding binding;
        public ViewHolder(ItemManageIngredientBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}