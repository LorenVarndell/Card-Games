package controllers;

import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


@Path("Blackjack/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class Blackjack {

    @GET
    @Path("get/{SessionID}/{UserID}")
    public String getSessions(@PathParam("SessionID") Integer SessionID, @PathParam("UserID") Integer UserIDclient) {
        System.out.println("Invoked Session.getSessionID() with SessionID " + SessionID);
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT * FROM Blackjack WHERE SessionID = ?");
            ps.setInt(1, SessionID);
            ResultSet results = ps.executeQuery();
            JSONObject response = new JSONObject();
            if (results.next() == true) {
                response.put("BlackjackSessionID", results.getString(1));
                response.put("Cards",results.getString(2));
                response.put("Start",results.getString(3));
                PreparedStatement ps2 = Main.db.prepareStatement("UPDATE Users SET SessionID = ?  WHERE UserID = ?");
                ps2.setInt(1, SessionID);
                ps2.setInt(2, UserIDclient);
                ps2.execute();

            } else {
                return "{\"Error1\": \"Unable to find Session.\"}";

            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error2\": \"Unable to get item, please see server console for more info.\"}";
        }
    }
}
