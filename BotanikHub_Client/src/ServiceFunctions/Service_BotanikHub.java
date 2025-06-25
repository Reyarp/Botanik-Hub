package ServiceFunctions;

import java.sql.SQLException;
import java.util.ArrayList;

import org.glassfish.jersey.jsonb.JsonBindingFeature;

import Modell.BotanikHub;
import Modell.Pflanze;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

public class Service_BotanikHub {
	
	// Sendet ein BotanikHub-Objekt an den Server (POST)
	public static void postBotanikHub(BotanikHub hub) throws SQLException {
		// HTTP-Client erstellen
		Client client = ClientBuilder.newClient();
		// Ziel-URL für POST-Anfrage
		WebTarget target = client.target("http://localhost:4711/botanik-hub");

		// Sende POST-Anfrage mit JSON-Daten
		Response response = target
				.request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(hub, MediaType.APPLICATION_JSON));

		// Prüfe auf erfolgreichen Status 201 CREATED
		if (response.getStatus() == Status.CREATED.getStatusCode()) {
			client.close();
		} else {
			// Fehlertext auslesen und weiterwerfen
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}

	// Löscht eine Pflanze aus dem BotanikHub eines Benutzers
	public static void deleteBotanikHub(int pflanzeID, int benutzeriD) throws SQLException {
		// HTTP-Client erstellen
		Client client = ClientBuilder.newClient();
		// Ziel-URL mit Pflanze- und Benutzer-ID
		WebTarget target = client.target("http://localhost:4711/botanik-hub/" + pflanzeID + "/" + benutzeriD);

		// Sende DELETE-Anfrage
		Response response = target
				.request(MediaType.APPLICATION_JSON)
				.delete();

		// Erfolgreich gelöscht (204 NO_CONTENT)?
		if (response.getStatus() == Status.NO_CONTENT.getStatusCode()) {
			client.close();
		} else {
			// Fehlertext auslesen und weiterwerfen
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}

	// Aktualisiert die Notiz im BotanikHub-Eintrag
	public static void putNotiz(BotanikHub hub) throws SQLException {
		// HTTP-Client erstellen
		Client client = ClientBuilder.newClient();
		// Ziel-URL für PUT-Anfrage zur Notizaktualisierung
		WebTarget target = client.target("http://localhost:4711/botanik-hub/updatenotiz");

		// Sende PUT-Anfrage mit aktualisiertem Hub-Objekt
		Response response = target
				.request(MediaType.APPLICATION_JSON)
				.put(Entity.entity(hub, MediaType.APPLICATION_JSON));

		// Erfolg prüfen
		if (response.getStatus() == Status.OK.getStatusCode()) {
			client.close();
		} else {
			// Fehlertext auslesen und weiterwerfen
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}

	// Holt alle Pflanzen eines Benutzers aus dem BotanikHub
	public static ArrayList<Pflanze> getBHPflanzen(int benutzerID) throws SQLException {
		// HTTP-Client mit JSON-Binding erstellen
		Client client = ClientBuilder.newBuilder().register(JsonBindingFeature.class).build();
		// Ziel-URL für GET-Anfrage mit Benutzer-ID
		WebTarget target = client.target("http://localhost:4711/botanik-hub/" + benutzerID);

		// Sende GET-Anfrage
		Response response = target.request(MediaType.APPLICATION_JSON).get();

		// Erfolg prüfen und Pflanzenliste zurückgeben
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
