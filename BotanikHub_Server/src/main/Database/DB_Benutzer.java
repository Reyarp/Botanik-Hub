package Database;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

import Enum.BenutzerTyp;
import Modell.Benutzer;

public class DB_Benutzer {

	public static void createBenutzer() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.createStatement();
			rs = conn.getMetaData().getTables(null, null, DB_Util.BENUTZER_TABLE.toUpperCase(), new String[]{"TABLE"});

			if (rs.next()) return; 

			// SQL Create: Erstellt die BENUTZER-Tabelle, falls sie noch nicht existiert
			// Legt anschließend einen Admin-Benutzer mit Standardwerten an -> Hartcodiert
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

	public static void insertBenutzer(Benutzer benutzer) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;

		// SQL Insert: Fügt einen neuen Benutzer in die Datenbank ein (Name, Passwort, Registrierungsdatum, Rolle)
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

			try {
			stmt.executeUpdate();
			} catch(SQLException e) {
				throw e;
			}

		} catch(SQLException e) {
			throw e;
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (conn != null) conn.close();
			} catch(SQLException e) {
				throw e;
			}
		}
	}

	public static void updateBenutzer(Benutzer benutzer) throws SQLException {
		// Verbindungs- und Statement-Variablen vorbereiten
		Connection conn = null;
		PreparedStatement checkStmt = null;
		PreparedStatement updateStmt = null;

		// SQL checkSQL: zum Überprüfen, ob ein Benutzername bereits vergeben ist – aber nicht von diesem Benutzer selbst
		String checkSQL = "SELECT COUNT(*) FROM " + DB_Util.BENUTZER_TABLE +
				" WHERE " + DB_Util.BENUTZER_NAME + " = ? AND " + DB_Util.BENUTZER_ID + " <> ?";

		// SQL update: zum Aktualisieren von Name und Passwort für einen bestimmten Benutzer
		String update = "UPDATE " + DB_Util.BENUTZER_TABLE + " SET " +
				DB_Util.BENUTZER_NAME + " = ?, " +
				DB_Util.BENUTZER_PASSWORT + " = ? " +
				"WHERE " + DB_Util.BENUTZER_ID + " = ?";

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);

			// 2 Benutzer ID's zum Duplikat prüfen
			checkStmt = conn.prepareStatement(checkSQL);
			checkStmt.setString(1, benutzer.getBenutzerName());
			checkStmt.setInt(2, benutzer.getBenutzerId());

			// Abfrage ausführen
			ResultSet rs = checkStmt.executeQuery();

			// Wenn Ergebnis vorhanden und mindestens 1 Treffer: Benutzername ist bereits belegt
			if (rs.next() && rs.getInt(1) > 0) {
				// Fehler werfen, der in der GUI ausgewertet werden kann
				throw new SQLException("unique");
			}

			// Wenn kein Duplikat vorhanden ist -> Update-Befehl vorbereiten
			updateStmt = conn.prepareStatement(update);
			updateStmt.setString(1, benutzer.getBenutzerName());
			updateStmt.setString(2, benutzer.getPasswort());
			updateStmt.setInt(3, benutzer.getBenutzerId());

			updateStmt.executeUpdate();

		} catch(SQLException e) {
			throw e;
		} finally {
			try {
				if (checkStmt != null) checkStmt.close();
				if (updateStmt != null) updateStmt.close();
				if (conn != null) conn.close();
			} catch(SQLException e) {
				throw e;
			}
		}
	}

	public static ArrayList<Benutzer> readAlleBenutzer() throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		// SQL select: Liest alle Benutzer aus, außer dem fest definierten Admin
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
		return alBenutzer;
	}

	public static Benutzer readBenutzerByID(int benutzerID) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		// SQL select: Liest einen Benutzer anhand seiner ID aus der Datenbank
		String select = "SELECT * FROM " + DB_Util.BENUTZER_TABLE + " WHERE " + DB_Util.BENUTZER_ID + "=?";

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(select);
			stmt.setInt(1, benutzerID);
			rs = stmt.executeQuery();

			if (rs.next()) {
				LocalDate registriertSeit = rs.getDate(DB_Util.BENUTZER_REGISTRIERT_SEIT) != null
						? rs.getDate(DB_Util.BENUTZER_REGISTRIERT_SEIT).toLocalDate()
								: LocalDate.now();

				return new Benutzer(
						rs.getString(DB_Util.BENUTZER_NAME),
						rs.getString(DB_Util.BENUTZER_PASSWORT),
						rs.getInt(DB_Util.BENUTZER_ID),
						BenutzerTyp.fromBeschreibung(rs.getString(DB_Util.BENUTZER_BENUTZERROLLE)),
						registriertSeit);
			} else {
				return null;
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
	}

	public static Benutzer loginBenutzer(String name, String passwort) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Benutzer benutzer = null;

		// SQL select: Benutzer anhand von Name und Passwort suchen
		String select = "SELECT * FROM " + DB_Util.BENUTZER_TABLE +
				" WHERE " + DB_Util.BENUTZER_NAME + "=? AND " + DB_Util.BENUTZER_PASSWORT + "=?";



		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(select);

			stmt.setString(1, name);
			stmt.setString(2, passwort);
			rs = stmt.executeQuery();

			// Prüfen ob ein Datensatz gefunden wurde
			if (rs.next()) {
				// LocalDate in Date umwandeln
				LocalDate registriertSeit = rs.getDate(DB_Util.BENUTZER_REGISTRIERT_SEIT) != null
						? rs.getDate(DB_Util.BENUTZER_REGISTRIERT_SEIT).toLocalDate()
								: LocalDate.now();

				// Wenn Ergebnis vorhanden -> Benutzer erzeugen
				benutzer = new Benutzer(
						rs.getString(DB_Util.BENUTZER_NAME),
						rs.getString(DB_Util.BENUTZER_PASSWORT),
						rs.getInt(DB_Util.BENUTZER_ID),
						BenutzerTyp.fromBeschreibung(rs.getString(DB_Util.BENUTZER_BENUTZERROLLE)),
						registriertSeit);
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
		return benutzer;
	}
}
