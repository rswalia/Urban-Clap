package com.rohit.UrbanEase.allcontrollers;

import com.rohit.vmm.DBLoader;
import com.rohit.UrbanEase.vmm.RDBMS_TO_JSON;
import jakarta.servlet.http.HttpSession;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class AdminRestController {

    @GetMapping("/checkAdminLogin")
    public String go1(@RequestParam String u, @RequestParam String p, HttpSession session) {
        String ans = "";

        try {
            ResultSet rs = DBLoader.executeSQL("select * from admin where username='" + u + "' and password='" + p + "'");

            if (rs.next()) {
                ans += "success";
                session.setAttribute("adminusername", u);
            } else {
                ans += "fail";
            }
        } catch (Exception ex) {
            ans += ex.toString();
        }

        return ans;
    }

    /**
     * ****************** Admin Manage Cities ***************************************************
     */
    // NAME + DESC insert
    @GetMapping("/addCity1")
    public String go2(@RequestParam String cname, @RequestParam String cdesc) {
        String ans = "";

        try {
            ResultSet rs = DBLoader.executeSQL("select * from city where cname='" + cname + "'");

            if (rs.next()) {
                ans += "Duplicate Value !!!";
            } else {
                // insert row
                rs.moveToInsertRow();

                rs.updateString("cname", cname);
                rs.updateString("cdesc", cdesc);

                rs.insertRow();

                ans += "Inserted Successfully ...";
            }
        } catch (Exception ex) {
            ans += ex.toString();
        }

        return ans;
    }

    // NAME + DESC + PHOTO(file uploading) insert
    @PostMapping("/addCity")
    public String go3(@RequestParam String cname, @RequestParam String cdesc, @RequestParam MultipartFile ph) {
        String ans = "";

        String origname = ph.getOriginalFilename();

        try {

            ResultSet rs = DBLoader.executeSQL("select * from city where cname='" + cname + "'");

            if (rs.next()) {
                ans += "fail";
            } else {
                String projectpath = System.getProperty("user.dir");
                String internalpath = "/src/main/resources/static/";
                String folder = "myuploads/";

                byte[] b = ph.getBytes();
                FileOutputStream fos = new FileOutputStream(projectpath + internalpath + folder + origname);

                fos.write(b);
                fos.close();

                rs.moveToInsertRow();

                rs.updateString("cname", cname);
                rs.updateString("cdesc", cdesc);
                rs.updateString("cphoto", folder + origname);

                rs.insertRow();

                ans += "success";
            }
        } catch (Exception e) {
            return e.toString();
        }

        return ans;
    }

    @GetMapping("/alreadyAddedCities")
    public String go4() {
        String ans = new RDBMS_TO_JSON().generateJSON("select * from city");
        return ans;
    }

    @GetMapping("/deleteCity")
    public String go5(@RequestParam String cname) {
        String ans = "";

        try {
            ResultSet rs = DBLoader.executeSQL("select * from city where cname='" + cname + "'");
            if (rs.next()) {
                rs.deleteRow();

                ans = "success";
            }
        } catch (Exception ex) {
            ans += ex.toString();

        }
        return ans;
    }

    /**
     * ****************** Admin Manage SERVICES ***************************************************
     */
    
    // NAME + DESC + PHOTO(file uploading) insert
    @PostMapping("/addService")
    public String addService(@RequestParam String sname, @RequestParam String sdesc, @RequestParam MultipartFile ph) {
        String ans = "";

        String origname = ph.getOriginalFilename();

        try {
            ResultSet rs = DBLoader.executeSQL("select * from service where sname='" + sname + "'");

            if (rs.next()) {
                ans = "fail";
            } else {
                String projectpath = System.getProperty("user.dir");
                String internalpath = "/src/main/resources/static/";
                String folder = "myuploads/";

                byte[] b = ph.getBytes();
                FileOutputStream fos = new FileOutputStream(projectpath + internalpath + folder + origname);

                fos.write(b);
                fos.close();

                rs.moveToInsertRow();

                rs.updateString("sname", sname);
                rs.updateString("sdesc", sdesc);
                rs.updateString("sphoto", folder + origname);

                rs.insertRow();

                ans = "success";
            }
        } catch (Exception e) {
            ans = e.toString();
        }

        return ans;
    }

    @GetMapping("/alreadyAddedServices")
    public String alreadyAddedServices() {
        String ans = new RDBMS_TO_JSON().generateJSON("select * from service");

        return ans;
    }

    @GetMapping("/deleteService")
    public String deleteService(@RequestParam String sname) {
        String ans = "";

        try {
            ResultSet rs = DBLoader.executeSQL("select * from service where sname='" + sname + "'");

            if (rs.next()) {
                rs.deleteRow();

                ans = "success";
            } else {
                ans = "fail";
            }
        } catch (Exception ex) {
            ans = ex.toString();
        }

        return ans;
    }

    /**
     * ****************** Admin Manage SUB SERVICES ***************************************************
     */
    
    // NAME + DESC + PHOTO(file uploading) insert
    @PostMapping("/addSubService")
    public String addSubService(@RequestParam String ssname, @RequestParam String ssdesc, @RequestParam String sname, @RequestParam MultipartFile ph) {
        String ans = "";

        String origname = ph.getOriginalFilename();

        try {
            ResultSet rs = DBLoader.executeSQL("select * from subservice where ssname='" + ssname + "'");

            if (rs.next()) {
                ans = "fail";
            } else {
                String projectpath = System.getProperty("user.dir");
                String internalpath = "/src/main/resources/static/";
                String folder = "myuploads/";

                byte[] b = ph.getBytes();
                FileOutputStream fos = new FileOutputStream(projectpath + internalpath + folder + origname);

                fos.write(b);
                fos.close();

                rs.moveToInsertRow();

                rs.updateString("ssname", ssname);
                rs.updateString("ssdesc", ssdesc);
                rs.updateString("ssphoto", folder + origname);
                rs.updateString("sname", sname);

                rs.insertRow();

                ans = "success";
            }
        } catch (Exception e) {
            ans = e.toString();
        }

        return ans;
    }

    @GetMapping("/alreadyAddedSubServices")
    public String alreadyAddedSubServices(@RequestParam String sname) {
        String ans = new RDBMS_TO_JSON().generateJSON("select * from subservice where sname = '" + sname + "' ");

        return ans;
    }

    @GetMapping("/deleteSubService")
    public String deleteSubService(@RequestParam String ssname) {
        String ans = "";

        try {
            ResultSet rs = DBLoader.executeSQL("select * from subservice where ssname='" + ssname + "'");

            if (rs.next()) {
                rs.deleteRow();

                ans = "success";
            } else {
                ans = "fail";
            }
        } catch (Exception ex) {
            ans = ex.toString();
        }

        return ans;
    }

    /**
     * ****************** Admin Manage VENDORS ***************************************************
     */
    
    @GetMapping("/renderVendors")
    public String renderVendors() {
        String ans = new RDBMS_TO_JSON().generateJSON("select * from vendor");

        return ans;
    }

    @GetMapping("/approveVendor")
    public String approveVendor(@RequestParam String vemail) {
        String ans = "";

        try 
        {
            ResultSet rs = DBLoader.executeSQL("select * from vendor where vemail='" + vemail + "'");

            if (rs.next())
            {
                rs.updateString("vstatus", "approved");

                rs.updateRow();

                ans = "success";
            } else {
                ans = "fail";
            }
        }
        catch (Exception ex) {
            ans = ex.toString();
        }

        return ans;
    }

    @GetMapping("/blockVendor")
    public String blockVendor(@RequestParam String vemail) {
        String ans = "";

        try {
            ResultSet rs = DBLoader.executeSQL("select * from vendor where vemail='" + vemail + "'");

            if (rs.next()) {
                rs.updateString("vstatus", "pending");

                rs.updateRow();

                ans = "success";
            } else {
                ans = "fail";
            }
        } catch (Exception ex) {
            ans = ex.toString();
        }

        return ans;
    }

    @GetMapping("/approveAllVendors")
    public String approveAllVendors() {
        String ans = "";

        try {
            ResultSet rs = DBLoader.executeSQL("SELECT * FROM vendor");
            
            while(rs.next())
            {
                rs.updateString("vstatus", "approved");
                rs.updateRow();
            }

            ans = "success";
        } catch (Exception ex) {
            ans = "error: " + ex.toString();
        }

        return ans;
    }

}
