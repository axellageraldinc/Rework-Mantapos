package project.blibli.mantapos.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import project.blibli.mantapos.Model.Restoran;
import project.blibli.mantapos.Model.User;
import project.blibli.mantapos.Service.AdminService;

import java.sql.SQLException;

@RestController
public class AdminController {

    AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService){
        this.adminService = adminService;
    }

    @GetMapping(value = "/restaurant", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView restaurantListHtml(@RequestParam(value = "page", required = false) Integer page) throws SQLException {
        return adminService.getMappingRestoran(page);
    }

    @PostMapping(value = "/add-restaurant", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView addRestaurantPost(@ModelAttribute("restoran") Restoran restoran,
                                          @ModelAttribute("user") User user) throws SQLException {
        return adminService.postMappingRestoran(restoran, user);
    }

}
