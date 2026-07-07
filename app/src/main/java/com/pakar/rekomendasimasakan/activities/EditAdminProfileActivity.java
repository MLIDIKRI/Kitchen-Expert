package com.pakar.rekomendasimasakan.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.pakar.rekomendasimasakan.database.DatabaseHelper;
import com.pakar.rekomendasimasakan.databinding.ActivityEditAdminProfileBinding;

public class EditAdminProfileActivity extends AppCompatActivity {

    private ActivityEditAdminProfileBinding binding;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditAdminProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        loadCurrentAdmin();

        binding.btnUpdate.setOnClickListener(v -> updateProfile());
    }

    private void loadCurrentAdmin() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT username, password FROM " + DatabaseHelper.TABLE_ADMIN + " LIMIT 1", null);
        if (cursor.moveToFirst()) {
            binding.etUsername.setText(cursor.getString(0));
            binding.etPassword.setText(cursor.getString(1));
        }
        cursor.close();
    }

    private void updateProfile() {
        String newUser = binding.etUsername.getText().toString().trim();
        String newPass = binding.etPassword.getText().toString().trim();

        boolean isValid = true;
        if (newUser.isEmpty()) {
            binding.etUsername.setError("Username tidak boleh kosong");
            isValid = false;
        }
        if (newPass.isEmpty()) {
            binding.etPassword.setError("Password tidak boleh kosong");
            isValid = false;
        }

        if (!isValid) return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", newUser);
        cv.put("password", newPass);

        int result = db.update(DatabaseHelper.TABLE_ADMIN, cv, null, null);
        if (result > 0) {
            Toast.makeText(this, "Profil admin berhasil diperbarui", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Gagal memperbarui profil", Toast.LENGTH_SHORT).show();
        }
    }
}