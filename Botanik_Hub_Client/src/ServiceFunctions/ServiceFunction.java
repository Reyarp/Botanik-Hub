package ServiceFunctions;

import java.sql.SQLException;
import java.util.ArrayList;

import Modell.Benutzer;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

public class ServiceFunction {

	public static ArrayList<Benutzer> getBenutzer() throws SQLException {
	    Client client = ClientBuilder.newClient();
	    WebTarget target = ClientBuilder.newClient().target("http://localhost:4711/benutzer");
	    Invocation.Builder request = target.request(MediaType.APPLICATION_JSON);
	    Response response = request.get();
	    
	    int rc = response.getStatus();
	    if(rc == Status.OK.getStatusCode()) {
	        ArrayList<Benutzer> benutzerListe = response.readEntity(new GenericType<ArrayList<Benutzer>>() {});
	        client.close();
	        return benutzerListe;
	    } else {
	        String e = response.readEntity(String.class);
	        client.close();
	        throw new SQLException(e);
	    }
	}

}
