package controllers;

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
            PreparedStatement NoPQuery = Main.db.prepareStatement("SELECT NoP FROM Blackjack WHERE Owner = ?");
            NoPQuery.setInt(1, UserIDclient);
            NoPQuery.execute();
            ResultSet NoPResults = NoPQuery.executeQuery();
            Integer numberOfPeople = 0;

            numberOfPeople = NoPResults.getInt(1);

            if (numberOfPeople > 1) {
                PreparedStatement updateStart = Main.db.prepareStatement("UPDATE Blackjack SET Start = ? WHERE Owner = ?");
                updateStart.setBoolean(1, true);
                updateStart.setInt(2, UserIDclient);
                updateStart.execute();

                PreparedStatement updateRound = Main.db.prepareStatement("UPDATE Blackjack SET Round = ? WHERE Owner = ?");
                updateRound.setInt(1, 1);
                updateRound.setInt(2, UserIDclient);
                updateRound.execute();

                PreparedStatement userIDQuery = Main.db.prepareStatement("SELECT UserID FROM Users WHERE SessionID IN (SELECT SessionID FROM Blackjack WHERE Owner = ?) AND UserID != ?");
                userIDQuery.setInt(1, UserIDclient);
                userIDQuery.setInt(2, UserIDclient);
                userIDQuery.execute();

                List<Integer> UserIDs = new ArrayList<Integer>();

                int rsCount =  0;
                boolean userIDResults  =  userIDQuery.execute();
                while (userIDResults) {
                    if (userIDResults) {
                        ResultSet rs = userIDQuery.getResultSet();
                        rsCount++;
                        while (rs.next()) {
                            System.out.println(rs.getInt("UserID"));
                            UserIDs.add(rs.getInt("UserID"));
                        }
                    }
                    userIDResults = userIDQuery.getMoreResults();
                }


                PreparedStatement updateTurn = Main.db.prepareStatement("UPDATE Users SET Turn = ? WHERE UserID = ?");
                updateTurn.setInt(1, 1);
                updateTurn.setInt(2, UserIDclient);
                updateTurn.execute();

                for (int i=1; i<numberOfPeople;i++) {
                    updateTurn = Main.db.prepareStatement("UPDATE Users SET Turn = ? WHERE UserID = ?");
                    updateTurn.setInt(1, i+1);
                    updateTurn.setInt(2, UserIDs.get(i-1));
                    updateTurn.execute();


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
