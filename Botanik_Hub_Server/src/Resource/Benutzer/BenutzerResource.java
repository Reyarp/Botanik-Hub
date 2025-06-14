package Resource.Benutzer;

import java.sql.SQLException;
import java.util.ArrayList;

import Database.DB_Benutzer;
import Modell.Benutzer;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("benutzer")
public class BenutzerResource {
	
	// Create Table
	static {
		try {
			DB_Benutzer.createBenutzer();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBenutzer() {
    	try {
			ArrayList<Benutzer> b = DB_Benutzer.readAlleBenutzer();
			return Response.status(Status.OK).entity(b).build();
		} catch (SQLException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();
		}
    }

}
