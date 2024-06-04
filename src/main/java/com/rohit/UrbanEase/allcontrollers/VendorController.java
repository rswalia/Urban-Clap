package com.rohit.UrbanEase.allcontrollers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VendorController 
{
    @GetMapping("/vendorLogin")
    public String vendorLogin()
    {
        return "vendorLogin";
    }
    
    @GetMapping("/vendorSignup")
    public String vendorSignup()
    {
        return "vendorSignup";
    }
    
    @GetMapping("/vendorHome")
    public String vendorHome(HttpSession session)
    {
        String vemail = (String) session.getAttribute("vemail");
        
        if(vemail==null)
        {
            return "redirect:/vendorLogin";
        }
        else
        {
            return "vendorHome";
        }
    }
    
    @GetMapping("/vendorEditProfile")
    public String vendorEditProfile(HttpSession session)
    {
        String vemail = (String) session.getAttribute("vemail");
        
        if(vemail==null)
        {
            return "redirect:/vendorLogin";
        }
        else
        {
            return "vendorEditProfile";
        }
    }
    
    @GetMapping("/vendorManagePhotos")
    public String vendorManagePhotos(HttpSession session)
    {
        String vemail = (String) session.getAttribute("vemail");
        
        if(vemail==null)
        {
            return "redirect:/vendorLogin";
        }
        else
        {
            return "vendorManagePhotos";
        }
    }
    
    @GetMapping("/vendorLogout")
    public String vendorLogout(HttpSession session)
    {
        session.removeAttribute("vemail");
        session.removeAttribute("vname");
        
        return "redirect:/vendorLogin";
    }
    
    @GetMapping("/vendorManageSlots")
    public String vendorManageSlots(HttpSession session)
    {
        String vemail = (String) session.getAttribute("vemail");
        
        if(vemail==null)
        {
            return "redirect:/vendorHome";
        }
        else
        {
            return "vendorManageSlots";
        }
    }
    
}