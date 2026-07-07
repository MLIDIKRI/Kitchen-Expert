package com.pakar.rekomendasimasakan.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.pakar.rekomendasimasakan.R;
import com.pakar.rekomendasimasakan.database.DatabaseHelper;
import com.pakar.rekomendasimasakan.databinding.ActivityManageRuleBinding;
import com.pakar.rekomendasimasakan.databinding.ItemManageRuleBinding;
import java.util.ArrayList;
import java.util.List;

public class ManageRuleActivity extends AppCompatActivity {

    private ActivityManageRuleBinding binding;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageRuleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        
        loadData();

        binding.fabAdd.setOnClickListener(v -> showDialog(null));
    }

    private void loadData() {
        List<RuleItem> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT r.id_rule, m.nama_masakan, b.nama_bahan, r.id_masakan, r.id_bahan FROM " + DatabaseHelper.TABLE_RULE + " r " +
                "JOIN " + DatabaseHelper.TABLE_MASAKAN + " m ON r.id_masakan = m.id_masakan " +
                "JOIN " + DatabaseHelper.TABLE_BAHAN + " b ON r.id_bahan = b.id_bahan " +
                "ORDER BY r.id_rule DESC";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new RuleItem(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4)));
            } while (cursor.moveToNext());
        }
        cursor.close();

        RuleAdapter adapter = new RuleAdapter(list);
        binding.rvManageRules.setLayoutManager(new LinearLayoutManager(this));
        binding.rvManageRules.setAdapter(adapter);
    }

    private void showDialog(RuleItem existingItem) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_rule, null);
        Spinner spMasakan = view.findViewById(R.id.spMasakan);
        Spinner spBahan = view.findViewById(R.id.spBahan);

        List<IdName> masakanList = getIdNames(DatabaseHelper.TABLE_MASAKAN, "id_masakan", "nama_masakan");
        List<IdName> bahanList = getIdNames(DatabaseHelper.TABLE_BAHAN, "id_bahan", "nama_bahan");

        if (masakanList.isEmpty() || bahanList.isEmpty()) {
            Toast.makeText(this, "Data masakan atau bahan masih kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        spMasakan.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, masakanList));
        spBahan.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, bahanList));

        // If editing, set selection
        if (existingItem != null) {
            for (int i = 0; i < masakanList.size(); i++) {
                if (masakanList.get(i).id == existingItem.masakanId) {
                    spMasakan.setSelection(i);
                    break;
                }
            }
            for (int i = 0; i < bahanList.size(); i++) {
                if (bahanList.get(i).id == existingItem.bahanId) {
                    spBahan.setSelection(i);
                    break;
                }
            }
        }

        new AlertDialog.Builder(this)
            .setTitle(existingItem == null ? "Tambah Rule" : "Edit Rule")
            .setView(view)
            .setPositiveButton("Simpan", (dialog, which) -> {
                int masakanId = ((IdName) spMasakan.getSelectedItem()).id;
                int bahanId = ((IdName) spBahan.getSelectedItem()).id;
                
                if (existingItem == null) {
                    if (isRuleExist(masakanId, bahanId)) {
                        Toast.makeText(this, "Rule sudah ada", Toast.LENGTH_SHORT).show();
                    } else {
                        dbHelper.getWritableDatabase().execSQL("INSERT INTO " + DatabaseHelper.TABLE_RULE + " (id_masakan, id_bahan) VALUES (?, ?)", new Object[]{masakanId, bahanId});
                        loadData();
                        Toast.makeText(this, "Rule berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    dbHelper.getWritableDatabase().execSQL("UPDATE " + DatabaseHelper.TABLE_RULE + " SET id_masakan = ?, id_bahan = ? WHERE id_rule = ?", new Object[]{masakanId, bahanId, existingItem.id});
                    loadData();
                    Toast.makeText(this, "Rule berhasil diupdate", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Batal", null)
            .show();
    }

    private boolean isRuleExist(int masakanId, int bahanId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_RULE + " WHERE id_masakan = ? AND id_bahan = ?", new String[]{String.valueOf(masakanId), String.valueOf(bahanId)});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    private List<IdName> getIdNames(String table, String idCol, String nameCol) {
        List<IdName> list = new ArrayList<>();
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT " + idCol + ", " + nameCol + " FROM " + table, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new IdName(cursor.getInt(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    private static class RuleItem {
        int id, masakanId, bahanId;
        String masakan, bahan;
        RuleItem(int id, String masakan, String bahan, int mId, int bId) {
            this.id = id; this.masakan = masakan; this.bahan = bahan;
            this.masakanId = mId; this.bahanId = bId;
        }
    }

    private static class IdName {
        int id; String name;
        IdName(int id, String name) { this.id = id; this.name = name; }
        @NonNull @Override public String toString() { return name; }
    }

    private class RuleAdapter extends RecyclerView.Adapter<RuleAdapter.ViewHolder> {
        List<RuleItem> list;
        RuleAdapter(List<RuleItem> list) { this.list = list; }
        @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemManageRuleBinding b = ItemManageRuleBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(b);
        }
        @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            RuleItem item = list.get(position);
            holder.b.tvMasakan.setText(item.masakan);
            holder.b.tvBahan.setText(item.bahan);
            
            holder.b.btnEdit.setOnClickListener(v -> showDialog(item));

            holder.b.btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(ManageRuleActivity.this)
                        .setTitle("Konfirmasi Hapus")
                        .setMessage("Apakah Anda yakin ingin menghapus rule: '" + item.masakan + " - " + item.bahan + "'?")
                        .setPositiveButton("Hapus", (dialog, which) -> {
                            dbHelper.getWritableDatabase().delete(DatabaseHelper.TABLE_RULE, "id_rule = ?", new String[]{String.valueOf(item.id)});
                            loadData();
                        })
                        .setNegativeButton("Batal", null)
                        .show();
            });
        }
        @Override public int getItemCount() { return list.size(); }
        class ViewHolder extends RecyclerView.ViewHolder {
            ItemManageRuleBinding b;
            ViewHolder(ItemManageRuleBinding b) { super(b.getRoot()); this.b = b; }
        }
    }
}