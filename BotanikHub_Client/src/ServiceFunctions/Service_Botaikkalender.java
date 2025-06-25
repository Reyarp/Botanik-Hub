package ServiceFunctions;

import java.sql.SQLException;
import java.util.ArrayList;

import Modell.Botanikkalender;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

public class Service_Botaikkalender {

	public static ArrayList<Botanikkalender> getBotanikkalender(int pflanzeID) throws SQLException {
		// HTTP-Client erstellen
		Client client = ClientBuilder.newClient();

		// Baut das Ziel der Anfrage zusammen: Basis-URL + pflanzeID
		// Beispiel: http://localhost:4711/botanikkalender/5
		WebTarget target = client.target("http://localhost:4711/botanikkalender/" + pflanzeID);

		// Sendet eine GET-Anfrage an die Ziel-URL und erwartet eine JSON-Antwort
		Response response = target.request(MediaType.APPLICATION_JSON).get();

		// Prüft, ob der Server mit HTTP 200 OK geantwortet hat
		if (response.getStatus() == Status.OK.getStatusCode()) {
			// Liest die Antwort in eine Liste von Botanikkalender-Objekten ein
			ArrayList<Botanikkalender> alP = response.readEntity(new GenericType<ArrayList<Botanikkalender>>() {});
			// Schliesst den Client, um Ressourcen freizugeben
			client.close();
			// Gibt die eingelesene Liste zurück
			return alP;
		} else {
			// Liest die Fehlermeldung als String aus der Antwort
			String e = response.readEntity(String.class);
			// Schliesst auch im Fehlerfall den Client
			client.close();
			// Wirft eine SQLException mit der Fehlermeldung
			throw new SQLException(e);
		}
	}

}
 