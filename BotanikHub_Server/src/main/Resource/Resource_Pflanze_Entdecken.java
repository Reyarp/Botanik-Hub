package Resource;

import java.sql.SQLException;
import java.util.ArrayList;

import Database.DB_PflanzenEntdecken;
import Modell.Pflanze;
import Modell.PflanzenEntdecken;
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

@Path("pflanze-entdecken")
public class Resource_Pflanze_Entdecken {

	// POST /pflanze-entdecken – fügt eine Entdecker-Pflanze ein
	@POST
	@Consumes(MediaType.APPLICATION_JSON) // Erwartet JSON-Daten vom Client
	@Produces(MediaType.TEXT_PLAIN) // Antwort: Textnachricht (z. B. OK, Fehler)
	public Response postPflanzeEntdecken(PflanzenEntdecken entdecken) {
		try {
			// Eintrag in die Entdecker-Tabelle einfügen
			DB_PflanzenEntdecken.insertPflanzenEntdecken(entdecken);
			return Response.status(Response.Status.CREATED).build();
		} catch (SQLException e) {
			// Fehler beim Einfügen behandeln
			return Response.serverError().entity("Fehler: POST Pflanze-Entdecken " + e).build();
		}
	}

	// DELETE /pflanze-entdecken/{pflanzeID}/{benutzerID} – löscht einen Entdecker-Eintrag
	@DELETE
	@Path("{pflanzeID}/{benutzerID}") // Pfadparameter für Pflanzen- und Benutzer-ID
	public Response deletePflanzeEntdecken(@PathParam("pflanzeID") int pflanzeID, @PathParam("benutzerID") int benutzerID) {
		try {
			// Eintrag löschen
			DB_PflanzenEntdecken.deletePflanzenEntdecken(pflanzeID, benutzerID);
			return Response.status(Status.NO_CONTENT).build();
		} catch (SQLException e) {
			// Fehler beim Löschen behandeln
			return Response.serverError().entity("Fehler: DELETE Pflanze-Entdecken " + e).build();
		}
	}

	// GET /pflanze-entdecken/{id} – gibt alle Entdecker-Pflanzen eines Benutzers zurück
	@GET
	@Path("{id}") // Benutzer-ID
	@Produces(MediaType.APPLICATION_JSON) // Antwort: Liste als JSON
	public Response getPflanzeEntdecken(@PathParam("id") int id) {
		try {
			// Pflanzen aus der Entdecker-Tabelle lesen
			ArrayList<Pflanze> p = DB_PflanzenEntdecken.readPflanzeEntdecken(id);
			return Response.status(Status.OK).entity(p).build();
		} catch (SQLException e) {
			// Fehler beim Abrufen
			return Response.serverError().entity("Fehler: GET Pflanze {Pflanze-Entdecken} " + e).build();
		}
	}
}