package com.pakar.rekomendasimasakan.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
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
        if (selectedBahan == null) finish();

        processRecommendation(selectedBahan);
    }

    private void processRecommendation(List<Integer> selectedBahan) {
        List<Masakan> allMasakan = getAllMasakan();
        List<Masakan> recommendations = ForwardChainingEngine.getRecommendations(selectedBahan, allMasakan);

        if (!recommendations.isEmpty()) {
            saveHistory(selectedBahan, recommendations.get(0).getNama());
        }

        MasakanAdapter adapter = new MasakanAdapter(recommendations, item -> {
            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra("masakan_id", item.getId());
            startActivity(intent);
        });

        binding.rvResults.setLayoutManager(new LinearLayoutManager(this));
        binding.rvResults.setAdapter(adapter);
    }

    private void saveHistory(List<Integer> selectedBahan, String bestMatch) {
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
        db.insert(DatabaseHelper.TABLE_HISTORY, null, cv);
    }

    private List<Masakan> getAllMasakan() {
        List<Masakan> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_MASAKAN, null);
        if (cursor.moveToFirst()) {
            do {
                Masakan m = new Masakan(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5)
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