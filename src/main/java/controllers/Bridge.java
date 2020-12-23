package controllers;

import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


@Path("Bridge/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class Bridge {

    @GET
    @Path("get/{SessionID}/{UserID}")
    public String getSessions(@PathParam("SessionID") Integer SessionID, @PathParam("UserID") Integer UserIDclient) {
        System.out.println("Invoked Session.getSessionID() with SessionID " + SessionID);
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT NoP FROM Bridge WHERE SessionID = ?");
            ps.setInt(1, SessionID);
            ps.execute();
            ResultSet results = ps.executeQuery();
            Integer numberOfPeople = 0;
            if (results.next() == true) {
               numberOfPeople = (results.getInt(1));
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


            if (numberOfPeople < 4) {
                PreparedStatement ps6 = Main.db.prepareStatement("SELECT SessionID FROM Bridge WHERE Owner = ?");
                ps6.setInt(1, UserIDclient);
                ps6.execute();
                ResultSet results4 = ps6.executeQuery();

                if (results4.next() == true) {
                    PreparedStatement ps7 = Main.db.prepareStatement("DELETE FROM Sessions WHERE SessionID = ?");
                    ps7.setInt(1, results4.getInt(1));
                    ps7.execute();

                    PreparedStatement ps8 = Main.db.prepareStatement("DELETE FROM Bridge WHERE SessionID = ?");
                    ps8.setInt(1, results4.getInt(1));
                    ps8.execute();
                }
                //These three statements will delete any trace of a Users current Session if they own it.

                PreparedStatement ps3 = Main.db.prepareStatement("SELECT * FROM Bridge WHERE SessionID = ?");
                ps3.setInt(1, SessionID);
                ResultSet results3 = ps3.executeQuery();
                JSONObject response = new JSONObject();
                if (results3.next() == true) {
                    response.put("BridgeSessionID", results3.getString(1));
                    PreparedStatement ps4 = Main.db.prepareStatement("UPDATE Users SET SessionID = ?, Cards = ?, Score = ?   WHERE UserID = ?");
                    ps4.setInt(1, SessionID);
                    ps4.setString(2,""); //resets cards
                    ps4.setInt(3, 0); //reset score
                    ps4.setInt(4, UserIDclient);
                    ps4.execute();


                    PreparedStatement ps5 = Main.db.prepareStatement("UPDATE Bridge SET NoP = ? WHERE SessionID = ?");
                    ps5.setInt(1, numberOfPeople+1);
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
