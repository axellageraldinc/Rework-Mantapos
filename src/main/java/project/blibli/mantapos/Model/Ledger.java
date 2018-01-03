package project.blibli.mantapos.Model;

import java.sql.Timestamp;

public class Ledger {
    private int id, month, year, id_resto;
    private int biaya;
    private String keperluan, tipe, quantity;
    private Timestamp dateCreated;

    public Ledger() {
    }

    public Ledger(int id_resto, int biaya, String keperluan, String tipe) {
        this.id_resto = id_resto;
        this.biaya = biaya;
        this.keperluan = keperluan;
        this.tipe = tipe;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public String getKeperluan() {
        return keperluan;
    }

    public void setKeperluan(String keperluan) {
        this.keperluan = keperluan;
    }

    public int getId_resto() {
        return id_resto;
    }

    public void setId_resto(int id_resto) {
        this.id_resto = id_resto;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getBiaya() {
        return biaya;
    }

    public void setBiaya(int biaya) {
        this.biaya = biaya;
    }
}
