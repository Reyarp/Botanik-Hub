package Resource;

import java.sql.SQLException;
import java.util.ArrayList;

import Database.DB_Erinnerungen;
import Modell.Erinnerungen;
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

@Path("erinnerung")
public class Resource_Erinnerung {

	// POST /erinnerung – fügt eine neue Erinnerung hinzu
	@POST
	@Consumes(MediaType.APPLICATION_JSON) // Erwartet JSON im Request
	@Produces(MediaType.TEXT_PLAIN) // Antwort als Text (z. B. OK oder Fehler)
	public Response postErinnerung(Erinnerungen erinnerung) {
		try {
			// Erinnerung in DB einfügen
			DB_Erinnerungen.insertErinnerung(erinnerung);
			return Response.status(Status.CREATED).build();
		} catch (SQLException e) {
			// Fehler beim Speichern
			return Response.serverError().entity("Fehler: POST Erinnerung " + e.toString()).build();
		}
	}

	// PUT /erinnerung/{id} – aktualisiert eine bestehende Erinnerung
	@PUT
	@Path("{id}") // Übergibt die ID aus dem Pfad
	@Consumes(MediaType.APPLICATION_JSON) // Erwartet JSON
	@Produces(MediaType.APPLICATION_JSON)
	public Response putErinnerung(@PathParam("id") int id, Erinnerungen erinnerung) {
		try {
			// ID im Objekt setzen (Sicherheit)
			erinnerung.setErinnerungID(id);
			// Erinnerung aktualisieren
			DB_Erinnerungen.updateErinnerungen(erinnerung);
			return Response.status(Status.OK).build();
		} catch (SQLException e) {
			// Fehler beim Update
			return Response.serverError().entity("Fehler: PUT Erinnerung " + e.toString()).build();
		}
	}

	// DELETE /erinnerung/{id} – löscht eine Erinnerung
	@DELETE
	@Path("{id}")
	public Response deleteErinnerung(@PathParam("id") int id) {
		try {
			// Erinnerung löschen
			DB_Erinnerungen.deleteErinnerungen(id);
			return Response.status(Status.NO_CONTENT).build();
		} catch (SQLException e) {
			// Fehler beim Löschen
			return Response.serverError().entity("Fehler: DELETE Erinnerung " + e.toString()).build();
		}
	}

	// GET /erinnerung/{id} – gibt Erinnerungen zu einer Pflanze zurück
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON) // Antwort: Liste als JSON
	public Response getErinnerung(@PathParam("id") int id) {
		try {
			// Erinnerungen für Pflanze laden
			ArrayList<Erinnerungen> p = DB_Erinnerungen.readErinnerungen(id);
			return Response.status(Status.OK).entity(p).build();
		} catch (SQLException e) {
			// Fehler beim Lesen
			return Response.serverError().entity("Fehler: GET Erinnerung " + e.toString()).build();
		}
	}
}