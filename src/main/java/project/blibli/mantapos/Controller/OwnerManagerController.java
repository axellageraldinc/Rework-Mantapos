package project.blibli.mantapos.Controller;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import project.blibli.mantapos.Model.*;
import project.blibli.mantapos.ImplementationDao.*;
import project.blibli.mantapos.MonthNameGenerator;
import project.blibli.mantapos.WeekGenerator;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//TODO : DISCOUNT belum dibuat sama sekali

@RestController
//Controller yang mengatur segala hal yang berkaitan dengan dashboard manager/owner
public class OwnerManagerController {
    private static String UPLOAD_LOCATION=System.getProperty("user.dir") + "/src/main/resources/static/images/";

    UserDaoImpl userDao = new UserDaoImpl();
    MenuDaoImpl menuDao = new MenuDaoImpl();
    private LedgerDaoImpl ledgerDao = new LedgerDaoImpl();
    private SaldoDaoImpl saldoDao = new SaldoDaoImpl();
    private RestoranDaoImpl restoranDao = new RestoranDaoImpl();
    int id_resto, itemPerPage=5; //itemPerPage dibuat 5 item saja.

    //Jika user mengakses /dashboard
    @GetMapping(value = "/dashboard", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView managerDashboardHtml(){
        return new ModelAndView("owner-manager/dashboard");
    }
    //Jika user mengakses menu/{page}, dimana {page} ini adalah page keberapa laman menu itu, misal menu/1 berarti laman menu page 1 di pagination-nya
    @GetMapping(value = "/menu/{page}")
    public ModelAndView menuPaginated(Menu menu, @PathVariable("page") int page, Authentication authentication){
        ModelAndView mav = new ModelAndView();
        mav.setViewName("owner-manager/menu");
        String username = authentication.getName(); //Mengambil username yang login
        id_resto = restoranDao.GetRestoranId(username); //Mengambil id restoran berdasarkan username yang login (username itu belong ke restoran mana)
        List<Menu> menuList = menuDao.getAllMenu(id_resto, itemPerPage, page); //Mengambil semua menu yang ada, dengan parameter id restoran yang sudah diambil tadi, jumlah itemPerPage (yaitu 5), dan page ke berapa diambil dari URL tadi, misal menu/1, berarti page = 1
        mav.setViewName("owner-manager/menu");
        mav.addObject("menuList", menuList);
        double jumlahMenu = menuDao.jumlahMenu(id_resto); //Mengambil jumlah menu yang ada
        double jumlahPage = Math.ceil(jumlahMenu/itemPerPage); //Menghitung jumlah page yang ada, yaitu dari jumlah total menu dibagi dengan jumlah item per page. Lalu dibulatkan ke atas. Misal jumlah menu adalah 12, jumlah item per page adalah 5, maka akan ada 3 page dengan jumlah item 5, 5, 3
        List<Integer> pageList = new ArrayList<>(); //List pageList untuk menampung nilai integer dari page. (jika jumlahPage=3, maka di pageList akan ada 1,2,3
        for (int i=1; i<=jumlahPage; i++){
            pageList.add(i);
        }
        mav.addObject("pageNo", page);
        mav.addObject("pageList", pageList);
        return mav;
    }
    //Jika user mengakses laman outcome. Kurang lebih sama dengan menu/{page} di atas.
    @GetMapping(value = "/outcome/{page}", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView outcomeHtml(@PathVariable("page") int page,
            Authentication authentication){
        ModelAndView mav = new ModelAndView();
        mav.setViewName("owner-manager/outcome");
        String username = authentication.getName(); //Mengambil username dari user yang login
        id_resto = restoranDao.GetRestoranId(username); //Mengambil id restoran berdasarkan username yang login tadi (username itu belong ke restoran mana)
        List<Ledger> outcomeList = ledgerDao.GetDailyKredit(id_resto, itemPerPage, page); //Mengambil object-object outcome yang ada di table ledger_harian. karena ini outcome, berarti kredit. Lalu itemPerPage itu untuk me-limit query SELECT nya sampai berapa item dan page itu untuk mencari offset (record dibaca mulai dari item ke-berapa)
        double jumlahBanyakOutcome = saldoDao.jumlahBanyakSaldo(id_resto); //Menghitung jumlah item outcome ada berapa row
        double jumlahPage = Math.ceil(jumlahBanyakOutcome/itemPerPage); //Menghitung jumlahPage, didapatkan dari jumlah row outcome dibagi dengan item per page. Misal jumlah row outcome 12 dan item per page 5, maka akan ada 3 page dengan item-nya 5,5,3
        List<Integer> pageList = new ArrayList<>(); //List untuk menampung nilai-nilai page (jika jumlahPage = 3, maka di pageList ini isinya 1,2,3
        for (int i=1; i<=jumlahPage; i++){
            pageList.add(i);
        }
        mav.addObject("outcomeList", outcomeList);
        mav.addObject("pageNo", page);
        mav.addObject("pageList", pageList);
        return mav;
    }
    //Jika user akses /employee, kurang lebih sama dengan menu/page dan outcome/page
    @GetMapping(value = "/employee/{page}", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView cashierListHtml(@PathVariable("page") int page,
            Authentication authentication){
        ModelAndView mav = new ModelAndView();
        mav.setViewName("owner-manager/employee");
        List<User> userList = new ArrayList<>();
        String username = authentication.getName();
        id_resto = restoranDao.GetRestoranId(username);
        double jumlahEmployee = userDao.jumlahEmployee(id_resto);
        double jumlahPage = Math.ceil(jumlahEmployee/itemPerPage);
        List<Integer> pageList = new ArrayList<>();
        for (int i=1; i<=jumlahPage; i++){
            pageList.add(i);
        }
        //Mengecek user yang login sekarang itu role-nya apa.
        if(authentication.getAuthorities().toString().equals("[manager]")){
            //jika role-nya adalah manager, maka mengambil semua user dengan role cashier
            userList = userDao.getAllUser(id_resto, "cashier", itemPerPage, page);
        } else if(authentication.getAuthorities().toString().equals("[owner]")){
            //jika role-nya adalah owner, maka mengambil semua user dengan role manager dan cashier
            userList = userDao.getAllUser(id_resto, "manager&cashier", itemPerPage, page);
        }
        String role = authentication.getAuthorities().toString();
        mav.addObject("pageNo", page);
        mav.addObject("pageList", pageList);
        mav.addObject("userList", userList);
        mav.addObject("role", role);
        return mav;
    }
    //Jika user mengakses link /range, yaitu link untuk memilih jangka waktu melihat ledger (buku besar)
    @GetMapping(value = "/range", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView ledgerChooseRangeHtml(Authentication authentication){
        String username = authentication.getName();
        id_resto = restoranDao.GetRestoranId(username);
        List<Ledger> monthAndYearList = ledgerDao.GetMonthAndYearList(id_resto); //Mengambil semua month dan year yang ada di database. Ini di pass ke pilih-range-ledger.html, supaya dropdown di html itu nanti sesuai dengan month dan year yang tersedia di database saja
        return new ModelAndView("owner-manager/pilih-range-ledger", "monthAndYearList", monthAndYearList);
    }
    //Jika user akses /saldo/page, sama dengan menu, outcome, dan employee untuk paging-nya
    @GetMapping(value = "/saldo/{page}")
    public ModelAndView addSaldoAwalHtml(@PathVariable("page") int page,
            Authentication authentication){
        ModelAndView mav = new ModelAndView();
        String username = authentication.getName();
        id_resto = restoranDao.GetRestoranId(username);
        int intBulan = LocalDate.now().getMonthValue();
        String bulan = MonthNameGenerator.MonthNameGenerator(intBulan); //Mengambil nama bulan berdasarkan nilai integer bulan-nya
        List<SaldoAwal> saldoAwalList = saldoDao.getSaldoAwalTiapBulan(id_resto, itemPerPage, page); //Mengambil saldo awal setiap bulannya untuk ditampilkan di main menu dari laman saldo
        double jumlahBanyakSaldo = saldoDao.jumlahBanyakSaldo(id_resto);
        double jumlahPage = Math.ceil(jumlahBanyakSaldo/itemPerPage);
        List<Integer> pageList = new ArrayList<>();
        for (int i=1; i<=jumlahPage; i++){
            pageList.add(i);
        }
        mav.addObject("pageNo", page);
        mav.addObject("pageList", pageList);
        mav.addObject("saldoAwalList", saldoAwalList);
        mav.addObject("bulan", bulan);
        mav.setViewName("owner-manager/saldo-awal");
        return mav;
    }

    //Jika user menambahkan saldo baru
    @PostMapping(value = "/saldo-post", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView addSaldoAwalHtml(Authentication authentication,
                                         @ModelAttribute("saldoAwal") SaldoAwal saldoAwal){
        String username = authentication.getName();
        id_resto = restoranDao.GetRestoranId(username);
        saldoDao.AddSaldoAwal(id_resto, saldoAwal.getSaldo_awal()); //Menambahkan saldo awal dengan foreign key id restoran yang sesuai dan nilai saldo awal ada di object saldoAwal yang di-pass dari html
        return new ModelAndView("redirect:/saldo/1");
    }
    //Jika user menambahkan menu baru
    @PostMapping(value = "/menu", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView addMenuJson(@ModelAttribute("menu") Menu menu,
                                    Authentication authentication){
        String username = authentication.getName();
        id_resto = restoranDao.GetRestoranId(username);
        try{
            String filename = String.valueOf(menuDao.getLastId(id_resto) + 1) + ".jpg"; //generate filename berdasarkan dari nilai id menu yang ditambahkan. Misal id menu yang barusan ditambahkan adalah 1, berarti nama gambarnya adalah 1.jpg
            FileCopyUtils.copy(menu.getMultipartFile().getBytes(), new File(UPLOAD_LOCATION + filename)); //Melakukan upload gambar
            menu.setLokasi_gambar_menu("/images/" + filename); //set lokasi gambarnya
            menuDao.Insert(id_resto, menu); //insert menu ke table menu di database
        } catch (Exception ex){
            System.out.println("Error add menu : " + ex.toString());
        }
        return new ModelAndView("redirect:/menu/1");
    }
    //Jika user menambahkan user baru
    @PostMapping(value = "/add-user", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView addUserHtml(@ModelAttribute("user")User user,
                                    Authentication authentication){
        String username = authentication.getName();
        id_resto = restoranDao.GetRestoranId(username);
        //Mengambil username yang login itu dia role-nya sebagai apa
        if(authentication.getAuthorities().toString().equals("[manager]")){
            //jika sebagai manager, maka user yang baru ditambahkan itu role-nya adalah cashier
            user.setRole("cashier");
        } else if(authentication.getAuthorities().toString().equals("[owner]")){
            //jika sebagai owner, maka user yang baru ditambahkan itu role-nya bisa manager atau cashier. Diambil dari value yang di-pick di employee.html
            user.setRole(user.getRole());
        }
        user.setIdResto(id_resto); //set ID resto (foreign key) user dengan id_resto dari username yang login sekarang (yaitu atasannya)
        userDao.Insert(user); //insert user ke table user dan user_roles di database. Insert ke table user_roles ada di dalam method Insert ini juga.
        return new ModelAndView("redirect:/employee/1");
    }
    //Jika user menambahkan outcome (pengeluaran baru)
    @PostMapping(value = "/outcome-post", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView outcomeHtmlPost(@ModelAttribute("ledger") Ledger ledger,
                                        @RequestParam("quantity") String qty,
                                        Authentication authentication){
        String username = authentication.getName();
        id_resto = restoranDao.GetRestoranId(username);
        String[] dateSplit = ledger.getWaktu().split("-"); //split date dengan character "-" dari date yang di-pick dari user melalui outcome.html (formatnya yyyy-mm-dd)
        int tanggal = Integer.parseInt(dateSplit[2]); //tanggal itu ada di elemen ke-2
        int week = WeekGenerator.GetWeek(tanggal); ledger.setWeek(week); //week di-generate melalui class WeekGenerator, lalu setWeek di model ledger
        int month = Integer.parseInt(dateSplit[1]); ledger.setMonth(month); //month itu ada di elemen ke-1, lalu setMonth
        int year = Integer.parseInt(dateSplit[0]); ledger.setYear(year); //year itu ada di elemen ke-0, lalu setYear
        ledger.setTipe("kredit"); //karena ini adalah pengeluaran (outcome), berarti tipenya adalah kredit
        ledger.setKeperluan(ledger.getKeperluan() + "(" + qty + ")"); //Keperluannya adalah diambil dari keperluan yang diinputkan oleh user ditambahkan dengan quantity yang diinputkan oleh user
        ledgerDao.Insert(ledger, id_resto); //insert pengeluaran ke table outcome di database
        return new ModelAndView("redirect:/outcome/1");
    }
    //Setelah pilih jangka waktu untuk melihat ledger, maka page ini diakses
    @PostMapping(value = "/ledger")
    public ModelAndView Ledger(@RequestParam(value = "Skala", required = false) String skala,
                               @RequestParam(value = "month", required = false) Integer month,
                               @RequestParam(value = "year", required = false) Integer year,
                               @RequestParam(value = "ledger_custom_awal", required = false)  String ledger_custom_awal,
                               @RequestParam(value = "ledger_custom_akhir", required = false)  String ledger_custom_akhir,
                               Authentication authentication){
        ModelAndView mav = new ModelAndView();
        String username = authentication.getName();
        id_resto = restoranDao.GetRestoranId(username);
        List<Ledger> ledgerList = new ArrayList<>();
        double saldo_awal=0, total_debit=0, total_kredit=0, saldo_akhir=0, mutasi=0;
        String skala_ledger=""; //skala ledger bisa harian, mingguan, bulanan, atau tahunan
        if(skala.equals("harian") || skala.equals("mingguan")){ //jika skala yang di-pass dari ledger.html adalah harian atau mingguan. Yang di-pick user adalah bulan dan tahunnya. Misal user ingin melihat ledger harian di bulan Oktober tahun 2017. Atau mingguan di bulan Desember tahun 2017
            total_kredit = ledgerDao.GetTotalKreditBulanan(id_resto, month, year); //Mengambil total kredit bulanan di bulan dan tahun yang di pick user
            total_debit = ledgerDao.GetTotalDebitBulanan(id_resto, month, year); //Mengambil total debit bulanan dari bulan dan tahun yang di pick user
            saldo_awal = saldoDao.getSaldoAwal(id_resto, month, year); //Mengambil saldo awal di bulan dan tahun yang di pick user
            saldo_akhir = saldo_awal+total_debit-total_kredit; //Menghitung saldo akhir (dari saldo awal ditambah pemasukkan (total debit) dikurangi pengeluaran (total kredit)
            mutasi = saldo_akhir-saldo_awal; //mutasi, masih belum tahu maksudnya mutasi ini apa. //TODO : Cari definisi dari mutasi itu apa
            if(skala.equals("harian")){
                //jika skalanya adalah harian
                ledgerList = ledgerDao.GetDailyLedger(id_resto, month, year); //mengambil ledger secara harian di bulan dan tahun yang di pick user
                skala_ledger = "HARIAN";
            } else if(skala.equals("mingguan")){
                //jika skalanya adalah mingguan
                ledgerList = ledgerDao.GetWeeklyLedger(id_resto, month, year); //mengambil ledger secara mingguan di bulan dan tahun yang di pick user
                skala_ledger = "MINGGUAN";
            }
        } else if(skala.equals("bulanan")){
            //jika skalanya adalah bulanan
            //karena bulanan, maka user memilih tahunnya. misal user ingin melihat ledger secara bulanan di tahun 2017
            total_kredit = ledgerDao.GetTotalKreditTahunan(id_resto, year); //mengambil total kredit
            total_debit = ledgerDao.GetTotalDebitTahunan(id_resto, year); //mengambil total debit
            saldo_awal = saldoDao.getSaldoAwal(id_resto, month, year); //mengambil saldo awal. //TODO : dimatangkan lagi saldo awal untuk ledger bulanan itu diambil dari saldo awal di bulan pertama tahun yang dipilih (misal 2017) atau saldo awal di awal buanget
            saldo_akhir = saldo_awal+total_debit-total_kredit;
            mutasi = saldo_akhir-saldo_awal;
            ledgerList = ledgerDao.GetMonthlyLedger(id_resto, year); //mengambil ledger bulanan di tahun yang di pick user
            skala_ledger = "BULANAN";
        } else if(skala.equals("tahunan")){ //tahunan //TODO : Ledger tahunan

        } else {
            //custom ledger range

            //inti dari 4 baris kode di bawah ini adalah untuk mengambil tanggal, bulan, dan tahun dari waktu awal yang dikehendaki user untuk melihat ledger
            String[] dateAwalSplit = ledger_custom_awal.split("-");
            int tanggalAwal = Integer.parseInt(dateAwalSplit[2]);
            int monthAwal = Integer.parseInt(dateAwalSplit[1]);
            int yearAwal = Integer.parseInt(dateAwalSplit[0]);

            //inti dari 4 baris kode di bawah ini adalah untuk mengambil tanggal, bulan, dan tahun dari waktu akhir yang dikehendaki user unutuk melihat ledger
            String[] dateAkhirSplit = ledger_custom_akhir.split("-");
            int tanggalAkhir = Integer.parseInt(dateAkhirSplit[2]);
            int monthAkhir = Integer.parseInt(dateAkhirSplit[1]);
            int yearAkhir = Integer.parseInt(dateAkhirSplit[0]);

            total_kredit = ledgerDao.getTotalKreditCustom(id_resto, tanggalAwal, monthAwal, yearAwal, tanggalAkhir,  monthAkhir, yearAkhir); //Total kredit dari waktu awal hingga akhir yang dikehendaki user
            total_debit = ledgerDao.getTotalDebitCustom(id_resto, tanggalAwal, monthAwal, yearAwal, tanggalAkhir,  monthAkhir, yearAkhir); //Total debit dari waktu awal hingga akhir yang dikehendaki user
            saldo_awal = saldoDao.getSaldoAwalCustom(id_resto, tanggalAwal, monthAwal, yearAwal); //Mengambil saldo awal dari waktu awal yang di pick user. //TODO: ini dipastikan lagi mau saldo awal dari waktu awal yang di pick user, atau dari awal buanget, atau gimana
            saldo_akhir = saldo_awal+total_debit-total_kredit;
            mutasi = saldo_akhir-saldo_awal;
            ledgerList = ledgerDao.getCustomLedger(id_resto, tanggalAwal, monthAwal, yearAwal, tanggalAkhir,  monthAkhir, yearAkhir); //Mengambil ledger berdasarkan waktu awal dan waktu akhir yang di pick user
            skala_ledger = "CUSTOM";
        }

        mav.addObject("saldo_awal", saldo_awal);
        mav.addObject("total_debit", total_debit);
        mav.addObject("total_kredit", total_kredit);
        mav.addObject("saldo_akhir", saldo_akhir);
        mav.addObject("mutasi", mutasi);
        mav.addObject("ledgerList", ledgerList);
        mav.addObject("skala_ledger", skala_ledger);
        mav.setViewName("owner-manager/ledger");
        return mav;
    }
    //Jika user menghapus user
    @GetMapping(value = "/delete/user/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView deleteCashier(@PathVariable("id") int id,
                                      Authentication authentication){
        //Pengecekan role dari user yang login sekarang
        if(authentication.getAuthorities().toString().equals("[admin]")){
            //jika user yang login sekarang adalah seorang admin, maka yang dihapus (dinonaktifkan) adalah user tersebut beserta user user yang bersesuaian.
            //Karena jika yg login skrg adalah admin, maka yg dinonaktifkan adalah owner, maka yg dinonaktifkan adalah owner, manager, dan cashier dari restoran yang sama.
            userDao.DeleteUserAndDependencies(id);
            return new ModelAndView("redirect:/restaurant");
        } else{
            //jika user yang login bukanlah admin (manager/owner)
            userDao.DeleteUser(id); //cukup menonaktifkan user itu saja
            return new ModelAndView("redirect:/employee/1");
        }
    }
    //Jika user mengaktifkan lagi user
    @GetMapping(value = "/active/user/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView activeCashier(@PathVariable("id") int id,
                                      Authentication authentication){
        userDao.ActivateUser(id); //tidak peduli role nya user yg login ini adalah admin atau bukan, yg diaktifkan tetaplah user yang ingin diaktifkan saja.
        //kenapa? karena misal jika admin mengaktifkan seorang owner, jika diaktifkan owner beserta user2 lainnya, kalau misal ada cashier yg ternyata sudah nonaktif karena pensiun, masa ya harus diaktifkan juga
        if(authentication.getAuthorities().toString().equals("[admin]"))
            return new ModelAndView("redirect:/restaurant");
        else
            return new ModelAndView("redirect:/employee/1");
    }
    //jika user menghapus menu
    @GetMapping(value = "/delete/menu/{id}")
    public ModelAndView deleteMenu(@PathVariable("id") int id){
        menuDao.DeleteMenu(id);
        return new ModelAndView("redirect:/menu/1");
    }
    //jika user mengakses halaman untuk mengedit menu (keluar form dengan value dari menu yg bersesuaian di set ke input2 yang ada
    @GetMapping(value = "/edit/menu/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView editMenuHtml(@PathVariable("id") int id,
                                     Authentication authentication){
        ModelAndView mav = new ModelAndView();
        String username = authentication.getName();
        int id_resto = restoranDao.GetRestoranId(username);
        List<Menu> menuList = menuDao.getMenuById(id_resto, id); //Mengambil detail sebuah menu berdasarkan id nya. //TODO : dipastikan lagi ini bagusnya di store ke list atau cukup model Menu saja
        mav.addObject("menuList", menuList);
        mav.setViewName("owner-manager/edit-menu");
        return mav;
    }
    //jika user posting menu yang sudah di edit
    @PostMapping(value = "/edit-menu", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView editMenuPostHtml(@ModelAttribute("menu") Menu menu,
                                         Authentication authentication){
        ModelAndView mav = new ModelAndView();
        int id_resto = restoranDao.GetRestoranId(authentication.getName()); //Mengambil id restoran berdasarkan username yang login sekarang (username ini belong ke restoran mana)
        try {
            if(!menu.getMultipartFile().isEmpty()){
                //jika multipartFile (tempat upload foto) itu tidak kosong, berarti user upload foto baru
                String filename = String.valueOf(menu.getId() + 1) + ".jpg";
                FileCopyUtils.copy(menu.getMultipartFile().getBytes(), new File(UPLOAD_LOCATION + filename)); //upload foto yang baru
                menu.setLokasi_gambar_menu("/images/" + filename);
            } else{
                menu.setLokasi_gambar_menu(menu.getLokasi_gambar_menu());
            }
            menuDao.UpdateMenu(id_resto, menu); //update menu di table menu di database
        } catch (IOException e) {
            e.printStackTrace();
        }
        mav.setViewName("redirect:/menu/1");
        return mav;
    }
    //Jika user mengakses laman edit user (show form) beserta value value yang bersesuaian di input2 yang ada
    @GetMapping(value = "/edit/user/{id}")
    public ModelAndView editUserHtml(@PathVariable("id") int id,
                                     Authentication authentication){
        int id_resto = restoranDao.GetRestoranId(authentication.getName());
        List<User> userList = userDao.GetUserById(id_resto, id); //Ambil detail user yang ingin diedit berdasarkan id-nya
        ModelAndView mav = new ModelAndView();
        mav.setViewName("owner-manager/edit-user");
        String role = authentication.getAuthorities().toString();
        mav.addObject("role", role);
        mav.addObject("userList", userList);
        return mav;
    }
    //Memposting data user yang baru
    @PostMapping(value = "/edit-user")
    public ModelAndView editUserPostHtml(@ModelAttribute("user") User user,
                                         Authentication authentication){
        ModelAndView mav = new ModelAndView();
        int id_resto = restoranDao.GetRestoranId(authentication.getName());
        userDao.UpdateUser(id_resto, user);
        mav.setViewName("redirect:/employee/1");
        return mav;
    }
}
