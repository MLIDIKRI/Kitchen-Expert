package com.pakar.rekomendasimasakan.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pakar.rekomendasimasakan.databinding.ItemHistoryBinding;
import com.pakar.rekomendasimasakan.models.History;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<History> list;
    private boolean isSelectionMode = false;
    private OnSelectionListener listener;

    public interface OnSelectionListener {
        void onSelectionChanged();
    }

    public HistoryAdapter(List<History> list, OnSelectionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHistoryBinding binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        History item = list.get(position);
        holder.binding.tvTanggal.setText(item.getTanggal());
        holder.binding.tvHasil.setText(item.getHasilMasakan());
        holder.binding.tvBahan.setText(item.getBahanInput());

        holder.binding.cbHistory.setVisibility(isSelectionMode ? View.VISIBLE : View.GONE);
        holder.binding.cbHistory.setOnCheckedChangeListener(null);
        holder.binding.cbHistory.setChecked(item.isSelected());

        holder.binding.cbHistory.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setSelected(isChecked);
            if (listener != null) listener.onSelectionChanged();
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (!isSelectionMode) {
                setSelectionMode(true);
                item.setSelected(true);
                notifyDataSetChanged();
                if (listener != null) listener.onSelectionChanged();
            }
            return true;
        });

        holder.itemView.setOnClickListener(v -> {
            if (isSelectionMode) {
                boolean nextState = !item.isSelected();
                item.setSelected(nextState);
                holder.binding.cbHistory.setChecked(nextState);
            }
        });
    }

    public void setSelectionMode(boolean selectionMode) {
        isSelectionMode = selectionMode;
        if (!selectionMode) {
            for (History h : list) h.setSelected(false);
        }
        notifyDataSetChanged();
    }

    public boolean isSelectionMode() {
        return isSelectionMode;
    }

    public void selectAll(boolean selectAll) {
        for (History h : list) h.setSelected(selectAll);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemHistoryBinding binding;
        public ViewHolder(ItemHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}