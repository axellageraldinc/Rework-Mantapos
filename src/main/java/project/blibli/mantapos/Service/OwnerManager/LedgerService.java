package project.blibli.mantapos.Service.OwnerManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import project.blibli.mantapos.Helper.GetIdResto;
import project.blibli.mantapos.Model.Ledger;
import project.blibli.mantapos.NewImplementationDao.LedgerDaoImpl;
import project.blibli.mantapos.NewImplementationDao.SaldoAkhirDaoImpl;
import project.blibli.mantapos.NewImplementationDao.SaldoAwalDaoImpl;
import project.blibli.mantapos.NewInterfaceDao.LedgerDao;
import project.blibli.mantapos.NewInterfaceDao.SaldoDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class LedgerService {

    LedgerDao ledgerDao;
    SaldoDao saldoAwalDao, saldoAkhirDao;

    @Autowired
    public LedgerService(LedgerDao ledgerDao,
                         @Qualifier("saldoAwalDaoImpl") SaldoDao saldoAwalDao,
                         @Qualifier("saldoAkhirDaoImpl") SaldoDao saldoAkhirDao){
        this.ledgerDao = ledgerDao;
        this.saldoAwalDao = saldoAwalDao;
        this.saldoAkhirDao = saldoAkhirDao;
    }

    public ModelAndView getMappingChooseRangeLedger(Authentication authentication) throws SQLException {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("owner-manager/pilih-range-ledger");
        int idResto = GetIdResto.getIdRestoBasedOnUsernameTerkait(authentication.getName());
        List<Integer> bulanList = new ArrayList<>();
        for (int i=0; i<12; i++){
            bulanList.add(i+1);
        }
        mav.addObject("bulanList", bulanList);
//        mav.addObject("monthAndYearList", getListBulanDanTahun(idResto));
        mav.addObject("username", authentication.getName());
        return mav;
    }

    public ModelAndView postMappingLihatLedger(Authentication authentication,
                                               String skalaLedger,
                                               int bulanTerpilih,
                                               int tahunTerpilih,
                                               String ledgerCustomWaktuAwal,
                                               String ledgerCustomWaktuAkhir) throws SQLException {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("owner-manager/ledger");
        List<Ledger> ledgerList = new ArrayList<>();
        int saldoAwalBulanTerpilih = 0, saldoAkhirBulanTerpilih=0, mutasi=0, totalDebit=0, totalKredit=0;
        int idResto = GetIdResto.getIdRestoBasedOnUsernameTerkait(authentication.getName());
        String skalaLedgerPassToHtml = null;
        if(skalaLedger.equals("harian")){
            saldoAwalBulanTerpilih = getSaldoAkhirBulanLaluSebagaiSaldoAwalBulanTerpilih(idResto, bulanTerpilih, tahunTerpilih);
            totalDebit = getTotalDebitDalamSebulan(idResto, bulanTerpilih, tahunTerpilih);
            totalKredit = getTotalKreditDalamSebulan(idResto, bulanTerpilih, tahunTerpilih);
            saldoAkhirBulanTerpilih = hitungSaldoAkhirSekarang(saldoAwalBulanTerpilih, totalDebit, totalKredit);
            mutasi = hitungMutasi(saldoAkhirBulanTerpilih, saldoAwalBulanTerpilih);
            ledgerList = getLedgerHarianDalamSebulan(idResto, bulanTerpilih, tahunTerpilih);
            skalaLedgerPassToHtml = "HARIAN";
        } else if(skalaLedger.equals("bulanan")){
            int bulanAwal = getBulanPalingAwalDalamTahun(idResto, tahunTerpilih);
            saldoAwalBulanTerpilih = getSaldoAkhirBulanLaluSebagaiSaldoAwalBulanTerpilih(idResto, bulanAwal, tahunTerpilih);
            totalDebit = getTotalDebitDalamSetahun(idResto, tahunTerpilih);
            totalKredit = getTotalKreditDalamSetahun(idResto, tahunTerpilih);
            saldoAkhirBulanTerpilih = hitungSaldoAkhirSekarang(saldoAwalBulanTerpilih, totalDebit, totalKredit);
            mutasi = hitungMutasi(saldoAkhirBulanTerpilih, saldoAwalBulanTerpilih);
            ledgerList = getLedgerBulananDalamSetahun(idResto, tahunTerpilih);
            skalaLedgerPassToHtml = "BULANAN";
        } else if(skalaLedger.equals("tahunan")){
            saldoAwalBulanTerpilih = getSaldoAwalBanget(idResto);
            totalDebit = getTotalDebitDariAwalBanget(idResto);
            totalKredit = getTotalKreditDariAwalBanget(idResto);
            saldoAkhirBulanTerpilih = hitungSaldoAkhirSekarang(saldoAwalBulanTerpilih, totalDebit, totalKredit);
            mutasi = hitungMutasi(saldoAkhirBulanTerpilih, saldoAwalBulanTerpilih);
            ledgerList = getLedgerTahunan(idResto);
            skalaLedgerPassToHtml = "TAHUNAN";
        } else {
            String[] dateAwalSplit = ledgerCustomWaktuAwal.split("-");
            int tanggalAwal = Integer.parseInt(dateAwalSplit[2]);
            int bulanAwal = Integer.parseInt(dateAwalSplit[1]);
            int tahunAwal = Integer.parseInt(dateAwalSplit[0]);
            String[] dateAkhirSplit = ledgerCustomWaktuAkhir.split("-");
            int tanggalAkhir = Integer.parseInt(dateAkhirSplit[2]);
            int bulanAkhir = Integer.parseInt(dateAkhirSplit[1]);
            int tahunAkhir = Integer.parseInt(dateAkhirSplit[0]);

            saldoAwalBulanTerpilih = getSaldoAkhirBulanLaluSebagaiSaldoAwalBulanTerpilih(idResto, bulanAwal, tahunAwal);
            totalDebit = getTotalDebitCustom(idResto, tanggalAwal, bulanAwal, tahunAwal, tanggalAkhir, bulanAkhir, tahunAkhir);
            totalKredit = getTotalKreditCustom(idResto, tanggalAwal, bulanAwal, tahunAwal, tanggalAkhir, bulanAkhir, tahunAkhir);
            saldoAkhirBulanTerpilih = hitungSaldoAkhirSekarang(saldoAwalBulanTerpilih, totalDebit, totalKredit);
            mutasi = hitungMutasi(saldoAkhirBulanTerpilih, saldoAwalBulanTerpilih);
            ledgerList = getLedgerCustomRange(idResto, tanggalAwal, bulanAwal, tahunAwal, tanggalAkhir, bulanAkhir, tahunAkhir);
            skalaLedgerPassToHtml = "CUSTOM";
        }
        mav.addObject("saldo_awal", saldoAwalBulanTerpilih);
        mav.addObject("total_debit", totalDebit);
        mav.addObject("total_kredit", totalKredit);
        mav.addObject("saldo_akhir", saldoAkhirBulanTerpilih);
        mav.addObject("mutasi", mutasi);
        mav.addObject("ledgerList", ledgerList);
        mav.addObject("skala_ledger", skalaLedgerPassToHtml);
        return mav;
    }

    private int getTotalDebitDalamSebulan(int idResto,
                                     int month,
                                     int year) throws SQLException {
        String condition = "id_resto=" + idResto +
                " AND EXTRACT(MONTH FROM date_created)=" + month + " AND EXTRACT(YEAR FROM date_created)=" + year +
                " AND tipe='debit'";
        int totalDebitDalamSebulan = ledgerDao.getTotal(condition);
        return totalDebitDalamSebulan;
    }

    private int getTotalKreditDalamSebulan(int idResto,
                                      int month,
                                      int year) throws SQLException {
        String condition = "id_resto=" + idResto +
                " AND EXTRACT(MONTH FROM date_created)=" + month + " AND EXTRACT(YEAR FROM date_created)=" + year +
                " AND tipe='kredit'";
        int totalKreditDalamSebulan = ledgerDao.getTotal(condition);
        return totalKreditDalamSebulan;
    }

    private int getTotalDebitDalamSetahun(int idResto,
                                          int year) throws SQLException {
        String condition = "id_resto=" + idResto +
                " AND EXTRACT(YEAR FROM date_created)=" + year +
                " AND tipe='debit'";
        int totalDebitDalamSetahun = ledgerDao.getTotal(condition);
        return totalDebitDalamSetahun;
    }

    private int getTotalKreditDalamSetahun(int idResto,
                                           int year) throws SQLException {
        String condition = "id_resto=" + idResto +
                " AND EXTRACT(YEAR FROM date_created)=" + year +
                " AND tipe='kredit'";
        int totalKreditDalamSetahun = ledgerDao.getTotal(condition);
        return totalKreditDalamSetahun;
    }

    private int getTotalDebitDariAwalBanget(int idResto) throws SQLException {
        String condition="id_resto=" + idResto +
                " AND tipe='debit'";
        int totalDebit = ledgerDao.getTotal(condition);
        return totalDebit;
    }

    private int getTotalKreditDariAwalBanget(int idResto) throws SQLException {
        String condition="id_resto=" + idResto +
                " AND tipe='kredit'";
        int totalKredit = ledgerDao.getTotal(condition);
        return totalKredit;
    }

    private int getTotalDebitCustom(int idResto,
                                    int tanggalAwal,
                                    int bulanAwal,
                                    int tahunAwal,
                                    int tanggalAkhir,
                                    int bulanAkhir,
                                    int tahunAkhir) throws SQLException {
        String condition = "id_resto=" + idResto +
                " AND EXTRACT(DAY FROM date_created) BETWEEN " + tanggalAwal + " AND " + tanggalAkhir +
                " AND EXTRACT(MONTH FROM date_created) BETWEEN " + bulanAwal + " AND " + bulanAkhir +
                " AND EXTRACT(YEAR FROM date_created) BETWEEN " + tahunAwal + " AND " + tahunAkhir +
                " AND tipe='debit'";
        int totalDebitCustom = ledgerDao.getTotal(condition);
        return totalDebitCustom;
    }

    private int getTotalKreditCustom(int idResto,
                                     int tanggalAwal,
                                     int bulanAwal,
                                     int tahunAwal,
                                     int tanggalAkhir,
                                     int bulanAkhir,
                                     int tahunAkhir) throws SQLException {
        String condition = "id_resto=" + idResto +
                " AND EXTRACT(DAY FROM date_created) BETWEEN " + tanggalAwal + " AND " + tanggalAkhir +
                " AND EXTRACT(MONTH FROM date_created) BETWEEN " + bulanAwal + " AND " + bulanAkhir +
                " AND EXTRACT(YEAR FROM date_created) BETWEEN " + tahunAwal + " AND " + tahunAkhir +
                " AND tipe='kredit'";
        int totalKreditCustom = ledgerDao.getTotal(condition);
        return totalKreditCustom;
    }

    private int getSaldoAwalBanget(int idResto) throws SQLException {
        int saldoAwalBanget = saldoAwalDao.getOne("id_resto=" + idResto).getSaldo();
        return saldoAwalBanget;
    }

    private int getSaldoAkhirBulanLaluSebagaiSaldoAwalBulanTerpilih(int idResto,
                                                                    int bulanTerpilih,
                                                                    int tahunTerpilih) throws SQLException {
        int saldoAwal;
        if(bulanTerpilih==1){
            saldoAwal = saldoAkhirDao.getOne("id_resto=" + idResto +
                        " AND EXTRACT(MONTH FROM date_created)=12" +
                        " AND EXTRACT(YEAR FROM date_created)=" + (tahunTerpilih-1)).getSaldo();
        } else {
            saldoAwal = saldoAkhirDao.getOne("id_resto=" + idResto +
                    " AND EXTRACT(MONTH FROM date_created)=" + (bulanTerpilih-1) +
                    " AND EXTRACT(YEAR FROM date_created)=" + tahunTerpilih).getSaldo();
        }
        //TODO : Lha kalau memang saldo bulan lalunya 0 piye? masa trus diputuskan harus pakai saldo awal?
        if(saldoAwal==0){
            saldoAwal = saldoAwalDao.getOne("id_resto=" + idResto).getSaldo();
        }
        return saldoAwal;
    }

    private List<Ledger> getLedgerHarianDalamSebulan(int idResto,
                                         int bulanTerpilih,
                                         int tahunTerpilih) throws SQLException {
        String condition = "id_resto=" + idResto +
                " AND EXTRACT(MONTH FROM date_created)=" + bulanTerpilih + " AND EXTRACT(YEAR FROM date_created)=" + tahunTerpilih;
        List<Ledger> ledgerList = ledgerDao.getAll(condition);
        return ledgerList;
    }

    private List<Ledger> getLedgerMingguanDalamSebulan(int idResto,
                                                       int bulanTerpilih,
                                                       int tahunTerpilih){
//        List<Ledger> ledgerList = ledgerDao.getLedgerMingguan(idResto, bulanTerpilih, tahunTerpilih);
        return null;
    }

    private List<Ledger> getLedgerBulananDalamSetahun(int idResto,
                                          int tahunTerpilih) throws SQLException {
        String condition="id_resto=" + idResto +
                " AND EXTRACT(YEAR FROM date_created)=" + tahunTerpilih +
                " GROUP BY EXTRACT(MONTH FROM date_created), tipe ORDER BY EXTRACT(MONTH FROM date_created), tipe ASC";
        List<Ledger> ledgerList = ledgerDao.getAllBulanan(condition);
        return ledgerList;
    }

    private List<Ledger> getLedgerTahunan(int idResto) throws SQLException {
        String condition="id_resto=" + idResto +
                " GROUP BY EXTRACT(YEAR FROM date_created), tipe ORDER BY EXTRACT(YEAR FROM date_created), tipe ASC";
        List<Ledger> ledgerList = ledgerDao.getAllTahunan(condition);
        return ledgerList;
    }

    private List<Ledger> getLedgerCustomRange(int idResto,
                                              int tanggalAwal,
                                              int bulanAwal,
                                              int tahunAwal,
                                              int tanggalAkhir,
                                              int bulanAkhir,
                                              int tahunAkhir) throws SQLException {
        String condition = "id_resto=" + idResto +
                " AND EXTRACT(DAY FROM date_created) BETWEEN " + tanggalAwal + " AND " + tanggalAkhir +
                " AND EXTRACT(MONTH FROM date_created) BETWEEN " + bulanAwal + " AND " + bulanAkhir +
                " AND EXTRACT(YEAR FROM date_created) BETWEEN " + tahunAwal + " AND " + tahunAkhir;
        List<Ledger> ledgerList = ledgerDao.getAll(condition);
        return ledgerList;
    }

    private int hitungSaldoAkhirSekarang(int saldoAwal,
                                         int totalDebit,
                                         int totalKredit){
        int saldoAkhirSekarang = saldoAwal+totalDebit-totalKredit;
        return saldoAkhirSekarang;
    }

    private int hitungMutasi(int saldoAkhir,
                             int saldoAwal){
        int mutasi = saldoAkhir-saldoAwal;
        return mutasi;
    }

    private int getBulanPalingAwalDalamTahun(int idResto,
                                             int yearTerpilih) throws SQLException {
        String condition="id_resto=" + idResto +
                " AND EXTRACT(YEAR FROM date_created)=" + yearTerpilih;
        int bulanAwal = saldoAkhirDao.getBulanAwal(condition);
        System.out.println("Bulan awal : " + bulanAwal);
        return bulanAwal;
    }

}
