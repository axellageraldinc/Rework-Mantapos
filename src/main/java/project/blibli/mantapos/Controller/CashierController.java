package project.blibli.mantapos.Controller;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import project.blibli.mantapos.Beans_Model.Ledger;
import project.blibli.mantapos.Beans_Model.Menu;
import project.blibli.mantapos.Beans_Model.Restoran;
import project.blibli.mantapos.ImplementationDao.*;
import project.blibli.mantapos.WeekGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class CashierController {

    MenuDaoImpl menuDao = new MenuDaoImpl();
    LedgerDaoImpl ledgerDao = new LedgerDaoImpl();
    MenuYangDipesanDaoImpl orderedMenuDao = new MenuYangDipesanDaoImpl();
    RestoranDaoImpl restaurantDao = new RestoranDaoImpl();
    UserDaoImpl userDao = new UserDaoImpl();
    Restoran restoran;

    @GetMapping(value = "/cashier", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView cashierHtml(Authentication authentication){
        ModelAndView mav = new ModelAndView();

        //Ambil username yang sedang login, untuk nantinya diambil ID restoran-nya
        String loggedInUsername = authentication.getName();
        mav.addObject("loggedInUsername", loggedInUsername);

        restoran = restaurantDao.GetRestaurantInfo(loggedInUsername);
        mav.addObject("restoran", restoran);

        List<Menu> menuList = menuDao.getAllMenu(restoran.getId());
        mav.setViewName("cashier");
        mav.addObject("menuList", menuList);
        return mav;
    }

//    @GetMapping(value = "/cashier", produces = MediaType.APPLICATION_JSON_VALUE)
//    public Map<String, List<Menu>> cashierJson(Authentication authentication){
//        Map<String, List<Menu>> param = new HashMap<>();
//        String loggedInUsername = authentication.getName();
//        restoran = restaurantDao.GetRestaurantInfo(loggedInUsername);
//        List<Menu> menuList = menuDao.getAllMenu(restoran.getId());
//        param.put("menu", menuList);
//        return param;
//    }

//    @PostMapping(value = "/add-order", produces = MediaType.APPLICATION_JSON_VALUE)
//    public Map<String, String> addOrderJson(@ModelAttribute("order") Ledger ledger){
//        Map<String, String> param = new HashMap<>();
//        int status = 0;
//        String errorMsg = null;
//        try{
//            status = ledgerDao.Insert(ledger, restoran.getId_restaurant());
//            Income income = new Income();
//        } catch (Exception ex){
//            errorMsg = ex.toString();
//        }
//        if (status==1){
//            param.put("status", "success!");
//            param.put("customer name", ledger.getCustomer_name());
//            param.put("table no", String.valueOf(ledger.getTable_no()));
//            param.put("price total", String.valueOf(ledger.getBiaya()));
//            param.put("notes", ledger.getNotes());
//            //ledger time, week, month, dan year return NULL
//            //karena kan di model ledger itu kosong value mereka, value mereka di assign
//            //di LedgerDao tanpa harus ke model
//            //atau mending assign waktu ledger nya disini aja daripada di DAO?
//            param.put("order time", ledger.getWaktu());
//            param.put("week", String.valueOf(ledger.getWeek()));
//            param.put("month", String.valueOf(ledger.getMonth()));
//            param.put("year", String.valueOf(ledger.getYear()));
//        } else{
//            param.put("status", "failed!");
//            param.put("message", errorMsg);
//        }
//        return param;
//    }

    @PostMapping(value = "/add-order", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView addOrderHtml(@ModelAttribute("order") Ledger ledger,
                                     @RequestParam(value = "array_id_order", required = false) String[] array_id_order,
                                     @RequestParam(value = "array_qty", required = false) String[] array_qty,
                                     Authentication authentication){
        ModelAndView mav = new ModelAndView();
        String username = authentication.getName();
        int user_id = userDao.GetUserIdBerdasarkanUsername(username);
        ledger.setTipe("debit"); ledger.setKeperluan("penjualan menu");
        ledger.setWaktu(LocalDate.now().toString());
        ledger.setWeek(WeekGenerator.GetWeek(LocalDateTime.now().getDayOfMonth()));
        ledger.setMonth(LocalDateTime.now().getMonthValue());
        ledger.setYear(LocalDateTime.now().getYear());
        ledgerDao.Insert(ledger, restoran.getId(), user_id);
        int lastOrderId = ledgerDao.GetLastOrderId();
        for(int i=0; i<array_id_order.length; i++){
            orderedMenuDao.Insert(lastOrderId, Integer.parseInt(array_id_order[i]),
                    Integer.parseInt(array_qty[i]));
        }
//        int tanggal = LocalDateTime.now().getDayOfMonth();
//        int week = WeekGenerator.GetWeek(tanggal);

//        boolean isIncomeExists = incomeDao.isIncomeExists(restoran.getId());
//        if(!isIncomeExists){
//            incomeDao.Insert(restoran.getId_restaurant(), week, LocalDate.now().getMonthValue(), LocalDateTime.now().getYear(), ledger.getBiaya());
//            totalDebitKreditDao.Insert(0, restoran.getId_restaurant(), LocalDate.now().getMonthValue(), LocalDateTime.now().getYear(), ledger.getBiaya());
//            saldoDao.Update(0, restoran.getId_restaurant(), LocalDate.now().getMonthValue(), LocalDate.now().getYear(), ledger.getBiaya());
//        }
//        else {
//            incomeDao.Update(restoran.getId_restaurant(), ledger.getBiaya());
//            totalDebitKreditDao.Update(0, restoran.getId_restaurant(), LocalDate.now().getMonthValue(), LocalDateTime.now().getYear(), ledger.getBiaya());
//            saldoDao.Update(0, restoran.getId_restaurant(), LocalDate.now().getMonthValue(), LocalDate.now().getYear(), ledger.getBiaya());
//        }
        mav.setViewName("redirect:/cashier");
//        if(statusOrder==1 && statusOrderedMenu==1)
//            mav.setViewName("redirect:/cashier");
//        else
//            mav.setViewName("redirect:/cashier"); //show error disini, redirect ke page 404 kek.
        return mav;
    }
//    @PostMapping(value = "/receipt", produces = MediaType.APPLICATION_JSON_VALUE)
//    public Map<String, String> receiptPostJson(@ModelAttribute("restoran")Restoran restoran,
//                                           @ModelAttribute("receipt") Receipt receipt,
//                                           @ModelAttribute("order")Ledger order){
//        Map<String, String> param = new HashMap<>();
//        param.put("restaurant_name", restoran.getRestaurant_name());
//        param.put("restaurant_address", restoran.getRestaurant_address());
//        param.put("customer_name", order.getCustomer_name());
//        param.put("price_total", String.valueOf(order.getBiaya()));
//        param.put("cash_paid", String.valueOf(receipt.getCash_paid()));
//        param.put("cash_change", String.valueOf(receipt.getCash_change()));
//        return param;
//    }
//
//    @PostMapping(value = "/receipt", produces = MediaType.TEXT_HTML_VALUE)
//    public ModelAndView receiptPostHtml(@ModelAttribute("restoran") Restoran restoran,
//                                        @ModelAttribute("receipt") Receipt receipt,
//                                        @ModelAttribute("order") Ledger order){
//        ModelAndView mav = new ModelAndView();
//        mav.addObject("restaurant_name", restoran.getRestaurant_name());
//        mav.addObject("restaurant_address", restoran.getRestaurant_address());
//        mav.addObject("customer_name", order.getCustomer_name());
//        mav.addObject("price_total", String.valueOf(order.getBiaya()));
//        mav.addObject("cash_paid", String.valueOf(receipt.getCash_paid()));
//        mav.addObject("cash_change", String.valueOf(receipt.getCash_change()));
//        mav.setViewName("receipt");
//        return mav;
//    }
//
//    @GetMapping(value = "/receipt", produces = MediaType.TEXT_HTML_VALUE)
//    public ModelAndView receiptGetHtml(){
//        ModelAndView mav = new ModelAndView();
//        mav.setViewName("receipt");
//        return mav;
//    }
}
