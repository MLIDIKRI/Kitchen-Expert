package com.pakar.rekomendasimasakan.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.pakar.rekomendasimasakan.R;
import com.pakar.rekomendasimasakan.adapters.ManageIngredientAdapter;
import com.pakar.rekomendasimasakan.database.DatabaseHelper;
import com.pakar.rekomendasimasakan.databinding.ActivityManageIngredientBinding;
import com.pakar.rekomendasimasakan.models.Bahan;
import java.util.ArrayList;
import java.util.List;

public class ManageIngredientActivity extends AppCompatActivity {

    private ActivityManageIngredientBinding binding;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageIngredientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        
        loadData();

        binding.fabAdd.setOnClickListener(v -> showDialog(null));
    }

    private void loadData() {
        List<Bahan> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_BAHAN, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new Bahan(cursor.getInt(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        cursor.close();

        ManageIngredientAdapter adapter = new ManageIngredientAdapter(list, new ManageIngredientAdapter.OnActionClickListener() {
            @Override
            public void onDelete(int id) {
                // Find the name of the ingredient from the list based on id
                String ingredientName = "";
                for (Bahan b : list) {
                    if (b.getId() == id) {
                        ingredientName = b.getNama();
                        break;
                    }
                }

                new AlertDialog.Builder(ManageIngredientActivity.this)
                        .setTitle("Konfirmasi Hapus")
                        .setMessage("Apakah Anda yakin ingin menghapus bahan: '" + ingredientName + "'?")
                        .setPositiveButton("Hapus", (dialog, which) -> {
                            dbHelper.getWritableDatabase().delete(DatabaseHelper.TABLE_BAHAN, "id_bahan = ?", new String[]{String.valueOf(id)});
                            loadData();
                        })
                        .setNegativeButton("Batal", null)
                        .show();
            }

            @Override
            public void onEdit(Bahan item) {
                showDialog(item);
            }
        });
        binding.rvManageIngredients.setLayoutManager(new LinearLayoutManager(this));
        binding.rvManageIngredients.setAdapter(adapter);
    }

    private void showDialog(Bahan item) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_ingredient, null);
        EditText et = view.findViewById(R.id.etNamaBahan);
        
        if (item != null) {
            et.setText(item.getNama());
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle(item == null ? "Tambah Bahan" : "Edit Bahan")
            .setView(view)
            .setPositiveButton("Simpan", null)
            .setNegativeButton("Batal", null)
            .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nama = et.getText().toString().trim();
            if (nama.isEmpty()) {
                et.setError("Nama bahan tidak boleh kosong");
            } else {
                if (item == null) {
                    dbHelper.getWritableDatabase().execSQL("INSERT INTO " + DatabaseHelper.TABLE_BAHAN + " (nama_bahan) VALUES (?)", new String[]{nama});
                } else {
                    dbHelper.getWritableDatabase().execSQL("UPDATE " + DatabaseHelper.TABLE_BAHAN + " SET nama_bahan = ? WHERE id_bahan = ?", new Object[]{nama, item.getId()});
                }
                loadData();
                dialog.dismiss();
            }
        });
    }
}