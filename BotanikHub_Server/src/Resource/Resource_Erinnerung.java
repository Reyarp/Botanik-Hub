package Resource;

import java.sql.SQLException;
import java.util.ArrayList;

import Database.DB_Erinnerungen;
import Modell.Erinnerungen;
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

@Path("erinnerung")
public class Resource_Erinnerung {

	/* POST /erinnerung
	 * insertErinnerung
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response postErinnerung(Erinnerungen erinnerung) {

		try {
			DB_Erinnerungen.insertErinnerung(erinnerung);
			return Response.status(Status.CREATED).build();

		} catch(SQLException e) {
			return Response.serverError().entity("Fehler: POST Erinnerung" + e.toString()).build();
		}
	}

	/* PUT /erinnerung
	 * updateErinnerung
	 */
	@PUT
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response putErinnerung(@PathParam("id") int id, Erinnerungen erinnerung) {
		try {
			erinnerung.setErinnerungID(id);
			DB_Erinnerungen.updateErinnerungen(erinnerung);
			return Response.status(Status.OK).build();
		} catch(SQLException e) {
			return Response.serverError().entity("Fehler: PUT Erinnerung" + e.toString()).build();
		}
	}

	/* DELETE /erinnerung
	 * deleteErinnerung
	 */
	@DELETE
	@Path("{id}")
	public Response deleteErinnerung(@PathParam("id") int id){
		try {
			DB_Erinnerungen.deleteErinnerungen(id);
			return Response.status(Status.NO_CONTENT).build();
		} catch(SQLException e) {
			return Response.serverError().entity("Fehler: DELETE Erinnerung" + e.toString()).build();
		}
	}

	/* GET /erinnerung
	 * readErinnerung
	 */
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getErinnerung(@PathParam("id") int id) {
		try {
			ArrayList<Erinnerungen> p = DB_Erinnerungen.readErinnerungen(id);
			return Response.status(Status.OK).entity(p).build();
		} catch(SQLException e) {
			return Response.serverError().entity("Fehler: GET Erinnerung" + e.toString()).build();
		}
	}
}
