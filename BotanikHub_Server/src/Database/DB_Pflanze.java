package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;

import Enum.BenutzerTyp;
import Enum.Intervall;
import Enum.Kalendertyp;
import Enum.Lebensdauer;
import Enum.Lichtbedarf;
import Enum.Month;
import Enum.Pflanzentyp;
import Enum.Standort;
import Enum.Vermehrungsarten;
import Enum.Vertraeglichkeit;
import Enum.VerwendeteTeile;
import Enum.Wasserbedarf;
import Modell.Benutzer;
import Modell.Botanikkalender;
import Modell.Pflanze;

public class DB_Pflanze {

	public static void createPflanze() throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.createStatement();
			rs = conn.getMetaData().getTables(null, null, DB_Util.PFLANZE_TABLE.toUpperCase(), new String[] {"TABLE"});

			if(rs.next())
				return;

			// SQL create: Erstellt die Tabelle PFLANZE mit allen Spalten für Pflanzenattribute
			// Enthält Fremdschlüssel auf BENUTZER_ID
			String create = "CREATE TABLE " +
					DB_Util.PFLANZE_TABLE + " (" +
					DB_Util.PFLANZE_ID + " INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
					DB_Util.PFLANZE_NAME + " VARCHAR(100) UNIQUE," +
					DB_Util.PFLANZE_BOTAN_NAME + " VARCHAR(200)," +
					DB_Util.PFLANZE_BILDPFAD + " VARCHAR(200)," +

					// Da Base64 sehr lange zeichenketten haben können reicht VARCHAR/TEXT nicht aus
					// CLOB ist für Apache Derby geeignet und unterstütz bis zu 2,147,483,647 zeichen
					// QUelle: https://db.apache.org/derby/docs/10.8/ref/rrefclob.html
					DB_Util.PFLANZE_BASE64 + " CLOB," + 

					DB_Util.PFLANZE_WASSERBEDARF + " VARCHAR(100)," +
					DB_Util.PFLANZE_LICHTBEDARF + " VARCHAR(100)," +
					DB_Util.PFLANZE_DUENGUNG + " VARCHAR(100)," +
					DB_Util.PFLANZE_STANDORT + " VARCHAR(100)," +
					DB_Util.PFLANZE_IS_GIFTIG + " BOOLEAN," +
					DB_Util.PFLANZE_WUCHSBREITE + " DOUBLE," +
					DB_Util.PFLANZE_WUCHSHOEHE + " DOUBLE," +
					DB_Util.PFLANZE_LEBENSDAUER + " VARCHAR(100)," +
					DB_Util.PFLANZE_VERTRAEGLICHKEIT + " VARCHAR(100)," + 
					DB_Util.BENUTZER_ID + " INTEGER," +
					"PRIMARY KEY(" + DB_Util.PFLANZE_ID + ")," +
					"FOREIGN KEY(" + DB_Util.BENUTZER_ID + ") REFERENCES " + DB_Util.BENUTZER_TABLE + "(" + DB_Util.BENUTZER_ID + ")" +
					")";
			stmt.executeUpdate(create);

		} catch(SQLException e) {
			throw e;
		} 
		finally {
			try {
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				throw e;
			}
		}
	}

	public static void insertPflanze(Pflanze pflanze) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		PreparedStatement vermehrungStmt = null;
		PreparedStatement pflanztypStmt = null;
		PreparedStatement teileStmt = null;
		PreparedStatement kalenderStmt = null;
		ResultSet rs = null;

		// SQL insert: Fügt eine neue Pflanze in die Haupttabelle ein
		// Die ID wird automatisch generiert und anschließend ins Objekt übernommen
		// Danach werden alle Verbindungstabellen (Vermehrung, Pflanzentyp, Teile, Kalender) separat befüllt
		String insert = "INSERT INTO " + DB_Util.PFLANZE_TABLE + " (" +
				DB_Util.PFLANZE_NAME + "," +
				DB_Util.PFLANZE_BOTAN_NAME + "," +
				DB_Util.PFLANZE_BILDPFAD + "," +
				DB_Util.PFLANZE_BASE64 + "," +
				DB_Util.PFLANZE_IS_GIFTIG + "," +
				DB_Util.PFLANZE_WUCHSBREITE + "," +
				DB_Util.PFLANZE_WUCHSHOEHE + "," +
				DB_Util.PFLANZE_WASSERBEDARF + "," +
				DB_Util.PFLANZE_LICHTBEDARF + "," +
				DB_Util.PFLANZE_DUENGUNG + "," +
				DB_Util.PFLANZE_VERTRAEGLICHKEIT + "," +
				DB_Util.PFLANZE_STANDORT + "," +
				DB_Util.PFLANZE_LEBENSDAUER + "," +
				DB_Util.BENUTZER_ID +
				") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS); // RETURN_GENERATED_KEYS gibt mir die ID der Pflanze (Primary Key) zurück


			stmt.setString(1, pflanze.getPflanzenName());
			stmt.setString(2, pflanze.getBotanikName());
			stmt.setString(3, pflanze.getBildPfad());
			stmt.setString(4, pflanze.getBildBase64());
			stmt.setBoolean(5, pflanze.isGiftig());
			stmt.setDouble(6, pflanze.getWuchsbreite());
			stmt.setDouble(7, pflanze.getWuchshoehe());
			stmt.setString(8, pflanze.getWasserbedarf().getBeschreibung());
			stmt.setString(9, pflanze.getLichtbedarf().getBeschreibung());
			stmt.setString(10, pflanze.getDuengung().getBeschreibung());
			stmt.setString(11, pflanze.getVertraeglichkeit().getBeschreibung());
			stmt.setString(12, pflanze.getStandort().getBeschreibung());
			stmt.setString(13, pflanze.getLebensdauer().getBeschreibung());
			stmt.setInt(14, pflanze.getBenutzer().getBenutzerId());

			/*
			 * 
			 * 
			 */
			System.out.println("Wird gespeichert - Base64 vorhanden? " + (pflanze.getBildBase64() != null));
			System.out.println("Base64 Länge: " + (pflanze.getBildBase64() != null ? pflanze.getBildBase64().length() : "null"));


			stmt.executeUpdate();			// Pflanze einfügen
			rs = stmt.getGeneratedKeys(); 	// & dann Pflanzen ID holen 

			/*
			 * Da ich einen eintrag in den Verbindungstabellen benötige brauch ich eine generierte PflanzenID
			 */
			int pflanzeID = -1;				// default wert (keine 0 für klarheit und um zu prüfen ob tatsächlich eine ID gesetzt wurde)				
			if(rs.next()) {
				pflanzeID = rs.getInt(1);	// ID wurde geneiert
				pflanze.setPflanzenID(pflanzeID); // id wurde ins Objekt übertragen
			}

			/*
			 *  Werte in Verbindungstabellen einfügen direkt über PreparedStatement
			 *  mit PflanzeID und ENUM.ordinal() oder getBeschreibung()
			 */
			for(Vermehrungsarten art : pflanze.getVermehrung()) {
				String insertVer = "INSERT INTO " + DB_Util.PFLANZE_VERMEHRUNG_TABLE + " (" +
						DB_Util.PFLANZE_ID + "," +
						DB_Util.VERMEHRUNG_ARTEN + ") VALUES (?, ?)";
				vermehrungStmt = conn.prepareStatement(insertVer);
				vermehrungStmt.setInt(1, pflanzeID);
				vermehrungStmt.setString(2, art.getBeschreibung());
				vermehrungStmt.executeUpdate();
			}

			for(Pflanzentyp typ : pflanze.getPflanzenTyp()) {
				String insertTyp = "INSERT INTO " + DB_Util.PFLANZE_PFLANZENTYP_TABLE + " (" +
						DB_Util.PFLANZE_ID + "," +
						DB_Util.PFLANZEN_TYPEN + ") VALUES (?, ?)";
				pflanztypStmt = conn.prepareStatement(insertTyp);
				pflanztypStmt.setInt(1, pflanzeID);
				pflanztypStmt.setString(2, typ.getBeschreibung());
				pflanztypStmt.executeUpdate();
			}

			for(VerwendeteTeile teile : pflanze.getVerwendeteTeile()) {
				String insertTeile = "INSERT INTO " + DB_Util.PFLANZE_VERWENDETE_TEILE_TABLE + " (" +
						DB_Util.PFLANZE_ID + "," +
						DB_Util.VERWENDETE_TEILE + ") VALUES (?, ?)";
				teileStmt = conn.prepareStatement(insertTeile);
				teileStmt.setInt(1, pflanzeID);
				teileStmt.setString(2, teile.getBeschreibung());
				teileStmt.executeUpdate();
			}

			for(Botanikkalender k : pflanze.getKalender()) {
				ArrayList<Month> monate = k.getMonat();		// Arraylist erzeugt mit den Monaten
				// 2te forschleife um durch die Monate zu iterieren für -> .ordinal()
				for(Month einMonat : monate) {
					String insertKal = "INSERT INTO " + DB_Util.BOTANIKKALENDER_TABLE + " (" +
							DB_Util.BOTANIKKALENDER_MONAT + "," +
							DB_Util.BOTANIKKALENDER_KALENDERTYP + "," +
							DB_Util.PFLANZE_ID + ") VALUES (?, ?, ?)";

					kalenderStmt = conn.prepareStatement(insertKal);
					kalenderStmt.setString(1, einMonat.getBeschreibung());
					kalenderStmt.setString(2, k.getKalendertyp().getBeschreibung());
					kalenderStmt.setInt(3, pflanzeID);
					kalenderStmt.executeUpdate();
				}
			}


		} catch(SQLException e) {
			throw e;
		} 
		finally {
			try {
				if(stmt != null) stmt.close();
				if(vermehrungStmt != null) vermehrungStmt.close();
				if(teileStmt != null) teileStmt.close();
				if(pflanztypStmt != null) pflanztypStmt.close();
				if(kalenderStmt != null) kalenderStmt.close();

				if(conn != null) conn.close();

			} catch(SQLException e) {
				throw e;
			}
		}
	}

	public static void updatePflanze(Pflanze pflanze) throws SQLException{
		Connection conn = null;
		PreparedStatement updateStmt = null;
		PreparedStatement checkStmt = null;
		// Eigene Statements für jedes ENUM für die Verbindungstabellen 
		PreparedStatement deleteVermehrungStmt = null;
		PreparedStatement insertVermehrungStmt = null;
		PreparedStatement deletePflanztypStmt = null;
		PreparedStatement insertPlfnaztypStmt = null;
		PreparedStatement deleteTeileStmt = null;
		PreparedStatement insertTeileStmt = null;
		PreparedStatement deleteKalenderStmt = null;
		PreparedStatement insertKalenderStmt = null;


		// Vor dem Update wird geprüft, ob der Pflanzenname bereits vergeben ist (außer bei gleicher ID)
		String checkSQL = "SELECT COUNT(*) FROM " + DB_Util.PFLANZE_TABLE +
				" WHERE " + DB_Util.PFLANZE_NAME + " = ? AND " + DB_Util.PFLANZE_ID + " <> ?";

		// SQL update: Aktualisiert alle Hauptdaten der Pflanze (nur wenn Benutzer-ID übereinstimmt)
		// Alle Verbindungstabellen werden vorher gelöscht und anschließend neu befüllt
		String update = "UPDATE " + DB_Util.PFLANZE_TABLE + " SET " +
				DB_Util.PFLANZE_NAME + "=?, " +
				DB_Util.PFLANZE_BOTAN_NAME + "=?, " +
				DB_Util.PFLANZE_BILDPFAD + "=?, " +
				DB_Util.PFLANZE_BASE64 + "=?," +
				DB_Util.PFLANZE_IS_GIFTIG + "=?, " +
				DB_Util.PFLANZE_WUCHSBREITE + "=?, " +
				DB_Util.PFLANZE_WUCHSHOEHE + "=?, " +
				DB_Util.PFLANZE_WASSERBEDARF + "=?, " +
				DB_Util.PFLANZE_LICHTBEDARF + "=?, " +
				DB_Util.PFLANZE_DUENGUNG + "=?, " +
				DB_Util.PFLANZE_VERTRAEGLICHKEIT + "=?, " +
				DB_Util.PFLANZE_STANDORT + "=?, " +
				DB_Util.PFLANZE_LEBENSDAUER + "=? " +
				" WHERE " + DB_Util.BENUTZER_ID + "=?" + " AND " + DB_Util.PFLANZE_ID + "=?";

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			updateStmt = conn.prepareStatement(update);

			// Duplikatsüberprüfung: Ist derselbe Name bei anderer Pflanze vergeben?
			checkStmt = conn.prepareStatement(checkSQL);
			checkStmt.setString(1, pflanze.getPflanzenName());
			checkStmt.setInt(2, pflanze.getPflanzenID());
			ResultSet rs = checkStmt.executeQuery();

			if(rs.next() && rs.getInt(1) > 0) {
				throw new SQLException("unique");
			}

			updateStmt.setString(1, pflanze.getPflanzenName());
			updateStmt.setString(2, pflanze.getBotanikName());
			updateStmt.setString(3, pflanze.getBildPfad());
			updateStmt.setString(4, pflanze.getBildBase64());
			updateStmt.setBoolean(5, pflanze.isGiftig());
			updateStmt.setDouble(6, pflanze.getWuchsbreite());
			updateStmt.setDouble(7, pflanze.getWuchshoehe());
			updateStmt.setString(8, pflanze.getWasserbedarf().getBeschreibung());
			updateStmt.setString(9, pflanze.getLichtbedarf().getBeschreibung());
			updateStmt.setString(10, pflanze.getDuengung().getBeschreibung());
			updateStmt.setString(11, pflanze.getVertraeglichkeit().getBeschreibung());
			updateStmt.setString(12, pflanze.getStandort().getBeschreibung());
			updateStmt.setString(13, pflanze.getLebensdauer().getBeschreibung());
			updateStmt.setInt(14, pflanze.getBenutzer().getBenutzerId());
			updateStmt.setInt(15, pflanze.getPflanzenID());


			/*
			 * 
			 * 
			 */
			System.out.println("Wird geupdatet - Base64 vorhanden? " + (pflanze.getBildBase64() != null));
			System.out.println("Base64 Länge: " + (pflanze.getBildBase64() != null ? pflanze.getBildBase64().length() : "null"));


			updateStmt.executeUpdate();

			/*
			 * Update bei Verbindungstabellen hab ich mir gedacht mach ich gleich 
			 *  über DELETE und INSERT mit den Verbindungstabellen um sicher alles aktuell zu halten.!
			 */

			// Vermehrung
			if(pflanze.getVermehrung() != null && !pflanze.getVermehrung().isEmpty()) {
				deleteVermehrungStmt = conn.prepareStatement(
						"DELETE FROM " + DB_Util.PFLANZE_VERMEHRUNG_TABLE + " WHERE " + DB_Util.PFLANZE_ID + "=?");
				deleteVermehrungStmt.setInt(1, pflanze.getPflanzenID());
				deleteVermehrungStmt.executeUpdate();

				// Durch ENUM iterieren um Inhalt rauszubekommen
				for(Vermehrungsarten art : pflanze.getVermehrung()) {
					String insertVer = "INSERT INTO " + DB_Util.PFLANZE_VERMEHRUNG_TABLE + " (" +
							DB_Util.PFLANZE_ID + "," +
							DB_Util.VERMEHRUNG_ARTEN + ") VALUES (?, ?)";
					insertVermehrungStmt = conn.prepareStatement(insertVer);
					insertVermehrungStmt.setInt(1, pflanze.getPflanzenID());
					insertVermehrungStmt.setString(2, art.getBeschreibung());

					insertVermehrungStmt.executeUpdate();
				}
			}

			// Pflanzentyp
			if(pflanze.getPflanzenTyp() != null && !pflanze.getPflanzenTyp().isEmpty()) {
				deletePflanztypStmt = conn.prepareStatement(
						"DELETE FROM " + DB_Util.PFLANZE_PFLANZENTYP_TABLE + " WHERE " + DB_Util.PFLANZE_ID + "=?");
				deletePflanztypStmt.setInt(1, pflanze.getPflanzenID());
				deletePflanztypStmt.executeUpdate();

				for(Pflanzentyp typ : pflanze.getPflanzenTyp()) {
					String insertTyp = "INSERT INTO " + DB_Util.PFLANZE_PFLANZENTYP_TABLE + " (" +
							DB_Util.PFLANZE_ID + "," +
							DB_Util.PFLANZEN_TYPEN + ") VALUES (?, ?)";
					insertPlfnaztypStmt = conn.prepareStatement(insertTyp);
					insertPlfnaztypStmt.setInt(1, pflanze.getPflanzenID());
					insertPlfnaztypStmt.setString(2, typ.getBeschreibung());
					insertPlfnaztypStmt.executeUpdate();
				}
			}

			// VerwendeteTeile
			if(pflanze.getVerwendeteTeile() != null && !pflanze.getVerwendeteTeile().isEmpty()) {
				deleteTeileStmt = conn.prepareStatement(
						"DELETE FROM " + DB_Util.PFLANZE_VERWENDETE_TEILE_TABLE + " WHERE " + DB_Util.PFLANZE_ID + "=?");
				deleteTeileStmt.setInt(1, pflanze.getPflanzenID());
				deleteTeileStmt.executeUpdate();

				for(VerwendeteTeile teile : pflanze.getVerwendeteTeile()) {
					String insertTeile = "INSERT INTO " + DB_Util.PFLANZE_VERWENDETE_TEILE_TABLE + " (" +
							DB_Util.PFLANZE_ID + "," +
							DB_Util.VERWENDETE_TEILE + ") VALUES (?, ?)";
					insertTeileStmt = conn.prepareStatement(insertTeile);
					insertTeileStmt.setInt(1, pflanze.getPflanzenID());
					insertTeileStmt.setString(2, teile.getBeschreibung());
					insertTeileStmt.executeUpdate();
				}
			}

			// Botanikkalender
			if(pflanze.getKalender() != null && !pflanze.getKalender().isEmpty()) {
				deleteKalenderStmt = conn.prepareStatement(
						"DELETE FROM " + DB_Util.BOTANIKKALENDER_TABLE + " WHERE " + DB_Util.PFLANZE_ID + " =?");
				deleteKalenderStmt.setInt(1, pflanze.getPflanzenID());
				deleteKalenderStmt.executeUpdate();

				for(Botanikkalender k : pflanze.getKalender()) {
					ArrayList<Month> monate = k.getMonat();		// Arraylist erzeugt mit den Monaten
					// 2te forschleife um durch die Monate zu iterieren für -> .ordinal()
					for(Month einMonat : monate) {
						String insertKal = "INSERT INTO " + DB_Util.BOTANIKKALENDER_TABLE + " (" +
								DB_Util.BOTANIKKALENDER_MONAT + "," +
								DB_Util.BOTANIKKALENDER_KALENDERTYP + "," +
								DB_Util.PFLANZE_ID + ") VALUES (?, ?, ?)";

						insertKalenderStmt = conn.prepareStatement(insertKal);
						insertKalenderStmt.setString(1, einMonat.getBeschreibung());
						insertKalenderStmt.setString(2, k.getKalendertyp().getBeschreibung());
						insertKalenderStmt.setInt(3, pflanze.getPflanzenID());
						insertKalenderStmt.executeUpdate();
					}
				}
			}

		} catch(SQLException e) {
			throw e;
		} finally {
			try {
				if (updateStmt != null) updateStmt.close();
				if (deleteVermehrungStmt != null) deleteVermehrungStmt.close();
				if (insertVermehrungStmt != null) insertVermehrungStmt.close();
				if (deletePflanztypStmt != null) deletePflanztypStmt.close();
				if (insertPlfnaztypStmt != null) insertPlfnaztypStmt.close();
				if (deleteTeileStmt != null) deleteTeileStmt.close();
				if (insertTeileStmt != null) insertTeileStmt.close();
				if (deleteKalenderStmt != null) deleteKalenderStmt.close();
				if (insertKalenderStmt != null) insertKalenderStmt.close();
				if(checkStmt != null) checkStmt.close();

				if (conn != null) conn.close();
			} catch(SQLException e) {
				throw e;
			}
		}
	}

	public static void deletePflanze(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		PreparedStatement vermehrungStmt = null;
		PreparedStatement pflanztypStmt = null;
		PreparedStatement teileStmt = null;
		PreparedStatement kalenderStmt = null;
		PreparedStatement erinnerungStmt = null;
		PreparedStatement wunschSttm = null;
		PreparedStatement entdeckenStmt = null;
		PreparedStatement hubStmt = null;

		// SQL delete: Löscht eine Pflanze inklusive aller Daten in den Verbindungstabellen
		// Wichtig: Die Reihenfolge muss stimmen – erst abhängige Tabellen löschen, dann PFLANZE
		// Tabellen: BOTANIK_HUB, WUNSCHLISTE, ENTDECKEN, ERINNERUNG, VERBINDUNGSTABELLEN (4×), danach PFLANZE

		String deleHub = "DELETE FROM " + DB_Util.BOTANIK_HUB_TABLE +
				" WHERE " + DB_Util.PFLANZE_ID + "=?";

		String deleteWunsch = "DELETE FROM " + DB_Util.WUNSCHLISTE_TABLE +
				" WHERE " + DB_Util.PFLANZE_ID + "=?";

		String deleteEntdecken = "DELETE FROM " + DB_Util.PFLANZE_ENTDECKEN_TABLE +
				" WHERE " + DB_Util.PFLANZE_ID + "=?";

		String deleteErin = "DELETE FROM " + DB_Util.ERINNERUNG_TABLE +
				" WHERE " + DB_Util.PFLANZE_ID + "=?";

		String deleteVerm = "DELETE FROM " + DB_Util.PFLANZE_VERMEHRUNG_TABLE + 
				" WHERE " + DB_Util.PFLANZE_ID + "=?";

		String deleteTyp = "DELETE FROM " + DB_Util.PFLANZE_PFLANZENTYP_TABLE + 
				" WHERE " + DB_Util. PFLANZE_ID + "=?";

		String deleteTeil = "DELETE FROM " + DB_Util.PFLANZE_VERWENDETE_TEILE_TABLE + 
				" WHERE " + DB_Util.PFLANZE_ID + "=?";

		String deleteKal = "DELETE FROM " + DB_Util.BOTANIKKALENDER_TABLE + 
				" WHERE " + DB_Util.PFLANZE_ID + " =?";

		String delete = "DELETE FROM " + DB_Util.PFLANZE_TABLE + 
				" WHERE " + DB_Util.PFLANZE_ID + "=?";
		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);

			try {
				// Auto-Commit ausschalten
				conn.setAutoCommit(false);

				// Verbindungstabellen zuerst löschen dann die Haupttabelle
				hubStmt = conn.prepareStatement(deleHub);
				hubStmt.setInt(1, id);
				hubStmt.executeUpdate();

				wunschSttm = conn .prepareStatement(deleteWunsch);
				wunschSttm.setInt(1, id);
				wunschSttm.executeUpdate();

				entdeckenStmt = conn .prepareStatement(deleteEntdecken);
				entdeckenStmt.setInt(1, id);
				entdeckenStmt.executeUpdate();

				erinnerungStmt = conn .prepareStatement(deleteErin);
				erinnerungStmt.setInt(1, id);
				erinnerungStmt.executeUpdate();

				vermehrungStmt = conn.prepareStatement(deleteVerm);
				vermehrungStmt.setInt(1, id);
				vermehrungStmt.executeUpdate();

				pflanztypStmt = conn.prepareStatement(deleteTyp);
				pflanztypStmt.setInt(1, id);
				pflanztypStmt.executeUpdate();

				teileStmt = conn.prepareStatement(deleteTeil);
				teileStmt.setInt(1, id);
				teileStmt.executeUpdate();

				kalenderStmt = conn.prepareStatement(deleteKal);
				kalenderStmt.setInt(1, id);
				kalenderStmt.executeUpdate();

			} catch(SQLException e) {
				// Wenn Eception eintrifft -> rollback auf alten Zustand
				conn.rollback();
				throw e;
			}
			stmt = conn.prepareStatement(delete);
			stmt.setInt(1, id);
			stmt.executeUpdate();

			// Transaktion abschließen
			conn.commit();
			System.out.println("Pflanze inkl. Verbindungstabellen erfolgreich gelöscht");

		} catch(SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				if(stmt != null) stmt.close();
				if(vermehrungStmt != null) vermehrungStmt.close();
				if(teileStmt != null) teileStmt.close();
				if(pflanztypStmt != null) pflanztypStmt.close();
				if(kalenderStmt != null) kalenderStmt.close();
				if(erinnerungStmt != null) erinnerungStmt.close();
				if(entdeckenStmt != null) entdeckenStmt.close();
				if(wunschSttm != null) wunschSttm.close();
				if(hubStmt != null) hubStmt.close();

				if(conn != null) conn.close();
			} catch(SQLException e) {
				throw e;
			}
		}
	}

	public static ArrayList<Pflanze> readAllePflanzen() throws SQLException {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		// SQL select: alle Pflanzen inkl. Benutzer + LEFT JOINs auf Verbindungstabellen
		String select =
				"SELECT " + DB_Util.PFLANZE_TABLE + ".*, " +
						DB_Util.BENUTZER_TABLE + "." + DB_Util.BENUTZER_NAME + ", " +
						DB_Util.BENUTZER_TABLE + "." + DB_Util.BENUTZER_BENUTZERROLLE + ", " +
						DB_Util.PFLANZE_VERMEHRUNG_TABLE + ".*, " +
						DB_Util.PFLANZE_PFLANZENTYP_TABLE + ".*, " +
						DB_Util.PFLANZE_VERWENDETE_TEILE_TABLE + ".*, " +
						DB_Util.BOTANIKKALENDER_TABLE + ".* " +

						"FROM " + DB_Util.PFLANZE_TABLE +
						" JOIN " + DB_Util.BENUTZER_TABLE +
						" ON " + DB_Util.PFLANZE_TABLE + "." + DB_Util.BENUTZER_ID + " = " + DB_Util.BENUTZER_TABLE + "." + DB_Util.BENUTZER_ID +
						" LEFT JOIN " + DB_Util.PFLANZE_VERMEHRUNG_TABLE +
						" ON " + DB_Util.PFLANZE_TABLE + "." + DB_Util.PFLANZE_ID + " = " + DB_Util.PFLANZE_VERMEHRUNG_TABLE + "." + DB_Util.PFLANZE_ID +
						" LEFT JOIN " + DB_Util.PFLANZE_PFLANZENTYP_TABLE +
						" ON " + DB_Util.PFLANZE_TABLE + "." + DB_Util.PFLANZE_ID + " = " + DB_Util.PFLANZE_PFLANZENTYP_TABLE + "." + DB_Util.PFLANZE_ID +
						" LEFT JOIN " + DB_Util.PFLANZE_VERWENDETE_TEILE_TABLE +
						" ON " + DB_Util.PFLANZE_TABLE + "." + DB_Util.PFLANZE_ID + " = " + DB_Util.PFLANZE_VERWENDETE_TEILE_TABLE + "." + DB_Util.PFLANZE_ID +
						" LEFT JOIN " + DB_Util.BOTANIKKALENDER_TABLE +
						" ON " + DB_Util.PFLANZE_TABLE + "." + DB_Util.PFLANZE_ID + " = " + DB_Util.BOTANIKKALENDER_TABLE + "." + DB_Util.PFLANZE_ID;

		// Leere ArrayList und Pflanzenobjekt zum befüllen
		ArrayList<Pflanze> alPflanze = new ArrayList<>();
		Pflanze pflanze = null;

		// Listen & Sets zum zwischenspeichern
		HashSet<Vermehrungsarten> vermehrungSet = new HashSet<>();
		HashSet<Pflanzentyp> pflanzentypSet = new HashSet<>();
		HashSet<VerwendeteTeile> teileSet = new HashSet<>();
		ArrayList<Botanikkalender> kalenderList = new ArrayList<>();

		/*
		 * Variable ist dazu da das Java erkennt ob es sich um eine Neue Pflanze beim lesen der Left-Joins handelt oder noch die alte ist
		 * Angefangt wird immer mit id -1, das sorgt dafür das die erste Zeile garantiert als Neue Pflanze erkannt wird
		 */
		int letzteId = -1;

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(select);
			rs = stmt.executeQuery();


			while (rs.next()) {
				// Pflanze ID holen zum Vergleichen -> letzeID != pflanzeID
				int pflanzenId = rs.getInt(DB_Util.PFLANZE_ID);
				int benutzerId = rs.getInt(DB_Util.BENUTZER_ID);

				// admin erstellen um ihn einer pflanze zuzuweisen beim lesen (ID = 1)
				Benutzer pflanzeBesitzer = new Benutzer();
				pflanzeBesitzer.setBenutzerId(benutzerId);
				pflanzeBesitzer.setBenutzerName(rs.getString(DB_Util.BENUTZER_NAME));
				pflanzeBesitzer.setTyp(BenutzerTyp.fromBeschreibung(rs.getString(DB_Util.BENUTZER_BENUTZERROLLE)));


				// Solange die aktuelle Pflanze != letzer pflanze und != null -> vorherige Pflanze abschliessen und speichern
				if (pflanzenId != letzteId) {
					if (pflanze != null) {
						// Daten übernehmen
						pflanze.getVermehrung().addAll(vermehrungSet);
						pflanze.getPflanzenTyp().addAll(pflanzentypSet);
						pflanze.getVerwendeteTeile().addAll(teileSet);
						pflanze.getKalender().addAll(kalenderList);

						// Pflanze zur ArrayList hinzufügen
						alPflanze.add(pflanze);

						// Sets und Listen für neue Pflanze leeren
						vermehrungSet.clear();
						pflanzentypSet.clear();
						teileSet.clear();
						kalenderList.clear();
					}

					// Neue Pflanze erzeugen und mit Daten befüllen
					pflanze = new Pflanze(
							rs.getString(DB_Util.PFLANZE_NAME),
							rs.getString(DB_Util.PFLANZE_BOTAN_NAME),
							rs.getString(DB_Util.PFLANZE_BILDPFAD),
							rs.getString(DB_Util.PFLANZE_BASE64),
							null,
							pflanzenId,
							rs.getBoolean(DB_Util.PFLANZE_IS_GIFTIG),
							rs.getDouble(DB_Util.PFLANZE_WUCHSBREITE),
							rs.getDouble(DB_Util.PFLANZE_WUCHSHOEHE),
							Wasserbedarf.fromBeschreibung(rs.getString(DB_Util.PFLANZE_WASSERBEDARF)),
							Lichtbedarf.fromBeschreibung(rs.getString(DB_Util.PFLANZE_LICHTBEDARF)),
							Intervall.fromBeschreibung(rs.getString(DB_Util.PFLANZE_DUENGUNG)),
							Vertraeglichkeit.fromBeschreibung(rs.getString(DB_Util.PFLANZE_VERTRAEGLICHKEIT)),
							Standort.fromBeschreibung(rs.getString(DB_Util.PFLANZE_STANDORT)),
							Lebensdauer.fromBeschreibung(rs.getString(DB_Util.PFLANZE_LEBENSDAUER)),
							new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
							new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
							pflanzeBesitzer,	// admin setzen
							null	// Keine Notiz bei Admin
							);


					// letzteID merken
					// Solange letzteID == pflanzenID ist -> wird aktuelle Pflanze befüllt
					letzteId = pflanzenId;
				}

				// JOIN-Daten einsammeln
				try {
					String vermehrung = rs.getString(DB_Util.VERMEHRUNG_ARTEN);
					if (vermehrung != null)
						vermehrungSet.add(Vermehrungsarten.fromBeschreibung(vermehrung));
				} catch (IllegalArgumentException e) {}

				try {
					String typ = rs.getString(DB_Util.PFLANZEN_TYPEN);
					if (typ != null)
						pflanzentypSet.add(Pflanzentyp.fromBeschreibung(typ));
				} catch (IllegalArgumentException e) {}

				try {
					String teil = rs.getString(DB_Util.VERWENDETE_TEILE);
					if (teil != null)
						teileSet.add(VerwendeteTeile.fromBeschreibung(teil));
				} catch (IllegalArgumentException e) {}

				try {
					String monat = rs.getString(DB_Util.BOTANIKKALENDER_MONAT);
					String typKalender = rs.getString(DB_Util.BOTANIKKALENDER_KALENDERTYP);
					int id = rs.getInt(DB_Util.BOTANIKKALENDER_ID);

					if (monat != null && typKalender != null) {
						Kalendertyp kaltyp = Kalendertyp.fromBeschreibung(typKalender);
						Month m = Month.fromBeschreibung(monat);
						// Über Botanikkalender streamen um den Kalendertyp rauszufiltern
						Botanikkalender vorhandener = kalenderList.stream()
								.filter(k -> k.getKalendertyp() == kaltyp)
								.findFirst().orElse(null);
						// Wenn Kalendertyp noch nicht vorhanden ist -> neue ArrayList erstellen mit Typ und ID
						if (vorhandener == null) {
							vorhandener = new Botanikkalender(new ArrayList<>(), kaltyp, id);
							kalenderList.add(vorhandener);
						}
						// Solange der vorhandene Kalender nicht Monat m hat -> befüllen
						if (!vorhandener.getMonat().contains(m)) {
							vorhandener.getMonat().add(m);
						}
					}
				} catch (IllegalArgumentException e) {}
			}

			// Letzte Pflanze noch hinzufügen, da der ID-Wechsel(letzteID = pflanzeID) beim letzten Datensatz nicht mehr eintritt
			if (pflanze != null) {
				pflanze.getVermehrung().addAll(vermehrungSet);
				pflanze.getPflanzenTyp().addAll(pflanzentypSet);
				pflanze.getVerwendeteTeile().addAll(teileSet);
				pflanze.getKalender().addAll(kalenderList);

				alPflanze.add(pflanze);
			}

		} catch(SQLException e) {
			throw e;
		} finally {
			try {
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
				if (conn != null) conn.close();
			} catch(SQLException e) {
				throw e;
			}
		}
		return alPflanze;
	}

	public static ArrayList<Pflanze> readPflanzenByFilter(String text) throws SQLException {

		/*
		 * Methode für eine Art Live Search -> für Pflanzen Entdecken Dialog gedacht
		 * String wird mitgegeben und hier verarbeitet
		 */

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		// SQL select: Liefert Pflanzen mit ID, Name, bei denen der Text im Namen oder botanischen Namen vorkommt
		// Wird für die Live-Suche in der „Pflanze entdecken“-Ansicht verwendet
		// Enthält nur Basisinformationen (ID, Name)
		String sql = "SELECT * FROM " + DB_Util.PFLANZE_TABLE;

		if(text.length() > 0) {
			sql += " WHERE LOWER(" + DB_Util.PFLANZE_NAME + ") LIKE ? " +
					" OR LOWER(" + DB_Util.PFLANZE_BOTAN_NAME + ") LIKE ?";
		}


		ArrayList<Pflanze> pflanzen = new ArrayList<>();

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(sql);

			// Sicherheitsüberprüfung, damit kann man bewusst null setzen und es wird ignoriert
			if (text == null || text.isEmpty()) {
				stmt.setString(1, null);
				stmt.setString(2, null);
			} else {
				String pattern = text + "%"; // % ist in SQL -> Represents zero, one, or multiple characters
				stmt.setString(1, pattern.toLowerCase());
				stmt.setString(2, pattern.toLowerCase());
			}

			rs = stmt.executeQuery();

			while (rs.next()) {
				pflanzen.add(new Pflanze(
						rs.getInt(DB_Util.PFLANZE_ID),
						rs.getString(DB_Util.PFLANZE_NAME),
						new ArrayList<>()
						));
			}

		} catch(SQLException e) {
			throw e;
		} finally {
			try {
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
				if (conn != null) conn.close();
			} catch(SQLException e) {
				throw e;
			}
		}

		return pflanzen;
	}

	public static Pflanze readPflanzeById(int pflanzeID) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		// SQL Select: Ein einfacher select nur für die ID
		String select = "SELECT " + DB_Util.PFLANZE_BILDPFAD + " FROM " + DB_Util.PFLANZE_TABLE +
				" WHERE " + DB_Util.PFLANZE_ID + " = ?";

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(select);
			stmt.setInt(1, pflanzeID);
			rs = stmt.executeQuery();

			if (rs.next()) {
				// Pflanzenobjekt zum setzen der ID -> Transportmittel
				Pflanze pflanze = new Pflanze();
				pflanze.setPflanzenID(pflanzeID);
				pflanze.setBildPfad(rs.getString(DB_Util.PFLANZE_BILDPFAD));
				return pflanze;
			}
			return null;

		} catch(SQLException e) {
			throw e;	
		} finally {
			try {
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
				if (conn != null) conn.close();
			} catch(SQLException e) {
				throw e;
			}
		}
	}

}
