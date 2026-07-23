package com.pakar.rekomendasimasakan.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.pakar.rekomendasimasakan.database.DatabaseHelper;
import com.pakar.rekomendasimasakan.databinding.ActivityRecipeDetailBinding;

public class RecipeDetailActivity extends AppCompatActivity {

    private ActivityRecipeDetailBinding binding;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecipeDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        int masakanId = getIntent().getIntExtra("masakan_id", -1);
        if (masakanId != -1) {
            loadDetail(masakanId);
        }
    }

    private void loadDetail(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_MASAKAN + " WHERE id_masakan = ?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            String nama = cursor.getString(cursor.getColumnIndexOrThrow("nama_masakan"));
            String resep = cursor.getString(cursor.getColumnIndexOrThrow("resep"));
            String cara = cursor.getString(cursor.getColumnIndexOrThrow("cara_masak"));
            String kategori = cursor.getString(cursor.getColumnIndexOrThrow("kategori"));

            binding.toolbarLayout.setTitle(nama);
            binding.tvDetailNama.setText(nama);
            binding.tvDetailBahan.setText(resep);
            binding.tvDetailCara.setText(cara);

            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image_path"));
            if (imagePath != null && !imagePath.isEmpty()) {
                java.io.File imgFile = new java.io.File(imagePath);
                if (imgFile.exists()) {
                    binding.imgRecipe.setImageBitmap(android.graphics.BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
                    binding.imgRecipe.setColorFilter(null);
                }
            }
        }
        cursor.close();
    }
}