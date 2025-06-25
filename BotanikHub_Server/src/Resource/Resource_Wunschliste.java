package Resource;

import java.sql.SQLException;
import java.util.ArrayList;

import Database.DB_Wunschliste;
import Modell.Benutzer;
import Modell.MeineWunschliste;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;


@Path("wunschliste")
public class Resource_Wunschliste {

	// POST /wunschliste – fügt eine Pflanze zur Wunschliste hinzu
	@POST
	@Consumes(MediaType.APPLICATION_JSON) // Erwartet JSON-Objekt vom Typ MeineWunschliste
	@Produces(MediaType.TEXT_PLAIN) // Antwort: Textnachricht oder Fehler
	public Response postWunschliste(MeineWunschliste wunsch) {
		try {
			// Eintrag in die Wunschliste speichern
			DB_Wunschliste.insertWunschliste(wunsch);
			return Response.status(Response.Status.CREATED).build();
		} catch (SQLException e) {
			// Fehler beim Speichern
			return Response.serverError().entity("Fehler: POST Wunschliste " + e).build();
		}
	}

	// DELETE /wunschliste/{pflanzeID}/{benutzerID} – löscht Pflanze aus Wunschliste
	@DELETE
	@Path("{pflanzeID}/{benutzerID}") // Übergibt Pflanze- und Benutzer-ID als Pfadparameter
	public Response deleteWunschliste(@PathParam("pflanzeID") int pflanzeID, @PathParam("benutzerID") int benutzerID) {
		try {
			// Eintrag löschen
			DB_Wunschliste.deleteWunschliste(pflanzeID, benutzerID);
			return Response.status(Status.NO_CONTENT).build();
		} catch (SQLException e) {
			// Fehler beim Löschen
			return Response.serverError().entity("Fehler: DELETE Wunschliste " + e).build();
		}
	}

	// GET /wunschliste/{id} – gibt Wunschlisten-Pflanzen eines Benutzers zurück
	@GET
	@Path("{id}") // Benutzer-ID als Pfadparameter
	@Produces(MediaType.APPLICATION_JSON) // Antwort: Liste als JSON
	public Response getWunschliste(@PathParam("id") int id) {
		try {
			// Dummy-Benutzerobjekt mit gesetzter ID (weil DAO ein Benutzerobjekt erwartet)
			Benutzer b = new Benutzer();
			b.setBenutzerId(id);

			// Pflanzen aus der Wunschliste abrufen
			ArrayList<MeineWunschliste> p = DB_Wunschliste.readWunschlistePflanze(b);
			return Response.status(Status.OK).entity(p).build();
		} catch (SQLException e) {
			// Fehler beim Abrufen
			return Response.serverError().entity("Fehler: GET Wunschliste " + e).build();
		}
	}
}
