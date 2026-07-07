package com.pakar.rekomendasimasakan.models;

import java.util.List;

public class Masakan {
    private int id;
    private String nama;
    private String resep;
    private String caraMasak;
    private String kategori;
    private String imagePath;
    private List<Integer> bahanIds;
    private double matchPercentage;
    private int matchCount;

    public Masakan(int id, String nama, String resep, String caraMasak, String kategori, String imagePath) {
        this.id = id;
        this.nama = nama;
        this.resep = resep;
        this.caraMasak = caraMasak;
        this.kategori = kategori;
        this.imagePath = imagePath;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getNama() { return nama; }
    public String getResep() { return resep; }
    public String getCaraMasak() { return caraMasak; }
    public String getKategori() { return kategori; }
    public String getImagePath() { return imagePath; }
    public List<Integer> getBahanIds() { return bahanIds; }
    public void setBahanIds(List<Integer> bahanIds) { this.bahanIds = bahanIds; }
    public double getMatchPercentage() { return matchPercentage; }
    public void setMatchPercentage(double matchPercentage) { this.matchPercentage = matchPercentage; }
    public int getMatchCount() { return matchCount; }
    public void setMatchCount(int matchCount) { this.matchCount = matchCount; }
}