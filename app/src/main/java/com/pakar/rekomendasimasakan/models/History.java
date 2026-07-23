package com.pakar.rekomendasimasakan.models;

public class History {
    private int id;
    private String tanggal;
    private String hasilMasakan;
    private String bahanInput;
    private int masakanId;
    private boolean isSelected = false;

    public History(int id, String tanggal, String hasilMasakan, String bahanInput, int masakanId) {
        this.id = id;
        this.tanggal = tanggal;
        this.hasilMasakan = hasilMasakan;
        this.bahanInput = bahanInput;
        this.masakanId = masakanId;
    }

    public int getId() { return id; }
    public String getTanggal() { return tanggal; }
    public String getHasilMasakan() { return hasilMasakan; }
    public String getBahanInput() { return bahanInput; }
    public int getMasakanId() { return masakanId; }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}