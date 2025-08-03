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

	// GET /botanikkalender/{id} – gibt alle Kalendereinträge für eine Pflanze zurück
	@GET
	@Path("{id}") // Pfadparameter: Pflanzen-ID
	@Produces(MediaType.APPLICATION_JSON) // Antwort: Liste als JSON
	public Response getBotanikkalender(@PathParam("id") int id) {
		try {
			// Kalenderdaten aus der DB laden
			ArrayList<Botanikkalender> alP = DB_BotanikKalender.readKalender(id);
			return Response.status(Status.OK).entity(alP).build();
		} catch (SQLException e) {
			// Fehler beim Abrufen
			return Response.serverError().entity("Fehler: GET Pflanze {Botanikkalender} " + e).build();
		}
	}
}
