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
            PreparedStatement startQuery = Main.db.prepareStatement("SELECT Start FROM Blackjack WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
            startQuery.setInt(1, UserIDclient);
            ResultSet startResults = startQuery.executeQuery();

            //checks if the game has started
            if (startResults.next() == true) {
                if (startResults.getBoolean(1) == true) {
                    PreparedStatement turnQuery = Main.db.prepareStatement("SELECT Turn FROM Users WHERE UserID = ?");
                    turnQuery.setInt(1, UserIDclient);
                    turnQuery.execute();
                    ResultSet turnResults = turnQuery.executeQuery();

                    PreparedStatement roundQuery = Main.db.prepareStatement("SELECT Round FROM Blackjack WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
                    roundQuery.setInt(1, UserIDclient);
                    roundQuery.execute();
                    ResultSet roundResults = roundQuery.executeQuery();

                    //this if statements check whether it is the Users move using the preparedstatement
                    if (turnResults.getInt(1) == roundResults.getInt(1)) {
                        JSONObject response = new JSONObject();
                        response.put("score", 0);
                        response.put("cards", "");
                        response.put("round", roundResults.getInt(1));
                        response.put("turn", true);

                        PreparedStatement scoreQuery = Main.db.prepareStatement("SELECT Score FROM Users WHERE UserID = ?");
                        scoreQuery.setInt(1, UserIDclient);
                        ResultSet scoreResults = scoreQuery.executeQuery();
                        // Gets the current score for a User
                        Integer score = scoreResults.getInt(1);

                        PreparedStatement topScoreQuery = Main.db.prepareStatement("SELECT topScore FROM Blackjack WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
                        topScoreQuery.setInt(1, UserIDclient);
                        ResultSet topScoreResult = topScoreQuery.executeQuery();

                        // This if statement will run when it is the Users turn and they press "hit" on the HTML.
                        if (playerInp.equals("hit")) {
                            System.out.println(playerInp);
                            if (score >= 0) {

                                // Gets all the available cards within the users Sessions
                                PreparedStatement cardsQuery = Main.db.prepareStatement("SELECT Cards FROM Blackjack WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
                                cardsQuery.setInt(1, UserIDclient);
                                ResultSet cardsResults = cardsQuery.executeQuery();

                                Integer allCardsLength = ((cardsResults.getString(1)).length()) / 2;
                                System.out.println(allCardsLength + " Cards Length");

                                List<String> allCards = new ArrayList<String>();
                                for (int i = 2; i < (allCardsLength * 2) + 2; i = i + 2) {
                                    allCards.add((cardsResults.getString(1).substring(i - 2, i)));
                                }

                                //creates a number from 0 to the length of the list allCards
                                Integer randomNumber = (int) (Math.random() * (allCardsLength - 1));
                                System.out.println(randomNumber + " Random Number");

                                //picks a random card from the list allCards
                                String randomCard = allCards.get(randomNumber);

                                String cardValue = randomCard.substring(1);

                                if (cardValue.equals("T") || cardValue.equals("J") || cardValue.equals("Q") || cardValue.equals("K")) {
                                    score = score + 10;
                                } else if ((randomCard.charAt(1) == 'A')) {
                                    score = score + 11;
                                } else {
                                    score = score + Integer.parseInt(cardValue);
                                }
                                //removes this card from the list
                                allCards.remove(randomCard);

                                //Converts the whole list "allCards" into a single String "newTotalCards"
                                String newTotalCards = "";
                                for (int i = 0; i < allCardsLength - 1; i++) {
                                    newTotalCards = newTotalCards + allCards.get(i);
                                }
                                System.out.println(newTotalCards);

                                //This updates the database with the new updated cards
                                PreparedStatement updateCards = Main.db.prepareStatement("UPDATE Blackjack SET Cards = ? WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
                                updateCards.setString(1, newTotalCards);
                                updateCards.setInt(2, UserIDclient);
                                updateCards.execute();

                                // Gets all the current cards from the User
                                PreparedStatement userCardsQuery = Main.db.prepareStatement("SELECT Cards FROM Users WHERE UserID = ?");
                                userCardsQuery.setInt(1, UserIDclient);
                                ResultSet userCardsResults = userCardsQuery.executeQuery();

                                //Users cards in the database
                                String userCards = "";
                                //How many aces the in the users deck
                                Integer Ace = 0;
                                if (userCardsResults.next() == true) {
                                    userCards = userCardsResults.getString(1) + randomCard;  //Concatenates the two strings to get a single string
                                } else {
                                    userCards = randomCard;
                                }

                                //Increments Ace if they're any aces
                                for (int i = 0; i < userCards.length(); i++) {
                                    if (userCards.charAt(i) == 'A') {
                                        Ace++;
                                    }
                                }

                                //Checks to see if scores including lowest ace values is over 21
                                Integer topScore = 0;
                                if (score > 21) {
                                    if (Ace > 0) {
                                        for (int i = 0; i <= Ace; i++) {
                                            if ((score - (i * 10)) < 21) {
                                                score = score - (i * 10);
                                                i = 10;
                                            } else if ((score - (i * 10)) == 21) {
                                                score = -21;
                                                topScore = 21;
                                                i = 10;
                                            } else if (Ace == i) {
                                                score = -1;
                                            }
                                        }
                                    } else {
                                        score = -1;
                                    }
                                } else if (score == 21) {
                                    score = -21;
                                    topScore = 21;
                                }

                                if (topScoreResult.getInt(1) < topScore) {
                                    PreparedStatement updateTopScore = Main.db.prepareStatement("UPDATE Blackjack SET topScore = ? WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
                                    updateTopScore.setInt(1, topScore);
                                    updateTopScore.setInt(2, UserIDclient);
                                    updateTopScore.execute();
                                }

                                //Adds the new random card to the Users Cards
                                PreparedStatement updateUserCards = Main.db.prepareStatement("UPDATE Users SET Cards = ? WHERE UserID = ?");
                                updateUserCards.setString(1, userCards);
                                updateUserCards.setInt(2, UserIDclient);
                                updateUserCards.execute();

                                //Adds new score to the database for the user
                                PreparedStatement updateUserScore = Main.db.prepareStatement("UPDATE Users SET Score = ? WHERE UserID = ?");
                                updateUserScore.setInt(1, score);
                                updateUserScore.setInt(2, UserIDclient);
                                updateUserScore.execute();

                                response.put("score", score);
                                response.put("cards", userCards);
                            }
                        } else if (playerInp.equals("stand")) {
                            if (score > 0) {
                                if (topScoreResult.getInt(1) < score) {
                                    PreparedStatement updateTopScore = Main.db.prepareStatement("UPDATE Blackjack SET topScore = ? WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
                                    updateTopScore.setInt(1, score);
                                    updateTopScore.setInt(2, UserIDclient);
                                    updateTopScore.execute();
                                }
                                System.out.println(playerInp);
                                PreparedStatement updateScore = Main.db.prepareStatement("UPDATE Users SET Score = ? WHERE UserID = ?");
                                updateScore.setInt(1, score * -1);
                                updateScore.setInt(2, UserIDclient);
                                updateScore.execute();
                                response.put("score", score * -1);

                            }
                        }

                        return response.toString();

                    } else {
                        JSONObject response = new JSONObject();

                        PreparedStatement NoPQuery = Main.db.prepareStatement("SELECT NoP FROM Blackjack WHERE SessionID = (SELECT SessionID FROM Users WHERE UserID = ?)");
                        NoPQuery.setInt(1, UserIDclient);
                        NoPQuery.execute();
                        ResultSet NoPResults = NoPQuery.executeQuery();

                        if (NoPResults.getInt(1) + 1 == roundResults.getInt(1)) {
                            PreparedStatement topScoreQuery = Main.db.prepareStatement("SELECT topScore FROM Blackjack WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
                            topScoreQuery.setInt(1, UserIDclient);
                            ResultSet topScoreResult = topScoreQuery.executeQuery();

                            PreparedStatement multipleScoreQuery = Main.db.prepareStatement("SELECT turn, score FROM Users WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
                            multipleScoreQuery.setInt(1, UserIDclient);
                            multipleScoreQuery.execute();

                            List<Integer> scores = new ArrayList<Integer>();
                            int rsCount = 0;
                            boolean multipleScoreResult = multipleScoreQuery.execute();
                            while (multipleScoreResult) {
                                if (multipleScoreResult) {
                                    ResultSet rs = multipleScoreQuery.getResultSet();
                                    rsCount++;
                                    while (rs.next()) {
                                        System.out.println(rs.getInt("turn"));
                                        scores.add(rs.getInt("turn"));
                                        scores.add(rs.getInt("score"));
                                    }
                                }
                                multipleScoreResult = multipleScoreQuery.getMoreResults();
                            }
                            System.out.println(scores);
                            String winners = "";
                            for (int i = 0; i < scores.size(); i = i + 2) {
                                if ((scores.get(i + 1)) * -1 == topScoreResult.getInt(1)) {
                                    winners = winners + scores.get(i);
                                }
                            }
                            if (turnResults.getInt(1) == 1) {
                                PreparedStatement deleteSession = Main.db.prepareStatement("DELETE FROM Sessions WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
                                deleteSession.setInt(1, UserIDclient);
                                deleteSession.execute();
                            }

                            response.put("finalScore", topScoreResult.getInt(1));
                            response.put("winners", winners);
                            return response.toString();
                        }

                        // Gets the current score for a User using double nested query
                        PreparedStatement scoreQuery = Main.db.prepareStatement("SELECT Score FROM Users WHERE Turn IN (SELECT Round FROM Blackjack WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)) AND SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
                        scoreQuery.setInt(1, UserIDclient);
                        scoreQuery.setInt(2, UserIDclient);
                        ResultSet scoreResults = scoreQuery.executeQuery();
                        Integer score = scoreResults.getInt(1);

                        // Gets all cards for current round

                        PreparedStatement userCardQuery = Main.db.prepareStatement("SELECT Cards FROM Users WHERE Turn IN (SELECT Round FROM Blackjack WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)) AND SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
                        userCardQuery.setInt(1, UserIDclient);
                        userCardQuery.setInt(2, UserIDclient);
                        ResultSet userCardResults = userCardQuery.executeQuery();


                        if ((turnResults.getInt(1) == 1) && (NoPResults.getInt(1) == roundResults.getInt(1))) {
                            if (score < 0) {
                                if (playerInp.equals("next")) {
                                    PreparedStatement updateRound = Main.db.prepareStatement("UPDATE Blackjack SET Round = ? WHERE SessionID IN (Select SessionID FROM Users WHERE UserID = ?)");
                                    updateRound.setInt(1, roundResults.getInt(1) + 1);
                                    updateRound.setInt(2, UserIDclient);
                                    updateRound.execute();
                                } else {
                                    response.put("standbyCurrentEnd", true);
                                }
                            }
                        }

                        response.put("standbyCurrent", false);
                        // checks to see the next persons turn
                        if (turnResults.getInt(1) == (roundResults.getInt(1) + 1)) {
                            if (score < 0) {
                                if (playerInp.equals("next")) {
                                    System.out.println(UserIDclient);
                                    //Increments round by 1
                                    PreparedStatement updateRound = Main.db.prepareStatement("UPDATE Blackjack SET Round = ? WHERE SessionID IN (Select SessionID FROM Users WHERE UserID = ?)");
                                    updateRound.setInt(1, roundResults.getInt(1) + 1);
                                    updateRound.setInt(2, UserIDclient);
                                    updateRound.execute();
                                } else {
                                    response.put("standbyCurrent", true);
                                    System.out.println(UserIDclient);
                                }
                            }
                        }

                        response.put("score", score);
                        response.put("round", roundResults.getInt(1));
                        response.put("cards", userCardResults.getString(1));
                        response.put("turn", false);
                        response.put("turnNum", turnResults.getInt(1));


                        return response.toString();

                    }
                }
                return "{\"Error2\": \"Game has not started\"}";
            } else {
                return "{\"Error3\": \"Game does not exist\"}";
            }


        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to get item, please see server console for more info.\"}";
        }
    }
}












/*
    PreparedStatement roundQuery = Main.db.prepareStatement("SELECT Start FROM Blackjack WHERE SessionID IN SELECT SessionID FROM Users WHERE UserID = ?");
            roundQuery.setInt(1, UserIDclient);
                    ResultSet turnResults = roundQuery.executeQuery();
                    System.out.println(turnResults.getInt(1));
  */