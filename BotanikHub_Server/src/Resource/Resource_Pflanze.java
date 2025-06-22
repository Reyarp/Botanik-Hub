package Resource;


import java.sql.SQLException;
import java.util.ArrayList;
import Database.DB_Pflanze;
import Modell.Pflanze;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;


@Path("pflanze")
public class Resource_Pflanze {

	/* POST /Pflanze
	 * Hier kommt der Teil für den Bildupload rein
	 * Einen Zielordner festlegen zum abspeichern über Path methoden
	 * Dateiname erstetzen (Optional) für bessere lesbarkeit
	 * byte[] Array dekodieren und die Datei in den ordner Speichern
	 * Bildpfad in die DB speichern -> anschliessend den Base64 String auf null setzen
	 * Da ich die datei nicht in die DB_Pflanze speichere
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postPflanze(Pflanze pflanze) {

		try {
			DB_Pflanze.insertPflanze(pflanze);
			return Response.status(Status.CREATED).entity(pflanze).build();

		} catch(SQLException e) {
			if(e.getMessage().contains("unique")) {
				return Response.status(Response.Status.CONFLICT).entity("Pflanzenname bereits vorhanden").build();
			}
			return Response.serverError().entity("Fehler: POST Pflanze" + e.toString()).build();
		}
	}

	/* PUT /pflanze
	 * updatePflanze
	 */
	@PUT
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response putPflanze(@PathParam("id") int id, Pflanze pflanze) {
		try {
			pflanze.setPflanzenID(id);
			DB_Pflanze.updatePflanze(pflanze);
			return Response.status(Status.OK).build();
		} catch(SQLException e) {
			return Response.serverError().entity("Pflanze existiert bereits").build();
		}
	}

	/* DELETE /pflanze
	 * Da das Bild nicht in der DB_Pflanze gespeichert wird 
	 * muss ich sie manuell entfernen
	 * Dazu brauch ich die PflanzeID -> DB_Pflanze methode
	 */
	@DELETE
	@Path("{id}")
	public Response deletePflanze(@PathParam("id") int id){
		try {
			DB_Pflanze.deletePflanze(id);
			return Response.status(Status.NO_CONTENT).build();
		} catch(SQLException e) {
			return Response.serverError().entity("Fehler: DELETE Pflanze" + e.toString()).build();
		}
	}

	/*
	 * GET /pflanze
	 * Liefert alle Pflanzen oder gefilterte Pflanzen über ?suchtext=
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPflanzen(@QueryParam("suchtext") String text) {
		try {
			ArrayList<Pflanze> pflanzen;

			if (text != null && !text.isBlank()) {
				pflanzen = DB_Pflanze.readPflanzenByFilter(text);
			} else {
				pflanzen = DB_Pflanze.readAllePflanzen();
			}

			return Response.status(Status.OK).entity(pflanzen).build();

		} catch (SQLException e) {
			return Response.serverError().entity("Fehler: GET Pflanze " + e.toString()).build();
		}
	}
	
//	/*
//	 * Das ist die Bild Upload Version mit eigenen Ordner Ohne DB Speicherung
//	 */
//	@POST
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response postPflanze(Pflanze pflanze) {
//
//		try {
//			// Überprüfung auf null
//			if(pflanze.getBildBase64() != null && !pflanze.getBildBase64().isEmpty()) {
//
//				// Zielordner erstellen
//				java.nio.file.Path ordner = Paths.get("C:\\Users\\denis\\Desktop\\Botanik-Hub\\Admin_Bilder");
//				// Wenn Ordner nicht existiert -> erstellen
//				if(!Files.exists(ordner)) {
//					Files.createDirectory(ordner);
//				}
//
//				// Bildname -> pflanzename + jpg erstellen (Leerzeichen durch _ erstezen)
//				String dateiname = pflanze.getPflanzenName().replaceAll("\\s+", "_") + ".jpg";
//				// Im Ordner ersetzen
//				java.nio.file.Path zielDatei = Paths.get(ordner.toString(), dateiname);
//
//				// Base64 byte[] array jetzt dekodieren und als Datei speichern
//				byte[] bildBytes = Base64.getDecoder().decode(pflanze.getBildBase64());
//				// In den Ordner speichern
//				Files.write(zielDatei, bildBytes);
//
//				// Ordnerpfad als String in Pflanze setzen (DB_Pflanze)
//				pflanze.setBildPfad(zielDatei.toString());
//			
//				// löschen, da keine DB Speicherung
//				pflanze.setBildBase64(null);
//
//				System.out.println(zielDatei + " erfolgreich in " + ordner + " gespeichert: ");
//			}
//
//			// Jetzt Pflanze in DB speichern
//			DB_Pflanze.insertPflanze(pflanze);
//			return Response.status(Status.CREATED).entity(pflanze).build();
//
//		} catch(SQLException e) {
//			if(e.getMessage().contains("unique")) {
//				return Response.status(Response.Status.CONFLICT).entity("Pflanzenname bereits vorhanden").build();
//			}
//			return Response.serverError().entity("Fehler: POST Pflanze" + e.toString()).build();
//		}
//	}
}
