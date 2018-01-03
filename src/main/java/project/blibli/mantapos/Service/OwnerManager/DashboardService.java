package project.blibli.mantapos.Service.OwnerManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import project.blibli.mantapos.Helper.GetIdResto;
import project.blibli.mantapos.NewImplementationDao.LedgerDaoImpl;
import project.blibli.mantapos.NewInterfaceDao.LedgerDao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    LedgerDao ledgerDao;

    @Autowired
    public DashboardService(LedgerDao ledgerDao){
        this.ledgerDao = ledgerDao;
    }

    public ModelAndView getMappingDashboard(Authentication authentication) throws SQLException {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("owner-manager/dashboard");
        int idResto = GetIdResto.getIdRestoBasedOnUsernameTerkait(authentication.getName());
        List<String> dummyLedgerList = getPemasukkanBulanan(idResto, LocalDate.now().getYear());
        mav.addObject("dummyList", dummyLedgerList);
        mav.addObject("total_pengeluaran", getTotalPengeluaranAllTime(idResto));
        mav.addObject("total_pemasukkan", getTotalPemasukkanAllTime(idResto));
        mav.addObject("username", authentication.getName());
        return mav;
    }

    private int getTotalPengeluaranAllTime(int idResto) throws SQLException {
        String condition = "id_resto=" + idResto +
                " AND tipe='kredit'";
        int totalPengeluaranAllTime = ledgerDao.getTotal(condition);
        return totalPengeluaranAllTime;
    }

    private int getTotalPemasukkanAllTime(int idResto) throws SQLException {
        String condition = "id_resto=" + idResto +
                " AND tipe='debit'";
        int totalPemasukkanAllTime = ledgerDao.getTotal(condition);
        return totalPemasukkanAllTime;
    }

    private List<String> getPemasukkanBulanan(int idResto,
                                             int year) throws SQLException {
        String condition="id_resto=" + idResto +
                " AND EXTRACT(YEAR FROM date_created)=" + year +
                " AND tipe='debit'" +
                " GROUP BY EXTRACT(MONTH FROM date_created), tipe ORDER BY EXTRACT(MONTH FROM date_created), tipe ASC";
        List<String> ledgerList = ledgerDao.getAllBulananUntukDashboard(condition);
        return ledgerList;
    }
}
