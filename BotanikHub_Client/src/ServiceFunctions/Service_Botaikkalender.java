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

	public static ArrayList<Botanikkalender> getBotanikkalender(int pflanzeID) throws SQLException{
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:4711/botanikkalender/" + pflanzeID);
		
		Response response = target.request(MediaType.APPLICATION_JSON).get();
		
		if(response.getStatus() == Status.OK.getStatusCode()) {
			ArrayList<Botanikkalender> alP = response.readEntity(new GenericType<ArrayList<Botanikkalender>>() {});
			client.close();
			return alP;
		} else {
			String e = response.readEntity(String.class);
			client.close();
			throw new SQLException(e);
		}
	}
}
 