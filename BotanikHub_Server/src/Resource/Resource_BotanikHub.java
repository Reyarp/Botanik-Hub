package Resource;

import java.sql.SQLException;
import java.util.ArrayList;
import Database.DB_BotanikHub;
import Modell.BotanikHub;
import Modell.Pflanze;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("botanik-hub")
public class Resource_BotanikHub {

	// POST /botanik-hub – fügt eine Pflanze in den BotanikHub ein
	@POST
	@Consumes(MediaType.APPLICATION_JSON) // Erwartet JSON im Request-Body
	@Produces(MediaType.APPLICATION_JSON) // Antwortformat JSON (z. B. für Text oder Fehler)
	public Response postBotanikHubPflanze(BotanikHub hub) {
		try {
			// Eintrag in DB speichern
			DB_BotanikHub.insertBotanikHub(hub);
			return Response.status(Response.Status.CREATED).build();
		} catch (SQLException e) {
			// Fehler beim Speichern
			return Response.serverError().entity("Fehler: POST BotanikHub " + e.toString()).build();
		}
	}

	// DELETE /botanik-hub/{pflanzeID}/{benutzerID} – entfernt eine Pflanze aus dem BotanikHub
	@DELETE
	@Path("{pflanzeID}/{benutzerID}")
	public Response deleteBotanikHubPflanze(@PathParam("pflanzeID") int pflanzeID, @PathParam("benutzerID") int benutzerID) {
		try {
			// Eintrag löschen
			DB_BotanikHub.deleteBotanikHub(pflanzeID, benutzerID);
			return Response.status(Status.NO_CONTENT).build();
		} catch (SQLException e) {
			// Fehler beim Löschen
			return Response.serverError().entity("Fehler: DELETE BotanikHub " + e.toString()).build();
		}
	}

	// PUT /botanik-hub/updatenotiz – aktualisiert die Notiz einer Pflanze im Hub
	@PUT
	@Path("updatenotiz")
	@Consumes(MediaType.APPLICATION_JSON) // Erwartet BotanikHub-Objekt als JSON
	@Produces(MediaType.APPLICATION_JSON)
	public Response putNotiz(BotanikHub hub) {
		try {
			// Notiz aktualisieren
			DB_BotanikHub.updateNotiz(hub);
			return Response.status(Status.OK).build();
		} catch (SQLException e) {
			// Fehler beim Update
			return Response.serverError().entity("Fehler: PUT Notiz {BotanikHub} " + e.toString()).build();
		}
	}

	// GET /botanik-hub/{id} – gibt alle Pflanzen im BotanikHub eines Benutzers zurück
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON) // Antwort: Liste als JSON
	public Response getBotanikHubPflanzen(@PathParam("id") int benutzeriD) {
		try {
			// Pflanzen des Benutzers auslesen
			ArrayList<Pflanze> p = DB_BotanikHub.readBotanikHubPflanzen(benutzeriD);
			return Response.status(Status.OK).entity(p).build();
		} catch (SQLException e) {
			// Fehler beim Lesen
			return Response.serverError().entity("Fehler: GET Pflanze {BotanikHub} " + e.toString()).build();
		}
	}
}
