package controllers;

import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Path("BlackjackStart/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class BlackjackStart {

    @POST
    @Path("add/{UserID}")
    public String getUpdate(@PathParam("UserID") Integer UserIDclient) {
        System.out.println("Invoked BlackjackStart.Start()");
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT NoP FROM Blackjack WHERE Owner = ?");
            ps.setInt(1, UserIDclient);
            ps.execute();
            ResultSet results = ps.executeQuery();
            Integer numberOfPeople = 0;

            numberOfPeople = results.getInt(1);

            if (numberOfPeople > 1) {
                PreparedStatement ps1 = Main.db.prepareStatement("UPDATE Blackjack SET Start = ? WHERE Owner = ?");
                ps1.setBoolean(1, true);
                ps1.setInt(2, UserIDclient);
                ps1.execute();

                PreparedStatement ps5 = Main.db.prepareStatement("UPDATE Blackjack SET Round = ? WHERE Owner = ?");
                ps5.setInt(1, 1);
                ps5.setInt(2, UserIDclient);
                ps5.execute();

                PreparedStatement ps2 = Main.db.prepareStatement("SELECT UserID FROM Users WHERE SessionID IN (SELECT SessionID FROM Blackjack WHERE Owner = ?) AND UserID != ?");
                ps2.setInt(1, UserIDclient);
                ps2.setInt(2, UserIDclient);
                ps2.execute();

                List<Integer> UserIDs = new ArrayList<Integer>();

                int rsCount =  0;
                boolean results2  =  ps2.execute();
                while (results2) {
                    if (results2) {
                        ResultSet rs = ps2.getResultSet();
                        rsCount++;
                        while (rs.next()) {
                            System.out.println(rs.getInt("UserID"));
                            UserIDs.add(rs.getInt("UserID"));
                        }
                    }
                    results2 = ps2.getMoreResults();
                }


                PreparedStatement ps3 = Main.db.prepareStatement("UPDATE Users SET Turn = ? WHERE UserID = ?");
                ps3.setInt(1, 1);
                ps3.setInt(2, UserIDclient);
                ps3.execute();

                for (int i=1; i<numberOfPeople;i++) {
                    PreparedStatement ps4 = Main.db.prepareStatement("UPDATE Users SET Turn = ? WHERE UserID = ?");
                    ps4.setInt(1, i+1);
                    ps4.setInt(2, UserIDs.get(i-1));
                    ps4.execute();


                }

                return "{\"OK\": \"Started Session.\"}";
            }

            return "{\"Error\": \"Not enough players\"}";

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error2\": \"Unable to get item, please see server console for more info.\"}";
        }
    }
}
