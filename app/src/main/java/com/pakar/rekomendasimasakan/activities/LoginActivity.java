package com.pakar.rekomendasimasakan.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.pakar.rekomendasimasakan.database.DatabaseHelper;
import com.pakar.rekomendasimasakan.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);

        binding.btnLogin.setOnClickListener(v -> {
            String user = binding.etUsername.getText().toString().trim();
            String pass = binding.etPassword.getText().toString().trim();

            boolean isValid = true;
            if (user.isEmpty()) {
                binding.etUsername.setError("Username tidak boleh kosong");
                isValid = false;
            }
            if (pass.isEmpty()) {
                binding.etPassword.setError("Password tidak boleh kosong");
                isValid = false;
            }

            if (!isValid) return;

            if (validate(user, pass)) {
                startActivity(new Intent(this, AdminDashboardActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Username atau password salah", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validate(String user, String pass) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_ADMIN + " WHERE username = ? AND password = ?", new String[]{user, pass});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
}