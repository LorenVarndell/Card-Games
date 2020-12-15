package controllers;

import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


@Path("BlackjackUpdate/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class BlackjackUpdate {

    @POST
    @Path("add/{UserID}")
    public String getUpdate(@PathParam("UserID") Integer UserIDclient) {
        System.out.println("Invoked BlackjackUpdate.getUpdate()");
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT Start FROM Blackjack WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
            ps.setInt(1, UserIDclient);
            ResultSet results = ps.executeQuery();
            System.out.println(results.getBoolean(1));

            if (results.getBoolean(1) == true) {

            }

            return results.getString(1);

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error2\": \"Unable to get item, please see server console for more info.\"}";
        }
    }
}












/*
    PreparedStatement ps3 = Main.db.prepareStatement("SELECT Start FROM Blackjack WHERE SessionID IN SELECT SessionID FROM Users WHERE UserID = ?");
            ps3.setInt(1, UserIDclient);
                    ResultSet results2 = ps3.executeQuery();
                    System.out.println(results2.getInt(1));
  */