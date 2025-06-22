package Resource;

import java.sql.SQLException;
import java.util.ArrayList;

import Database.DB_Wunschliste;
import Modell.Benutzer;
import Modell.MeineWunschliste;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;


@Path("wunschliste")
public class Resource_Wunschliste {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response postWunschliste (MeineWunschliste wunsch) {

		try {
			DB_Wunschliste.insertWunschliste(wunsch);
			return Response.status(Response.Status.CREATED).build();		

		} catch(SQLException e) {
			return Response.serverError().entity("Fehler: POST Wunschliste" + e).build();
		}
	}

	@DELETE
	@Path("{pflanzeID}/{benutzerID}")
	public Response deleteWunschliste(@PathParam("pflanzeID") int pflanzeID, @PathParam("benutzerID") int benutzerID){
		try {
			DB_Wunschliste.deleteWunschliste(pflanzeID, benutzerID);
			return Response.status(Status.NO_CONTENT).build();
		} catch(SQLException e) {
			return Response.serverError().entity("Fehler: DELETE Wunschliste" + e).build();
		}
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getWunschliste(@PathParam("id") int id) {
		try {
			Benutzer b = new Benutzer();		// Dummy-Benutzerobjekt mit gesetzter ID,
												// notwendig für den Methodenaufruf, der ein Benutzer-Objekt erwartet.
			b.setBenutzerId(id);
			ArrayList<MeineWunschliste> p = DB_Wunschliste.readWunschlistePflanze(b);
			return Response.status(Status.OK).entity(p).build();
		} catch(SQLException e) {
			return Response.serverError().entity("Fehler: GET Pflanze{Pflanze-Entdecken}" + e).build();
		}
	}
}
