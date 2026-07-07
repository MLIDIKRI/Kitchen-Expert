package com.pakar.rekomendasimasakan.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.pakar.rekomendasimasakan.databinding.ActivityAboutBinding;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAboutBinding binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());
    }
}