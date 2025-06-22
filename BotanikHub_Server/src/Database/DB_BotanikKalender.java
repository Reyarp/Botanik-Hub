package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Enum.Kalendertyp;
import Enum.Month;
import Modell.Botanikkalender;

public class DB_BotanikKalender {

	public static void createBotanikkalender() throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.createStatement();
			rs = conn.getMetaData().getTables(null, null, DB_Util.BOTANIKKALENDER_TABLE.toUpperCase(), new String[] {"TABLE"});

			if(rs.next())
				return;

			// SQL create: Erstellt die Tabelle BOTANIKKALENDER mit ID, Monat, Kalendertyp und Fremdschlüssel auf PFLANZE
			String create = "CREATE TABLE " +
					DB_Util.BOTANIKKALENDER_TABLE + " (" +
					DB_Util.BOTANIKKALENDER_ID + " INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
					DB_Util.BOTANIKKALENDER_MONAT + " VARCHAR(50)," +
					DB_Util.BOTANIKKALENDER_KALENDERTYP + " VARCHAR(100)," +
					DB_Util.PFLANZE_ID + " INTEGER," +
					"PRIMARY KEY(" + DB_Util.BOTANIKKALENDER_ID + ")," +
					"FOREIGN KEY(" + DB_Util.PFLANZE_ID + ") REFERENCES " + DB_Util.PFLANZE_TABLE + "(" + DB_Util.PFLANZE_ID + ")" +
					")";
			stmt.executeUpdate(create);

		} catch(SQLException e) {
			throw e;
		} finally {
			try {
				if(stmt != null) stmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				throw e;
			}
		}
	}

	public static ArrayList<Botanikkalender> readKalender(int pflanzeID) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		// SQL select: Liest alle Monate und Kalendertypen zu einer bestimmten Pflanze aus der BOTANIKKALENDER-Tabelle
		String select = "SELECT " + DB_Util.BOTANIKKALENDER_MONAT + "," + DB_Util.BOTANIKKALENDER_KALENDERTYP 
				+ " FROM " + DB_Util.BOTANIKKALENDER_TABLE +
				" WHERE " + DB_Util.PFLANZE_ID + "=?";

		// Map zur Gruppierung der Monate nach Kalendertyp (z. B. AUSSAAAT → [März, April])
		Map<Kalendertyp, ArrayList<Month>> kalenderMap = new HashMap<>();	
		ArrayList<Botanikkalender> alKalender;
		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(select);
			stmt.setInt(1, pflanzeID); 	
			rs = stmt.executeQuery();

			// Alle Zeilen durchlaufen
			while(rs.next()) {
				// Monat und Kalendertyp auslesen
				Month monat = Month.fromBeschreibung(rs.getString(DB_Util.BOTANIKKALENDER_MONAT));	
				Kalendertyp typ = Kalendertyp.fromBeschreibung(rs.getString(DB_Util.BOTANIKKALENDER_KALENDERTYP));

				// Neue Liste anlegen, falls Kalendertyp noch nicht enthalten ist
				kalenderMap.putIfAbsent(typ, new ArrayList<>());
				// Monat zur entsprechenden Liste hinzufügen
				kalenderMap.get(typ).add(monat);
			}

			// Ergebnisliste vorbereiten
			alKalender = new ArrayList<>();

			// Key = Kalendertyp, Value = Month -> iterieren über entrySet 
			for(Map.Entry<Kalendertyp, ArrayList<Month>> e : kalenderMap.entrySet()) {
				// Für jeden Kalendertyp ein Botanikkalender-Objekt erzeugen
				alKalender.add(new Botanikkalender(e.getValue(), e.getKey(), 0)); // ID auf 0, nicht notwendig -> wird von DB generiert
			}
		} catch(SQLException e) {
			throw e;
		} finally {
			try {
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				throw e;
			}
		}
		return alKalender;
	}
}
