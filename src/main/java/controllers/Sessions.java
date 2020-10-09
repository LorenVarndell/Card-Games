package controllers;

import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


@Path("Sessions/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class Sessions {

    @GET
    @Path("get/{SessionID}")
    public String getSessions(@PathParam("SessionID") Integer SessionID) {
        System.out.println("Invoked Session.getSessionID() with SessionID " + SessionID);
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT ? FROM Sessions");
            ps.setInt(1, SessionID);
            ResultSet results = ps.executeQuery();
            JSONObject response = new JSONObject();
            if (results.next() == true) {
                response.put("SessionID", SessionID);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to get item, please see server console for more info.\"}";
        }
    }
}

