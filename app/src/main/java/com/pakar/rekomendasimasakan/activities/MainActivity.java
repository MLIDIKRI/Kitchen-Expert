package com.pakar.rekomendasimasakan.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.pakar.rekomendasimasakan.R;
import com.pakar.rekomendasimasakan.adapters.InspirationAdapter;
import com.pakar.rekomendasimasakan.database.DatabaseHelper;
import com.pakar.rekomendasimasakan.databinding.ActivityMainBinding;
import com.pakar.rekomendasimasakan.models.Masakan;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);

        setupClickListeners();
        setupInspirationList();
        setupBottomNavigation();
    }

    private void setupClickListeners() {
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

    private void setupInspirationList() {
        List<Masakan> masakanList = dbHelper.getAllMasakan();
        Collections.shuffle(masakanList);

        InspirationAdapter adapter = new InspirationAdapter(masakanList, masakan -> {
            Intent intent = new Intent(MainActivity.this, RecipeDetailActivity.class);
            intent.putExtra("masakan_id", masakan.getId());
            startActivity(intent);
        });

        binding.rvInspirasi.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvInspirasi.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_about) {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
    }
}