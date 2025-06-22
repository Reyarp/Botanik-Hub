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

	public static void postWunschliste(MeineWunschliste wunsch) throws SQLException{
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:4711/wunschliste");	 	// Ziel-URL: http://localhost:4711/benutzer

		Response response = target												 // Sende POST-Request: Benutzer als JSON, akzeptiere Text als Antwort
				.request(MediaType.TEXT_PLAIN)                        			 // Erwartet eine Textantwort
				.post(Entity.entity(wunsch, MediaType.APPLICATION_JSON));		 	 // Sendet JSON-Daten

		if (response.getStatus() == Status.CREATED.getStatusCode()) {			 // Wenn der Server keinen 201 CREATED zurückgibt -> Fehler
			client.close();
		} else {
			String e = response.readEntity(String.class);						 // Lese den Fehlertext aus der Serverantwort
			client.close();
			throw new SQLException(e);											 // Wirf SQL-Fehler mit dem Servertext
		}
	}
	
	public static void deleteWunschliste(int pflanzeID, int benutzeriD) throws SQLException{
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:4711/wunschliste/" + pflanzeID + "/" + benutzeriD);
		
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
	
	public static ArrayList<MeineWunschliste> getWLPflanzen(int benutzerID) throws SQLException{
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:4711/wunschliste/" + benutzerID);
		
		Response response = target.request(MediaType.APPLICATION_JSON).get();
		
		if(response.getStatus() == Status.OK.getStatusCode()) {
			ArrayList<MeineWunschliste> alW = response.readEntity(new GenericType<ArrayList<MeineWunschliste>>() {});
			client.close();
			return alW;
		} else {
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}
}
