package com.pakar.rekomendasimasakan.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.chip.Chip;
import com.pakar.rekomendasimasakan.R;
import com.pakar.rekomendasimasakan.adapters.MasakanAdapter;
import com.pakar.rekomendasimasakan.database.DatabaseHelper;
import com.pakar.rekomendasimasakan.databinding.ActivityRecommendationBinding;
import com.pakar.rekomendasimasakan.models.Masakan;
import com.pakar.rekomendasimasakan.utils.ForwardChainingEngine;
import java.util.ArrayList;
import java.util.List;

public class RecommendationActivity extends AppCompatActivity {

    private ActivityRecommendationBinding binding;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecommendationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        List<Integer> selectedBahan = getIntent().getIntegerArrayListExtra("selected_bahan");
        if (selectedBahan == null) {
            finish();
            return;
        }

        setupUI(selectedBahan);
        processRecommendation(selectedBahan);
        setupBottomNavigation();
    }

    private void setupUI(List<Integer> selectedBahan) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        for (Integer id : selectedBahan) {
            Cursor c = db.rawQuery("SELECT nama_bahan FROM " + DatabaseHelper.TABLE_BAHAN + " WHERE id_bahan = ?", new String[]{String.valueOf(id)});
            if (c.moveToFirst()) {
                Chip chip = new Chip(this);
                chip.setText(c.getString(0));
                chip.setChipBackgroundColorResource(R.color.surfaceContainerHigh);
                chip.setTextColor(ContextCompat.getColor(this, R.color.onSurfaceVariant));
                chip.setChipStrokeWidth(0);
                binding.cgIngredients.addView(chip);
            }
            c.close();
        }

        binding.btnUbahBahan.setOnClickListener(v -> finish());
        binding.btnSearchOther.setOnClickListener(v -> finish());

    }

    private void processRecommendation(List<Integer> selectedBahan) {
        List<Masakan> allMasakan = getAllMasakan();
        List<Masakan> recommendations = ForwardChainingEngine.getRecommendations(selectedBahan, allMasakan);

        binding.tvResultCount.setText(getString(R.string.resep_ditemukan, recommendations.size()));

        if (!recommendations.isEmpty()) {
            saveHistory(selectedBahan, recommendations.get(0).getNama(), recommendations.get(0).getId());
        }

        MasakanAdapter adapter = new MasakanAdapter(recommendations, item -> {
            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra("masakan_id", item.getId());
            startActivity(intent);
        });

        binding.rvResults.setLayoutManager(new LinearLayoutManager(this));
        binding.rvResults.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(RecommendationActivity.this, MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(RecommendationActivity.this, HistoryActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_about) {
                startActivity(new Intent(RecommendationActivity.this, AboutActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    private void saveHistory(List<Integer> selectedBahan, String bestMatch, int masakanId) {
        StringBuilder bahanNames = new StringBuilder();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (int i = 0; i < selectedBahan.size(); i++) {
            Cursor c = db.rawQuery("SELECT nama_bahan FROM " + DatabaseHelper.TABLE_BAHAN + " WHERE id_bahan = ?", new String[]{String.valueOf(selectedBahan.get(i))});
            if (c.moveToFirst()) {
                bahanNames.append(c.getString(0));
                if (i < selectedBahan.size() - 1) bahanNames.append(", ");
            }
            c.close();
        }

        android.content.ContentValues cv = new android.content.ContentValues();
        cv.put("tanggal", new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm", java.util.Locale.getDefault()).format(new java.util.Date()));
        cv.put("hasil_masakan", bestMatch);
        cv.put("bahan_input", bahanNames.toString());
        cv.put("id_masakan", masakanId);
        db.insert(DatabaseHelper.TABLE_HISTORY, null, cv);
    }

    private List<Masakan> getAllMasakan() {
        List<Masakan> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_MASAKAN, null);
        if (cursor.moveToFirst()) {
            do {
                Masakan m = new Masakan(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id_masakan")),
                    cursor.getString(cursor.getColumnIndexOrThrow("nama_masakan")),
                    cursor.getString(cursor.getColumnIndexOrThrow("resep")),
                    cursor.getString(cursor.getColumnIndexOrThrow("cara_masak")),
                    cursor.getString(cursor.getColumnIndexOrThrow("kategori")),
                    cursor.getString(cursor.getColumnIndexOrThrow("image_path"))
                );
                m.setBahanIds(getBahanForMasakan(m.getId()));
                list.add(m);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    private List<Integer> getBahanForMasakan(int masakanId) {
        List<Integer> ids = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id_bahan FROM " + DatabaseHelper.TABLE_RULE + " WHERE id_masakan = ?", new String[]{String.valueOf(masakanId)});
        if (cursor.moveToFirst()) {
            do {
                ids.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ids;
    }
}