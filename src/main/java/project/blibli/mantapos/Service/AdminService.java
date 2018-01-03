package project.blibli.mantapos.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import project.blibli.mantapos.Helper.GetIdResto;
import project.blibli.mantapos.Model.Restoran;
import project.blibli.mantapos.Model.User;
import project.blibli.mantapos.NewImplementationDao.RestoranDaoImpl;
import project.blibli.mantapos.NewImplementationDao.UserDaoImpl;
import project.blibli.mantapos.NewInterfaceDao.RestoranDao;
import project.blibli.mantapos.NewInterfaceDao.UserDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {

    RestoranDao restoranDao;
    UserDao userDao;
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public AdminService(RestoranDao restoranDao, UserDao userDao, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.restoranDao = restoranDao;
        this.userDao = userDao;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    int itemPerPage=5;

    public ModelAndView getMappingRestoran(Integer page) throws SQLException {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("admin-restaurant");
        mav.addObject("restoranList", getAllRegisteredRestoran());
        if (page==null){
            page=1;
        }
        double jumlahRestoran = getCountRestoran();
        double jumlahPage = Math.ceil(jumlahRestoran/itemPerPage);
        List<Integer> pageList = new ArrayList<>();
        for (int i=1; i<=jumlahPage; i++){
            pageList.add(i); //menambahkan angka page (1, 2, 3, dst) ke list PageList, nanti di HTML angka2nya akan di plot ke pagination
        }
        mav.addObject("pageNo", page);
        mav.addObject("pageList", pageList);
        return mav;
    }

    public ModelAndView postMappingRestoran(Restoran restoran,
                                            User user) throws SQLException {
        ModelAndView mav = new ModelAndView();
        insertNewRestoran(restoran);
        int idResto = GetIdResto.getIdRestoBasedOnNamaResto(restoran.getNamaResto());
        user.setRole("owner");
        user.setIdResto(idResto);
        String hashedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        insertNewOwner(user);
        mav.setViewName("redirect:/restaurant");
        return mav;
    }

    public List<Restoran> getAllRegisteredRestoran() throws SQLException {
        List<Restoran> restoranList = restoranDao.getAll("users_roles.role='owner'");
        return restoranList;
    }

    public int getCountRestoran() throws SQLException {
        int countRestoran = restoranDao.count(null);
        return countRestoran;
    }

    public void insertNewRestoran(Restoran restoran) throws SQLException {
        restoranDao.insert(restoran);
    }

    public void insertNewOwner(User user) throws SQLException {
        userDao.insert(user);
        userDao.insertTableUsersRole(user);
    }

}
