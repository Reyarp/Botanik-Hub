package TEST_DB;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

import Enum.BenutzerTyp;
import Modell.Benutzer;

public class DB_Benutzer {

	/*--------------------------------------------
	 * Erstellt die Tabelle BENUTZER, falls sie noch nicht existiert.
	 * Zusätzlich wird automatisch ein Admin-Benutzer mit festen Zugangsdaten angelegt.
	 *
	 * Aufruf: einmalig beim Start der Anwendung aus BotanikHub_Client
	 *-------------------------------------------- */
	public static void createBenutzer() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.createStatement();
			rs = conn.getMetaData().getTables(null, null, DB_Util.BENUTZER_TABLE.toUpperCase(), new String[]{"TABLE"});

			if (rs.next()) return; // Tabelle existiert bereits

			String create = "CREATE TABLE " + DB_Util.BENUTZER_TABLE + " (" +
					DB_Util.BENUTZER_ID + " INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
					DB_Util.BENUTZER_NAME + " VARCHAR(100) UNIQUE," +
					DB_Util.BENUTZER_REGISTRIERT_SEIT + " DATE," +
					DB_Util.BENUTZER_PASSWORT + " VARCHAR(50)," +
					DB_Util.BENUTZER_BENUTZERROLLE + " VARCHAR(50)," +
					"PRIMARY KEY(" + DB_Util.BENUTZER_ID + "))";

			stmt.executeUpdate(create);

			// Admin-Benutzer (hartcodiert)
			Benutzer admin = new Benutzer();
			admin.setBenutzerName(DB_Util.ADMIN_NAME);
			admin.setPasswort(DB_Util.ADMIN_PASSWORT);
			admin.setTyp(BenutzerTyp.ADMIN);
			admin.setRegistriertSeit(LocalDate.now());
			insertBenutzer(admin);

		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
	}

	/*--------------------------------------------
	 * Fügt einen neuen Benutzer in die Datenbank ein.
	 *
	 * Eingabe: voll befülltes Benutzer-Objekt mit Name, Passwort, Rolle, Datum
	 * Verwendung: bei Registrierung neuer Benutzer
	 *-------------------------------------------- */
	public static void insertBenutzer(Benutzer benutzer) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;

		String insert = "INSERT INTO " + DB_Util.BENUTZER_TABLE + " (" +
				DB_Util.BENUTZER_NAME + "," +
				DB_Util.BENUTZER_PASSWORT + "," +
				DB_Util.BENUTZER_REGISTRIERT_SEIT + "," +
				DB_Util.BENUTZER_BENUTZERROLLE + ") VALUES (?, ?, ?, ?)";

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(insert);
			stmt.setString(1, benutzer.getBenutzerName());
			stmt.setString(2, benutzer.getPasswort());
			stmt.setDate(3, Date.valueOf(benutzer.getRegistriertSeit()));
			stmt.setString(4, benutzer.getTyp().getBeschreibung());
			stmt.executeUpdate();

		} finally {
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
	}

	/*--------------------------------------------
	 * Aktualisiert Name und Passwort eines vorhandenen Benutzers.
	 *
	 * Achtung: Die ID im Benutzerobjekt muss gesetzt sein!
	 * Verwendung: Profil bearbeiten (Benutzer) oder Benutzer bearbeiten (Admin)
	 *-------------------------------------------- */
	public static void updateBenutzer(Benutzer benutzer) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;

		String update = "UPDATE " + DB_Util.BENUTZER_TABLE + " SET " +
				DB_Util.BENUTZER_NAME + "=?, " +
				DB_Util.BENUTZER_PASSWORT + "=? WHERE " + DB_Util.BENUTZER_ID + "=?";

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(update);
			stmt.setString(1, benutzer.getBenutzerName());
			stmt.setString(2, benutzer.getPasswort());
			stmt.setInt(3, benutzer.getBenutzerId());
			stmt.executeUpdate();

		} finally {
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
	}

	/*--------------------------------------------
	 * Liefert alle Benutzer (außer Admin) aus der Datenbank zurück.
	 *
	 * Rückgabe: ArrayList mit Benutzer-Objekten (inkl. ID, Rolle, Registrierdatum)
	 * Verwendung: Benutzerverwaltung durch Admin
	 *-------------------------------------------- */
	public static ArrayList<Benutzer> readAlleBenutzer() throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		String select = "SELECT * FROM " + DB_Util.BENUTZER_TABLE +
				" WHERE " + DB_Util.BENUTZER_NAME + "!=?";

		ArrayList<Benutzer> alBenutzer = new ArrayList<>();

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(select);
			stmt.setString(1, DB_Util.ADMIN_NAME);
			rs = stmt.executeQuery();

			while (rs.next()) {
				LocalDate regSeit = rs.getDate(DB_Util.BENUTZER_REGISTRIERT_SEIT) != null
						? rs.getDate(DB_Util.BENUTZER_REGISTRIERT_SEIT).toLocalDate()
								: LocalDate.now();

				alBenutzer.add(new Benutzer(
						rs.getString(DB_Util.BENUTZER_NAME),
						rs.getString(DB_Util.BENUTZER_PASSWORT),
						rs.getInt(DB_Util.BENUTZER_ID),
						BenutzerTyp.fromBeschreibung(rs.getString(DB_Util.BENUTZER_BENUTZERROLLE)),
						regSeit));
			}
		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
		return alBenutzer;
	}

	/*--------------------------------------------
	 * Loginprüfung anhand Benutzername und Passwort.
	 *
	 * Rückgabe: Benutzer-Objekt bei Erfolg, null bei falschen Daten
	 * Verwendung: Login_Dialog
	 *-------------------------------------------- */
	public static Benutzer loginBenutzer(String name, String passwort) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Benutzer benutzer = null;

		String select = "SELECT * FROM " + DB_Util.BENUTZER_TABLE +
				" WHERE " + DB_Util.BENUTZER_NAME + "=? AND " + DB_Util.BENUTZER_PASSWORT + "=?";

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(select);
			stmt.setString(1, name);
			stmt.setString(2, passwort);
			rs = stmt.executeQuery();

			if (rs.next()) {
				LocalDate registriertSeit = rs.getDate(DB_Util.BENUTZER_REGISTRIERT_SEIT) != null
						? rs.getDate(DB_Util.BENUTZER_REGISTRIERT_SEIT).toLocalDate()
								: LocalDate.now();

				benutzer = new Benutzer(
						rs.getString(DB_Util.BENUTZER_NAME),
						rs.getString(DB_Util.BENUTZER_PASSWORT),
						rs.getInt(DB_Util.BENUTZER_ID),
						BenutzerTyp.fromBeschreibung(rs.getString(DB_Util.BENUTZER_BENUTZERROLLE)),
						registriertSeit);
			}
		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
		return benutzer;
	}

	/*--------------------------------------------
	 * Prüft, ob ein Benutzername bereits existiert (z. B. bei Registrierung).
	 *
	 * Rückgabe: true = existiert bereits
	 *-------------------------------------------- */
	public static boolean insertNameExistiert(String name) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		boolean vorhanden = false;

		String sql = "SELECT COUNT(*) FROM " + DB_Util.BENUTZER_TABLE +
				" WHERE " + DB_Util.BENUTZER_NAME + "=?";

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, name);
			rs = stmt.executeQuery();

			if (rs.next()) {
				vorhanden = rs.getInt(1) > 0;
			}
		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
		return vorhanden;
	}

	/*--------------------------------------------
	 * Prüft bei Update, ob ein Benutzername bereits an einen anderen Benutzer vergeben ist.
	 *
	 * Eingabe: Benutzername, eigene Benutzer-ID
	 * Rückgabe: true = Konflikt vorhanden (Name ist bei jemand anderem bereits vergeben)
	 *-------------------------------------------- */
	public static boolean updateNameExistiert(String name, int benutzerID) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		boolean vorhanden = false;

		String sql = "SELECT COUNT(*) FROM " + DB_Util.BENUTZER_TABLE +
				" WHERE " + DB_Util.BENUTZER_NAME + "=? AND " + DB_Util.BENUTZER_ID + "!=?";

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, name);
			stmt.setInt(2, benutzerID);
			rs = stmt.executeQuery();

			if (rs.next()) {
				vorhanden = rs.getInt(1) > 0;
			}
		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
		return vorhanden;
	}
}
