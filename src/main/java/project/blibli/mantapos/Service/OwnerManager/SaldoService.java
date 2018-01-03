package project.blibli.mantapos.Service.OwnerManager;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import project.blibli.mantapos.Helper.GetIdResto;
import project.blibli.mantapos.Model.Saldo;
import project.blibli.mantapos.NewImplementationDao.SaldoAkhirDaoImpl;
import project.blibli.mantapos.NewImplementationDao.SaldoAwalDaoImpl;

import java.sql.SQLException;
import java.util.List;

@Service
public class SaldoService {

    SaldoAwalDaoImpl saldoAwalDao = new SaldoAwalDaoImpl();
    SaldoAkhirDaoImpl saldoAkhirDao = new SaldoAkhirDaoImpl();
    int itemPerPage=5;

    public ModelAndView getMappingSaldoAwal(Authentication authentication) throws SQLException {
        ModelAndView mav = new ModelAndView();
        int idResto = GetIdResto.getIdRestoBasedOnUsernameTerkait(authentication.getName());
        mav.addObject("saldoAwal", getSaldoAwal(idResto));
        mav.setViewName("owner-manager/saldo-awal");
        mav.addObject("username", authentication.getName());
        return mav;
    }

    public ModelAndView postMappingAddSaldoAwal(Authentication authentication,
                                                Saldo saldo) throws SQLException {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:/saldo");
        saldo.setTipe_saldo("awal");
        int idResto = GetIdResto.getIdRestoBasedOnUsernameTerkait(authentication.getName());
        insertSaldoAwal(idResto, saldo);
        return mav;
    }

    private void insertSaldoAwal(int idResto,
                                 Saldo saldo) throws SQLException {
        saldo.setId_resto(idResto);
        saldoAwalDao.insert(saldo);
    }

    private int getSaldoAwalRestoran(int idResto) throws SQLException {
        int saldoAwal = saldoAwalDao.getOne("id_resto=" + idResto).getSaldo();
        return saldoAwal;
    }

    private Saldo getSaldoAwal(int idResto) throws SQLException {
        Saldo saldo = saldoAwalDao.getOne("id_resto=" + idResto);
        return saldo;
    }

}
