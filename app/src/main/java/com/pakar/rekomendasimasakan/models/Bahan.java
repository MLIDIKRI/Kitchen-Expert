package com.pakar.rekomendasimasakan.models;

public class Bahan {
    private int id;
    private String nama;
    private boolean isSelected;

    public Bahan(int id, String nama) {
        this.id = id;
        this.nama = nama;
    }

    public int getId() { return id; }
    public String getNama() { return nama; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}