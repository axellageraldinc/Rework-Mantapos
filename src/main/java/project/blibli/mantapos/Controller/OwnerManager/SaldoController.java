package project.blibli.mantapos.Controller.OwnerManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import project.blibli.mantapos.Model.Saldo;
import project.blibli.mantapos.Service.OwnerManager.SaldoService;

@RestController
public class SaldoController {

    SaldoService saldoService;

    @Autowired
    public SaldoController (SaldoService saldoService){
        this.saldoService = saldoService;
    }

    //Jika user akses /saldo/page, sama dengan menu, outcome, dan employee untuk paging-nya
    @GetMapping(value = "/saldo")
    public ModelAndView addSaldoAwalHtml(@RequestParam(value = "page", required = false) Integer page,
                                         Authentication authentication){
        return saldoService.getMappingSaldoAwal(authentication, page);
    }

    //Jika user menambahkan saldo baru
    @PostMapping(value = "/saldo-post", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView addSaldoAwalHtml(Authentication authentication,
                                         @ModelAttribute("saldoAwal") Saldo saldo){
        return saldoService.postMappingAddSaldoAwal(authentication, saldo);
    }

}
