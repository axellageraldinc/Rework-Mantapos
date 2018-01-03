package project.blibli.mantapos.Service.OwnerManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import project.blibli.mantapos.Helper.GetIdResto;
import project.blibli.mantapos.Model.User;
import project.blibli.mantapos.NewImplementationDao.UserDaoImpl;
import project.blibli.mantapos.NewInterfaceDao.UserDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {

    UserDao userDao;
    BCryptPasswordEncoder bCryptPasswordEncoder;

    public EmployeeService(UserDao userDao, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.userDao = userDao;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    int itemPerPage=5;

    public ModelAndView getMappingEmployee(Authentication authentication,
                                           Integer page) throws SQLException {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("owner-manager/employee");
        String role = getLoggedInUserRole(authentication);
        int idResto = GetIdResto.getIdRestoBasedOnUsernameTerkait(authentication.getName());
        if(page == null){
            page=1;
        }
        List<User> userList = getAllUser(authentication, idResto, page);
        double jumlahEmployee = getCountUser(idResto);
        double jumlahPage = Math.ceil(jumlahEmployee/itemPerPage);
        List<Integer> pageList = new ArrayList<>();
        for (int i=1; i<=jumlahPage; i++){
            pageList.add(i);
        }
        mav.addObject("pageNo", page);
        mav.addObject("pageList", pageList);
        mav.addObject("userList", userList);
        mav.addObject("role", role);
        mav.addObject("username", authentication.getName());
        return mav;
    }

    public ModelAndView postMappingAddNewEmployee(Authentication authentication,
                                                  User user) throws SQLException {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:/employee");
        if (getLoggedInUserRole(authentication).equals("[manager]")){
            user.setRole("cashier");
        }
        int idResto = GetIdResto.getIdRestoBasedOnUsernameTerkait(authentication.getName());
        user.setIdResto(idResto);
        insertNewUser(user);
        return mav;
    }

    public ModelAndView getMappingEditEmployee(Authentication authentication,
                                               Integer idUser) throws SQLException {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("owner-manager/edit-user");
        mav.addObject("role", getLoggedInUserRole(authentication));
        mav.addObject("userObject", getDetailUserById(idUser));
        return mav;
    }

    public ModelAndView postMappingEditEmployee(Authentication authentication,
                                                User user) throws SQLException {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:/employee");
        int idResto = GetIdResto.getIdRestoBasedOnUsernameTerkait(authentication.getName());
        updateUser(user);
        return mav;
    }

    public ModelAndView postMappingDeleteEmployee(Authentication authentication,
                                                  Integer idUser) throws SQLException {
        ModelAndView mav = new ModelAndView();
        if(idUser==null){
            mav.setViewName("redirect:/employee");
        } else{
            deleteUser(authentication,idUser);
            if(getLoggedInUserRole(authentication).equals("[admin]")){
                mav.setViewName("redirect:/restaurant");
            } else{
                mav.setViewName("redirect:/employee");
            }
        }
        return mav;
    }

    public ModelAndView postMappingActivateEmployee(Authentication authentication,
                                                    Integer idUser) throws SQLException {
        ModelAndView mav = new ModelAndView();
        if(idUser==null){
            mav.setViewName("redirect:/employee");
        } else{
            activateUser(idUser);
            if(getLoggedInUserRole(authentication).equals("[admin]")){
                mav.setViewName("redirect:/restaurant");
            } else {
                mav.setViewName("redirect:/employee");
            }
        }
        return mav;
    }

    private void insertNewUser(User user) throws SQLException {
        String hashedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        userDao.insert(user);
        userDao.insertTableUsersRole(user);
    }

    private List<User> getAllUser(Authentication authentication,
                                  int idResto,
                                  int page) throws SQLException {
        List<User> userList;
        if (getLoggedInUserRole(authentication).equals("[manager]")) {
            userList = userDao.getAll("id_resto=" + idResto + " AND users_roles.username=users.username AND users_roles.role IN ('cashier') ORDER BY users.id_user ASC LIMIT " + itemPerPage + " OFFSET " + (page-1)*itemPerPage);
        } else{
            userList = userDao.getAll("id_resto=" + idResto + " AND users_roles.username=users.username AND users_roles.role IN ('manager','cashier') ORDER BY users.id_user ASC LIMIT " + itemPerPage + " OFFSET " + (page-1)*itemPerPage);
        }
        return userList;
    }

    private User getDetailUserById(int idUser) throws SQLException {
        User user = userDao.getOne("users.id_user=" + idUser);
        return user;
    }

    private void updateUser(User user) throws SQLException {
        if(user.getPassword().equals("") || user.getPassword().equals(null)){
            userDao.updateUserWithoutNewPassword(user, "id_user=" + user.getId());
            userDao.updateTableUsersRole(user, "id_user=" + user.getId());
        } else{
            String hashedPassword = bCryptPasswordEncoder.encode(user.getPassword());
            user.setPassword(hashedPassword);
            userDao.update(user, "id_user=" + user.getId());
            userDao.updateTableUsersRole(user, "id_user=" + user.getId());
        }
    }

    private void deleteUser(Authentication authentication,
                            int idUser) throws SQLException {
        if(getLoggedInUserRole(authentication).equals("[admin]")) {
            int idResto = GetIdResto.getIdRestoBasedOnUsernameTerkait(userDao.getOne("users.id_user=" + idUser).getUsername());
            userDao.deactivate("id_resto=" + idResto);
        } else {
            userDao.deactivate("id_user=" + idUser);
        }
    }

    private void activateUser(int idUser) throws SQLException {
        userDao.activate("id_user=" + idUser);
    }

    private String getLoggedInUserRole(Authentication authentication){
        String role = authentication.getAuthorities().toString();
        return role;
    }

    private int getCountUser(int idResto) throws SQLException {
        int countUser = userDao.count("id_resto=" + idResto);
        return countUser;
    }
}
