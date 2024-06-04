package com.rohit.UrbanEase.allcontrollers;

import com.rohit.UrbanEase.vmm.RDBMS_TO_JSON;
import com.rohit.vmm.DBLoader;
import jakarta.servlet.http.HttpSession;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.util.StringTokenizer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UserRestController {

    @Autowired
    private EmailSenderService service;

    // NAME + DESC + PHOTO(file uploading) insert
    @PostMapping("/addUser")
    public String addUser(
            @RequestParam String uname,
            @RequestParam String uemail,
            @RequestParam String upass,
            @RequestParam String uaddress,
            @RequestParam String ucontact,
            @RequestParam MultipartFile ph) {
        String ans = "";

        String origname = ph.getOriginalFilename();

        try {

            ResultSet rs = DBLoader.executeSQL("select * from user where email='" + uemail + "'");

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

                rs.updateString("name", uname);
                rs.updateString("email", uemail);
                rs.updateString("pass", upass);
                rs.updateString("address", uaddress);
                rs.updateString("contact", ucontact);
                rs.updateString("photo", folder + origname);

                rs.insertRow();

                ans += "success";
            }
        } catch (Exception e) {
            return e.toString();
        }

        return ans;
    }

    @GetMapping("/checkUserLogin")
    public String checkUserLogin(@RequestParam String e, @RequestParam String p, HttpSession session) {
        String ans = "";

        try 
        {
            ResultSet rs = DBLoader.executeSQL("select * from user where email='" + e + "' and pass='" + p + "'");

            if (rs.next())
            {
                ans += "success";
                session.setAttribute("useremail", e);

                String username = rs.getString("name");
                session.setAttribute("username", username);
            } else {
                ans += "fail";
            }
        }
        catch (Exception ex) {
            ans += ex.toString();
        }

        return ans;
    }

    @GetMapping("/renderUserCity")
    public String renderUserCity() {
        String ans = new RDBMS_TO_JSON().generateJSON("select * from city");
        return ans;
    }

    @GetMapping("/renderUserService")
    public String renderUserService(@RequestParam String cname) {
        String query
                = " SELECT DISTINCT sname,sphoto"
                + " FROM "
                + " vendor v INNER JOIN service s"
                + " ON v.vservice=s.sname"
                + " WHERE vcity='" + cname + "'";

        String ans = new RDBMS_TO_JSON().generateJSON(query);
//        String ans = new RDBMS_TO_JSON().generateJSON("select DISTINCT sname,sphoto from vendor v INNER JOIN service s ON v.vservice=s.sname where vcity='" + cname + "'");
        return ans;
    }

    @GetMapping("/renderUserSubService")
    public String renderUserSubService(@RequestParam String sname, @RequestParam String cname) {
        String ans = new RDBMS_TO_JSON().generateJSON("select DISTINCT ssname,ssphoto from vendor v INNER JOIN subservice s ON v.vsservice=s.ssname where vservice='" + sname + "' and vcity='" + cname + "'");
        return ans;
    }

    @GetMapping("/renderUserVendors")
    public String renderVendors(@RequestParam String cname, @RequestParam String sname, @RequestParam String ssname) {
        String ans = new RDBMS_TO_JSON().generateJSON("select vname,vphoto,vemail from vendor where vcity='" + cname + "' and vservice='" + sname + "' and vsservice='" + ssname + "' and vstatus ='approved' ;");
        return ans;
    }

    @GetMapping("/renderVendorDetails")
    public String renderVendorDetails(@RequestParam String vemail) {
        String ans = new RDBMS_TO_JSON().generateJSON("select * from vendor where vemail='" + vemail + "'");
        return ans;
    }

    @GetMapping("/view_slots")
    public String view_slots(@RequestParam String email, @RequestParam String date) {
        try {
            ResultSet rs = DBLoader.executeSQL("select * from vendor where vemail='" + email + "'");

            String start;
            String end;
            String slot;
            if (rs.next()) {
                start = rs.getString("vst");
                end = rs.getString("vet");
                slot = rs.getString("vprice");

            } else {
                String err = "failed";
                return err;
            }
            int Start = Integer.parseInt(start);
            int End = Integer.parseInt(end);
            int Slot = Integer.parseInt(slot);
            JSONObject ans = new JSONObject();
            //Define JSONArray
            JSONArray arr = new JSONArray();
            for (int i = Start; i <= End; i++) {
                JSONObject row = new JSONObject();
                row.put("start_slot", Start);
                row.put("end_slot", ++Start);
                row.put("slot_amount", slot);

                ResultSet rs2 = DBLoader.executeSQL("select * from booking_detail where start_slot ='" + i + "' and booking_id in (select booking_id from booking where date=\'" + date + "\' and vendor_email =\'" + email + "\' ) ");
                if (rs2.next()) {
                    row.put("status", "Booked");
                } else {
                    row.put("status", "Available");
                }

                arr.add(row);
            }
            ans.put("ans", arr);
            System.out.println(ans.toString());
            return (ans.toJSONString());

        } catch (Exception e) {
            return e.toString();
        }

    }

    @GetMapping("/pay")
    public String payment(@RequestParam String date, @RequestParam String v_email, @RequestParam String amount, @RequestParam String slots,
            HttpSession session, @RequestParam String type, @RequestParam String status) {
        String ans = "";
        String useremail = (String) (session.getAttribute("useremail"));

        try 
        {
            ResultSet rs = DBLoader.executeSQL("select * from  booking");

            rs.moveToInsertRow();
            rs.updateString("date", date);
            rs.updateString("vendor_email", v_email);
            rs.updateString("user_email", useremail);
            rs.updateString("total_price", amount);
            rs.updateString("payment_type", type);
            rs.updateString("status", status);
            rs.insertRow();

            int bookingid = 0;
            ResultSet rs2 = DBLoader.executeSQL("select MAX(booking_id) from booking");
            if (rs2.next()) {
                bookingid = rs2.getInt("MAX(booking_id)");
                System.out.println(bookingid);
            }

            ResultSet rs4 = DBLoader.executeSQL("select * from user where email='" + useremail + "'");
            String username = "";
            String userAddress = "";
            String userPhone = "";
            if (rs4.next()) {
                username = rs4.getString("name");
                userAddress = rs4.getString("address");
                userPhone = rs4.getString("contact");
            }

            ResultSet rs5 = DBLoader.executeSQL("select * from vendor where vemail='" + v_email + "'");
            String vsservice = "";
            String vcontact = "";
            String vname = "";
            String vphoto = "";
            if (rs5.next()) {
                vsservice = rs5.getString("vsservice");
                vcontact = rs5.getString("vcontact");
                vname = rs5.getString("vname");
                vphoto = rs5.getString("vphoto");
            }

            StringTokenizer st = new StringTokenizer(slots, ",");
            String slotTime = "";
            while (st.hasMoreTokens()) {
                int stslot = Integer.parseInt(st.nextToken());
                int endslot = stslot + 1;
                ResultSet rs3 = DBLoader.executeSQL("select * from booking_detail");
                rs3.moveToInsertRow();
                rs3.updateInt("start_slot", stslot);
                rs3.updateInt("end_slot", endslot);
                rs3.updateInt("booking_id", bookingid);
                rs3.insertRow();
                slotTime += stslot + "-" + endslot + " , ";
            }

//            String emailSubjectUser = "Confirmation of Your Service Booking with UrbanEase💐🎉";
            String emailSubjectUser = "Your Service Request Has Been Received";
            String emailBodyUser = "Dear " + username + ",\n"
                    + "\n"
                    + "Thank you for choosing UrbanEase for your " + vsservice + " needs. We are pleased to inform you that your booking request has been successfully received.\n"
                    + "\n"
                    + "Booking Details-:\n"
                    + "\n"
                    + "Service: " + vsservice + "\n"
                    + "Vendor Name: " + vname + "\n"
                    + "Vendor Contact: " + vcontact + "\n"
                    + "Date: " + date + "\n"
                    + "Time: " + slotTime + "\n"
                    + "Total Price: " + amount + "\n"
                    + "Payment Type: " + type + "\n"
                    + "Status: " + status + "\n\n"
                    + "We are now in the process of contacting the vendor to confirm the availability of the service you have requested. You will receive a follow-up email once your booking has been approved by the vendor.\n"
                    + "\n"
                    + "In the meantime, if you have any questions or need to make any changes to your booking, please feel free to reach out to our customer support team at support@urbanease.com or call us at 9915273118\n"
                    + "\n"
                    + "Thank you for choosing UrbanEase. We look forward to providing you with exceptional service.\n"
                    + "\n"
                    + "Best regards,\n"
                    + "Customer Service Team\n"
                    + "UrbanEase";

            service.sendSimpleEmail("waliarohit2001@gmail.com", emailBodyUser, emailSubjectUser);
//            service.sendEmailWithAttachment("waliarohit2001@gmail.com", emailBody, emailSubject, vphoto);

            String emailSubjectVendor = "New Service Booking Confirmation – " + vsservice + "";
            String emailbodyVendor = "Dear " + vname + ",\n"
                    + "\n"
                    + "We are pleased to inform you that a new booking for your service has been successfully processed through UrbanEase. Below are the details of the booking:\n"
                    + "\n"
                    + "Booking Details:\n"
                    + "\n"
                    + "Service: " + vsservice + "\n"
                    + "Customer Name: " + username + "\n"
                    + "Date: " + date + "\n"
                    + "Time: " + slotTime + "\n"
                    + "Address: " + userAddress + "\n"
                    + "Email: " + useremail + "\n"
                    + "Phone Number: " + userPhone + "\n"
                    + "\n"
                    + "Please ensure that you are prepared to provide the service at the scheduled time and location. Your professionalism and dedication are essential to maintaining the high standards of service that UrbanEase promises to our clients.\n"
                    + "\n"
                    + "Next Steps:\n"
                    + "\n"
                    + "Confirm your availability for this booking by responding to this email.\n"
                    + "Contact the customer directly if you need additional information or to discuss any special requirements.\n"
                    + "Arrive at the service location at least 10 minutes before the scheduled time to ensure punctuality.\n"
                    + "Important Information:\n"
                    + "\n"
                    + "If you need to modify or cancel this booking, please notify us immediately to make alternative arrangements for the customer.\n"
                    + "Any changes to the booking must be communicated promptly to avoid inconveniences.\n"
                    + "Thank you for your continued partnership with UrbanEase. We look forward to another successful service delivery.\n"
                    + "\n"
                    + "Should you have any questions or need further assistance, please do not hesitate to contact us at support@urbanease.com or call us at 9915273118.\n"
                    + "\n"
                    + "Best regards,\n"
                    + "Vendor Relations Team\n"
                    + "UrbanEase";

            service.sendSimpleEmail("waliarohit2001@gmail.com", emailbodyVendor, emailSubjectVendor);

            ans = ans + "success";
            return ans;
        }
        catch (Exception ex) {
            return ex.toString();
        }

    }

    @GetMapping("/renderBookings")
    public String renderBookings(HttpSession session) {
//        SELECT DISTINCT b.date, v.vphoto, v.vname, v.vservice, vsservice, bd.start_slot, bd.end_slot, b.total_price, b.payment_type, b.status
//        FROM vendor v, booking b, booking_detail bd
//        WHERE v.vemail = b.vendor_email
//        AND b.booking_id = bd.booking_id
//        AND b.user_email='waliarohit2001@gmail.com';

        String useremail = (String) session.getAttribute("useremail");

//        String query = "SELECT DISTINCT b.date, v.vphoto, v.vname, v.vservice, vsservice, bd.start_slot, bd.end_slot, b.total_price, b.payment_type FROM vendor v, booking b, booking_detail bd WHERE v.vemail = b.vendor_email AND b.booking_id = bd.booking_id AND b.user_email='"+ useremail +"'";
        String query = "SELECT DISTINCT b.booking_id, b.date, v.vphoto, v.vname, v.vservice, vsservice, b.total_price, b.payment_type, b.status FROM vendor v, booking b WHERE v.vemail = b.vendor_email AND b.user_email='" + useremail + "' ORDER BY b.booking_id DESC";

        String ans = new RDBMS_TO_JSON().generateJSON(query);
        return ans;
    }

    @GetMapping("/submitRating")
    public String submitRating(@RequestParam String rev, @RequestParam int n, @RequestParam String vemail, HttpSession session)
    {
        String ans = "";
        try
        {
            String useremail = (String) session.getAttribute("useremail");
            ResultSet rs = DBLoader.executeSQL("select * from review where useremail='"+useremail+"'");
            
                rs.moveToInsertRow();
                
                rs.updateInt("rating",n);
                rs.updateString("comment",rev);
                rs.updateString("vendoremail",vemail);
                rs.updateString("useremail",useremail);
                
                rs.insertRow();
                
                ans = "success";
            
        }
        catch(Exception ex)
        {
            ans = ex.toString();
        }
        return ans;
    }
    
    @GetMapping("/userVendorAverageRating")
    public String userVendorAverageRating(@RequestParam String vemail)
    {
        String ans = "";
        try 
        {
            ans = new RDBMS_TO_JSON().generateJSON("select avg(rating) as r1 from review where vendoremail='"+vemail+"'");
        } 
        catch (Exception e) 
        {
            ans = e.toString();
        }
        return ans;
    }
}
