package ServiceFunctions;

import java.sql.SQLException;
import java.util.ArrayList;

import Modell.Pflanze;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

public class Service_Pflanze {

	// Legt eine neue Pflanze an (POST /pflanze)
	public static Pflanze postPflanze(Pflanze pflanze) throws SQLException {
		// HTTP-Client erstellen
		Client client = ClientBuilder.newClient();
		// Ziel-URL zum Anlegen der Pflanze
		WebTarget target = client.target("http://localhost:4711/pflanze");

		// Sende POST-Anfrage mit Pflanze als JSON
		Response response = target
				.request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(pflanze, MediaType.APPLICATION_JSON));

		// Erfolg: 201 CREATED → Rückgabe der gespeicherten Pflanze mit ID
		if (response.getStatus() == Status.CREATED.getStatusCode()) {
			Pflanze p = response.readEntity(Pflanze.class);
			client.close();
			return p;
		} else {
			// Fehlertext auslesen und weitergeben
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}

	// Aktualisiert eine vorhandene Pflanze (PUT /pflanze/{id})
	public static void putPflanze(Pflanze pflanze) throws SQLException {
		// HTTP-Client erstellen
		Client client = ClientBuilder.newClient();
		// Ziel-URL mit ID der Pflanze
		WebTarget target = client.target("http://localhost:4711/pflanze/" + pflanze.getPflanzenID());

		// Sende PUT-Anfrage mit aktualisiertem Pflanzendaten
		Response response = target
				.request(MediaType.APPLICATION_JSON)
				.put(Entity.entity(pflanze, MediaType.APPLICATION_JSON));

		// Erfolg prüfen
		if (response.getStatus() == Status.OK.getStatusCode()) {
			client.close();
		} else {
			// Fehlertext auslesen und weitergeben
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}

	// Löscht eine Pflanze anhand ihrer ID (DELETE /pflanze/{id})
	public static void deletePflanze(int id) throws SQLException {
		// HTTP-Client erstellen
		Client client = ClientBuilder.newClient();
		// Ziel-URL mit Pflanzen-ID
		WebTarget target = client.target("http://localhost:4711/pflanze/" + id);

		// Sende DELETE-Anfrage
		Response response = target
				.request(MediaType.APPLICATION_JSON)
				.delete();

		// Erfolg: 204 NO_CONTENT
		if (response.getStatus() == Status.NO_CONTENT.getStatusCode()) {
			client.close();
		} else {
			// Fehlertext auslesen und weitergeben
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}

	// Holt alle Pflanzen aus der Datenbank (GET /pflanze)
	public static ArrayList<Pflanze> getPflanze() throws SQLException {
		// HTTP-Client erstellen
		Client client = ClientBuilder.newClient();
		// Ziel-URL zum Abrufen aller Pflanzen
		WebTarget target = client.target("http://localhost:4711/pflanze");

		// Sende GET-Anfrage
		Response response = target.request(MediaType.APPLICATION_JSON).get();

		// Erfolg: 200 OK → Liste einlesen
		if (response.getStatus() == Status.OK.getStatusCode()) {
			ArrayList<Pflanze> alP = response.readEntity(new GenericType<ArrayList<Pflanze>>() {});
			client.close();
			return alP;
		} else {
			// Fehlertext auslesen und weitergeben
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}

	// Holt Pflanzen nach Suchtext (GET /pflanze?suchtext=...)
	public static ArrayList<Pflanze> getPflanze(String text) throws SQLException {
		// HTTP-Client erstellen
		Client client = ClientBuilder.newClient();
		// Ziel-URL mit Query-Parameter für Textsuche
		WebTarget target = client.target("http://localhost:4711/pflanze")
				.queryParam("suchtext", text);

		// Sende GET-Anfrage
		Response response = target.request(MediaType.APPLICATION_JSON).get();

		// Erfolg: 200 OK → Ergebnisliste einlesen
		if (response.getStatus() == Status.OK.getStatusCode()) {
			ArrayList<Pflanze> alP = response.readEntity(new GenericType<ArrayList<Pflanze>>() {});
			client.close();
			return alP;
		} else {
			// Fehlertext auslesen und weitergeben
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}
}
