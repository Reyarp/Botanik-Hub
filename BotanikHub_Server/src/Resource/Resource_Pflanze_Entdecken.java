package Resource;

import java.sql.SQLException;
import java.util.ArrayList;

import Database.DB_PflanzenEntdecken;
import Modell.Pflanze;
import Modell.PflanzenEntdecken;
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

@Path("pflanze-entdecken")
public class Resource_Pflanze_Entdecken {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response postPflanzeEntdecken(PflanzenEntdecken entdecken) {

		try {
			DB_PflanzenEntdecken.insertPflanzenEntdecken(entdecken);
			return Response.status(Response.Status.CREATED).build();		

		} catch(SQLException e) {
			return Response.serverError().entity("Fehler: POST Pflanze-Entdecken" + e).build();
		}
	}

	@DELETE
	@Path("{pflanzeID}/{benutzerID}")
	public Response deletePflanzeEntdecken(@PathParam("pflanzeID") int pflanzeID, @PathParam("benutzerID") int benutzerID){
		try {
			DB_PflanzenEntdecken.deletePflanzenEntdecken(pflanzeID, benutzerID);
			return Response.status(Status.NO_CONTENT).build();
		} catch(SQLException e) {
			return Response.serverError().entity("Fehler: DELETE Pflanze-Entdecken" + e).build();
		}
	}
	
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPflanzeEntdecken(@PathParam("id") int id) {
		try {
			ArrayList<Pflanze> p = DB_PflanzenEntdecken.readPflanzeEntdecken(id);
			return Response.status(Status.OK).entity(p).build();
		} catch(SQLException e) {
			return Response.serverError().entity("Fehler: GET Pflanze{Pflanze-Entdecken}" + e).build();
		}
	}
}
