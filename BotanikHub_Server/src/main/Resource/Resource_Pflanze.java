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

	// POST /pflanze – legt eine neue Pflanze an
	// Hinweis: Bildverarbeitung (Base64 → Pfad) ist derzeit nicht aktiv, nur DB-Insert
	@POST
	@Consumes(MediaType.APPLICATION_JSON) // Erwartet Pflanzendaten im JSON-Format
	@Produces(MediaType.APPLICATION_JSON) // Gibt Pflanze als JSON zurück
	public Response postPflanze(Pflanze pflanze) {
		try {
			// Neue Pflanze in DB einfügen
			DB_Pflanze.insertPflanze(pflanze);
			return Response.status(Status.CREATED).entity(pflanze).build();
		} catch (SQLException e) {
			// Prüfung auf doppelte Einträge (z. B. Pflanzenname)
			if (e.getMessage().contains("unique")) {
				return Response.status(Response.Status.CONFLICT)
					.entity("Pflanzenname bereits vorhanden").build();
			}
			// Allgemeiner SQL-Fehler
			return Response.serverError().entity("Fehler: POST Pflanze " + e.toString()).build();
		}
	}

	// PUT /pflanze/{id} – aktualisiert eine bestehende Pflanze
	@PUT
	@Path("{id}") // ID aus Pfad übernehmen
	@Consumes(MediaType.APPLICATION_JSON) // Erwartet Pflanzendaten als JSON
	@Produces(MediaType.APPLICATION_JSON)
	public Response putPflanze(@PathParam("id") int id, Pflanze pflanze) {
		try {
			// ID im Objekt setzen (zur Sicherheit)
			pflanze.setPflanzenID(id);
			// Pflanze in DB aktualisieren
			DB_Pflanze.updatePflanze(pflanze);
			return Response.status(Status.OK).build();
		} catch (SQLException e) {
			// Fehler bei Konflikt (z. B. Pflanzenname)
			return Response.serverError().entity("Pflanze existiert bereits").build();
		}
	}

	// DELETE /pflanze/{id} – löscht eine Pflanze anhand der ID
	@DELETE
	@Path("{id}")
	public Response deletePflanze(@PathParam("id") int id) {
		try {
			// Pflanze aus DB löschen
			DB_Pflanze.deletePflanze(id);
			return Response.status(Status.NO_CONTENT).build();
		} catch (SQLException e) {
			// Fehler beim Löschen
			return Response.serverError().entity("Fehler: DELETE Pflanze " + e.toString()).build();
		}
	}

	// GET /pflanze[?suchtext=...] – gibt alle oder gefilterte Pflanzen zurück
	@GET
	@Produces(MediaType.APPLICATION_JSON) // Antwort ist Liste von Pflanzen als JSON
	public Response getPflanzen(@QueryParam("suchtext") String text) {
		try {
			ArrayList<Pflanze> pflanzen;
			// Wenn Suchtext vorhanden → gefiltert suchen
			if (text != null && !text.isBlank()) {
				pflanzen = DB_Pflanze.readPflanzenByFilter(text);
			} else {
				// Sonst alle Pflanzen laden
				pflanzen = DB_Pflanze.readAllePflanzen();
			}
			return Response.status(Status.OK).entity(pflanzen).build();
		} catch (SQLException e) {
			// Fehler beim Lesen
			return Response.serverError().entity("Fehler: GET Pflanze " + e.toString()).build();
		}
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
