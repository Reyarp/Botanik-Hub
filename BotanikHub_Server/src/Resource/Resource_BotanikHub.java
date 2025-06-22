package Resource;

import java.sql.SQLException;
import java.util.ArrayList;
import Database.DB_BotanikHub;
import Modell.BotanikHub;
import Modell.Pflanze;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("botanik-hub")
public class Resource_BotanikHub {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postBotanikHubPflanze (BotanikHub hub) {

		try {
			DB_BotanikHub.insertBotanikHub(hub);
			return Response.status(Response.Status.CREATED).build();		

		} catch(SQLException e) {
			return Response.serverError().entity("Fehler: POST BotanikHub" + e.toString()).build();
		}
	}

	@DELETE
	@Path("{pflanzeID}/{benutzerID}")
	public Response deleteBotanikHubPflanze(@PathParam("pflanzeID") int pflanzeID, @PathParam("benutzerID") int benutzerID){
		try {
			DB_BotanikHub.deleteBotanikHub(pflanzeID, benutzerID);
			return Response.status(Status.NO_CONTENT).build();
		} catch(SQLException e) {
			return Response.serverError().entity("Fehler: DELETE BotanikHub" + e.toString()).build();
		}
	}

	@PUT
	@Path("updatenotiz")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response putNotiz(BotanikHub hub) {
		try {
			DB_BotanikHub.updateNotiz(hub);			
			return Response.status(Status.OK).build();
		} catch(SQLException e) {
			return Response.serverError().entity("Fehler: PUT Notiz{BotanikHub}" + e.toString()).build();
		}
	}

	@PUT
	@Path("updateUserPflanzenbild")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response putUserBase64(BotanikHub hub) {
		try {
			DB_BotanikHub.updateUserBase64(hub);
			return Response.status(Status.OK).build();
		} catch(SQLException e) {
			return Response.serverError().entity("Fehler: PUT UserBase64{BotanikHub}" + e.toString()).build();
		}
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBotanikHubPflanzen(@PathParam("id") int benutzeriD) {
		try {
			ArrayList<Pflanze> p = DB_BotanikHub.readBotanikHubPflanzen(benutzeriD);
			return Response.status(Status.OK).entity(p).build();
		} catch(SQLException e) {
			return Response.serverError().entity("Fehler: GET Pflanze{BotanikHub}" + e.toString()).build();
		}
	}
}
