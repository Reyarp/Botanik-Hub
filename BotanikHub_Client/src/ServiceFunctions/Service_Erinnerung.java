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

	/* 
	 * Erinnerung hinzufügen POST /erinnerung
	 */
	/*
	 * Neue Pflanze anlegen über POST /pflanze
	 */
	public static void postErinnerung(Erinnerungen erinnerung) throws SQLException{
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:4711/erinnerung");	 	// Ziel-URL: http://localhost:4711/benutzer

		Response response = target												 // Sende POST-Request: Benutzer als JSON, akzeptiere Text als Antwort
				.request(MediaType.TEXT_PLAIN)                        			 // Erwartet eine Textantwort
				.post(Entity.entity(erinnerung, MediaType.APPLICATION_JSON));		 // Sendet JSON-Daten

		if (response.getStatus() == Status.CREATED.getStatusCode()) {			 // Wenn der Server keinen 201 CREATED zurückgibt -> Fehler
			client.close();
		} else {
			String e = response.readEntity(String.class);						 // Lese den Fehlertext aus der Serverantwort
			client.close();
			throw new SQLException(e);											 // Wirf SQL-Fehler mit dem Servertext
		}
	}
	
	/*
	 * Pflaze bearbeiten über PUT /pflanze
	 */
	public static void putErinnerung(Erinnerungen erinnerung) throws SQLException {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:4711/erinnerung/" + erinnerung.getErinnerungID());

		Response response = target
				.request(MediaType.APPLICATION_JSON)
				.put(Entity.entity(erinnerung, MediaType.APPLICATION_JSON));

		if (response.getStatus() == Status.OK.getStatusCode()) {
			client.close();
		} else {
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}
	
	/*
	 * Pflanze löschen über DELETE /pflanze
	 */
	public static void deleteErinnerung(int id) throws SQLException{
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:4711/erinnerung/" + id);
		
		Response response = target
				.request(MediaType.APPLICATION_JSON)
				.delete();
		
		if(response.getStatus() == Status.NO_CONTENT.getStatusCode()) {
			client.close();
		} else {
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}
	
	/*
	 * Pflanze lesen über GET /pflanze
	 */
	public static ArrayList<Erinnerungen> getErinnerung(int id) throws SQLException{
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:4711/erinnerung/" + id);
		
		Response response = target.request(MediaType.APPLICATION_JSON).get();
		
		if(response.getStatus() == Status.OK.getStatusCode()) {
			ArrayList<Erinnerungen> alP = response.readEntity(new GenericType<ArrayList<Erinnerungen>>() {});
			client.close();
			return alP;
		} else {
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}
}
