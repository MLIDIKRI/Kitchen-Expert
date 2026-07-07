package com.pakar.rekomendasimasakan.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pakar.rekomendasimasakan.databinding.ItemIngredientBinding;
import com.pakar.rekomendasimasakan.models.Bahan;
import java.util.ArrayList;
import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    private List<Bahan> list;

    public IngredientAdapter(List<Bahan> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemIngredientBinding binding = ItemIngredientBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bahan item = list.get(position);
        
        // Clear listener first to avoid recycling issues
        holder.binding.checkBox.setOnCheckedChangeListener(null);
        
        holder.binding.checkBox.setText(item.getNama());
        holder.binding.checkBox.setChecked(item.isSelected());
        
        // Re-set listener after setChecked
        holder.binding.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setSelected(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public List<Integer> getSelectedIds() {
        List<Integer> ids = new ArrayList<>();
        for (Bahan b : list) {
            if (b.isSelected()) ids.add(b.getId());
        }
        return ids;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemIngredientBinding binding;
        public ViewHolder(ItemIngredientBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}