package ServiceFunctions;

import java.sql.SQLException;
import java.util.ArrayList;

import Modell.Benutzer;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

public class Service_Benutzer {


	// Registriert einen neuen Benutzer über die REST-Schnittstelle
	public static void postBenutzer(Benutzer benutzer) throws SQLException {
		// HTTP-Client erstellen
		Client client = ClientBuilder.newClient();
		// Ziel-URL für die Registrierung des Benutzers
		WebTarget target = client.target("http://localhost:4711/benutzer/registrieren");
		// Sendet die POST-Anfrage mit JSON-Daten
		Response response = target
				.request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(benutzer, MediaType.APPLICATION_JSON));
		// Wenn der Status 201 CREATED ist, wurde der Benutzer erfolgreich erstellt
		if (response.getStatus() == Status.CREATED.getStatusCode()) {
			client.close(); // Client schließen
		} else {
			// Fehlernachricht auslesen
			String e = response.readEntity(String.class);
			client.close(); // Client auch im Fehlerfall schließen
			throw new SQLException(e); // Fehler weiterreichen
		}
	}

	// Aktualisiert einen vorhandenen Benutzer
	public static void putBenutzer(Benutzer benutzer) throws SQLException {
		// HTTP-Client erstellen
		Client client = ClientBuilder.newClient();
		// Ziel-URL mit Benutzer-ID für das Update
		WebTarget target = client.target("http://localhost:4711/benutzer/" + benutzer.getBenutzerId());
		// Sendet die PUT-Anfrage mit aktualisiertem Benutzerobjekt
		Response response = target
				.request(MediaType.APPLICATION_JSON)
				.put(Entity.entity(benutzer, MediaType.APPLICATION_JSON));
		// Erfolgreiche Aktualisierung
		if (response.getStatus() == Status.OK.getStatusCode()) {
			client.close();
		} else {
			// Fehlernachricht auslesen
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}

	// Führt einen Login-Versuch durch
	public static Benutzer loginBenutzer(String name, String passwort) throws SQLException {
		// HTTP-Client erstellen
		Client client = ClientBuilder.newClient();
		// Ziel-URL mit Name und Passwort
		WebTarget target = client.target("http://localhost:4711/benutzer/login/" + name + "/" + passwort);
		// Sendet GET-Anfrage
		Response response = target.request(MediaType.APPLICATION_JSON).get();
		// Wenn erfolgreich, Benutzerobjekt auslesen
		if (response.getStatus() == Status.OK.getStatusCode()) {
			Benutzer benutzer = response.readEntity(Benutzer.class);
			client.close();
			return benutzer;
		} else {
			// Fehlernachricht auslesen
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}

	// Holt alle Benutzer aus der Datenbank
	public static ArrayList<Benutzer> getBenutzer() throws SQLException {
		// HTTP-Client erstellen
		Client client = ClientBuilder.newClient();
		// Ziel-URL zum Abrufen aller Benutzer
		WebTarget target = client.target("http://localhost:4711/benutzer");
		// Sendet GET-Anfrage
		Response response = target.request(MediaType.APPLICATION_JSON).get();
		// Erfolgreich? Dann Liste lesen
		if (response.getStatus() == Status.OK.getStatusCode()) {
			ArrayList<Benutzer> alB = response.readEntity(new GenericType<ArrayList<Benutzer>>() {});
			client.close();
			return alB;
		} else {
			// Fehlernachricht auslesen
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}
}
