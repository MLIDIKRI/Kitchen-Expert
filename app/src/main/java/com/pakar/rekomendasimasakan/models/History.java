package com.pakar.rekomendasimasakan.models;

public class History {
    private int id;
    private String tanggal;
    private String hasilMasakan;
    private String bahanInput;
    private boolean isSelected = false;

    public History(int id, String tanggal, String hasilMasakan, String bahanInput) {
        this.id = id;
        this.tanggal = tanggal;
        this.hasilMasakan = hasilMasakan;
        this.bahanInput = bahanInput;
    }

    public int getId() { return id; }
    public String getTanggal() { return tanggal; }
    public String getHasilMasakan() { return hasilMasakan; }
    public String getBahanInput() { return bahanInput; }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}