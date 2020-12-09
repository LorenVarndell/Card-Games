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
            PreparedStatement ps = Main.db.prepareStatement("SELECT NoP FROM Blackjack WHERE SessionID = ?");
            ps.setInt(1, SessionID);
            ps.execute();
            ResultSet results = ps.executeQuery();
            Integer numberOfPeople = 0;
            if (results.next() == true) {
               numberOfPeople = (results.getInt(1)) + 1;
            }

            PreparedStatement ps2 = Main.db.prepareStatement("SELECT UserID FROM Users WHERE UserID = ? AND SessionID = ?");
            ps2.setInt(1, UserIDclient);
            ps2.setInt(2, SessionID);
            ps2.execute();
            ResultSet results2 = ps2.executeQuery();
            Integer secondUserID = 0;
            if (results2.next() == true) {
                secondUserID = results2.getInt(1);
                numberOfPeople--;
            }
            //System.out.println(secondUserID);
            //System.out.println(UserIDclient);

            if (numberOfPeople <= 7) {
                PreparedStatement ps3 = Main.db.prepareStatement("SELECT * FROM Blackjack WHERE SessionID = ?");
                ps3.setInt(1, SessionID);
                ResultSet results3 = ps3.executeQuery();
                JSONObject response = new JSONObject();
                if (results3.next() == true) {
                    response.put("BlackjackSessionID", results3.getString(1));
                    response.put("Cards", results3.getString(2));
                    response.put("Start", results3.getString(3));
                    PreparedStatement ps4 = Main.db.prepareStatement("UPDATE Users SET SessionID = ?  WHERE UserID = ?");
                    ps4.setInt(1, SessionID);
                    ps4.setInt(2, UserIDclient);
                    ps4.execute();


                    PreparedStatement ps5 = Main.db.prepareStatement("UPDATE Blackjack SET NoP = ? WHERE SessionID = ?");
                    ps5.setInt(1, numberOfPeople);
                    ps5.setInt(2, SessionID);
                    ps5.execute();


                } else {
                    return "{\"Error1\": \"Unable to find Session.\"}";

                }
                return response.toString();
            } else {
                return "{\"Error\": \"Session is full!\"}";
            }

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error2\": \"Unable to get item, please see server console for more info.\"}";
        }
    }
}
