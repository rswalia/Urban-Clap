package com.rohit.UrbanEase.allcontrollers;

import com.rohit.UrbanEase.model.VendorPhotos;
import com.rohit.vmm.DBLoader;
import jakarta.servlet.http.HttpSession;
import java.sql.ResultSet;
import java.util.ArrayList;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController 
{
    @GetMapping("/")
    public String go1()
    {   
        return "index"; 
    }
    
//    @GetMapping("/welcome")
//    public String welcome()
//    {
//        return "welcome_page";
//    }
    
    @GetMapping("/userSignup")
    public String userSignup()
    {
        return "userSignup";
    }
    
    @GetMapping("/userLogin")
    public String userLogin()
    {
        return "userLogin";
    }
    
    @GetMapping("/userService")
    public String userService(@RequestParam String cname, Model model, HttpSession session)
    {
        String useremail = (String) session.getAttribute("useremail");
        if(useremail==null)
        {
            return "redirect:/userLogin";
        }
        else
        {
            model.addAttribute("cname", cname);
            return "userService";
        }
    }
    
    @GetMapping("/userSubService")
    public String userSubService(@RequestParam String sname, @RequestParam String cname, Model model, HttpSession session)
    {
        String useremail = (String) session.getAttribute("useremail");
        
        if(useremail==null)
        {
            return "redirect:/userLogin";
        }
        else
        {
            model.addAttribute("cname", cname);
            model.addAttribute("sname", sname);

            return "userSubService";
        }
        
    }
    
    @GetMapping("/userShowVendors")
    public String userShowVendors(@RequestParam String cname, @RequestParam String sname, @RequestParam String ssname, Model model, HttpSession session)
    {
        String useremail = (String) session.getAttribute("useremail");
        
        if(useremail==null)
        {
            return "redirect:/userLogin";
        }
        else
        {
            model.addAttribute("cname", cname);
            model.addAttribute("sname", sname);
            model.addAttribute("ssname", ssname);

            return "userShowVendors";
        }
    } 
    
    @GetMapping("/userVendorDetails")
    public String userVendorDetails(@RequestParam String vemail, Model model, HttpSession session)
    {
        String useremail = (String) session.getAttribute("useremail");
        
        if(useremail==null)
        {
            return "redirect:/userLogin";
        }
        else
        {
            ArrayList<VendorPhotos> al = new ArrayList<>();
        
            try
            {
                ResultSet rs = DBLoader.executeSQL("select * from vendorphotos where vemail='"+vemail+"'");
                while(rs.next())
                {
                    int id = rs.getInt("pid");
                    String ph = rs.getString("photo");

                    al.add(new VendorPhotos(id, ph, vemail));
                }
                model.addAttribute("al",al);
                model.addAttribute("vemail",vemail);
            }
            catch(Exception ex)
            {
                String ans = ex.toString();
                model.addAttribute("ans",ans);
                return "fail";
            }

            return "userVendorDetails";
        }
    } 
    
    @GetMapping("/userVendorBookSlot")
    public String userVendorBookSlot(@RequestParam String vemail, Model model, HttpSession session)
    {
        String useremail = (String) session.getAttribute("useremail");
        
        if(useremail==null)
        {
            return "redirect:/userLogin";
        }
        else
        {
            System.out.println(vemail);
            model.addAttribute("vemail",vemail);

            return "userVendorBookSlot";
        }
        
    } 
    
    @GetMapping("/payment")
    public String payment(@RequestParam String date, @RequestParam String email, @RequestParam String total, @RequestParam String slots, Model model, HttpSession session)
    {
        
        String useremail = (String) session.getAttribute("useremail");
        
        if(useremail==null)
        {
            return "redirect:/userLogin";
        }
        else
        {
            model.addAttribute("date",date);
            model.addAttribute("email",email);
            model.addAttribute("total",total);
            model.addAttribute("slots",slots);

            return "payment";
        }
    }
    
    @GetMapping("/userLogout")
    public String userLogout(HttpSession session)
    {
        session.removeAttribute("useremail");
        session.removeAttribute("username");
        
        return "redirect:/";
    }
    
    @GetMapping("/paymentdoneicon")
    public String paymentdoneicon()
    {
        return "/payment_done_icon";
    }
    
    @GetMapping("/userViewBookings")
    public String userViewBookings(HttpSession session)
    {
        String useremail = (String) session.getAttribute("useremail");
        
        if(useremail==null)
        {
            return "redirect:/userLogin";
        }
        else
        {
            return "userViewBookings";
        }
    }
    
}