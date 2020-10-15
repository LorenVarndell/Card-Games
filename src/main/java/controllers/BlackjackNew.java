package controllers;

import org.glassfish.jersey.media.multipart.FormDataParam;
import server.Main;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;

@Path("Blackjack/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)



public class BlackjackNew {

    @POST
    @Path("add")
    public String SessionAdd() {
        int random_int = (int)(Math.random() * (999999 - 99999 + 1) + 99999);
        System.out.println("Invoked Blackjack.SessionIDAdd()");
        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Blackjack (SessionID) VALUES (?)");
            ps.setInt(1, random_int);
            ps.execute();
            PreparedStatement ps1 = Main.db.prepareStatement("INSERT INTO Sessions (SessionID, Game) VALUES (?,?)");
            ps1.setInt(1, random_int);
            ps1.setString(2, "Blackjack");
            ps1.execute();
            return "{\"OK\": \"Added SessionID.\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to create new item, please see server console for more info.\"}";
        }

    }
}