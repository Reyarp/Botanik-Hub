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

	/* POST /benutzer
	 * insertBenutzer
	 *
	 * @Consumes = erwartet JSON-Daten im Request-Body
	 * @Produces = sendet reinen Text (z. B. Erfolg oder Fehlermeldung)
	 * MediaType.APPLICATION_JSON = "application/json"
	 * MediaType.TEXT_PLAIN       = "text/plain"
	 */
	@POST
	@Path("registrieren")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postBenutzer (Benutzer benutzer) {

		try {
			DB_Benutzer.insertBenutzer(benutzer);
			return Response.status(Response.Status.CREATED).build();	
			/*
			 * Fehlermeldungen sollten immer Serverseitig kommen, nie Clientseitig
			 */

		} catch(SQLException e) {
			// Da ich ein UNIQUE im Namen habe prüfe ich hier gleich nach und fang es gleich ab
			if(e.getMessage().contains("unique")) {		
				return Response.status(Response.Status.CONFLICT).entity("Benutzer existiert bereits").build();
			}
			return Response.serverError().entity("Fehler: POST Benutzer" + e).build();
		}
	}

	/*
	 * PUT /benutzer/{id}
	 * updateBenutzer
	 *
	 * @Consumes = erwartet JSON (Benutzerdaten)
	 * @Produces = liefert eine Textmeldung als Antwort
	 *
	 */
	@PUT
	@Path("{id}")	// Das ist die Target adresse also http://localhost:4711/benutzer/{id}
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response putBenutzer(@PathParam("id") int id, Benutzer benutzer) {
		try {
			benutzer.setBenutzerId(id); 				// Sicherstellen das ID korrekt ist
			DB_Benutzer.updateBenutzer(benutzer);
			return Response.ok().build();

		} catch(SQLException e) {
			return Response.serverError().entity("Benutzer existiert bereits").build();
		}
	}

	/* GET /benutzer/login/{name}/{passwort}
	 * Benutzer-Login überprüfen
	 *
	 * @Produces = liefert Benutzerobjekt im JSON-Format
	 */
	@GET
	@Path("login/{name}/{passwort}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
	public Response loginBenutzer(@PathParam("name") String name, @PathParam("passwort") String passwort) {
		try {
			Benutzer b = DB_Benutzer.loginBenutzer(name, passwort);
			if (b != null) {
				return Response.ok(b).build();
			} else {
				return Response.status(Response.Status.UNAUTHORIZED)
						.entity("Benutzername oder Passwort ungültig").build();
			}
		} catch (SQLException e) {
			return Response.serverError().entity(e).build();
		}
	}

	/** GET /benutzer
	 * readBenutzer
	 * Gibt alle registrierten Benutzer zurück
	 *
	 * @Produces = gibt Liste als JSON zurück
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBenutzer() {
		try {
			ArrayList<Benutzer> b = DB_Benutzer.readAlleBenutzer();
			return Response.status(Status.OK).entity(b).build();
		} catch (SQLException e) {
			return Response.serverError().entity(e).build();
		}
	}
}
