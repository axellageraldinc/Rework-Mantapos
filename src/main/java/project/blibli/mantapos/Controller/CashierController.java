package project.blibli.mantapos.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.TemplateEngine;
import project.blibli.mantapos.Config.Mail;
import project.blibli.mantapos.Model.*;
import project.blibli.mantapos.Service.CashierService;

import java.sql.SQLException;

@RestController
//Controller untuk cashier (akses URL /cashier, penerimaan order baru, dan pengiriman receipt melalui email)
public class CashierController {

    Mail mail = new Mail();

    CashierService cashierService;
    TemplateEngine templateEngine;

    @Autowired
    public CashierController(CashierService cashierService, TemplateEngine templateEngine){
        this.cashierService = cashierService;
        this.templateEngine = templateEngine;
    }

    @GetMapping(value = "/cashier", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView cashierHtml(Authentication authentication) throws SQLException {
        return cashierService.getMappingCashier(authentication);
    }

    //Mapping untuk menambah order dari cashier ke database
    @PostMapping(value = "/add-order", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView addOrderHtml(@ModelAttribute Ledger ledger,
                                     @RequestParam(value = "array_id_order", required = false) String[] array_id_order,
                                     @RequestParam(value = "array_qty", required = false) String[] array_qty,
                                     @RequestParam(value = "is_kirim_email_receipt", required = false) String is_kirim_email_receipt,
                                     @RequestParam(value = "email_kirim_receipt", required = false) String email_kirim_receipt,
                                     @RequestParam("namaResto") String nama_resto,
                                     @RequestParam("customer_name") String nama_customer,
                                     @RequestParam(value = "notes", required = false) String notes,
                                     Authentication authentication) throws SQLException {
        return cashierService.postMappingCashier(ledger, array_id_order, array_qty, is_kirim_email_receipt, email_kirim_receipt, nama_resto, nama_customer, authentication, notes, templateEngine, mail);
    }
}
