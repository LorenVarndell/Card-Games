package controllers;

import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;

@Path("Users/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)



public class Users {
    @POST
    @Path("add")
    public String UserIDAdd() {
        int random_int = (int) (Math.random() * (9999999 - 999999 + 1) + 999999);
        // String random_intTotal = Integer.toString(random_int) + "" + Integer.toString(random_int2);
        //System.out.println(random_intTotal);
        System.out.println(random_int);
        System.out.println("Invoked Blackjack.SessionIDAdd()");
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


    }
}