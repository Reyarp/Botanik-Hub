package ServiceFunctions;

import java.sql.SQLException;
import java.util.ArrayList;

import Modell.Pflanze;
import Modell.PflanzenEntdecken;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

public class Service_PflanzeEntdecken {

	// Legt einen Eintrag in der Tabelle pflanze-entdecken an (POST /pflanze-entdecken)
	public static void postPflanzeEntdecken(PflanzenEntdecken entdecken) throws SQLException {
		// HTTP-Client erstellen
		Client client = ClientBuilder.newClient();
		// Ziel-URL für POST-Anfrage
		WebTarget target = client.target("http://localhost:4711/pflanze-entdecken");

		// Sende POST-Anfrage mit JSON-Daten
		Response response = target
				.request(MediaType.TEXT_PLAIN)
				.post(Entity.entity(entdecken, MediaType.APPLICATION_JSON));

		// Erfolg: 201 CREATED
		if (response.getStatus() == Status.CREATED.getStatusCode()) {
			client.close();
		} else {
			// Fehlertext auslesen und weiterwerfen
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}

	// Löscht einen Eintrag aus pflanze-entdecken (DELETE /pflanze-entdecken/{pflanzeID}/{benutzerID})
	public static void deletePflanzeEntdecken(int pflanzeID, int benutzeriD) throws SQLException {
		// HTTP-Client erstellen
		Client client = ClientBuilder.newClient();
		// Ziel-URL mit Pflanze- und Benutzer-ID
		WebTarget target = client.target("http://localhost:4711/pflanze-entdecken/" + pflanzeID + "/" + benutzeriD);

		// Sende DELETE-Anfrage
		Response response = target
				.request(MediaType.APPLICATION_JSON)
				.delete();

		// Erfolg: 204 NO_CONTENT
		if (response.getStatus() == Status.NO_CONTENT.getStatusCode()) {
			client.close();
		} else {
			// Fehlertext auslesen und weiterwerfen
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}

	// Holt alle Entdecker-Pflanzen für einen Benutzer (GET /pflanze-entdecken/{benutzerID})
	public static ArrayList<Pflanze> getPEPflanzen(int benutzerID) throws SQLException {
		// HTTP-Client erstellen
		Client client = ClientBuilder.newClient();
		// Ziel-URL mit Benutzer-ID
		WebTarget target = client.target("http://localhost:4711/pflanze-entdecken/" + benutzerID);

		// Sende GET-Anfrage
		Response response = target.request(MediaType.APPLICATION_JSON).get();

		// Erfolg: 200 OK → Liste auslesen
		if (response.getStatus() == Status.OK.getStatusCode()) {
			ArrayList<Pflanze> alP = response.readEntity(new GenericType<ArrayList<Pflanze>>() {});
			client.close();
			return alP;
		} else {
			// Fehlertext auslesen und weiterwerfen
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}
}
