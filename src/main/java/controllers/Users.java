package controllers;

import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path("Users/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)



public class Users {
    @POST
    @Path("delete/{UserID}")
    public String UserIDAdd(@PathParam("UserID") Integer UserIDclient) {
        System.out.println(UserIDclient);
        int random_int = (int) (Math.random() * (9999999 - 999999 + 1) + 999999);
        // String random_intTotal = Integer.toString(random_int) + "" + Integer.toString(random_int2);
        //System.out.println(random_intTotal);
        System.out.println(random_int);
        System.out.println("Invoked Blackjack.UserIDAdd()");
        /*if (UserIDclient == 1) {
            try {
                PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Users (UserID) VALUES (?)");
                ps.setInt(1, (int) random_int);
                JSONObject response = new JSONObject();
                response.put("UserID", (int) random_int);
                ps.execute();
                return response.toString();
            } catch (Exception exception) {
                System.out.println("Database error: " + exception.getMessage());
                return "{\"Error\": \"Unable to create new item, please see server console for more info.\"}";
            }
        }  else { */
        try {
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Users SET UserID = ?  WHERE UserID = ?");
            ps.setInt(1, random_int);
            ps.setInt(2, UserIDclient);
            ps.execute();
            //System.out.println("test");
            JSONObject response = new JSONObject();
            response.put("UserID", random_int);

            PreparedStatement ps1 = Main.db.prepareStatement("SELECT UserID FROM Users WHERE UserID = ?");
            ps1.setInt(1, random_int);
            ResultSet results = ps1.executeQuery();
            if (results.next() == true) {
                System.out.println("test");
                return response.toString();
            } else {
                PreparedStatement ps2 = Main.db.prepareStatement("INSERT INTO Users (UserID) VALUES (?)");
                ps2.setInt(1, (int) random_int);
                JSONObject response2 = new JSONObject();
                response2.put("UserID", (int) random_int);
                ps2.execute();
                return response2.toString();
            }

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to create new item, please see server console for more info.\"}";
        }
    }

}
