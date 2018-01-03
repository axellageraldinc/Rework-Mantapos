package project.blibli.mantapos.Service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

@Service
public class LoginService {
    public ModelAndView getMappingLogin(String error,
                                        String logout){
        ModelAndView mav = new ModelAndView();
        mav.setViewName("login");
        //jika dalam URL itu ada parameter error (/login?error), maka ada error, maka tambahkan object error ke mav. Nantinya akan di-check di login.html
        if(error!=null)
            mav.addObject("error", "Invalid username and/or password");
            //jika dalam URL itu ada parameter logout (/login?logout), maka user berhasil logout, maka tambahkan object logout ke mav. Nantinya akan di-check di login.html
        else if(logout!=null)
            mav.addObject("logout", "You've been logged out successfully!");
        return mav;
    }

    public ModelAndView getMappingIndexPageDefaultSuccessLogin(Authentication authentication){
        ModelAndView mav = new ModelAndView();
        if(authentication == null){
            mav.setViewName("redirect:/login");
        }
        else if(authentication.getAuthorities().toString().equals("[cashier]"))
            mav.setViewName("redirect:/cashier");
        else if(authentication.getAuthorities().toString().equals("[manager]") || authentication.getAuthorities().toString().equals("[owner]")){
            mav.setViewName("redirect:/dashboard");
        }
        else if(authentication.getAuthorities().toString().equals("[admin]")) {
            mav.setViewName("redirect:/restaurant");
        }
        return mav;
    }

    public ModelAndView getMappingAccessDeniedPage(Authentication authentication){
        ModelAndView mav = new ModelAndView();
        mav.setViewName("privilege");
        mav.addObject("privilege", "You don't have permission to access this page");
        String username = authentication.getName();
        mav.addObject("username", username);
        String role = String.valueOf(authentication.getAuthorities());
        mav.addObject("role", role);
        return mav;
    }

}
