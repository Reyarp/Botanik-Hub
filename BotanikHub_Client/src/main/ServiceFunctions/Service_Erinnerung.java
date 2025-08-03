package ServiceFunctions;

import java.sql.SQLException;
import java.util.ArrayList;

import Modell.Erinnerungen;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

public class Service_Erinnerung {

	// Fügt eine neue Erinnerung hinzu (POST /erinnerung)
	public static void postErinnerung(Erinnerungen erinnerung) throws SQLException {
		// HTTP-Client erstellen
		Client client = ClientBuilder.newClient();
		// Ziel-URL für POST-Anfrage
		WebTarget target = client.target("http://localhost:4711/erinnerung");

		// Sende POST-Anfrage mit Erinnerung als JSON, akzeptiere Text-Antwort
		Response response = target
				.request(MediaType.TEXT_PLAIN)
				.post(Entity.entity(erinnerung, MediaType.APPLICATION_JSON));

		// Erfolg: 201 CREATED
		if (response.getStatus() == Status.CREATED.getStatusCode()) {
			client.close();
		} else {
			// Fehlertext lesen und weiterwerfen
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}

	// Aktualisiert eine vorhandene Erinnerung (PUT /erinnerung/{id})
	public static void putErinnerung(Erinnerungen erinnerung) throws SQLException {
		// HTTP-Client erstellen
		Client client = ClientBuilder.newClient();
		// Ziel-URL mit ID der Erinnerung
		WebTarget target = client.target("http://localhost:4711/erinnerung/" + erinnerung.getErinnerungID());

		// Sende PUT-Anfrage mit aktualisierter Erinnerung
		Response response = target
				.request(MediaType.APPLICATION_JSON)
				.put(Entity.entity(erinnerung, MediaType.APPLICATION_JSON));

		// Erfolg: 200 OK
		if (response.getStatus() == Status.OK.getStatusCode()) {
			client.close();
		} else {
			// Fehlertext lesen und weiterwerfen
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}

	// Löscht eine Erinnerung (DELETE /erinnerung/{id})
	public static void deleteErinnerung(int id) throws SQLException {
		// HTTP-Client erstellen
		Client client = ClientBuilder.newClient();
		// Ziel-URL mit ID der zu löschenden Erinnerung
		WebTarget target = client.target("http://localhost:4711/erinnerung/" + id);

		// Sende DELETE-Anfrage
		Response response = target
				.request(MediaType.APPLICATION_JSON)
				.delete();

		// Erfolg: 204 NO_CONTENT
		if (response.getStatus() == Status.NO_CONTENT.getStatusCode()) {
			client.close();
		} else {
			// Fehlertext lesen und weiterwerfen
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}

	// Holt alle Erinnerungen für eine Pflanze (GET /erinnerung/{id})
	public static ArrayList<Erinnerungen> getErinnerung(int id) throws SQLException {
		// HTTP-Client erstellen
		Client client = ClientBuilder.newClient();
		// Ziel-URL mit Pflanzen-ID
		WebTarget target = client.target("http://localhost:4711/erinnerung/" + id);

		// Sende GET-Anfrage
		Response response = target.request(MediaType.APPLICATION_JSON).get();

		// Erfolg: 200 OK -> Liste einlesen
		if (response.getStatus() == Status.OK.getStatusCode()) {
			ArrayList<Erinnerungen> alP = response.readEntity(new GenericType<ArrayList<Erinnerungen>>() {});
			client.close();
			return alP;
		} else {
			// Fehlertext lesen und weiterwerfen
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}
}
