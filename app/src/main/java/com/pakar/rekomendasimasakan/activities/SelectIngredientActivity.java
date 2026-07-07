package com.pakar.rekomendasimasakan.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.pakar.rekomendasimasakan.adapters.IngredientAdapter;
import com.pakar.rekomendasimasakan.database.DatabaseHelper;
import com.pakar.rekomendasimasakan.databinding.ActivitySelectIngredientBinding;
import com.pakar.rekomendasimasakan.models.Bahan;
import java.util.ArrayList;
import java.util.List;

public class SelectIngredientActivity extends AppCompatActivity {

    private ActivitySelectIngredientBinding binding;
    private DatabaseHelper dbHelper;
    private IngredientAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectIngredientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);
        
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        
        loadBahan();

        binding.btnAnalyze.setOnClickListener(v -> {
            ArrayList<Integer> selectedIds = (ArrayList<Integer>) adapter.getSelectedIds();
            if (selectedIds.isEmpty()) {
                android.widget.Toast.makeText(this, "Silakan pilih minimal 1 bahan", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, RecommendationActivity.class);
            intent.putIntegerArrayListExtra("selected_bahan", selectedIds);
            startActivity(intent);
        });
    }

    private void loadBahan() {
        List<Bahan> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_BAHAN, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new Bahan(cursor.getInt(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter = new IngredientAdapter(list);
        binding.rvIngredients.setLayoutManager(new LinearLayoutManager(this));
        binding.rvIngredients.setAdapter(adapter);
    }
}