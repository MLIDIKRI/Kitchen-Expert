package com.pakar.rekomendasimasakan.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.pakar.rekomendasimasakan.R;
import com.pakar.rekomendasimasakan.adapters.HistoryAdapter;
import com.pakar.rekomendasimasakan.database.DatabaseHelper;
import com.pakar.rekomendasimasakan.databinding.ActivityHistoryBinding;
import com.pakar.rekomendasimasakan.models.History;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private ActivityHistoryBinding binding;
    private DatabaseHelper dbHelper;
    private List<History> historyList = new ArrayList<>();
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);
        binding.toolbar.setNavigationOnClickListener(v -> {
            if (adapter != null && adapter.isSelectionMode()) {
                exitSelectionMode();
            } else {
                finish();
            }
        });

        binding.cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (adapter != null) adapter.selectAll(isChecked);
        });

        binding.btnDeleteSelected.setOnClickListener(v -> confirmDelete());

        setupBottomNavigation();
        loadData();
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setSelectedItemId(R.id.nav_history);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(HistoryActivity.this, MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_history) {
                return true;
            } else if (id == R.id.nav_about) {
                startActivity(new Intent(HistoryActivity.this, AboutActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.bottomNavigation.setSelectedItemId(R.id.nav_history);
    }

    private void loadData() {
        historyList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_HISTORY + " ORDER BY id_history DESC", null);
        if (cursor.moveToFirst()) {
            do {
                historyList.add(new History(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id_history")),
                        cursor.getString(cursor.getColumnIndexOrThrow("tanggal")),
                        cursor.getString(cursor.getColumnIndexOrThrow("hasil_masakan")),
                        cursor.getString(cursor.getColumnIndexOrThrow("bahan_input")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("id_masakan"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter = new HistoryAdapter(historyList, () -> {
            // Update UI based on selection
            if (adapter.isSelectionMode()) {
                binding.layoutSelection.setVisibility(View.VISIBLE);
            } else {
                binding.layoutSelection.setVisibility(View.GONE);
            }
        }, history -> {
            Intent intent = new Intent(HistoryActivity.this, RecipeDetailActivity.class);
            intent.putExtra("masakan_id", history.getMasakanId());
            startActivity(intent);
        });
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.rvHistory.setAdapter(adapter);
    }

    private void exitSelectionMode() {
        if (adapter != null) {
            adapter.setSelectionMode(false);
            binding.layoutSelection.setVisibility(View.GONE);
            binding.cbSelectAll.setChecked(false);
        }
    }

    private void confirmDelete() {
        List<History> toDelete = new ArrayList<>();
        for (History h : historyList) {
            if (h.isSelected()) toDelete.add(h);
        }

        if (toDelete.isEmpty()) return;

        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Hapus")
                .setMessage("Apakah Anda yakin ingin menghapus " + toDelete.size() + " riwayat terpilih?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    for (History h : toDelete) {
                        db.delete(DatabaseHelper.TABLE_HISTORY, "id_history = ?", new String[]{String.valueOf(h.getId())});
                    }
                    exitSelectionMode();
                    loadData();
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        if (adapter != null && adapter.isSelectionMode()) {
            exitSelectionMode();
        } else {
            super.onBackPressed();
        }
    }
}