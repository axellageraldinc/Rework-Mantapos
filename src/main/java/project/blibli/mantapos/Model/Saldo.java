package project.blibli.mantapos.Model;

public class Saldo {
    private int id_resto, tanggal, month, year;
    private int saldo;
    private String tipe_saldo;

    public String getTipe_saldo() {
        return tipe_saldo;
    }

    public void setTipe_saldo(String tipe_saldo) {
        this.tipe_saldo = tipe_saldo;
    }

    public int getId_resto() {
        return id_resto;
    }

    public void setId_resto(int id_resto) {
        this.id_resto = id_resto;
    }

    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }

    public int getTanggal() {
        return tanggal;
    }

    public void setTanggal(int tanggal) {
        this.tanggal = tanggal;
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
}
