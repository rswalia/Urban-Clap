package com.rohit.UrbanEase.allcontrollers;

import com.rohit.UrbanEase.vmm.RDBMS_TO_JSON;
import com.rohit.vmm.DBLoader;
import jakarta.servlet.http.HttpSession;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class VendorRestController {

    @Autowired
    private EmailSenderService service;

    /**
     * ********************** VENDOR SIGNUP
     * ************************************************************
     */
    @GetMapping("/renderCity")
    public String renderCity() {
        String ans = new RDBMS_TO_JSON().generateJSON("select cname from city");
        return ans;
    }

    @GetMapping("/renderService")
    public String renderService() {
        String ans = new RDBMS_TO_JSON().generateJSON("select sname from service");
        return ans;
    }

    @GetMapping("/renderSubServices")
    public String renderSubService(@RequestParam String sname) {
        String ans = new RDBMS_TO_JSON().generateJSON("select ssname from subservice where sname='" + sname + "'");
        return ans;
    }

    // NAME + DESC + PHOTO(file uploading) insert
    @PostMapping("/addVendor")
    public String addVendor(@RequestParam String vname,
            @RequestParam String vemail,
            @RequestParam String vpass,
            @RequestParam String vcity,
            @RequestParam String sname,
            @RequestParam String ssname,
            @RequestParam String vst,
            @RequestParam String vet,
            @RequestParam String vprice,
            @RequestParam String vlat,
            @RequestParam String vlong,
            @RequestParam String vcontact,
            @RequestParam String vdesc,
            @RequestParam MultipartFile vph) {
        String ans = "";

        String origname = vph.getOriginalFilename();

        try {

            ResultSet rs = DBLoader.executeSQL("select * from vendor where vname='" + vname + "'");

            if (rs.next()) {
                ans += "fail";
            } else {
                String projectpath = System.getProperty("user.dir");
                String internalpath = "/src/main/resources/static/";
                String folder = "myuploads/";

                byte[] b = vph.getBytes();
                FileOutputStream fos = new FileOutputStream(projectpath + internalpath + folder + origname);

                fos.write(b);
                fos.close();

                rs.moveToInsertRow();

                rs.updateString("vname", vname);
                rs.updateString("vemail", vemail);
                rs.updateString("vpass", vpass);
                rs.updateString("vcity", vcity);
                rs.updateString("vservice", sname);
                rs.updateString("vsservice", ssname);
                rs.updateString("vst", vst);
                rs.updateString("vet", vet);
                rs.updateString("vprice", vprice);
                rs.updateString("vlat", vlat);
                rs.updateString("vlong", vlong);
                rs.updateString("vcontact", vcontact);
                rs.updateString("vdesc", vdesc);
                rs.updateString("vphoto", folder + origname);
                rs.updateString("vstatus", "pending");

                rs.insertRow();

                ans += "success";
            }
        } catch (Exception e) {
            return e.toString();
        }

        return ans;
    }

    /**
     * ********************** VENDOR LOGIN
     * ************************************************************
     */
    @GetMapping("/checkVendorLogin")
    public String checkVendorLogin(@RequestParam String e, @RequestParam String p, HttpSession session) {
        String ans = "";

        try {
            ResultSet rs = DBLoader.executeSQL("select * from vendor where vemail='" + e + "' and vpass='" + p + "'");

            if (rs.next()) {
                ans += "success";

                session.setAttribute("vemail", e);

                // Set Extra Parameters in Session
                String vname = rs.getString("vname");
//                String vdesc = rs.getString("vdesc");
//                int vprice = rs.getInt("vprice");
//                int vst = rs.getInt("vst");
//                int vet = rs.getInt("vet");
//                String vcontact = rs.getString("vcontact");

                session.setAttribute("vname", vname);
//                session.setAttribute("vdesc", vdesc);
//                session.setAttribute("vprice", vprice);
//                session.setAttribute("vst", vst);
//                session.setAttribute("vet", vet);
//                session.setAttribute("vcontact", vcontact);

            } else {
                ans += "fail";
            }
        } catch (Exception ex) {
            ans += ex.toString();
        }

        return ans;
    }

    /**
     * ********************** VENDOR EDIT PROFILE
     * ************************************************************
     */
    // Onload
    @GetMapping("/fillDetails")
    public String fillDetails(HttpSession session) {
        String vemail = (String) session.getAttribute("vemail");   // Primary Key

        String ans = new RDBMS_TO_JSON().generateJSON("select * from vendor where vemail = '" + vemail + "' ");

        return ans;
    }

    @PostMapping("/editProfile")
    public String editProfile(@RequestParam String vemail,
            @RequestParam String vname,
            @RequestParam String vdesc,
            @RequestParam String vst,
            @RequestParam String vet,
            @RequestParam String vprice,
            @RequestParam String vcontact) {
        String ans = "";

        try {
            ResultSet rs = DBLoader.executeSQL("select * from vendor where vemail='" + vemail + "'");

            if (rs.next()) {
                rs.updateString("vname", vname);
                rs.updateString("vdesc", vdesc);
                rs.updateString("vst", vst);
                rs.updateString("vet", vet);
                rs.updateString("vprice", vprice);
                rs.updateString("vcontact", vcontact);

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

    /**
     * ********************** VENDOR ADD PHOTOS
     * ************************************************************
     */
    // PHOTO(file uploading) insert
    @PostMapping("/addPhoto")
    public String addPhoto(@RequestParam MultipartFile ph, HttpSession session) {
        String ans = "";

        String vemail = (String) session.getAttribute("vemail");

        String origname = ph.getOriginalFilename();

        try {
            ResultSet rs = DBLoader.executeSQL("select * from vendorphotos");

            String projectpath = System.getProperty("user.dir");
            String internalpath = "/src/main/resources/static/";
            String folder = "myuploads/";

            byte[] b = ph.getBytes();
            FileOutputStream fos = new FileOutputStream(projectpath + internalpath + folder + origname);

            fos.write(b);
            fos.close();

            rs.moveToInsertRow();

            rs.updateString("photo", folder + origname);
            rs.updateString("vemail", vemail);

            rs.insertRow();

            ans = "success";
        } catch (Exception e) {
            ans = e.toString();
        }

        return ans;
    }

    @GetMapping("/alreadyAddedPhotos")
    public String alreadyAddedPhotos(HttpSession session) {
        String vemail = (String) session.getAttribute("vemail");

        String ans = new RDBMS_TO_JSON().generateJSON("select * from vendorphotos where vemail='" + vemail + "'");

        return ans;
    }

    /**
     * ********************** VENDOR DELETE PHOTOS
     * ************************************************************
     */
    @GetMapping("/deletePhoto")
    public String deletePhoto(@RequestParam String pid) {
        String ans = "";

        try {
            ResultSet rs = DBLoader.executeSQL("select * from vendorphotos where pid='" + pid + "'");

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
     * ********************** VENDOR MANAGE SLOTS
     * ************************************************************
     */
    @GetMapping("/renderVendorBookings")
    public String renderVendorBookings(HttpSession session) {
//        SELECT DISTINCT b.date, u.name, u.email, b.total_price, b.payment_type, b.status
//        FROM user u, booking b
//        WHERE u.email = b.user_email
//        AND b.vendor_email='caquzuz@mailinator.com';

        String vendoremail = (String) session.getAttribute("vemail");

        String query = "SELECT DISTINCT b.booking_id, b.date, u.name, u.email, u.photo, b.total_price, b.payment_type, b.status FROM user u, booking b WHERE u.email = b.user_email AND b.vendor_email='" + vendoremail + "' ORDER BY b.booking_id DESC;";

        String ans = new RDBMS_TO_JSON().generateJSON(query);
        return ans;
    }

    @GetMapping("/renderBookingDetail")
    public String renderBookingDetail(@RequestParam String booking_id) {
        String query = "SELECT start_slot, end_slot FROM booking_detail WHERE booking_id='" + booking_id + "'";

        String ans = new RDBMS_TO_JSON().generateJSON(query);
        return ans;
    }

    @GetMapping("/approveUserPayment")
    public String approveUserPayment(@RequestParam int booking_id) {
        String ans = "";

        try {
            ResultSet rs = DBLoader.executeSQL("select * from booking where booking_id='" + booking_id + "'");
            
//            String useremail = rs.getString("user_email");
//            ResultSet rs2 = DBLoader.executeSQL("select * from user where email='" + useremail + "'");
            
            if (rs.next()) {
                rs.updateString("status", "approved");

                rs.updateRow();
                
//                String username = rs2.getString("name");

                String emailSubjectUser = "Confirmation of Your Service Booking with UrbanEase💐🎉";
                String emailBodyUser = "We are pleased to inform you that your booking request has been successfully approved by the vendor.\n"
                        + "\n"
                        + "Your service is now confirmed and scheduled. If you need to reschedule or have any specific requirements, please do not hesitate to contact us at support@urbanease.com or directly reach out to us at 9915273118.\n"
                        + "\n"
                        + "**What to Expect Next:**\n"
                        + "\n"
                        + "- Our professional will arrive at the specified location on the scheduled date and time.\n"
                        + "- You will receive a reminder notification 24 hours before the service appointment.\n"
                        + "- Please ensure that the service area is accessible and ready for our team member.\n"
                        + "\n"
                        + "Thank you for choosing UrbanEase. We are dedicated to providing you with a seamless and satisfying experience.\n"
                        + "\n"
                        + "Best regards,\n"
                        + "Customer Service Team  \n"
                        + "The UrbanEase Team";

                service.sendSimpleEmail("waliarohit2001@gmail.com", emailBodyUser, emailSubjectUser);

                ans = "success";
            } else {
                ans = "fail";
            }
        } catch (Exception ex) {
            ans = ex.toString();
        }

        return ans;
    }

    @GetMapping("/blockUserPayment")
    public String blockUserPayment(@RequestParam int booking_id) {
        String ans = "";

        try {
            ResultSet rs = DBLoader.executeSQL("select * from booking where booking_id='" + booking_id + "'");

            if (rs.next()) {
                rs.updateString("status", "pending");

                rs.updateRow();

                String emailSubjectUser = "Update on Your Service Booking with UrbanEase - Action Required 😞❗";
                String emailBodyUser = "We regret to inform you that your booking request has been declined by the vendor due to unforeseen circumstances.\n"
                        + "\n"
                        + "We understand this may be disappointing news and we sincerely apologize for any inconvenience this may cause. We are committed to helping you find an alternative solution as quickly as possible.\n"
                        + "\n"
                        + "**Next Steps:**\n"
                        + "\n"
                        + "- You can choose to rebook the same service with a different vendor through our platform.\n"
                        + "- If you need assistance with rebooking or have any other questions, please do not hesitate to contact our customer support team at support@urbanease.com. We are here to help you find the best available options.\n"
                        + "\n"
                        + "We value your understanding and patience in this matter. Thank you for choosing UrbanEase, and we look forward to assisting you with your service needs.\n"
                        + "\n"
                        + "Best regards,\n"
                        + "Customer Service Team  \n"
                        + "The UrbanEase Team";

                service.sendSimpleEmail("waliarohit2001@gmail.com", emailBodyUser, emailSubjectUser);

                ans = "success";
            } else {
                ans = "fail";
            }
        } catch (Exception ex) {
            ans = ex.toString();
        }

        return ans;
    }

    @GetMapping("/approveAllUsersPayment")
    public String approveAllUsersPayment() {
        String ans = "";

        try {
            ResultSet rs = DBLoader.executeSQL("SELECT * FROM booking");

            while (rs.next()) {
                rs.updateString("status", "approved");
                rs.updateRow();
            }

            ans = "success";
        } catch (Exception ex) {
            ans = "error: " + ex.toString();
        }

        return ans;
    }
}
