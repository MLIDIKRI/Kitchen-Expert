package com.pakar.rekomendasimasakan.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.pakar.rekomendasimasakan.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.cardPilihBahan.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SelectIngredientActivity.class));
        });

        binding.cardHistory.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, HistoryActivity.class));
        });

        binding.cardAbout.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        });

        binding.btnAdminLogin.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });
    }
}