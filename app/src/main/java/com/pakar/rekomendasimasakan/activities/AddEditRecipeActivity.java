package com.pakar.rekomendasimasakan.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.pakar.rekomendasimasakan.database.DatabaseHelper;
import com.pakar.rekomendasimasakan.databinding.ActivityAddEditRecipeBinding;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AddEditRecipeActivity extends AppCompatActivity {

    private ActivityAddEditRecipeBinding binding;
    private DatabaseHelper dbHelper;
    private int masakanId = -1;
    private String selectedImagePath = "";

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    saveImageToInternalStorage(imageUri);
                }
            }
    );

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getExtras() != null) {
                    Bitmap photo = (Bitmap) result.getData().getExtras().get("data");
                    if (photo != null) {
                        saveBitmapToInternalStorage(photo);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        masakanId = getIntent().getIntExtra("masakan_id", -1);
        if (masakanId != -1) {
            binding.toolbar.setTitle("Edit Masakan");
            loadData();
        } else {
            binding.toolbar.setTitle("Tambah Masakan");
        }

        binding.btnPickImage.setOnClickListener(v -> showImageSourceDialog());
        binding.btnSave.setOnClickListener(v -> save());
    }

    private void showImageSourceDialog() {
        String[] options = {"Kamera", "Galeri", "Hapus Gambar"};
        new AlertDialog.Builder(this)
                .setTitle("Pilih Sumber Gambar")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                            androidx.core.app.ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 100);
                        } else {
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            cameraLauncher.launch(cameraIntent);
                        }
                    } else if (which == 1) {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        galleryLauncher.launch(galleryIntent);
                    } else {
                        removeImage();
                    }
                })
                .show();
    }

    private void removeImage() {
        selectedImagePath = "";
        binding.imgPreview.setImageResource(android.R.drawable.ic_menu_gallery);
        binding.imgPreview.setAlpha(0.3f);
        binding.imgPreview.setColorFilter(android.graphics.Color.GRAY);
        Toast.makeText(this, "Gambar dihapus", Toast.LENGTH_SHORT).show();
    }

    private void saveImageToInternalStorage(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            saveBitmapToInternalStorage(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal memproses gambar", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveBitmapToInternalStorage(Bitmap bitmap) {
        try {
            String fileName = "recipe_" + System.currentTimeMillis() + ".jpg";
            File file = new File(getFilesDir(), fileName);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();

            selectedImagePath = file.getAbsolutePath();
            binding.imgPreview.setImageBitmap(bitmap);
            binding.imgPreview.setAlpha(1.0f);
            binding.imgPreview.setColorFilter(null);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal menyimpan gambar", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_MASAKAN + " WHERE id_masakan = ?", new String[]{String.valueOf(masakanId)});
        if (cursor.moveToFirst()) {
            binding.etNama.setText(cursor.getString(1));
            binding.etResep.setText(cursor.getString(2));
            binding.etCaraMasak.setText(cursor.getString(3));
            binding.etKategori.setText(cursor.getString(4));
            selectedImagePath = cursor.getString(5);
            
            if (selectedImagePath != null && !selectedImagePath.isEmpty()) {
                File imgFile = new File(selectedImagePath);
                if (imgFile.exists()) {
                    binding.imgPreview.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
                    binding.imgPreview.setAlpha(1.0f);
                    binding.imgPreview.setColorFilter(null);
                } else {
                    binding.imgPreview.setAlpha(0.3f);
                    binding.imgPreview.setColorFilter(android.graphics.Color.GRAY);
                }
            } else {
                binding.imgPreview.setAlpha(0.3f);
                binding.imgPreview.setColorFilter(android.graphics.Color.GRAY);
            }
        }
        cursor.close();
    }

    private void save() {
        String nama = binding.etNama.getText().toString().trim();
        String resep = binding.etResep.getText().toString().trim();
        String cara = binding.etCaraMasak.getText().toString().trim();
        String kategori = binding.etKategori.getText().toString().trim();

        boolean isValid = true;
        if (nama.isEmpty()) {
            binding.etNama.setError("Nama masakan tidak boleh kosong");
            isValid = false;
        }
        if (resep.isEmpty()) {
            binding.etResep.setError("Resep tidak boleh kosong");
            isValid = false;
        }
        if (cara.isEmpty()) {
            binding.etCaraMasak.setError("Cara masak tidak boleh kosong");
            isValid = false;
        }
        if (kategori.isEmpty()) {
            binding.etKategori.setError("Kategori tidak boleh kosong");
            isValid = false;
        }

        if (!isValid) return;

        ContentValues cv = new ContentValues();
        cv.put("nama_masakan", nama);
        cv.put("resep", resep);
        cv.put("cara_masak", cara);
        cv.put("kategori", kategori);
        cv.put("image_path", selectedImagePath);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (masakanId == -1) {
            db.insert(DatabaseHelper.TABLE_MASAKAN, null, cv);
            Toast.makeText(this, "Berhasil menambah masakan", Toast.LENGTH_SHORT).show();
        } else {
            db.update(DatabaseHelper.TABLE_MASAKAN, cv, "id_masakan = ?", new String[]{String.valueOf(masakanId)});
            Toast.makeText(this, "Berhasil mengupdate masakan", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}