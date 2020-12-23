package controllers;

import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


@Path("BridgeUpdate/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class BridgeUpdate {

    @POST
    @Path("add/{UserID}/{Hit}")
    public String getUpdate(@PathParam("UserID") Integer UserIDclient, @PathParam("Hit") String playerInp) {
        System.out.println("Invoked BridgeUpdate.getUpdate()");
        try {
            JSONObject response = new JSONObject();
            PreparedStatement ps = Main.db.prepareStatement("SELECT Start FROM Bridge WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
            ps.setInt(1, UserIDclient);
            ResultSet results = ps.executeQuery();

            //checks if the game has started
            if (results.next() == true) {
                if (results.getBoolean(1) == true) {
                    PreparedStatement turnQuery = Main.db.prepareStatement("SELECT Turn FROM Users WHERE UserID = ?");
                    turnQuery.setInt(1, UserIDclient);
                    turnQuery.execute();
                    ResultSet turnResults = turnQuery.executeQuery();
                    PreparedStatement roundQuery = Main.db.prepareStatement("SELECT Round FROM Bridge WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
                    roundQuery.setInt(1, UserIDclient);
                    roundQuery.execute();
                    ResultSet roundResults = roundQuery.executeQuery();

                    PreparedStatement gameRoundQuery = Main.db.prepareStatement("SELECT gameRound FROM Bridge WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
                    gameRoundQuery.setInt(1, UserIDclient);
                    gameRoundQuery.execute();
                    ResultSet gameRoundResults = gameRoundQuery.executeQuery();

                    PreparedStatement trumpQuery = Main.db.prepareStatement("SELECT trump FROM Bridge WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
                    trumpQuery.setInt(1, UserIDclient);
                    trumpQuery.execute();
                    ResultSet trumpResults = trumpQuery.executeQuery();

                    if (gameRoundResults.getInt(1) == 0) {

                        PreparedStatement cardsQuery = Main.db.prepareStatement("SELECT Cards FROM Users WHERE UserID = ?");
                        cardsQuery.setInt(1, UserIDclient);
                        cardsQuery.execute();
                        ResultSet cardsResults = cardsQuery.executeQuery();
                        response.put("cards", cardsResults.getString(1));
                        if (turnResults.getInt(1) == 4) {
                            //System.out.println(playerInp);
                            String trump = "";
                            if (playerInp.equals("none")) {
                                trump = playerInp;
                            } else if (playerInp.equals("hrt")) {
                                trump = playerInp;
                            } else if (playerInp.equals("dmd")) {
                                trump = playerInp;
                            }  else if (playerInp.equals("spd")) {
                                trump = playerInp;
                            }else if (playerInp.equals("clb")) {
                                trump = playerInp;
                            }
                            if (!trump.equals("")) {
                                PreparedStatement updateTrump = Main.db.prepareStatement("UPDATE Bridge SET trump = ? WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
                                updateTrump.setString(1, trump);
                                updateTrump.setInt(2, UserIDclient);
                                updateTrump.execute();
                            }

                            PreparedStatement teamCardsQuery = Main.db.prepareStatement("SELECT Cards FROM Users WHERE Turn = ? AND SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
                            teamCardsQuery.setInt(1, 2);
                            teamCardsQuery.setInt(2, UserIDclient);
                            teamCardsQuery.execute();
                            ResultSet teamCardsResults = teamCardsQuery.executeQuery();
                            response.put("teamCards", teamCardsResults.getString(1));

                        }

                        if (trumpResults.next() == true) {
                            if (!trumpResults.getString(1).equals("")) {
                                //System.out.println(trumpResults.getString(1));
                                PreparedStatement updateGameRound = Main.db.prepareStatement("UPDATE Bridge SET gameRound = ? WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
                                updateGameRound.setInt(1, 1);
                                updateGameRound.setInt(2, UserIDclient);
                                updateGameRound.execute();
                                response.put("gameRound", 1);
                            }
                        } else {
                            response.put("gameRound", 0);
                        }
                        response.put("turn", turnResults.getInt(1));
                        response.put("round", 1);
                        response.put("enemyCards", 26);
                    //if gameRound is not 0
                    } else {
                        if (turnResults.getInt(1) == roundResults.getInt(1)) {
                            if (turnResults.getInt(1)  == 4) {
                                PreparedStatement teamCardsQuery = Main.db.prepareStatement("SELECT Cards FROM Users WHERE Turn = ? AND SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
                                teamCardsQuery.setInt(1, 2);
                                teamCardsQuery.setInt(2, UserIDclient);
                                teamCardsQuery.execute();
                                ResultSet teamCardsResults = teamCardsQuery.executeQuery();
                                response.put("teamCards", teamCardsResults.getString(1));
                            }
                            response.put("backCards", 13-gameRoundResults.getInt(1));
                            response.put("trump", trumpResults.getString(1));
                        }
                    }

                }
            }
            return response.toString();
                    /*
                    //this if statements check whether it is the Users move using the preparedstatement
                    if (turnResults.getInt(1) == results3.getInt(1)) {
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

                        PreparedStatement topScoreQuery = Main.db.prepareStatement("SELECT topScore FROM Bridge WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
                        topScoreQuery.setInt(1, UserIDclient);
                        ResultSet topScoreResult = topScoreQuery.executeQuery();

                        // This if statement will run when it is the Users turn and they press "hit" on the HTML.
                        if (playerInp.equals("hit")) {
                            System.out.println(playerInp);
                            if (score >= 0) {

                                // Gets the current score for a User

                                PreparedStatement ps5 = Main.db.prepareStatement("SELECT Cards FROM Bridge WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
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

                                PreparedStatement ps6 = Main.db.prepareStatement("UPDATE Bridge SET Cards = ? WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
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
                                    PreparedStatement updateTopScore = Main.db.prepareStatement("UPDATE Bridge SET topScore = ? WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
                                    updateTopScore.setInt(1, topScore);
                                    updateTopScore.setInt(2, UserIDclient);
                                    updateTopScore.execute();
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
                                if (topScoreResult.getInt(1) < score) {
                                    PreparedStatement updateTopScore = Main.db.prepareStatement("UPDATE Bridge SET topScore = ? WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
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
                        if (NoPResults.getInt(1) + 1 == results3.getInt(1)) {

                        }

                        return response.toString();

                    } else {
                        JSONObject response = new JSONObject();
                        if (NoPResults.getInt(1) + 1 == results3.getInt(1)) {
                            PreparedStatement topScoreQuery = Main.db.prepareStatement("SELECT topScore FROM Bridge WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
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
                            if (results2.getInt(1) == 1) {
                                PreparedStatement deleteSession = Main.db.prepareStatement("DELETE FROM Sessions WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?)");
                                deleteSession.setInt(1, UserIDclient);
                                deleteSession.execute();
                            }

                            response.put("finalScore", topScoreResult.getInt(1));
                            response.put("winners", winners);
                            return response.toString();
                        }
                        response.put("standbyCurrent", false);
                        response.put("end", false);
                        // Gets the current score for a User using double nested query
                        PreparedStatement scoreQuery = Main.db.prepareStatement("SELECT Score FROM Users WHERE Turn IN (SELECT Round FROM Bridge WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?))");
                        scoreQuery.setInt(1, UserIDclient);
                        ResultSet scoreResults = scoreQuery.executeQuery();
                        Integer score = scoreResults.getInt(1);
                        // Gets all cards for current round

                        PreparedStatement userCardQuery = Main.db.prepareStatement("SELECT Cards FROM Users WHERE Turn IN (SELECT Round FROM Bridge WHERE SessionID IN (SELECT SessionID FROM Users WHERE UserID = ?))");
                        userCardQuery.setInt(1, UserIDclient);
                        ResultSet userCardResults = userCardQuery.executeQuery();


                        if ((results2.getInt(1) == 1) && (NoPResults.getInt(1) == results3.getInt(1))) {
                            if (score < 0) {
                                if (playerInp.equals("next")) {
                                    PreparedStatement updateRound = Main.db.prepareStatement("UPDATE Bridge SET Round = ? WHERE SessionID IN (Select SessionID FROM Users WHERE UserID = ?)");
                                    updateRound.setInt(1, results3.getInt(1) + 1);
                                    updateRound.setInt(2, UserIDclient);
                                    updateRound.execute();
                                } else {
                                    response.put("standbyCurrentEnd", true);
                                }
                            }
                        }

                        // checks to see the next persons turn
                        if (results2.getInt(1) == (results3.getInt(1) + 1)) {
                            if (playerInp.equals("next")) {

                                System.out.println(UserIDclient);
                                if (score == 21 || score < 0) {
                                    //Increments round by 1
                                    PreparedStatement updateRound = Main.db.prepareStatement("UPDATE Bridge SET Round = ? WHERE SessionID IN (Select SessionID FROM Users WHERE UserID = ?)");
                                    updateRound.setInt(1, results3.getInt(1) + 1);
                                    updateRound.setInt(2, UserIDclient);
                                    updateRound.execute();
                                }
                            } else {
                                if (score == 21 || score < 0) {
                                    response.put("standbyCurrent", true);
                                    System.out.println(UserIDclient);
                                }
                            }
                        }

                        response.put("score", score);
                        response.put("round", results3.getInt(1));
                        response.put("cards", userCardResults.getString(1));
                        response.put("turn", false);
                        response.put("turnNum", results2.getInt(1));


                        return response.toString();

                    }
                }
                return "{\"Error3\": \"Game has not started\"}";
            } else {
                return "{\"Error4\": \"Game does not exist\"}";
            }
        */

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error2\": \"Unable to get item, please see server console for more info.\"}";
        }
    }
}












/*
    PreparedStatement ps3 = Main.db.prepareStatement("SELECT Start FROM Bridge WHERE SessionID IN SELECT SessionID FROM Users WHERE UserID = ?");
            ps3.setInt(1, UserIDclient);
                    ResultSet results2 = ps3.executeQuery();
                    System.out.println(results2.getInt(1));
  */