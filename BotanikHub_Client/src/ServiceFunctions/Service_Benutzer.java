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


	/*
	 * Registriert einen neuen Benutzer über die REST-Schnittstelle
	 * POST /benutzer
	 * Sendet das Benutzerobjekt als JSON an den Server
	 * Erwartet eine Textantwort (z. B. „Benutzer erfolgreich erstellt“)
	 */
	public static void postBenutzer(Benutzer benutzer) throws SQLException {
		Client client = ClientBuilder.newClient();
		// Ziel-URL: http://localhost:4711/benutzer/registrieren
		WebTarget target = client.target("http://localhost:4711/benutzer/registrieren");	 	
		//Kein Invocation.Builder verwendet – request().post usw. übernimmt beide Schritte direkt -> Keine extra variable nötig

		// Sende POST-Request: Benutzer als JSON, akzeptiere Text als Antwort
		Response response = target	
				// Erwartet JSON-Daten
				.request(MediaType.APPLICATION_JSON)    
				// Sendet JSON-Daten
				.post(Entity.entity(benutzer, MediaType.APPLICATION_JSON));		 

		if (response.getStatus() == Status.CREATED.getStatusCode()) {
			client.close();
			 // Wenn der Server keinen 201 CREATED zurückgibt -> Fehler
		} else {	
			// Lese den Fehlertext aus der Serverantwort
			String e = response.readEntity(String.class);		
			// Resource freigeben
			client.close(); 	
			 // SQL Fehler weiterwerfen
			throw new SQLException(e);											
		}
	} 


	/*
	 * Aktualisiert einen bestehenden Benutzer
	 * PUT /benutzer/{id}
	 * Sendet das aktualisierte Benutzerobjekt als JSON
	 */
	public static void putBenutzer(Benutzer benutzer) throws SQLException {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:4711/benutzer/" + benutzer.getBenutzerId());

		Response response = target
				.request(MediaType.APPLICATION_JSON)
				.put(Entity.entity(benutzer, MediaType.APPLICATION_JSON));

		if (response.getStatus() == Status.OK.getStatusCode()) {
			client.close();
		} else {
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}

	/*
	 * Führt einen Login-Versuch durch
	 * GET /benutzer/login/{name}/{passwort}
	 * Liefert bei Erfolg das Benutzerobjekt als JSON
	 */
	public static Benutzer loginBenutzer(String name, String passwort) throws SQLException {
		Client client = ClientBuilder.newClient();
		// Statt Webtarget basetarget hab ich gleich alles in einen String getan
		WebTarget target = client.target("http://localhost:4711/benutzer/login/" + name + "/" + passwort); 
		Response response = target.request(MediaType.APPLICATION_JSON).get();

		if (response.getStatus() == Status.OK.getStatusCode()) {
			// Vorher benutzer lesen dann -> close
			Benutzer benutzer = response.readEntity(Benutzer.class);			
			client.close();
			return benutzer;													// Benutzer wird auf JSON_PLAIN_TEXT in ein Java Objekt umgewandelt
		} else {
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}

	/*
	 * Holt alle Benutzer aus der Datenbank
	 * GET /benutzer
	 * Gibt eine Liste von Benutzerobjekten im JSON-Format zurück
	 */
	public static ArrayList<Benutzer> getBenutzer() throws SQLException {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:4711/benutzer");
		Response response = target.request(MediaType.APPLICATION_JSON).get();

		if (response.getStatus() == Status.OK.getStatusCode()) {
			ArrayList<Benutzer> alB = response.readEntity(new GenericType<ArrayList<Benutzer>>() {});
			client.close();
			return alB;
		} else {
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}

}
