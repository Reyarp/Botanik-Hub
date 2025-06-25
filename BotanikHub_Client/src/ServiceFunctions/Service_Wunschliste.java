package ServiceFunctions;

import java.sql.SQLException;
import java.util.ArrayList;

import Modell.MeineWunschliste;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

public class Service_Wunschliste {

	// Fügt einen Eintrag in die Wunschliste ein (POST /wunschliste)
	public static void postWunschliste(MeineWunschliste wunsch) throws SQLException {
		// HTTP-Client erstellen
		Client client = ClientBuilder.newClient();
		// Ziel-URL für POST-Anfrage
		WebTarget target = client.target("http://localhost:4711/wunschliste");

		// Sende POST-Anfrage mit JSON-Daten
		Response response = target
				.request(MediaType.TEXT_PLAIN)
				.post(Entity.entity(wunsch, MediaType.APPLICATION_JSON));

		// Erfolg: 201 CREATED
		if (response.getStatus() == Status.CREATED.getStatusCode()) {
			client.close();
		} else {
			// Fehlertext auslesen und weitergeben
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}

	// Entfernt eine Pflanze aus der Wunschliste (DELETE /wunschliste/{pflanzeID}/{benutzerID})
	public static void deleteWunschliste(int pflanzeID, int benutzeriD) throws SQLException {
		// HTTP-Client erstellen
		Client client = ClientBuilder.newClient();
		// Ziel-URL mit Pflanze- und Benutzer-ID
		WebTarget target = client.target("http://localhost:4711/wunschliste/" + pflanzeID + "/" + benutzeriD);

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

	// Holt alle Wunschlistenpflanzen eines Benutzers (GET /wunschliste/{benutzerID})
	public static ArrayList<MeineWunschliste> getWLPflanzen(int benutzerID) throws SQLException {
		// HTTP-Client erstellen
		Client client = ClientBuilder.newClient();
		// Ziel-URL mit Benutzer-ID
		WebTarget target = client.target("http://localhost:4711/wunschliste/" + benutzerID);

		// Sende GET-Anfrage
		Response response = target.request(MediaType.APPLICATION_JSON).get();

		// Erfolg: 200 OK → Liste einlesen
		if (response.getStatus() == Status.OK.getStatusCode()) {
			ArrayList<MeineWunschliste> alW = response.readEntity(new GenericType<ArrayList<MeineWunschliste>>() {});
			client.close();
			return alW;
		} else {
			// Fehlertext auslesen und weitergeben
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}
}
