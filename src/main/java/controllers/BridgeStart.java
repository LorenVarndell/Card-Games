package controllers;

import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Path("BridgeStart/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class BridgeStart {

    @POST
    @Path("add/{UserID}")
    public String getUpdate(@PathParam("UserID") Integer UserIDclient) {
        System.out.println("Invoked BridgeStart.Start()");
        List<String> allCards = Arrays.asList("S2", "D2", "H2", "C2", "S3", "D3", "H3", "C3", "S4",
                "D4", "H4", "C4", "S5", "D5", "H5", "C5", "S6", "D6", "H6", "C6", "S7", "D7", "H7",
                "C7", "S8", "D8", "H8", "C8", "S9", "D9", "H9", "C9", "ST", "DT", "HT", "CT",
                "SJ", "DJ", "HJ", "CJ", "SQ", "DQ", "HQ", "CQ", "SK", "DK", "HK", "CK", "SA",
                "DA", "HA", "CA");
        ArrayList<String> totalCards = new ArrayList<String>();
        for (int i = 0; i<52;i ++)  {
            totalCards.add(allCards.get(i));
        }
        ArrayList<String> cards = new ArrayList<String>();


        //List<String> totalCards = new ArrayList<String>();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT NoP FROM Bridge WHERE Owner = ?");
            ps.setInt(1, UserIDclient);
            ps.execute();
            ResultSet results = ps.executeQuery();
            Integer numberOfPeople = 0;

            numberOfPeople = results.getInt(1);

            if (numberOfPeople == 4) {
                PreparedStatement ps1 = Main.db.prepareStatement("UPDATE Bridge SET Start = ? WHERE Owner = ?");
                ps1.setBoolean(1, true);
                ps1.setInt(2, UserIDclient);
                ps1.execute();

                PreparedStatement ps5 = Main.db.prepareStatement("UPDATE Bridge SET Round = ? WHERE Owner = ?");
                ps5.setInt(1, 1);
                ps5.setInt(2, UserIDclient);
                ps5.execute();

                PreparedStatement ps2 = Main.db.prepareStatement("SELECT UserID FROM Users WHERE SessionID IN (SELECT SessionID FROM Bridge WHERE Owner = ?)");
                ps2.setInt(1, UserIDclient);
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



                Integer Random = 0;
                Integer score = 0;
                Integer maxScore = 0;
                String currentCard = "";
                Integer User = 0;
                for (int t=0; t<4;t++) {
                    String userCards = "";
                    score = 0;
                    for (int i = 0; i < 13; i++) {
                        Random = (int) (Math.random() * (totalCards.size()-1));
                        cards.add(totalCards.get(Random));
                        currentCard = (totalCards.get(Random)).substring(1);
                        totalCards.remove(totalCards.get(Random));
                        if (currentCard.equals("J")) {
                            score = score + 1;
                            System.out.println("J");
                        } else if (currentCard.equals("Q")) {
                            score = score + 2;
                            System.out.println("Q");
                        } else if (currentCard.equals("K")) {
                            score = score + 3;
                            System.out.println("K");
                        } else if (currentCard.equals("A")) {
                            score = score + 4;
                            System.out.println("A");
                        }
                    }
                    Collections.sort(cards);
                    for (int j = 0; j < 13; j++) {
                        userCards = userCards + cards.get(j);
                    }
                    PreparedStatement updateUserCards = Main.db.prepareStatement("UPDATE Users SET Cards = ? WHERE UserID = ?");
                    updateUserCards.setString(1, userCards);
                    updateUserCards.setInt(2, UserIDs.get(t));
                    updateUserCards.execute();

                    if (maxScore < score) {
                        maxScore = score;
                        User = t;
                    }

                    PreparedStatement updateUserScore = Main.db.prepareStatement("UPDATE Users SET Score = ? WHERE UserID = ?");
                    updateUserScore.setInt(1, score);
                    updateUserScore.setInt(2, UserIDs.get(t));
                    updateUserScore.execute();

                }
                System.out.println(totalCards);
                //Player with highest score is last in the round
                PreparedStatement updateTurn = Main.db.prepareStatement("UPDATE Users SET Turn = ? WHERE UserID = ?");
                updateTurn.setInt(1, 4);
                updateTurn.setInt(2, UserIDs.get(User));
                updateTurn.execute();
                //adds turns to other plays based on order in database (almost random)
                for (int i=0; i<3;i++) {
                    updateTurn = Main.db.prepareStatement("UPDATE Users SET Turn = ? WHERE UserID = ?");
                    updateTurn.setInt(1, i+1);
                    if (i >= User) {
                        updateTurn.setInt(2, UserIDs.get(i+1));
                    } else {
                        updateTurn.setInt(2, UserIDs.get(i));
                    }
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
