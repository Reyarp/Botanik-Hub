package Database;

import java.sql.*;
import java.util.*;

import Enum.*;
import Modell.*;

public class DB_BotanikHub {

	/*--------------------------------------------
	 * Erstellt die Tabelle BOTANIK_HUB, falls sie nicht existiert.
	 * Die Tabelle enthält:
	 * - Fremdschlüssel auf Pflanze
	 * - Fremdschlüssel auf Benutzer
	 * - Eine Notiz (optional) zur Pflanze
	 *
	 * Wird beim Programmstart einmalig ausgeführt.
	 *-------------------------------------------- */
	public static void createBotanikHub() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.createStatement();
			rs = conn.getMetaData().getTables(null, null, DB_Util.BOTANIK_HUB_TABLE.toUpperCase(), new String[]{"TABLE"});

			if (rs.next()) return; // Tabelle existiert bereits

			String create = "CREATE TABLE " + DB_Util.BOTANIK_HUB_TABLE + " (" +
					DB_Util.PFLANZE_ID + " INTEGER," +
					DB_Util.BENUTZER_ID + " INTEGER," +
					DB_Util.BOTANIK_HUB_PFLANZE_NOTIZ + " VARCHAR(1000)," +
					"PRIMARY KEY(" + DB_Util.PFLANZE_ID + "," + DB_Util.BENUTZER_ID + ")," +
					"FOREIGN KEY(" + DB_Util.PFLANZE_ID + ") REFERENCES " + DB_Util.PFLANZE_TABLE + "(" + DB_Util.PFLANZE_ID + ")," +
					"FOREIGN KEY(" + DB_Util.BENUTZER_ID + ") REFERENCES " + DB_Util.BENUTZER_TABLE + "(" + DB_Util.BENUTZER_ID + "))";

			stmt.executeUpdate(create);

		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
	}

	/*--------------------------------------------
	 * Fügt eine Beziehung zwischen einer Pflanze und einem Benutzer
	 * in den BotanikHub ein (quasi: "Pflanze übernehmen").
	 *
	 * Parameter:
	 * - pflanze: das Pflanze-Objekt
	 * - benutzer: der Benutzer, dem sie zugeordnet wird
	 *
	 * Wird z. B. beim Klick auf "zu BotanikHub hinzufügen" verwendet.
	 *-------------------------------------------- */
	public static void insertBotanikHub(Pflanze pflanze, Benutzer benutzer) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;

		String insert = "INSERT INTO " + DB_Util.BOTANIK_HUB_TABLE + " (" +
				DB_Util.PFLANZE_ID + "," +
				DB_Util.BENUTZER_ID + "," +
				DB_Util.BOTANIK_HUB_PFLANZE_NOTIZ + ") VALUES (?, ?, ?)";

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(insert);
			stmt.setInt(1, pflanze.getPflanzenID());
			stmt.setInt(2, benutzer.getBenutzerId());
			stmt.setString(3, pflanze.getNotiz());
			stmt.executeUpdate();

		} finally {
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
	}

	/*--------------------------------------------
	 * Entfernt eine Pflanze aus dem BotanikHub eines Benutzers.
	 *
	 * Wird z. B. bei Klick auf "Pflanze entfernen" verwendet.
	 *-------------------------------------------- */
	public static void deleteBotanikHub(Pflanze pflanze, Benutzer benutzer) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;

		String delete = "DELETE FROM " + DB_Util.BOTANIK_HUB_TABLE +
				" WHERE " + DB_Util.PFLANZE_ID + "=? AND " +
				DB_Util.BENUTZER_ID + "=?";

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(delete);
			stmt.setInt(1, pflanze.getPflanzenID());
			stmt.setInt(2, benutzer.getBenutzerId());
			stmt.executeUpdate();

		} finally {
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
	}

	/*--------------------------------------------
	 * Aktualisiert die Notiz zu einer Pflanze im BotanikHub.
	 *
	 * Erwartet:
	 * - Pflanze mit gesetztem Benutzer (für ID-Ermittlung)
	 *
	 * Aufruf aus: Pflanzen_Notiz_Bearbeiten_Dialog
	 *-------------------------------------------- */
	public static void updateNotiz(Pflanze pflanze) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;

		String update = "UPDATE " + DB_Util.BOTANIK_HUB_TABLE + " SET " +
				DB_Util.BOTANIK_HUB_PFLANZE_NOTIZ + "=?" +
				" WHERE " + DB_Util.PFLANZE_ID + "=? AND " + DB_Util.BENUTZER_ID + "=?";

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(update);
			stmt.setString(1, pflanze.getNotiz());
			stmt.setInt(2, pflanze.getPflanzenID());
			stmt.setInt(3, pflanze.getBenutzer().getBenutzerId());
			stmt.executeUpdate();

		} finally {
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
	}

	/*--------------------------------------------
	 * Liest alle Pflanzen aus dem BotanikHub des aktuell eingeloggten Benutzers.
	 * Inklusive:
	 * - Stammdaten aus PFLANZE_TABLE
	 * - Verknüpfungen zu Typ, Vermehrung, Teile, Kalender
	 *
	 * Rückgabe:
	 * - Eine Liste mit vollständig befüllten Pflanze-Objekten
	 *
	 * Verwendung:
	 * - Anzeige im BotanikHub-Dialog
	 * - Filter- und Suchfunktionen
	 *-------------------------------------------- */
	public static ArrayList<Pflanze> readBotanikHubPflanzen(int benutzerID) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		String select =
				"SELECT * FROM " + DB_Util.BOTANIK_HUB_TABLE +
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


		ArrayList<Pflanze> alPflanze = new ArrayList<>();
		Pflanze pflanze = null;

		HashSet<Vermehrungsarten> vermehrungSet = new HashSet<>();
		HashSet<Pflanzentyp> pflanzentypSet = new HashSet<>();
		HashSet<VerwendeteTeile> teileSet = new HashSet<>();
		ArrayList<Botanikkalender> kalenderList = new ArrayList<>();

		int letzteId = -1;

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(select);
			stmt.setInt(1, benutzerID);
			rs = stmt.executeQuery();

			while (rs.next()) {
				int pflanzenId = rs.getInt(DB_Util.PFLANZE_ID);
				Benutzer user = new Benutzer();
				user.setBenutzerId(benutzerID);

				if (pflanzenId != letzteId) {
					if (pflanze != null) {
						pflanze.getVermehrung().addAll(vermehrungSet);
						pflanze.getPflanzenTyp().addAll(pflanzentypSet);
						pflanze.getVerwendeteTeile().addAll(teileSet);
						pflanze.getKalender().addAll(kalenderList);
						alPflanze.add(pflanze);
						vermehrungSet.clear();
						pflanzentypSet.clear();
						teileSet.clear();
						kalenderList.clear();
					}

					pflanze = new Pflanze(
							rs.getString(DB_Util.PFLANZE_NAME),
							rs.getString(DB_Util.PFLANZE_BOTAN_NAME),
							rs.getString(DB_Util.PFLANZE_BILDPFAD),
							pflanzenId,
							rs.getBoolean(DB_Util.PFLANZE_IS_GIFTIG),
							rs.getDouble(DB_Util.PFLANZE_WUCHSBREITE),
							rs.getDouble(DB_Util.PFLANZE_WUCHSHOEHE),
							rs.getString(DB_Util.BOTANIK_HUB_PFLANZE_NOTIZ),
							Wasserbedarf.fromBeschreibung(rs.getString(DB_Util.PFLANZE_WASSERBEDARF)),
							Lichtbedarf.fromBeschreibung(rs.getString(DB_Util.PFLANZE_LICHTBEDARF)),
							Intervall.fromBeschreibung(rs.getString(DB_Util.PFLANZE_DUENGUNG)),
							Vertraeglichkeit.fromBeschreibung(rs.getString(DB_Util.PFLANZE_VERTRAEGLICHKEIT)),
							Standort.fromBeschreibung(rs.getString(DB_Util.PFLANZE_STANDORT)),
							Lebensdauer.fromBeschreibung(rs.getString(DB_Util.PFLANZE_LEBENSDAUER)),
							new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
							new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
							user
							);
					letzteId = pflanzenId;
				}

				// JOIN-Daten einsammeln
				try {
					String vermehrung = rs.getString(DB_Util.VERMEHRUNG_ARTEN);
					if (vermehrung != null)
						vermehrungSet.add(Vermehrungsarten.fromBeschreibung(vermehrung));
				} catch (IllegalArgumentException ignored) {}

				try {
					String typ = rs.getString(DB_Util.PFLANZEN_TYPEN);
					if (typ != null)
						pflanzentypSet.add(Pflanzentyp.fromBeschreibung(typ));
				} catch (IllegalArgumentException ignored) {}

				try {
					String teil = rs.getString(DB_Util.VERWENDETE_TEILE);
					if (teil != null)
						teileSet.add(VerwendeteTeile.fromBeschreibung(teil));
				} catch (IllegalArgumentException ignored) {}

				try {
					String monat = rs.getString(DB_Util.BOTANIKKALENDER_MONAT);
					String typKalender = rs.getString(DB_Util.BOTANIKKALENDER_KALENDERTYP);
					int id = rs.getInt(DB_Util.BOTANIKKALENDER_ID);
					if (monat != null && typKalender != null) {
						Kalendertyp kaltyp = Kalendertyp.fromBeschreibung(typKalender);
						Month m = Month.fromBeschreibung(monat);
						Botanikkalender vorhandener = kalenderList.stream()
								.filter(k -> k.getKalendertyp() == kaltyp)
								.findFirst().orElse(null);
						if (vorhandener == null) {
							vorhandener = new Botanikkalender(new ArrayList<>(), kaltyp, id);
							kalenderList.add(vorhandener);
						}
						if (!vorhandener.getMonat().contains(m)) {
							vorhandener.getMonat().add(m);
						}
					}
				} catch (IllegalArgumentException ignored) {}
			}

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
}
