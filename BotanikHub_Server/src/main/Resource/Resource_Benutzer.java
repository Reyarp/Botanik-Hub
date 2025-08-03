package Resource;

import java.sql.SQLException;
import java.util.ArrayList;

import Database.DB_Benutzer;
import Modell.Benutzer;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;


@Path("benutzer")
public class Resource_Benutzer {

	// POST /benutzer/registrieren – legt neuen Benutzer an
	@POST
	@Path("registrieren")
	@Consumes(MediaType.APPLICATION_JSON) // Erwartet Benutzerdaten als JSON
	@Produces(MediaType.APPLICATION_JSON) // Gibt ggf. Text oder Status zurück
	public Response postBenutzer(Benutzer benutzer) {
		try {
			// Benutzer in DB einfügen
			DB_Benutzer.insertBenutzer(benutzer);
			return Response.status(Response.Status.CREATED).build();
		} catch (SQLException e) {
			// Fehlerbehandlung bei UNIQUE-Verletzung (z. B. Benutzername existiert)
			if (e.getMessage().toLowerCase().contains("unique")) {
				return Response.status(Response.Status.CONFLICT)
						.entity("Benutzer existiert bereits").build();
			}
			// Sonst allgemeiner Fehler
			return Response.serverError().entity("Fehler: POST Benutzer " + e).build();
		}
	}

	// PUT /benutzer/{id} – aktualisiert einen vorhandenen Benutzer
	@PUT
	@Path("{id}") // Platzhalter für Benutzer-ID in der URL
	@Consumes(MediaType.APPLICATION_JSON) // Erwartet JSON im Request
	@Produces(MediaType.APPLICATION_JSON) // Antwortformat
	public Response putBenutzer(@PathParam("id") int id, Benutzer benutzer) {
		try {
			// ID aus dem Pfad ins Objekt übernehmen (zur Sicherheit)
			benutzer.setBenutzerId(id);
			// Benutzer aktualisieren
			DB_Benutzer.updateBenutzer(benutzer);
			return Response.ok().build();
		} catch (SQLException e) {
			// Fehlerbehandlung, z. B. bei Namenskonflikt
			return Response.serverError().entity("Benutzer existiert bereits").build();
		}
	}

	// GET /benutzer/login/{name}/{passwort} – Login-Überprüfung
	@GET
	@Path("login/{name}/{passwort}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN }) // JSON oder Textausgabe
	public Response loginBenutzer(@PathParam("name") String name, @PathParam("passwort") String passwort) {
		try {
			// Login-Daten prüfen
			Benutzer b = DB_Benutzer.loginBenutzer(name, passwort);
			if (b != null) {
				// Erfolgreich: Benutzerobjekt zurückgeben
				return Response.ok(b).build();
			} else {
				// Kein Treffer → 401 Unauthorized
				return Response.status(Response.Status.UNAUTHORIZED)
						.entity("Benutzername oder Passwort ungültig").build();
			}
		} catch (SQLException e) {
			// Datenbankfehler
			return Response.serverError().entity(e).build();
		}
	}

	// GET /benutzer – gibt alle Benutzer zurück
	@GET
	@Produces(MediaType.APPLICATION_JSON) // Antwort: Liste als JSON
	public Response getBenutzer() {
		try {
			// Alle Benutzer aus DB lesen
			ArrayList<Benutzer> b = DB_Benutzer.readAlleBenutzer();
			return Response.status(Status.OK).entity(b).build();
		} catch (SQLException e) {
			// Fehler bei Datenbankzugriff
			return Response.serverError().entity(e).build();
		}
	}
}