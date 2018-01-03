package project.blibli.mantapos.Service.OwnerManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.ModelAndView;
import project.blibli.mantapos.Helper.GetIdResto;
import project.blibli.mantapos.Model.Menu;
import project.blibli.mantapos.NewImplementationDao.MenuDaoImpl;
import project.blibli.mantapos.NewInterfaceDao.MenuDao;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MenuService {

    MenuDao menuDao;

    @Autowired
    public MenuService(MenuDao menuDao){
        this.menuDao = menuDao;
    }

    int itemPerPage=5;
    private static String UPLOAD_LOCATION=System.getProperty("user.dir") + "/src/main/resources/static/images/";

    public ModelAndView getMappingMenu(Authentication authentication,
                                       Integer page) throws SQLException {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("owner-manager/menu");
        int idResto = GetIdResto.getIdRestoBasedOnUsernameTerkait(authentication.getName());
        if(page == null){
            page=1;
        }
        mav.addObject("menuList", getAllMenu(idResto, page));
        mav.addObject("pageNo", page);
        double jumlahMenu = getCountMenu(idResto);
        double jumlahPage = Math.ceil(jumlahMenu/itemPerPage); //Menghitung jumlah page yang ada, yaitu dari jumlah total menu dibagi dengan jumlah item per page. Lalu dibulatkan ke atas. Misal jumlah menu adalah 12, jumlah item per page adalah 5, maka akan ada 3 page dengan jumlah item 5, 5, 3
        List<Integer> pageList = new ArrayList<>(); //List pageList untuk menampung nilai integer dari page. (jika jumlahPage=3, maka di pageList akan ada 1,2,3
        for (int i=1; i<=jumlahPage; i++){
            pageList.add(i);
        }
        mav.addObject("pageList", pageList);
        mav.addObject("username", authentication.getName());
        return mav;
    }

    public ModelAndView postMappingAddNewMenu(Authentication authentication,
                                              Menu menu) throws SQLException {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:/menu");
        int idResto = GetIdResto.getIdRestoBasedOnUsernameTerkait(authentication.getName());
        insertNewMenu(idResto, menu);
        return mav;
    }

    public ModelAndView getMappingEditMenu(int idMenu) throws SQLException {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("owner-manager/edit-menu");
        mav.addObject("menuObject", getDetailMenuByIdMenu(idMenu));
        return mav;
    }

    public ModelAndView postMappingEditMenu(Authentication authentication,
                                            Menu menu) throws SQLException {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:/menu");
        updateMenu(menu);
        return mav;
    }

    public ModelAndView getMappingDeleteMenu(int idMenu) throws SQLException {
        ModelAndView mav = new ModelAndView();
        deleteMenu(idMenu);
        mav.setViewName("redirect:/menu");
        return mav;
    }

    private List<Menu> getAllMenu(int idResto,
                                 int page) throws SQLException {
        List<Menu> menuList = menuDao.getAll("id_resto=" + idResto +
                " AND enabled=true" + " ORDER BY kategori_menu DESC, id_menu ASC LIMIT " + itemPerPage + " OFFSET " + (page-1)*itemPerPage);
        return menuList;
    }

    private int getCountMenu(int idResto) throws SQLException {
        int countMenu = menuDao.count("id_resto=" + idResto);
        return countMenu;
    }

    private void insertNewMenu(int idResto,
                              Menu menu) throws SQLException {
        menu.setIdResto(idResto);
        String filename = String.valueOf(menuDao.getLastId("id_resto=" + idResto) + 1) + ".jpg"; //generate filename berdasarkan dari nilai id menu yang ditambahkan. Misal id menu yang barusan ditambahkan adalah 1, berarti nama gambarnya adalah 1.jpg
        try {
            FileCopyUtils.copy(menu.getMultipartFile().getBytes(), new File(UPLOAD_LOCATION + filename)); //Melakukan upload gambar
        } catch (IOException e) {
            System.out.println("Gagal upload foto : " + e.toString());
        }
        menu.setLokasi_gambar_menu("/images/" + filename); //set lokasi gambarnya
        menuDao.insert(menu);
    }

    private Menu getDetailMenuByIdMenu(int idMenu) throws SQLException {
        Menu menu = menuDao.getOne("id_menu=" + idMenu);
        return menu;
    }

    private void updateMenu(Menu menu) throws SQLException {
        try {
            if(!menu.getMultipartFile().isEmpty()){
                //jika multipartFile (tempat upload foto) itu tidak kosong, berarti user upload foto baru
                String filename = String.valueOf(menu.getId()) + ".jpg";
                FileCopyUtils.copy(menu.getMultipartFile().getBytes(), new File(UPLOAD_LOCATION + filename)); //upload foto yang baru
                menu.setLokasi_gambar_menu("/images/" + filename);
            } else{
                menu.setLokasi_gambar_menu(menu.getLokasi_gambar_menu());
            }
            menuDao.update(menu, "id_menu=" + menu.getId()); //update menu di table menu di database
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteMenu(int idMenu) throws SQLException {
        menuDao.deactivate("id_menu=" + idMenu);
    }

}
