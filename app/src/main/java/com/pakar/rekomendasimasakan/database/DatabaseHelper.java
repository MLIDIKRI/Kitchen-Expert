package com.pakar.rekomendasimasakan.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.pakar.rekomendasimasakan.models.Masakan;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "kitchen_expert.db";
    private static final int DATABASE_VERSION = 5;

    // Table names
    public static final String TABLE_MASAKAN = "masakan";
    public static final String TABLE_BAHAN = "bahan";
    public static final String TABLE_RULE = "rule";
    public static final String TABLE_HISTORY = "history";
    public static final String TABLE_ADMIN = "admin";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables
        db.execSQL("CREATE TABLE " + TABLE_ADMIN + " (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_BAHAN + " (id_bahan INTEGER PRIMARY KEY AUTOINCREMENT, nama_bahan TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_MASAKAN + " (id_masakan INTEGER PRIMARY KEY AUTOINCREMENT, nama_masakan TEXT, resep TEXT, cara_masak TEXT, kategori TEXT, image_path TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_RULE + " (id_rule INTEGER PRIMARY KEY AUTOINCREMENT, id_masakan INTEGER, id_bahan INTEGER)");
        db.execSQL("CREATE TABLE " + TABLE_HISTORY + " (id_history INTEGER PRIMARY KEY AUTOINCREMENT, tanggal TEXT, hasil_masakan TEXT, bahan_input TEXT, id_masakan INTEGER)");

        // Initial Data
        db.execSQL("INSERT INTO " + TABLE_ADMIN + " (username, password) VALUES ('admin', 'admin123')");

        // Seed 41 Ingredients
        String[] bahans = {
            "Nasi Putih", "Telur", "Ayam", "Kecap Manis", "Bawang Merah",
            "Bawang Putih", "Cabai", "Mie Telur", "Kol", "Sawi",
            "Tauge", "Daun Bawang", "Kunyit", "Jahe", "Nangka Muda",
            "Santan", "Gula Merah", "Labu Siam", "Terong", "Tempe",
            "Kemiri", "Daging Sapi", "Kluwek", "Kacang Tanah", "Bayam",
            "Kacang Panjang", "Ketumbar", "Tahu", "Tepung", "Udang",
            "Petis", "Kulit Lumpia", "Rebung", "Kulit Bawang", "Teh",
            "Garam", "Kentang", "Ati Ayam", "Wortel", "Tomat",
            "Kuah Kaldu"
        };
        for (String b : bahans) {
            db.execSQL("INSERT INTO " + TABLE_BAHAN + " (nama_bahan) VALUES ('" + b + "')");
        }

        // Seed 20 Masakan
        insertMasakan(db, "Nasi Goreng Jawa", "Nasi putih, Telur, Ayam suwir, Kecap manis, Bawang merah, Bawang putih, Cabai", "Tumis bumbu halus, masukkan telur, ayam, dan nasi. Tambahkan kecap lalu aduk sampai matang.", "Utama", "");
        insertMasakan(db, "Mie Jawa Goreng", "Mie telur, Telur, Ayam, Kol, Sawi, Kecap", "Tumis bumbu, masukkan telur dan ayam, tambahkan mie serta sayuran lalu masak hingga matang.", "Utama", "");
        insertMasakan(db, "Soto Kudus", "Ayam, Tauge, Daun bawang, Bawang putih, Kunyit, Jahe", "Rebus ayam, buat kuah dengan bumbu tumis, lalu sajikan bersama tauge dan ayam suwir.", "Sup", "");
        insertMasakan(db, "Gudeg Jogja", "Nangka muda, Santan, Telur, Ayam, Gula merah", "Masak nangka bersama santan, gula merah, dan rempah hingga empuk.", "Utama", "");
        insertMasakan(db, "Sayur Lodeh Jawa", "Santan, Labu siam, Terong, Tempe, Cabai", "Masak sayuran bersama santan dan bumbu hingga matang.", "Sayur", "");
        insertMasakan(db, "Opor Ayam", "Ayam, Santan, Kunyit, Kemiri, Bawang putih", "Tumis bumbu, masukkan ayam dan santan, masak hingga empuk.", "Utama", "");
        insertMasakan(db, "Rawon", "Daging sapi, Kluwek, Bawang merah, Bawang putih, Tauge", "Masak daging bersama bumbu kluwek sampai kuah berwarna hitam.", "Sup", "");
        insertMasakan(db, "Pecel Madiun", "Kacang tanah, Bayam, Tauge, Kacang panjang, Cabai", "Rebus sayuran, buat sambal kacang lalu siram di atas sayuran.", "Sayur", "");
        insertMasakan(db, "Tempe Bacem", "Tempe, Gula merah, Kecap, Bawang putih, Ketumbar", "Rebus tempe dengan bumbu hingga meresap kemudian goreng.", "Lauk", "");
        insertMasakan(db, "Tahu Bacem", "Tahu, Gula merah, Kecap, Bawang putih", "Masak tahu dengan bumbu hingga air habis kemudian goreng.", "Lauk", "");
        insertMasakan(db, "Ayam Geprek Jawa", "Ayam, Tepung, Cabai, Bawang putih", "Goreng ayam tepung lalu geprek bersama sambal.", "Utama", "");
        insertMasakan(db, "Bakmi Godog Jawa", "Mie, Telur, Ayam, Kol, Kuah kaldu", "Masak mie bersama kuah, ayam, telur, dan sayuran.", "Sup", "");
        insertMasakan(db, "Tahu Gimbal Semarang", "Tahu, Udang, Kol, Kacang tanah, Petis", "Goreng bahan, campur dengan saus kacang.", "Cemilan", "");
        insertMasakan(db, "Lumpia Semarang", "Kulit lumpia, Rebung, Ayam, Telur", "Masak isian, bungkus kulit lumpia, lalu goreng.", "Cemilan", "");
        insertMasakan(db, "Sate Ayam Madura", "Ayam, Kacang tanah, Kecap, Cabai", "Tusuk ayam, bakar, lalu sajikan dengan saus kacang.", "Utama", "");
        insertMasakan(db, "Telur Pindang", "Telur, Kulit bawang, Teh, Garam", "Rebus telur bersama rempah hingga warna meresap.", "Lauk", "");
        insertMasakan(db, "Sambal Goreng Kentang Ati", "Kentang, Ati ayam, Cabai, Santan", "Goreng kentang, masak bersama ati dan sambal.", "Lauk", "");
        insertMasakan(db, "Gado-Gado Jawa", "Tahu, Tempe, Sayuran, Kacang tanah", "Rebus sayuran, goreng tahu tempe, lalu campurkan saus kacang.", "Sayur", "");
        insertMasakan(db, "Bakwan Sayur", "Tepung, Wortel, Kol, Daun bawang", "Campur semua bahan kemudian goreng sampai matang.", "Cemilan", "");
        insertMasakan(db, "Telur Balado", "Telur, Cabai, Bawang merah, Tomat", "Rebus telur, goreng sebentar, lalu masak dengan sambal.", "Lauk", "");

        // Seed Rules (id_masakan, id_bahan)
        int[][] rules = {
            {1, 1}, {1, 2}, {1, 3}, {1, 4}, {1, 5}, {1, 6}, {1, 7},
            {2, 8}, {2, 2}, {2, 3}, {2, 9}, {2, 10}, {2, 4},
            {3, 3}, {3, 11}, {3, 12}, {3, 6}, {3, 13}, {3, 14},
            {4, 15}, {4, 16}, {4, 2}, {4, 3}, {4, 17},
            {5, 16}, {5, 18}, {5, 19}, {5, 20}, {5, 7},
            {6, 3}, {6, 16}, {6, 13}, {6, 21}, {6, 6},
            {7, 22}, {7, 23}, {7, 5}, {7, 6}, {7, 11},
            {8, 24}, {8, 25}, {8, 11}, {8, 26}, {8, 7},
            {9, 20}, {9, 17}, {9, 4}, {9, 6}, {9, 27},
            {10, 28}, {10, 17}, {10, 4}, {10, 6},
            {11, 3}, {11, 29}, {11, 7}, {11, 6},
            {12, 8}, {12, 2}, {12, 3}, {12, 9}, {12, 41},
            {13, 28}, {13, 30}, {13, 9}, {13, 24}, {13, 31},
            {14, 32}, {14, 33}, {14, 3}, {14, 2},
            {15, 3}, {15, 24}, {15, 4}, {15, 7},
            {16, 2}, {16, 34}, {16, 35}, {16, 36},
            {17, 37}, {17, 38}, {17, 7}, {17, 16},
            {18, 28}, {18, 20}, {18, 25}, {18, 24},
            {19, 29}, {19, 39}, {19, 9}, {19, 12},
            {20, 2}, {20, 7}, {20, 5}, {20, 40}
        };

        for (int[] r : rules) {
            db.execSQL("INSERT INTO " + TABLE_RULE + " (id_masakan, id_bahan) VALUES (" + r[0] + ", " + r[1] + ")");
        }
    }

    private void insertMasakan(SQLiteDatabase db, String nama, String resep, String cara, String kategori, String imagePath) {
        ContentValues cv = new ContentValues();
        cv.put("nama_masakan", nama);
        cv.put("resep", resep);
        cv.put("cara_masak", cara);
        cv.put("kategori", kategori);
        cv.put("image_path", imagePath);
        db.insert(TABLE_MASAKAN, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADMIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BAHAN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MASAKAN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RULE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }

    public List<Masakan> getAllMasakan() {
        List<Masakan> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MASAKAN, null);

        if (cursor.moveToFirst()) {
            do {
                Masakan masakan = new Masakan(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id_masakan")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nama_masakan")),
                        cursor.getString(cursor.getColumnIndexOrThrow("resep")),
                        cursor.getString(cursor.getColumnIndexOrThrow("cara_masak")),
                        cursor.getString(cursor.getColumnIndexOrThrow("kategori")),
                        cursor.getString(cursor.getColumnIndexOrThrow("image_path"))
                );
                masakan.setBahanIds(getBahanForMasakan(masakan.getId()));
                list.add(masakan);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<Integer> getBahanForMasakan(int masakanId) {
        List<Integer> ids = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id_bahan FROM " + TABLE_RULE + " WHERE id_masakan = ?", new String[]{String.valueOf(masakanId)});
        if (cursor.moveToFirst()) {
            do {
                ids.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ids;
    }
}
