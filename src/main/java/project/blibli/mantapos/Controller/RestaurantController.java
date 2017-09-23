package project.blibli.mantapos.Controller;

import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.blibli.mantapos.Beans_Model.Restaurant;
import project.blibli.mantapos.Dao.RestaurantDao;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
public class RestaurantController {
    @PostMapping(value = "/add-restaurant", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> addRestoJson(@Valid @ModelAttribute("restaurant") Restaurant restaurant,
                                            BindingResult bindingResult){
        Map<String, String> param = new HashMap<>();
        int status = RestaurantDao.Insert(restaurant);
        if (status==1){
            param.put("status", "success!");
            param.put("resto_name", restaurant.getRestaurantName());
            param.put("resto_address", restaurant.getRestaurantAddress());
        } else if(bindingResult.hasErrors()){
            param.put("status", "failed");
        }
        return param;
    }
}
