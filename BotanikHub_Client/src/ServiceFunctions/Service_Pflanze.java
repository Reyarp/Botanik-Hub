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

	/*
	 * Neue Pflanze anlegen über POST /pflanze
	 */
	public static Pflanze postPflanze(Pflanze pflanze) throws SQLException{
		Client client = ClientBuilder.newClient(); 
		WebTarget target = client.target("http://localhost:4711/pflanze");	 	// Ziel-URL: http://localhost:4711/benutzer

		Response response = target												 // Sende POST-Request: Benutzer als JSON, akzeptiere Text als Antwort
				.request(MediaType.APPLICATION_JSON)                        	 // Erwartet eine Textantwort
				.post(Entity.entity(pflanze, MediaType.APPLICATION_JSON));		 // Sendet JSON-Daten

		if (response.getStatus() == Status.CREATED.getStatusCode()) {			 // Wenn der Server keinen 201 CREATED zurückgibt -> Fehler
			Pflanze p = response.readEntity(Pflanze.class);						 // Eingefügte Pflanze mit ID
			client.close();
			return p;
		} else {
			String e = response.readEntity(String.class);						 // Lese den Fehlertext aus der Serverantwort
			client.close();
			throw new SQLException(e);											 // Wirf SQL-Fehler mit dem Servertext
		}
	}
	
	/*
	 * Pflaze bearbeiten über PUT /pflanze
	 */
	public static void putPflanze(Pflanze pflanze) throws SQLException {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:4711/pflanze/" + pflanze.getPflanzenID());

		Response response = target
				.request(MediaType.APPLICATION_JSON)
				.put(Entity.entity(pflanze, MediaType.APPLICATION_JSON));

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
	public static void deletePflanze(int id) throws SQLException{
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:4711/pflanze/" + id);
		
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
	public static ArrayList<Pflanze> getPflanze() throws SQLException{
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:4711/pflanze");
		
		Response response = target.request(MediaType.APPLICATION_JSON).get();
		
		if(response.getStatus() == Status.OK.getStatusCode()) {
			ArrayList<Pflanze> alP = response.readEntity(new GenericType<ArrayList<Pflanze>>() {});
			client.close();
			return alP;
		} else {
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}
	
	public static ArrayList<Pflanze> getPflanze(String text) throws SQLException{
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:4711/pflanze")
								.queryParam("suchtext", text);
		
		Response response = target.request(MediaType.APPLICATION_JSON).get();
		
		if(response.getStatus() == Status.OK.getStatusCode()) {
			ArrayList<Pflanze> alP = response.readEntity(new GenericType<ArrayList<Pflanze>>() {});
			client.close();
			return alP;
		} else {
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}
}
