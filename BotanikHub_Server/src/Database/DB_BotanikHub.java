package Database;

import java.sql.*;
import java.util.*;

import Enum.*;
import Modell.*;

public class DB_BotanikHub {

	public static void createBotanikHub() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.createStatement();
			rs = conn.getMetaData().getTables(null, null, DB_Util.BOTANIK_HUB_TABLE.toUpperCase(), new String[]{"TABLE"});

			if (rs.next()) return;

			// SQL create: Erstellt die Tabelle BOTANIK_HUB mit den Spalten: PFLANZE_ID, BENUTZER_ID und NOTIZ. 
			// Primärschlüssel ist die Kombination aus PFLANZE_ID und BENUTZER_ID.
			// Fremdschlüssel sichern Referenzen zur PFLANZE- und BENUTZER-Tabelle.
			String create = "CREATE TABLE " + DB_Util.BOTANIK_HUB_TABLE + " (" +
					DB_Util.PFLANZE_ID + " INTEGER," +
					DB_Util.BENUTZER_ID + " INTEGER," +
					DB_Util.BOTANIK_HUB_PFLANZE_NOTIZ + " VARCHAR(1000)," +
					DB_Util.BOTANIK_HUB_IS_ADMIN_PFLANZE + " BOOLEAN DEFAULT FALSE," +
					"PRIMARY KEY(" + DB_Util.PFLANZE_ID + "," + DB_Util.BENUTZER_ID + ")," +
					"FOREIGN KEY(" + DB_Util.PFLANZE_ID + ") REFERENCES " + DB_Util.PFLANZE_TABLE + "(" + DB_Util.PFLANZE_ID + ")," +
					"FOREIGN KEY(" + DB_Util.BENUTZER_ID + ") REFERENCES " + DB_Util.BENUTZER_TABLE + "(" + DB_Util.BENUTZER_ID + "))";


			stmt.executeUpdate(create);

		} catch(SQLException e) {
			throw e;
		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
	}

	public static void insertBotanikHub(BotanikHub hub) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;

		// Fügt einen Eintrag in die BOTANIK_HUB-Tabelle ein (Pflanze-ID, Benutzer-ID, Notiz)
		String insert = "INSERT INTO " + DB_Util.BOTANIK_HUB_TABLE + " (" +
				DB_Util.PFLANZE_ID + "," +
				DB_Util.BENUTZER_ID + "," +
				DB_Util.BOTANIK_HUB_IS_ADMIN_PFLANZE + "," +
				DB_Util.BOTANIK_HUB_PFLANZE_NOTIZ + ") VALUES (?, ?, ?, ?)";


		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(insert);
			stmt.setInt(1, hub.getPflanze().getPflanzenID());
			stmt.setInt(2, hub.getBenutzer().getBenutzerId());
			stmt.setBoolean(3, hub.getPflanze().isAdminPflanze());
			stmt.setString(4, hub.getPflanze().getNotiz());

			stmt.executeUpdate();

		} catch(SQLException e) {
			throw e;

		} finally {
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
	}

	public static void deleteBotanikHub(int pflanzeID, int benutzerID) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;

		// Löscht einen Eintrag aus der BOTANIK_HUB-Tabelle basierend auf Pflanze-ID und Benutzer-ID
		String delete = "DELETE FROM " + DB_Util.BOTANIK_HUB_TABLE +
				" WHERE " + DB_Util.PFLANZE_ID + "=? AND " +
				DB_Util.BENUTZER_ID + "=?";


		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(delete);
			stmt.setInt(1, pflanzeID);
			stmt.setInt(2, benutzerID);
			stmt.executeUpdate();

		} catch(SQLException e) {
			throw e;
		
		} finally {
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
	}

	public static void updateNotiz(BotanikHub hub) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;

		String update = "UPDATE " + DB_Util.BOTANIK_HUB_TABLE + " SET " +
				DB_Util.BOTANIK_HUB_PFLANZE_NOTIZ + "=?" +
				" WHERE " + DB_Util.PFLANZE_ID + "=? AND " + DB_Util.BENUTZER_ID + "=?";


		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(update);
			stmt.setString(1, hub.getPflanze().getNotiz());
			stmt.setInt(2, hub.getPflanze().getPflanzenID());
			stmt.setInt(3, hub.getBenutzer().getBenutzerId());
			stmt.executeUpdate();

		} catch(SQLException e) {
			throw e;
		} finally {
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
	}

	public static ArrayList<Pflanze> readBotanikHubPflanzen(int benutzerID) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;


		// Liest alle Pflanzen eines bestimmten Benutzers aus dem BotanikHub inklusive:
		// Benutzer -> ersteller
		// Notiz aus BOTANIK_HUB
		// Pflanzendaten aus PFLANZE
		// Verknüpfte Tabellen: VERMEHRUNG, PFLANZENTYP, VERWENDETE_TEILE, BOTANIKKALENDER
		// Verwendet LEFT JOINs, um auch Pflanzen ohne Zusatzdaten zu laden (z.b Wenn Rose keine Vermehrung hat wird sie trotzdem mitgeladen)
		String select =
				"SELECT " +
						DB_Util.BENUTZER_TABLE + "." + DB_Util.BENUTZER_ID + "," +
						DB_Util.BENUTZER_TABLE + "." + DB_Util.BENUTZER_NAME + "," +
						DB_Util.BENUTZER_TABLE + "." + DB_Util.BENUTZER_BENUTZERROLLE + "," +
						DB_Util.BOTANIK_HUB_TABLE + "." + DB_Util.BOTANIK_HUB_PFLANZE_NOTIZ + "," +
						DB_Util.PFLANZE_TABLE + "." + DB_Util.PFLANZE_BASE64 + "," +
						DB_Util.BOTANIK_HUB_TABLE + "." + DB_Util.BOTANIK_HUB_IS_ADMIN_PFLANZE + "," +
						DB_Util.PFLANZE_TABLE + ".*," +
						DB_Util.PFLANZE_VERMEHRUNG_TABLE + ".*," +
						DB_Util.PFLANZE_PFLANZENTYP_TABLE + ".*," +
						DB_Util.PFLANZE_VERWENDETE_TEILE_TABLE + ".*," +
						DB_Util.BOTANIKKALENDER_TABLE + ".* " +

						"FROM " + DB_Util.BOTANIK_HUB_TABLE +
						" JOIN " + DB_Util.BENUTZER_TABLE +
						" ON " + DB_Util.BOTANIK_HUB_TABLE + "." + DB_Util.BENUTZER_ID + " = " + DB_Util.BENUTZER_TABLE + "." + DB_Util.BENUTZER_ID +
						" JOIN " + DB_Util.PFLANZE_TABLE +
						" ON " + DB_Util.BOTANIK_HUB_TABLE + "." + DB_Util.PFLANZE_ID + " = " + DB_Util.PFLANZE_TABLE + "." + DB_Util.PFLANZE_ID +
						" LEFT JOIN " + DB_Util.PFLANZE_VERMEHRUNG_TABLE +
						" ON " + DB_Util.PFLANZE_TABLE + "." + DB_Util.PFLANZE_ID + " = " + DB_Util.PFLANZE_VERMEHRUNG_TABLE + "." + DB_Util.PFLANZE_ID +
						" LEFT JOIN " + DB_Util.PFLANZE_PFLANZENTYP_TABLE +
						" ON " + DB_Util.PFLANZE_TABLE + "." + DB_Util.PFLANZE_ID + " = " + DB_Util.PFLANZE_PFLANZENTYP_TABLE + "." + DB_Util.PFLANZE_ID +
						" LEFT JOIN " + DB_Util.PFLANZE_VERWENDETE_TEILE_TABLE +
						" ON " + DB_Util.PFLANZE_TABLE + "." + DB_Util.PFLANZE_ID + " = " + DB_Util.PFLANZE_VERWENDETE_TEILE_TABLE + "." + DB_Util.PFLANZE_ID +
						" LEFT JOIN " + DB_Util.BOTANIKKALENDER_TABLE +
						" ON " + DB_Util.PFLANZE_TABLE + "." + DB_Util.PFLANZE_ID + " = " + DB_Util.BOTANIKKALENDER_TABLE + "." + DB_Util.PFLANZE_ID +
						" WHERE " + DB_Util.BOTANIK_HUB_TABLE + "." + DB_Util.BENUTZER_ID + "=?";

		// Leere ArrayList und BotanikHub Objekte zum befüllen
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
			stmt.setInt(1, benutzerID);

			rs = stmt.executeQuery();


			while (rs.next()) {
				// Pflanze ID holen zum Vergleichen -> letzeID != pflanzeID
				int pflanzenId = rs.getInt(DB_Util.PFLANZE_ID);

				// Benutzer ersteller
				Benutzer benutzer = new Benutzer();
				benutzer.setBenutzerId(rs.getInt(DB_Util.BENUTZER_ID));
				benutzer.setBenutzerName(rs.getString(DB_Util.BENUTZER_NAME));
				benutzer.setTyp(BenutzerTyp.fromBeschreibung(rs.getString(DB_Util.BENUTZER_BENUTZERROLLE)));

				// Solange die aktuelle Pflanze != letzer pflanze und != null -> vorherige Pflanze abschliessen und speichern
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

					// Neue Pflanze erzeugen und mit Daten befüllen
					pflanze = new Pflanze(
							rs.getString(DB_Util.PFLANZE_NAME),
							rs.getString(DB_Util.PFLANZE_BOTAN_NAME),
							rs.getString(DB_Util.PFLANZE_BILDPFAD),
							rs.getString(DB_Util.PFLANZE_BASE64),
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
							benutzer,
							rs.getString(DB_Util.BOTANIK_HUB_PFLANZE_NOTIZ),
							rs.getBoolean(DB_Util.BOTANIK_HUB_IS_ADMIN_PFLANZE)
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
		} catch (SQLException e) {
			throw e; 

		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
		return alPflanze;
	}
}
