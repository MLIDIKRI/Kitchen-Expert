package com.pakar.rekomendasimasakan.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.pakar.rekomendasimasakan.R;
import com.pakar.rekomendasimasakan.database.DatabaseHelper;
import com.pakar.rekomendasimasakan.databinding.ActivityManageRuleBinding;
import com.pakar.rekomendasimasakan.databinding.ItemManageRuleBinding;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Join masakan with rules and bahans
        String query = "SELECT m.id_masakan, m.nama_masakan, b.nama_bahan, b.id_bahan " +
                "FROM " + DatabaseHelper.TABLE_MASAKAN + " m " +
                "JOIN " + DatabaseHelper.TABLE_RULE + " r ON m.id_masakan = r.id_masakan " +
                "JOIN " + DatabaseHelper.TABLE_BAHAN + " b ON r.id_bahan = b.id_bahan " +
                "ORDER BY m.nama_masakan ASC";
        
        Cursor cursor = db.rawQuery(query, null);
        Map<Integer, RuleItem> map = new LinkedHashMap<>();
        
        if (cursor.moveToFirst()) {
            do {
                int mId = cursor.getInt(0);
                String mName = cursor.getString(1);
                String bName = cursor.getString(2);
                int bId = cursor.getInt(3);
                
                if (!map.containsKey(mId)) {
                    map.put(mId, new RuleItem(mName, "", mId, new ArrayList<>()));
                }
                
                RuleItem item = map.get(mId);
                if (item != null) {
                    item.bahanIds.add(bId);
                    if (item.bahan.isEmpty()) item.bahan = bName;
                    else item.bahan += ", " + bName;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        
        List<RuleItem> list = new ArrayList<>(map.values());

        RuleAdapter adapter = new RuleAdapter(list);
        binding.rvManageRules.setLayoutManager(new LinearLayoutManager(this));
        binding.rvManageRules.setAdapter(adapter);
    }

    private void showDialog(RuleItem existingItem) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_rule, null);
        Spinner spMasakan = view.findViewById(R.id.spMasakan);
        RecyclerView rvBahanMulti = view.findViewById(R.id.rvBahanMulti);
        CheckBox cbSelectAll = view.findViewById(R.id.cbSelectAll);
        TextInputEditText etSearchBahan = view.findViewById(R.id.etSearchBahan);

        List<IdName> masakanList = getIdNames(DatabaseHelper.TABLE_MASAKAN, "id_masakan", "nama_masakan");
        List<IdName> bahanList = getIdNames(DatabaseHelper.TABLE_BAHAN, "id_bahan", "nama_bahan");

        if (masakanList.isEmpty() || bahanList.isEmpty()) {
            Toast.makeText(this, "Data masakan atau bahan masih kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        spMasakan.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, masakanList));
        
        List<SelectableBahan> selectableBahans = new ArrayList<>();
        for (IdName b : bahanList) {
            boolean isSelected = false;
            if (existingItem != null) {
                isSelected = existingItem.bahanIds.contains(b.id);
            }
            selectableBahans.add(new SelectableBahan(b, isSelected));
        }

        SelectBahanAdapter bahanAdapter = new SelectBahanAdapter(selectableBahans);
        rvBahanMulti.setLayoutManager(new LinearLayoutManager(this));
        rvBahanMulti.setAdapter(bahanAdapter);

        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> bahanAdapter.selectAll(isChecked));

        etSearchBahan.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                bahanAdapter.filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        if (existingItem != null) {
            for (int i = 0; i < masakanList.size(); i++) {
                if (masakanList.get(i).id == existingItem.masakanId) {
                    spMasakan.setSelection(i);
                    break;
                }
            }
            spMasakan.setEnabled(false);
        }

        new AlertDialog.Builder(this)
            .setTitle(existingItem == null ? "Tambah Rule" : "Edit Rule")
            .setView(view)
            .setPositiveButton("Simpan", (dialog, which) -> {
                int masakanId = ((IdName) spMasakan.getSelectedItem()).id;
                List<Integer> selectedBahanIds = bahanAdapter.getSelectedIds();

                if (selectedBahanIds.isEmpty()) {
                    Toast.makeText(this, "Pilih minimal satu bahan", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.beginTransaction();
                try {
                    db.delete(DatabaseHelper.TABLE_RULE, "id_masakan = ?", new String[]{String.valueOf(masakanId)});
                    for (Integer bId : selectedBahanIds) {
                        db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_RULE + " (id_masakan, id_bahan) VALUES (?, ?)", new Object[]{masakanId, bId});
                    }
                    db.setTransactionSuccessful();
                    Toast.makeText(this, "Berhasil memperbarui rule masakan", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Terjadi kesalahan saat menyimpan", Toast.LENGTH_SHORT).show();
                } finally {
                    db.endTransaction();
                }
                loadData();
            })
            .setNegativeButton("Batal", null)
            .show();
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
        int masakanId;
        String masakan, bahan;
        List<Integer> bahanIds;
        RuleItem(String masakan, String bahan, int mId, List<Integer> bIds) {
            this.masakan = masakan; this.bahan = bahan;
            this.masakanId = mId; this.bahanIds = bIds;
        }
    }

    private static class IdName {
        int id; String name;
        IdName(int id, String name) { this.id = id; this.name = name; }
        @NonNull @Override public String toString() { return name; }
    }

    private static class SelectableBahan {
        IdName data; boolean isSelected;
        SelectableBahan(IdName data, boolean isSelected) { this.data = data; this.isSelected = isSelected; }
    }

    private static class SelectBahanAdapter extends RecyclerView.Adapter<SelectBahanAdapter.ViewHolder> {
        private final List<SelectableBahan> allItems;
        private final List<SelectableBahan> filteredItems;

        SelectBahanAdapter(List<SelectableBahan> list) {
            this.allItems = list;
            this.filteredItems = new ArrayList<>(list);
        }

        void filter(String query) {
            filteredItems.clear();
            if (query.isEmpty()) {
                filteredItems.addAll(allItems);
            } else {
                String q = query.toLowerCase();
                for (SelectableBahan item : allItems) {
                    if (item.data.name.toLowerCase().contains(q)) {
                        filteredItems.add(item);
                    }
                }
            }
            notifyDataSetChanged();
        }

        void selectAll(boolean isSelected) {
            for (SelectableBahan item : allItems) item.isSelected = isSelected;
            notifyDataSetChanged();
        }

        List<Integer> getSelectedIds() {
            List<Integer> ids = new ArrayList<>();
            for (SelectableBahan item : allItems) if (item.isSelected) ids.add(item.data.id);
            return ids;
        }

        @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            CheckBox cb = new CheckBox(parent.getContext());
            cb.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            cb.setPadding(16, 8, 16, 8);
            return new ViewHolder(cb);
        }
        @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SelectableBahan item = filteredItems.get(position);
            holder.cb.setText(item.data.name);
            holder.cb.setOnCheckedChangeListener(null);
            holder.cb.setChecked(item.isSelected);
            holder.cb.setOnCheckedChangeListener((v, isChecked) -> item.isSelected = isChecked);
        }
        @Override public int getItemCount() { return filteredItems.size(); }
        static class ViewHolder extends RecyclerView.ViewHolder {
            CheckBox cb;
            ViewHolder(View v) { super(v); this.cb = (CheckBox) v; }
        }
    }

    private class RuleAdapter extends RecyclerView.Adapter<RuleAdapter.ViewHolder> {
        private final List<RuleItem> list;
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
                        .setMessage("Apakah Anda yakin ingin menghapus semua rule untuk masakan: '" + item.masakan + "'?")
                        .setPositiveButton("Hapus", (dialog, which) -> {
                            dbHelper.getWritableDatabase().delete(DatabaseHelper.TABLE_RULE, "id_masakan = ?", new String[]{String.valueOf(item.masakanId)});
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
