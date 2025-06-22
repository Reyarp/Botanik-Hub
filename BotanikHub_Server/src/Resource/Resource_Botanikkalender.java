package Resource;

import java.sql.SQLException;
import java.util.ArrayList;

import Database.DB_BotanikKalender;
import Modell.Botanikkalender;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("botanikkalender")
public class Resource_Botanikkalender {

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBotanikkalender(@PathParam("id") int id) {
		try {
			ArrayList<Botanikkalender> alP = DB_BotanikKalender.readKalender(id);
			return Response.status(Status.OK).entity(alP).build();
		} catch(SQLException e) {
			return Response.serverError().entity("Fehler: GET Pflanze{Botanikkalender}" + e).build(); 
		}
	}
}
