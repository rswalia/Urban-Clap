package com.rohit.UrbanEase.allcontrollers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController 
{
    @GetMapping("/adminLogin")
    public String adminLogin()
    {
        return "adminLogin";
    }
    
    @GetMapping("/adminHome")
    public String adminHome(HttpSession session)
    {
        String username = (String) session.getAttribute("adminusername");
        
        if(username==null)
        {
            return "redirect:/adminLogin";
        }
        else
        {
            return "adminHome";
        }
    }
    
    @GetMapping("/adminManageCities")
    public String adminManageCities(HttpSession session)
    {
        String username = (String) session.getAttribute("adminusername");
        
        if(username==null)
        {
            return "redirect:/adminLogin";
        }
        else
        {
            return "adminManageCities";
        }
    }
    
    @GetMapping("/adminManageServices")
    public String adminManageServices(HttpSession session)
    {
        String username = (String) session.getAttribute("adminusername");
        
        if(username==null)
        {
            return "redirect:/adminLogin";
        }
        else
        {
            return "adminManageServices";
        }
    }
    
    @GetMapping("/adminManageSubServices")
    public String adminManageSubServices(@RequestParam String sname, @RequestParam String sphoto, Model model, HttpSession session)
    {
        String username = (String) session.getAttribute("adminusername");
        
        if(username==null)
        {
            return "redirect:/adminLogin";
        }
        else
        {
            model.addAttribute("sname",sname);
            model.addAttribute("sphoto",sphoto);
        
            return "adminManageSubServices";
        }
        
    }
    
    @GetMapping("/adminManageVendors")
    public String adminManageVendors(HttpSession session)
    {
        String username = (String) session.getAttribute("adminusername");
        
        if(username==null)
        {
            return "redirect:/adminLogin";
        }
        else
        {
            return "adminManageVendors";
        }
    }
    
    @GetMapping("/adminLogout")
    public String adminLogout(HttpSession session)
    {
        session.removeAttribute("adminusername");
        
        return "redirect:/adminLogin";
    }
}