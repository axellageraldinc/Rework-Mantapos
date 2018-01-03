package project.blibli.mantapos.Service.OwnerManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import project.blibli.mantapos.Helper.GetIdResto;
import project.blibli.mantapos.Model.Ledger;
import project.blibli.mantapos.Model.Saldo;
import project.blibli.mantapos.NewImplementationDao.*;
import project.blibli.mantapos.NewInterfaceDao.LedgerDao;
import project.blibli.mantapos.NewInterfaceDao.SaldoDao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OutcomeService {

    LedgerDao ledgerDao;
    SaldoDao saldoAwalDao, saldoAkhirDao;

    @Autowired
    public OutcomeService(LedgerDao ledgerDao,
                          @Qualifier("saldoAwalDaoImpl") SaldoDao saldoAwalDao,
                          @Qualifier("saldoAkhirDaoImpl") SaldoDao saldoAkhirDao){
        this.ledgerDao = ledgerDao;
        this.saldoAwalDao = saldoAwalDao;
        this.saldoAkhirDao = saldoAkhirDao;
    }

    int itemPerPage=5;
    int bulan = LocalDate.now().getMonthValue();
    int tahun = LocalDate.now().getYear();

    public ModelAndView getMappingOutcome(Authentication authentication,
                                          Integer page) throws SQLException {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("owner-manager/outcome");
        if(page == null){
            page=1;
        }
        int idResto = GetIdResto.getIdRestoBasedOnUsernameTerkait(authentication.getName());
        double jumlahBanyakOutcome = getCountOutcome(idResto);
        double jumlahPage = Math.ceil(jumlahBanyakOutcome/itemPerPage); //Menghitung jumlahPage, didapatkan dari jumlah row outcome dibagi dengan item per page. Misal jumlah row outcome 12 dan item per page 5, maka akan ada 3 page dengan item-nya 5,5,3
        List<Integer> pageList = new ArrayList<>(); //List untuk menampung nilai-nilai page (jika jumlahPage = 3, maka di pageList ini isinya 1,2,3
        for (int i=1; i<=jumlahPage; i++){
            pageList.add(i);
        }
        mav.addObject("outcomeList", getPengeluaranHarian(idResto, page));
        mav.addObject("pageNo", page);
        mav.addObject("pageList", pageList);
        mav.addObject("username", authentication.getName());
        return mav;
    }

    public ModelAndView postMappingOutcome(Authentication authentication,
                                           Ledger ledger) throws SQLException {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:/outcome");
        int idResto = GetIdResto.getIdRestoBasedOnUsernameTerkait(authentication.getName());
        ledger.setId_resto(idResto);
        ledger.setTipe("kredit");
        insertNewPengeluaran(ledger);
        updateSaldoAkhir(idResto);
        return mav;
    }

    private void insertNewPengeluaran(Ledger ledger) throws SQLException {
        ledgerDao.insert(ledger);
    }

    private void updateSaldoAkhir(int idResto) throws SQLException {
        Saldo saldo = new Saldo();
        saldo.setId_resto(idResto);
        int saldoAwal;
        if(bulan==1){
            saldoAwal = saldoAkhirDao.getOne("id_resto=" + idResto +
                    " AND EXTRACT(MONTH FROM date_created)=12 AND EXTRACT(YEAR FROM date_created)=" + (tahun-1)).getSaldo();
        } else {
            saldoAwal = saldoAkhirDao.getOne("id_resto=" + idResto +
                    " AND EXTRACT(MONTH FROM date_created)=" + (bulan-1) + " AND EXTRACT(YEAR FROM date_created)=" + tahun).getSaldo();
        }
        if(saldoAwal==0){
            saldoAwal = saldoAwalDao.getOne("id_resto=" + idResto).getSaldo();
        }
        String condition = "id_resto=" + idResto +
                " AND EXTRACT(MONTH FROM date_created)=" + bulan + " AND EXTRACT(YEAR FROM date_created)=" + tahun; //between 2018-01-01 00:00:00 AND 2018-01-31 23:59:59
        int totalPemasukkanBulanIni = ledgerDao.getTotal(condition + " AND tipe='debit'");
        int totalPengeluaranBulanIni = ledgerDao.getTotal(condition + " AND tipe='kredit'");
        saldo.setSaldo(saldoAwal + totalPemasukkanBulanIni - totalPengeluaranBulanIni);
        if(saldoAkhirDao.count("id_resto=" + idResto)==0){
            saldoAkhirDao.insert(saldo);
        } else{
            saldoAkhirDao.update(saldo, "id_resto=" + idResto +
                    " AND EXTRACT(MONTH FROM date_created)=" + bulan + " AND EXTRACT(YEAR FROM date_created)=" + tahun);
        }
    }

    private List<Ledger> getPengeluaranHarian(int idResto,
                                              int page) throws SQLException {
        List<Ledger> pengeluaranHarianList = ledgerDao.getAll("id_resto=" + idResto + " AND tipe='kredit'" + " LIMIT " + itemPerPage + " OFFSET " + (page-1)*itemPerPage);
        return pengeluaranHarianList;
    }

    private int getCountOutcome(int idResto) throws SQLException {
        int countOutcome = ledgerDao.count("id_resto=" + idResto);
        return countOutcome;
    }

}
