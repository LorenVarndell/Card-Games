package controllers;

import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


@Path("BlackjackUpdate/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class BlackjackUpdate {

    @POST
    @Path("add/{UserID}/{Hit}")
    public String getUpdate(@PathParam("UserID") Integer UserIDclient, @PathParam("Hit") String playerInp) {
        System.out.println("Invoked BlackjackUpdate.getUpdate()");
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT Start FROM Blackjack WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
            ps.setInt(1, UserIDclient);
            ResultSet results = ps.executeQuery();
            System.out.println(results.getBoolean(1));
            //checks if the game has started
            if (results.getBoolean(1) == true) {
                PreparedStatement ps2 = Main.db.prepareStatement("SELECT Turn FROM Users WHERE UserID = ?");
                ps2.setInt(1, UserIDclient);
                ps2.execute();
                ResultSet results2 = ps2.executeQuery();

                PreparedStatement ps3 = Main.db.prepareStatement("SELECT Round FROM Blackjack WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
                ps3.setInt(1, UserIDclient);
                ps3.execute();
                ResultSet results3 = ps3.executeQuery();

                PreparedStatement NoPQuery = Main.db.prepareStatement("SELECT NoP FROM Blackjack WHERE SessionID = (SELECT SessionID FROM Users WHERE UserID = ?)");
                NoPQuery.setInt(1, UserIDclient);
                NoPQuery.execute();
                ResultSet NoP = ps.executeQuery();
                //this if statements check whether it is the Users move using the preparedstatement
                if (results2.getInt(1) == results3.getInt(1)) {
                    JSONObject response = new JSONObject();
                    response.put("score", 0);
                    response.put("cards", "");
                    response.put("round", results3.getInt(1));
                    response.put("turn", true);

                    PreparedStatement ps4 = Main.db.prepareStatement("SELECT Score FROM Users WHERE UserID = ?");
                    ps4.setInt(1, UserIDclient);
                    ResultSet results4 = ps4.executeQuery();
                    // Gets the current score for a User
                    Integer score = results4.getInt(1);

                    // This if statement will run when it is the Users turn and they press "hit" on the HTML.
                    if (playerInp.equals("hit")) {
                        System.out.println(playerInp);
                        if (score != -1) {
                            PreparedStatement ps5 = Main.db.prepareStatement("SELECT Cards FROM Blackjack WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
                            ps5.setInt(1, UserIDclient);
                            ResultSet results5 = ps5.executeQuery();
                            // Gets all the available cards within the users Sessions

                            Integer allCardsLength = ((results5.getString(1)).length()) / 2;
                            System.out.println(allCardsLength + " Cards Length");

                            List<String> allCards = new ArrayList<String>();
                            for (int i = 2; i < (allCardsLength * 2) + 2; i = i + 2) {
                                allCards.add((results5.getString(1).substring(i - 2, i)));
                            }

                            System.out.println("test1");
                            Integer randomNumber = (int) (Math.random() * (allCardsLength - 1));
                            System.out.println(randomNumber + " Random Number");
                            //creates a number from 0 to the length of the list allCards

                            String randomCard = allCards.get(randomNumber);
                            //picks a random card from the list allCards

                            String cardValue = randomCard.substring(1);

                            if (cardValue.equals("T") || cardValue.equals("J") || cardValue.equals("Q") || cardValue.equals("K")) {
                                score = score + 10;
                            } else if ((randomCard.charAt(1) == 'A')) {
                                score = score + 11;
                            } else {
                                score = score + Integer.parseInt(cardValue);
                            }

                            allCards.remove(randomNumber);
                            allCards.remove(randomCard);
                            //removes this card from the list
                            System.out.println("test2");
                            String newTotalCards = "";
                            for (int i = 0; i < allCardsLength - 1; i++) {
                                newTotalCards = newTotalCards + allCards.get(i);
                            }
                            //Converts the whole list "allCards" into a single String "newTotalCards"
                            System.out.println(newTotalCards);

                            PreparedStatement ps6 = Main.db.prepareStatement("UPDATE Blackjack SET Cards = ? WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
                            ps6.setString(1, newTotalCards);
                            ps6.setInt(2, UserIDclient);
                            ps6.execute();
                            //This updates the database with the new updated cards

                            PreparedStatement ps7 = Main.db.prepareStatement("SELECT Cards FROM Users WHERE UserID = ?");
                            ps7.setInt(1, UserIDclient);
                            ResultSet results7 = ps7.executeQuery();
                            // Gets all the current cards from the User

                            //Users cards in the database
                            String userCards = "";
                            //How many aces the in the users deck
                            Integer Ace = 0;
                            if (results7.next() == true) {
                                userCards = results7.getString(1) + randomCard;  //Concatenates the two strings to get a single string
                            } else {
                                userCards = randomCard;
                            }

                            //Increments Ace if they're any aces
                            for (int i = 0; i < userCards.length(); i++) {
                                if (userCards.charAt(i) == 'A') {
                                    Ace++;
                                    System.out.println("test");
                                }
                            }

                            //Checks to see if scores including lowest ace values is over 21
                            if (score > 21 && (score - (Ace * 10)) > 21) {
                                score = -1;

                            } else if (score == 21 || (score - (Ace*10)) == 21) {

                            }


                            System.out.println("test3");
                            //Adds the new random card to the Users Cards
                            PreparedStatement ps8 = Main.db.prepareStatement("UPDATE Users SET Cards = ? WHERE UserID = ?");
                            ps8.setString(1, userCards);
                            ps8.setInt(2, UserIDclient);
                            ps8.execute();

                            //Adds new score to the database for the user
                            PreparedStatement ps9 = Main.db.prepareStatement("UPDATE Users SET Score = ? WHERE UserID = ?");
                            ps9.setInt(1, score);
                            ps9.setInt(2, UserIDclient);
                            ps9.execute();

                            response.put("score", score);
                            response.put("cards", userCards);
                        }
                    } else if (playerInp.equals("stand")) {
                        if (score > 0) {
                            System.out.println(playerInp);
                            PreparedStatement updateScore = Main.db.prepareStatement("UPDATE Users SET Score = ? WHERE UserID = ?");
                            updateScore.setInt(1, score*-1);
                            updateScore.setInt(2, UserIDclient);
                            updateScore.execute();
                            response.put("score", score*-1);

                        }
                    }

                    return response.toString();

                } else {
                    JSONObject response = new JSONObject();
                    response.put("standbyCurrent", false);

                    // Gets the current score for a User using double nested query
                    PreparedStatement scoreQuery = Main.db.prepareStatement("SELECT Score FROM Users WHERE Turn IN (SELECT Round FROM Blackjack WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?))");
                    scoreQuery.setInt(1, UserIDclient);
                    ResultSet scoreResults = scoreQuery.executeQuery();

                    Integer score = scoreResults.getInt(1);
                    // Gets all cards for current round
                    PreparedStatement userCardQuery = Main.db.prepareStatement("SELECT Cards FROM Users WHERE Turn IN (SELECT Round FROM Blackjack WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?))");
                    userCardQuery.setInt(1, UserIDclient);
                    ResultSet userCardResults = userCardQuery.executeQuery();


                    // checks to see the next persons turn
                    if (results2.getInt(1) == (results3.getInt(1)+1)) {
                        if (playerInp.equals("next")) {

                            System.out.println(UserIDclient);
                            if (score == 21 || score < 0) {
                                //Increments round by 1
                                PreparedStatement updateRound = Main.db.prepareStatement("UPDATE Blackjack SET Round = ? WHERE SessionID IN (Select SessionID FROM Users WHERE UserID = ?)");
                                updateRound.setInt(1, results3.getInt(1) + 1);
                                updateRound.setInt(2, UserIDclient);
                                updateRound.execute();
                            }
                        } else {
                            response.put("standbyCurrent", true);
                            System.out.println(UserIDclient);
                        }
                    }

                    response.put("score", score);
                    response.put("cards", userCardResults.getString(1));
                    response.put("round", results3.getInt(1));
                    response.put("turn", false);

                    return response.toString();

                }
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