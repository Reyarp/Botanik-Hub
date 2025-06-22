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



	public static void postBotanikHub(BotanikHub hub) throws SQLException{
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:4711/botanik-hub");	 	// Ziel-URL: http://localhost:4711/benutzer

		Response response = target												 // Sende POST-Request: Benutzer als JSON, akzeptiere Text als Antwort
				.request(MediaType.APPLICATION_JSON)                        			 // Erwartet eine Textantwort
				.post(Entity.entity(hub, MediaType.APPLICATION_JSON));		 	 // Sendet JSON-Daten

		if (response.getStatus() == Status.CREATED.getStatusCode()) {			 // Wenn der Server keinen 201 CREATED zurückgibt -> Fehler
			client.close();
		} else {
			String e = response.readEntity(String.class);						 // Lese den Fehlertext aus der Serverantwort
			client.close();
			throw new SQLException(e);											 // Wirf SQL-Fehler mit dem Servertext
		}
	}

	public static void deleteBotanikHub(int pflanzeID, int benutzeriD) throws SQLException{
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:4711/botanik-hub/" + pflanzeID + "/" + benutzeriD);

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

	public static void putNotiz(BotanikHub hub) throws SQLException {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:4711/botanik-hub/updatenotiz");

		Response response = target
				.request(MediaType.APPLICATION_JSON)
				.put(Entity.entity(hub, MediaType.APPLICATION_JSON));

		if (response.getStatus() == Status.OK.getStatusCode()) {
			client.close();
		} else {
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}

	public static void putUserBase64(BotanikHub hub) throws SQLException{
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:4711/botanik-hub/updateUserPflanzenbild");

		Response response = target
				.request(MediaType.APPLICATION_JSON)
				.put(Entity.entity(hub, MediaType.APPLICATION_JSON));

		if (response.getStatus() == Status.OK.getStatusCode()) {
			client.close();
		} else {
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}

	public static ArrayList<Pflanze> getBHPflanzen(int benutzerID) throws SQLException{
		Client client = ClientBuilder.newBuilder().register(JsonBindingFeature.class).build();
		WebTarget target = client.target("http://localhost:4711/botanik-hub/" + benutzerID);

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
