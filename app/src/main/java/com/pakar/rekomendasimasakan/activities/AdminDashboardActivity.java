package com.pakar.rekomendasimasakan.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.pakar.rekomendasimasakan.databinding.ActivityAdminDashboardBinding;

public class AdminDashboardActivity extends AppCompatActivity {

    private ActivityAdminDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnManageMasakan.setOnClickListener(v -> {
            startActivity(new Intent(this, ManageRecipeActivity.class));
        });

        binding.btnManageBahan.setOnClickListener(v -> {
            startActivity(new Intent(this, ManageIngredientActivity.class));
        });

        binding.btnManageRule.setOnClickListener(v -> {
            startActivity(new Intent(this, ManageRuleActivity.class));
        });

        binding.btnEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, EditAdminProfileActivity.class));
        });

        binding.btnLogout.setOnClickListener(v -> {
            finish();
        });
    }
}