package com.pakar.rekomendasimasakan.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "kitchen_expert.db";
    private static final int DATABASE_VERSION = 1;

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
        db.execSQL("CREATE TABLE " + TABLE_HISTORY + " (id_history INTEGER PRIMARY KEY AUTOINCREMENT, tanggal TEXT, hasil_masakan TEXT, bahan_input TEXT)");

        // Initial Data
        db.execSQL("INSERT INTO " + TABLE_ADMIN + " (username, password) VALUES ('admin', 'admin123')");

        // Seed 30 Ingredients
        String[] bahans = {
            "Bawang Merah", "Bawang Putih", "Cabai Merah", "Cabai Rawit", "Garam", 
            "Gula", "Minyak Goreng", "Telur", "Ayam", "Daging Sapi", 
            "Ikan", "Tempe", "Tahu", "Kangkung", "Bayam", 
            "Wortel", "Kentang", "Santan", "Kecap Manis", "Saus Tiram", 
            "Tepung Terigu", "Tepung Tapioka", "Jagung", "Tomat", "Daun Bawang", 
            "Seledri", "Kemiri", "Kunyit", "Jahe", "Lengkuas"
        };
        for (String b : bahans) {
            db.execSQL("INSERT INTO " + TABLE_BAHAN + " (nama_bahan) VALUES ('" + b + "')");
        }

        // Seed 10 Masakan
        insertMasakan(db, "Nasi Goreng Specials", "Nasi, Telur, Bumbu", "Tumis bumbu, masukkan telur, masukkan nasi.", "Utama");
        insertMasakan(db, "Soto Ayam Lamongan", "Ayam, Kunyit, Soun", "Rebus ayam dengan bumbu, sajikan dengan soun.", "Sup");
        insertMasakan(db, "Rendang Padang", "Daging Sapi, Santan, Rempah", "Masak daging dengan santan hingga kering.", "Daging");
        insertMasakan(db, "Gado-Gado Jakarta", "Sayuran, Kacang, Tahu", "Rebus sayuran, sajikan dengan bumbu kacang.", "Salad");
        insertMasakan(db, "Sayur Sop", "Wortel, Kentang, Ayam", "Rebus sayuran dan ayam dalam kaldu bening.", "Sup");
        insertMasakan(db, "Opor Ayam", "Ayam, Santan, Kemiri", "Masak ayam dalam kuah santan kuning.", "Daging");
        insertMasakan(db, "Tumis Kangkung", "Kangkung, Bawang, Cabai", "Tumis kangkung dengan bawang dan cabai.", "Sayuran");
        insertMasakan(db, "Semur Daging", "Daging Sapi, Kecap, Kentang", "Masak daging dengan kecap manis dan kentang.", "Daging");
        insertMasakan(db, "Perkedel Kentang", "Kentang, Telur, Seledri", "Haluskan kentang, campur telur, lalu goreng.", "Lauk");
        insertMasakan(db, "Bakwan Jagung", "Jagung, Tepung, Daun Bawang", "Campur jagung dengan adonan tepung, goreng.", "Camilan");

        // Seed 40+ Rules (id_masakan, id_bahan)
        // Nasi Goreng (1): Bawang Merah(1), Bawang Putih(2), Cabai(3), Garam(5), Telur(8), Kecap(19)
        int[][] rules = {
            {1, 1}, {1, 2}, {1, 3}, {1, 5}, {1, 8}, {1, 19},
            {2, 9}, {2, 28}, {2, 2}, {2, 5}, {2, 25},
            {3, 10}, {3, 18}, {3, 27}, {3, 28}, {3, 30},
            {4, 12}, {4, 13}, {4, 16}, {4, 27}, {4, 5},
            {5, 16}, {5, 17}, {5, 9}, {5, 25}, {5, 26},
            {6, 9}, {6, 18}, {6, 27}, {6, 28}, {6, 1},
            {7, 14}, {7, 1}, {7, 2}, {7, 3}, {7, 20},
            {8, 10}, {8, 19}, {8, 17}, {8, 2}, {8, 1},
            {9, 17}, {9, 8}, {9, 26}, {9, 2},
            {10, 23}, {10, 21}, {10, 25}, {10, 7}
        };

        for (int[] r : rules) {
            db.execSQL("INSERT INTO " + TABLE_RULE + " (id_masakan, id_bahan) VALUES (" + r[0] + ", " + r[1] + ")");
        }
    }

    private void insertMasakan(SQLiteDatabase db, String nama, String resep, String cara, String kategori) {
        ContentValues cv = new ContentValues();
        cv.put("nama_masakan", nama);
        cv.put("resep", resep);
        cv.put("cara_masak", cara);
        cv.put("kategori", kategori);
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
}