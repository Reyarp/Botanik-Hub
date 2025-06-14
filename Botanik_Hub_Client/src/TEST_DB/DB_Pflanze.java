package TEST_DB;

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

			String create = "CREATE TABLE " +
					DB_Util.PFLANZE_TABLE + " (" +
					DB_Util.PFLANZE_ID + " INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
					DB_Util.PFLANZE_NAME + " VARCHAR(100) UNIQUE," +
					DB_Util.PFLANZE_BOTAN_NAME + " VARCHAR(200)," +
					DB_Util.PFLANZE_BILDPFAD + " VARCHAR(200)," +
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

		String insert = "INSERT INTO " + DB_Util.PFLANZE_TABLE + " (" +
				DB_Util.PFLANZE_NAME + "," +
				DB_Util.PFLANZE_BOTAN_NAME + "," +
				DB_Util.PFLANZE_BILDPFAD + "," +
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
				") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS); // RETURN_GENERATED_KEYS gibt mir die ID der Pflanze (Primary Key) zurücks


			stmt.setString(1, pflanze.getPflanzenName());
			stmt.setString(2, pflanze.getBotanikName());
			stmt.setString(3, pflanze.getBildPfad());
			stmt.setBoolean(4, pflanze.isGiftig());
			stmt.setDouble(5, pflanze.getWuchsbreite());
			stmt.setDouble(6, pflanze.getWuchshoehe());
			stmt.setString(7, pflanze.getWasserbedarf().getBeschreibung());
			stmt.setString(8, pflanze.getLichtbedarf().getBeschreibung());
			stmt.setString(9, pflanze.getDuengung().getBeschreibung());
			stmt.setString(10, pflanze.getVertraeglichkeit().getBeschreibung());
			stmt.setString(11, pflanze.getStandort().getBeschreibung());
			stmt.setString(12, pflanze.getLebensdauer().getBeschreibung());
			stmt.setInt(13, pflanze.getBenutzer().getBenutzerId());

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
		PreparedStatement stmt = null;
		// Eigene Statements für jedes ENUM für die Verbindungstabellen 
		PreparedStatement deleteVermehrungStmt = null;
		PreparedStatement insertVermehrungStmt = null;
		PreparedStatement deletePflanztypStmt = null;
		PreparedStatement insertPlfnaztypStmt = null;
		PreparedStatement deleteTeileStmt = null;
		PreparedStatement insertTeileStmt = null;
		PreparedStatement deleteKalenderStmt = null;
		PreparedStatement insertKalenderStmt = null;

		String update = "UPDATE " + DB_Util.PFLANZE_TABLE + " SET " +
				DB_Util.PFLANZE_NAME + "=?, " +
				DB_Util.PFLANZE_BOTAN_NAME + "=?, " +
				DB_Util.PFLANZE_BILDPFAD + "=?, " +
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
			stmt = conn.prepareStatement(update);

			stmt.setString(1, pflanze.getPflanzenName());
			stmt.setString(2, pflanze.getBotanikName());
			stmt.setString(3, pflanze.getBildPfad());
			stmt.setBoolean(4, pflanze.isGiftig());
			stmt.setDouble(5, pflanze.getWuchsbreite());
			stmt.setDouble(6, pflanze.getWuchshoehe());
			stmt.setString(7, pflanze.getWasserbedarf().getBeschreibung());
			stmt.setString(8, pflanze.getLichtbedarf().getBeschreibung());
			stmt.setString(9, pflanze.getDuengung().getBeschreibung());
			stmt.setString(10, pflanze.getVertraeglichkeit().getBeschreibung());
			stmt.setString(11, pflanze.getStandort().getBeschreibung());
			stmt.setString(12, pflanze.getLebensdauer().getBeschreibung());
			stmt.setInt(13, pflanze.getBenutzer().getBenutzerId());
			stmt.setInt(14, pflanze.getPflanzenID());

			stmt.executeUpdate();

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
		} 
		finally {
			try {
				if (stmt != null) stmt.close();
				if (deleteVermehrungStmt != null) deleteVermehrungStmt.close();
				if (insertVermehrungStmt != null) insertVermehrungStmt.close();
				if (deletePflanztypStmt != null) deletePflanztypStmt.close();
				if (insertPlfnaztypStmt != null) insertPlfnaztypStmt.close();
				if (deleteTeileStmt != null) deleteTeileStmt.close();
				if (insertTeileStmt != null) insertTeileStmt.close();
				if (deleteKalenderStmt != null) deleteKalenderStmt.close();
				if (insertKalenderStmt != null) insertKalenderStmt.close();

				if (conn != null) conn.close();

			} catch(SQLException e) {
				throw e;
			}
		}
	}

	

	public static void deletePflanze(Pflanze pflanze) throws SQLException{
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

		/*
		 * Zuerst alle Verbindungstabellen löschen, dann die haupttabelle
		 */
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

			// Verbindungstabellen zuerst löschen dann die Haupttabelle
			hubStmt = conn.prepareStatement(deleHub);
			hubStmt.setInt(1, pflanze.getPflanzenID());
			hubStmt.executeUpdate();

			wunschSttm = conn .prepareStatement(deleteWunsch);
			wunschSttm.setInt(1, pflanze.getPflanzenID());
			wunschSttm.executeUpdate();

			entdeckenStmt = conn .prepareStatement(deleteEntdecken);
			entdeckenStmt.setInt(1, pflanze.getPflanzenID());
			entdeckenStmt.executeUpdate();

			erinnerungStmt = conn .prepareStatement(deleteErin);
			erinnerungStmt.setInt(1, pflanze.getPflanzenID());
			erinnerungStmt.executeUpdate();

			vermehrungStmt = conn.prepareStatement(deleteVerm);
			vermehrungStmt.setInt(1, pflanze.getPflanzenID());
			vermehrungStmt.executeUpdate();

			pflanztypStmt = conn.prepareStatement(deleteTyp);
			pflanztypStmt.setInt(1, pflanze.getPflanzenID());
			pflanztypStmt.executeUpdate();

			teileStmt = conn.prepareStatement(deleteTeil);
			teileStmt.setInt(1, pflanze.getPflanzenID());
			teileStmt.executeUpdate();

			kalenderStmt = conn.prepareStatement(deleteKal);
			kalenderStmt.setInt(1, pflanze.getPflanzenID());
			kalenderStmt.executeUpdate();

			stmt = conn.prepareStatement(delete);
			stmt.setInt(1, pflanze.getPflanzenID());
			stmt.executeUpdate();

		} catch(SQLException e) {
			e.printStackTrace();
			throw e;
		} 
		finally {
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

	public static ArrayList<Pflanze> readPflanzenByFilter(String text) throws SQLException {

		/*
		 * Methode für eine Art Live Search -> für Pflanzen Entdecken Dialog gedacht
		 * String wird mitgegeben und hier verarbeitet
		 */

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM " + DB_Util.PFLANZE_TABLE;

		if(text.length() > 0) {
			sql += " WHERE " + DB_Util.PFLANZE_NAME + " LIKE ? " +
					" OR " + DB_Util.PFLANZE_BOTAN_NAME + " LIKE ?";
		}


		ArrayList<Pflanze> pflanzen = new ArrayList<>();

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(sql);

			// Sicherheitsüberprüfung, damit kann man bewusst null setzen und es wird ignoriert
			if (text == null || text.isEmpty()) {
				stmt.setString(1, null);
				stmt.setString(2, null);
				stmt.setString(3, null);
			} else {
				String pattern = "%" + text + "%"; // % ist in SQL -> Represents zero, one, or multiple characters
				stmt.setString(1, text);
				stmt.setString(2, pattern);
				stmt.setString(3, pattern);
			}

			rs = stmt.executeQuery();

			while (rs.next()) {
				pflanzen.add(new Pflanze(
						rs.getInt(DB_Util.PFLANZE_ID),
						rs.getString(DB_Util.PFLANZE_NAME),
						new ArrayList<>()
						));
			}

		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}

		return pflanzen;
	}

	public static ArrayList<Pflanze> readAllePflanzen() throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;


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


		ArrayList<Pflanze> alPflanze = new ArrayList<>();

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(select);
			rs = stmt.executeQuery();

			Pflanze pflanze = null;

			// Temporäre Sets zur Duplikatvermeidung für die alPflanze
			HashSet<Vermehrungsarten> vermehrungSet = new HashSet<>();
			HashSet<Pflanzentyp> pflanzentypSet = new HashSet<>();
			HashSet<VerwendeteTeile> teileSet = new HashSet<>();
			ArrayList<Botanikkalender> kalenderList = new ArrayList<>();

			int letzteId = -1;
			while (rs.next()) {
				int pflanzenId = rs.getInt(DB_Util.PFLANZE_ID);
				int benutzerId = rs.getInt(DB_Util.BENUTZER_ID);
				Benutzer admin;

				// admin erstellen um ihn einer pflanze zuzuweisen beim lesen
				admin = new Benutzer();
				admin.setBenutzerId(benutzerId);
				admin.setBenutzerName(rs.getString(DB_Util.BENUTZER_NAME));
				admin.setTyp(BenutzerTyp.fromBeschreibung(rs.getString(DB_Util.BENUTZER_BENUTZERROLLE)));


				// Wenn wir zu einer neuen Pflanze wechseln, aktuelle Pflanze speichern und neue anlegen
				if (pflanzenId != letzteId) {
					if (pflanze != null) {
						// Daten übernehmen
						pflanze.getVermehrung().addAll(vermehrungSet);
						pflanze.getPflanzenTyp().addAll(pflanzentypSet);
						pflanze.getVerwendeteTeile().addAll(teileSet);
						pflanze.getKalender().addAll(kalenderList);

						alPflanze.add(pflanze);

						// Sets und Listen für neue Pflanze leeren
						vermehrungSet.clear();
						pflanzentypSet.clear();
						teileSet.clear();
						kalenderList.clear();
					}

					// Neue Pflanze anlegen
					pflanze = new Pflanze(
							rs.getString(DB_Util.PFLANZE_NAME),
							rs.getString(DB_Util.PFLANZE_BOTAN_NAME),
							rs.getString(DB_Util.PFLANZE_BILDPFAD),
							pflanzenId,
							rs.getBoolean(DB_Util.PFLANZE_IS_GIFTIG),
							rs.getDouble(DB_Util.PFLANZE_WUCHSBREITE),
							rs.getDouble(DB_Util.PFLANZE_WUCHSHOEHE),
							null,	//Admin hat keine Notiz
							Wasserbedarf.fromBeschreibung(rs.getString(DB_Util.PFLANZE_WASSERBEDARF)),
							Lichtbedarf.fromBeschreibung(rs.getString(DB_Util.PFLANZE_LICHTBEDARF)),
							Intervall.fromBeschreibung(rs.getString(DB_Util.PFLANZE_DUENGUNG)),
							Vertraeglichkeit.fromBeschreibung(rs.getString(DB_Util.PFLANZE_VERTRAEGLICHKEIT)),
							Standort.fromBeschreibung(rs.getString(DB_Util.PFLANZE_STANDORT)),
							Lebensdauer.fromBeschreibung(rs.getString(DB_Util.PFLANZE_LEBENSDAUER)),
							new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
							new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
							admin
							);

					letzteId = pflanzenId;
				}

				// Vermehrung
				String vermehrung = rs.getString(DB_Util.VERMEHRUNG_ARTEN);
				if (vermehrung != null) {
					try {
						vermehrungSet.add(Vermehrungsarten.fromBeschreibung(vermehrung));
					} catch (IllegalArgumentException ignored) {}
				}

				// Pflanzentyp
				String typ = rs.getString(DB_Util.PFLANZEN_TYPEN);
				if (typ != null) {
					try {
						pflanzentypSet.add(Pflanzentyp.fromBeschreibung(typ));
					} catch (IllegalArgumentException ignored) {}
				}

				// Verwendete Teile
				String teil = rs.getString(DB_Util.VERWENDETE_TEILE);
				if (teil != null) {
					try {
						teileSet.add(VerwendeteTeile.fromBeschreibung(teil));
					} catch (IllegalArgumentException ignored) {}
				}

				// Botanikkalender
				int id = rs.getInt(DB_Util.BOTANIKKALENDER_ID);
				String monat = rs.getString(DB_Util.BOTANIKKALENDER_MONAT);
				String typKalender = rs.getString(DB_Util.BOTANIKKALENDER_KALENDERTYP);

				if (monat != null && typKalender != null) {
					try {
						Kalendertyp kaltyp = Kalendertyp.fromBeschreibung(typKalender);
						Month m = Month.fromBeschreibung(monat);

						// Versuche bestehenden Kalender dieses Typs zu finden
						Botanikkalender vorhandener = kalenderList.stream()
								.filter(k -> k.getKalendertyp() == kaltyp)
								.findFirst()
								.orElse(null);

						if (vorhandener == null) {
							// Erstellen wenn nicht vorhanden
							vorhandener = new Botanikkalender(new ArrayList<>(), kaltyp, id);
							kalenderList.add(vorhandener);
						}

						// Monat hinzufügen, wenn nicht schon drin
						if (!vorhandener.getMonat().contains(m)) {
							vorhandener.getMonat().add(m);
						}
					} catch (IllegalArgumentException ignored) {}
				}
			}

			// Letzte Pflanze noch hinzufügen
			if (pflanze != null) {
				pflanze.getVermehrung().addAll(vermehrungSet);
				pflanze.getPflanzenTyp().addAll(pflanzentypSet);
				pflanze.getVerwendeteTeile().addAll(teileSet);
				pflanze.getKalender().addAll(kalenderList);

				alPflanze.add(pflanze);
			}

		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}

		return alPflanze;
	}

	public static boolean insertNameExistiert(String name) throws SQLException {	

		/*
		 * Diese Methode Prüft, ob der Pflanzenname bereits in der Datenbank vorhanden ist bei insertPflanze anhand des mitgegebenen Parameters
		 */

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		boolean vorhanden = false;  // Rückgabewert: true, wenn Name schon existiert

		// SQL-Abfrage: Zähle, wie viele Pflanzen es mit diesem Namen gibt
		String sql = "SELECT COUNT(*) FROM " + DB_Util.PFLANZE_TABLE + 
				" WHERE " + DB_Util.PFLANZE_NAME + "=?";

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, name);  // Platzhalter mit übergebenem Namen befüllen
			rs = stmt.executeQuery();

			if (rs.next()) {
				vorhanden = rs.getInt(1) > 0;  // Wenn mindestens 1 Eintrag -> Name existiert
			}
		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}

		return vorhanden;
	}

	public static boolean updateNameExistiert(String name, int pflanzeID) throws SQLException {	

		/*
		 * Prüft, ob der Name bereits vergeben ist anhand von name und der pflanzenid
		 */

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		boolean vorhanden = false;

		// SQL: Zähle alle Pflanzen mit diesem Namen, ausser der aktuellen (ID != aktuelleId)
		String sql = "SELECT COUNT(*) FROM " + DB_Util.PFLANZE_TABLE +
				" WHERE " + DB_Util.PFLANZE_NAME + "=? AND " + DB_Util.PFLANZE_ID + "!=?";

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, name);          // Name setzen
			stmt.setInt(2, pflanzeID);       // Die eigene ID ausnehmen (damit Update erlaubt bleibt)
			rs = stmt.executeQuery();

			if (rs.next()) {
				vorhanden = rs.getInt(1) > 0; // Wenn andere Pflanzen den Namen haben: true
			}
		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
		return vorhanden;
	}

}
