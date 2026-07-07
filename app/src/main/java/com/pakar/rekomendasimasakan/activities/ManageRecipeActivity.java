package com.pakar.rekomendasimasakan.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.pakar.rekomendasimasakan.adapters.ManageMasakanAdapter;
import com.pakar.rekomendasimasakan.database.DatabaseHelper;
import com.pakar.rekomendasimasakan.databinding.ActivityManageRecipeBinding;
import com.pakar.rekomendasimasakan.models.Masakan;
import java.util.ArrayList;
import java.util.List;

public class ManageRecipeActivity extends AppCompatActivity {

    private ActivityManageRecipeBinding binding;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        
        binding.fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, AddEditRecipeActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        List<Masakan> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_MASAKAN, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new Masakan(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();

        ManageMasakanAdapter adapter = new ManageMasakanAdapter(list, new ManageMasakanAdapter.OnActionClickListener() {
            @Override
            public void onDelete(int id) {
                // Find the name of the recipe from the list based on id
                String recipeName = "";
                for (Masakan m : list) {
                    if (m.getId() == id) {
                        recipeName = m.getNama();
                        break;
                    }
                }

                new AlertDialog.Builder(ManageRecipeActivity.this)
                        .setTitle("Konfirmasi Hapus")
                        .setMessage("Apakah Anda yakin ingin menghapus masakan: '" + recipeName + "'?")
                        .setPositiveButton("Hapus", (dialog, which) -> {
                            dbHelper.getWritableDatabase().delete(DatabaseHelper.TABLE_MASAKAN, "id_masakan = ?", new String[]{String.valueOf(id)});
                            loadData();
                        })
                        .setNegativeButton("Batal", null)
                        .show();
            }

            @Override
            public void onEdit(Masakan item) {
                Intent intent = new Intent(ManageRecipeActivity.this, AddEditRecipeActivity.class);
                intent.putExtra("masakan_id", item.getId());
                startActivity(intent);
            }
        });
        binding.rvManageRecipes.setLayoutManager(new LinearLayoutManager(this));
        binding.rvManageRecipes.setAdapter(adapter);
    }
}