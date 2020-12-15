package controllers;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Path("Blackjack/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)



public class BlackjackNew {

    @POST
    @Path("add/{UserID}")
    public String SessionAdd(@PathParam("UserID") Integer UserIDclient) {
        int random_int = (int)(Math.random() * (999999 - 99999 + 1) + 99999);
        System.out.println("Invoked Blackjack.SessionIDAdd()");
        System.out.println(random_int);
        //cards
        List<String> allCards = Arrays.asList("S2", "D2", "H2", "C2", "S3", "D3", "H3", "C3", "S4",
                "D4", "H4", "C4", "S5", "D5", "H5", "C5", "S6", "D6", "H6", "C6", "S7", "D7", "H7",
                "C7", "S8", "D8", "H8", "C8", "S9", "D9", "H9", "C9", "ST", "DT", "HT", "CT",
                "SJ", "DJ", "HJ", "CJ", "SQ", "DQ", "HQ", "CQ", "SK", "DK", "HK", "CK", "SA",
                "DA", "HA", "CA");

        String TotalCards = "";
        for (int i=0; i<52;i++)  {
            TotalCards = TotalCards + allCards.get(i);
        }
        System.out.println(TotalCards);
        try {
            PreparedStatement ps10 = Main.db.prepareStatement("SELECT Sessions.SessionID FROM Sessions JOIN Blackjack ON Owner = ? ");
            ps10.setInt(1, UserIDclient);
            ps10.execute();
            ResultSet results = ps10.executeQuery();

            if (results.next() == true) {
                Integer Found = Integer.parseInt(results.getString(1));
                System.out.println(Found);
                PreparedStatement ps4 = Main.db.prepareStatement("DELETE FROM Sessions WHERE SessionID = ?");
                ps4.setInt(1, Found);
                ps4.execute();
            }


            PreparedStatement ps1 = Main.db.prepareStatement("INSERT INTO Sessions (SessionID, Game) VALUES (?,?)");
            ps1.setInt(1, random_int);
            ps1.setString(2, "Blackjack");
            ps1.execute();
            //System.out.println("test");


            PreparedStatement ps2 = Main.db.prepareStatement("UPDATE Users SET SessionID = ?  WHERE UserID = ?");
            ps2.setInt(1, random_int);
            ps2.setInt(2, UserIDclient);
            ps2.execute();
            //System.out.println("test1");


            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Blackjack (SessionID, Owner, Cards) VALUES (?,?,?)");
            ps.setInt(1, random_int);
            ps.setInt(2, UserIDclient);
            ps.setString(3, TotalCards);
            ps.execute();
            //System.out.println("test2");

            //System.out.println(allCards.toString());
            //System.out.println(allCards.get(9));
            //PreparedStatement ps6 = Main.db.prepareStatement("UPDATE Blackjack SET Cards=? WHERE Owner=?");
            //PreparedStatement ps6 = Main.db.prepareStatement("INSERT INTO Blackjack (SessionID, Owner) VALUES (?,?)");
            //ps6.setString(1, "test");
            //ps6.setInt(2, random_int);
            //ps6.execute();

            return "{\"OK\": \"Added SessionID.\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to create new item, please see server console for more info.\"}";
        }

    }
}

