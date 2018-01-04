package project.blibli.mantapos.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import project.blibli.mantapos.Config.Mail;
import project.blibli.mantapos.Helper.GetIdResto;
import project.blibli.mantapos.Model.*;
import project.blibli.mantapos.NewImplementationDao.*;
import project.blibli.mantapos.NewInterfaceDao.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CashierService {

    LedgerDao ledgerDao;
    SaldoDao saldoAkhirDao, saldoAwalDao;
    RestoranDao restoranDao;
    MenuDao menuDao;
    MenuYangDipesanDao menuYangDipesanDao;

    @Autowired
    public CashierService(LedgerDao ledgerDao,
                          @Qualifier("saldoAkhirDaoImpl") SaldoDao saldoAkhirDao,
                          @Qualifier("saldoAwalDaoImpl") SaldoDao saldoAwalDao,
                          RestoranDao restoranDao,
                          MenuDao menuDao,
                          MenuYangDipesanDao menuYangDipesanDao){
        this.ledgerDao = ledgerDao;
        this.saldoAkhirDao = saldoAkhirDao;
        this.saldoAwalDao = saldoAwalDao;
        this.restoranDao = restoranDao;
        this.menuDao = menuDao;
        this.menuYangDipesanDao = menuYangDipesanDao;
    }

    int bulan = LocalDate.now().getMonthValue();
    int tahun = LocalDate.now().getYear();

    public ModelAndView getMappingCashier(Authentication authentication) throws SQLException {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("cashier");
        String loggedInUsername = getLoggedInUsername(authentication);
        int idResto = GetIdResto.getIdRestoBasedOnUsernameTerkait(loggedInUsername);
        mav.addObject("loggedInUsername", loggedInUsername);
        mav.addObject("restoran", getInfoRestoran(idResto));
        mav.addObject("menuList", getAllMenu(idResto));
        return mav;
    }

    public ModelAndView postMappingCashier(Ledger ledger,
                                           String[] arrayIdMenu,
                                           String[] arrayQtyMenu,
                                           String apakahMauKirimReceiptMelaluiEmail,
                                           String emailTujuan,
                                           String namaResto,
                                           String namaCustomer,
                                           Authentication authentication,
                                           String notes,
                                           TemplateEngine templateEngine,
                                           Mail mail) throws SQLException {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:/cashier");
        String loggedInUsername = authentication.getName();
        int idResto = GetIdResto.getIdRestoBasedOnUsernameTerkait(loggedInUsername);
        ledger.setId_resto(idResto);
        ledger.setTipe("debit");
        ledger.setKeperluan("penjualan menu");
        insertPemasukkan(ledger);
        updateSaldoAkhir(idResto);
        insertDetailMenuYangDipesan(getLastIdOrder(idResto), arrayIdMenu, arrayQtyMenu);
        if(apakahMauKirimReceiptMelaluiEmail.equals("yes")){
            kirimReceiptMelaluiEmail(emailTujuan, namaResto, namaCustomer, notes, templateEngine, mail, ledger.getBiaya(), arrayIdMenu, arrayQtyMenu);
        }
        return mav;
    }

    private List<Menu> getAllMenu(int idResto) throws SQLException {
        List<Menu> menuList = menuDao.getAll("id_resto=" + idResto + " AND enabled=true ORDER BY kategori_menu DESC");
        return menuList;
    }

    private Restoran getInfoRestoran(int idResto) throws SQLException {
        Restoran restoran = restoranDao.getOne("id=" + idResto);
        return restoran;
    }

    private String getLoggedInUsername(Authentication authentication){
        String username = authentication.getName();
        return username;
    }

    private int getLastIdOrder(int idResto) throws SQLException {
        int lastIdOrder = ledgerDao.getId("id_resto=" + idResto);
        return lastIdOrder;
    }

    private void insertDetailMenuYangDipesan(int idOrder, String[] arrayIdMenu, String[] arrayQtyMenu) throws SQLException {
        for (int i=0; i<arrayIdMenu.length; i++){
            menuYangDipesanDao.insert(new OrderedMenu(idOrder, Integer.parseInt(arrayIdMenu[i]), Integer.parseInt(arrayQtyMenu[i])));
        }
    }

    private void insertPemasukkan(Ledger ledger) throws SQLException {
        ledgerDao.insert(ledger);
    }

    private void updateSaldoAkhir(int idResto) throws SQLException {
        Saldo saldo = new Saldo();
        saldo.setId_resto(idResto);
        int saldoAwal=0;
        if(bulan==1){
            saldoAwal = saldoAkhirDao.getOne("id_resto=" + idResto +
                    " AND EXTRACT(MONTH FROM date_created)=12 AND EXTRACT(YEAR from date_created)=" + (tahun-1)).getSaldo();
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
        if(saldoAkhirDao.count("id_resto=" + idResto + " AND EXTRACT(MONTH FROM date_created)=" + bulan + " AND EXTRACT(YEAR FROM date_created)=" + tahun)==0){
            saldoAkhirDao.insert(saldo);
        } else{
            saldoAkhirDao.update(saldo, "id_resto=" + idResto +
                    " AND EXTRACT(MONTH FROM date_created)=" + bulan + " AND EXTRACT(YEAR FROM date_created)=" + tahun);
        }
    }

    private void kirimReceiptMelaluiEmail(String alamatEmailTujuan,
                                         String namaResto,
                                         String namaCustomer,
                                         String notes,
                                         TemplateEngine templateEngine,
                                         Mail mail,
                                         int totalBiaya,
                                         String[] arrayIdMenu,
                                         String[] arrayQtyMenu) throws SQLException {
        MimeMessage maill = mail.send().createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(maill, true);
            Context context = new Context();
            helper.setFrom("mantapos@axella.online");
            helper.setTo(alamatEmailTujuan); //Alamat tujuan pengiriman email, di-pass dari HTML kesini dalam variabel email_kirim_receipt
            helper.setSubject("RECEIPT PEMBELIAN DI " + namaResto); //Subject email
            context.setVariable("nama_customer", namaCustomer); //Melempar nama customer ke HTML receipt (email.html)
            context.setVariable("namaResto", namaResto); //Melempar nama restoran ke HTML receipt (email.html)
            context.setVariable("tanggal", LocalDate.now().toString()); //Melempar tanggal sekarang ke HTML receipt (email.html)
            context.setVariable("total_harga", totalBiaya); //Melempar total biaya ke HTML receipt (email.html). Total biaya didapat dari object ledger yang dilempar dari kasir kesini.
            context.setVariable("notes", notes);
            List<OrderedMenu> ordered_menu_list = new ArrayList<>(); //List yang berisi detail menu (nama menu, harga menu, qty, dan total harga menu)
            for (int i=0; i<arrayIdMenu.length; i++){
                Menu menuObject = menuDao.getOne("id_menu=" + Integer.parseInt(arrayIdMenu[i])); //Mengambil detail dari masing-masing menu. Cara mengambil detailnya adalah berdasarkan id menu yang ada di Array array_id_order
                ordered_menu_list.add(new OrderedMenu(
                        menuObject.getNama_menu(),
                        (menuObject.getHarga_menu() * Integer.parseInt(arrayQtyMenu[i])),
                        Integer.parseInt(arrayQtyMenu[i])));
            }
            context.setVariable("ordered_menu_list", ordered_menu_list); //Melempar list ordered_menu_list yang berisi detail menu yang di-order ke HTML receipt (email.html)
            String body = templateEngine.process("email", context); //Menspecify body dari email adalah email.html dengan context (yaitu variabel2 yang dilempar tadi)
            helper.setText(body, true);
            mail.send().send(maill); //Melakukan send email. Harap cek spam untuk melihat email yang dikirim. Tidak tahu kenapa masuk spam.
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
