package TEST_DB;

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
		} 
		finally {
			try {
				if(stmt != null) {
					stmt.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch(SQLException e) {
				throw e;
			}
		}
	}

	public static ArrayList<Botanikkalender> readKalender(int pflanzeID) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		/*
		 * Ich hab hier versucht eine Map aufzubauen mit Kalendertyp (Key) und Monat(Wert)
		 * Select from hab ich kalendertyp und monat genommen aus der Kalender table -> PflanzenID(ComboBox später)
		 */
		String select = "SELECT " + DB_Util.BOTANIKKALENDER_MONAT + "," + DB_Util.BOTANIKKALENDER_KALENDERTYP 
				+ " FROM " + DB_Util.BOTANIKKALENDER_TABLE +
				" WHERE " + DB_Util.PFLANZE_ID + "=?";

		/*
		 *  Sammeln der Monate gruppiet nach Kalendertyp z.B. KalendetTyp.Aussaat(März, April, Mai)
		 */
		Map<Kalendertyp, ArrayList<Month>> kalenderMap = new HashMap<>();	

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(select);
			stmt.setInt(1, pflanzeID); 	
			rs = stmt.executeQuery();


			while(rs.next()) {
				Month monat = Month.fromBeschreibung(rs.getString(DB_Util.BOTANIKKALENDER_MONAT));	
				Kalendertyp typ = Kalendertyp.fromBeschreibung(rs.getString(DB_Util.BOTANIKKALENDER_KALENDERTYP));
				
				/*
				 *  in die Map einfügen, mit putIfAbsent
				 */
				kalenderMap.putIfAbsent(typ, new ArrayList<>());
				kalenderMap.get(typ).add(monat);
			}
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
		
		/*
		 *  Arraylist von BotanikHub Objekte
		 *  Für jeden Kalendertyp (z. B. BLÜTE, ERNTE, AUSSAAT) wird ein Objekt erzeugt
		 */
		ArrayList<Botanikkalender> alKalender = new ArrayList<>();
		/* e.getValue() -> Liste der Monate (z. B. JANUARY, FEBRUARY, ...)
	    * e.getKey() -> der Kalendertyp (z. B. BLÜTE, AUSSAAAT, ERNTE)
	    * 0 -> eine temporäre ID (wird auf 0 gesetzt, da sie beim Speichern in der DB generiert wird
	    */
		for(Map.Entry<Kalendertyp, ArrayList<Month>> e : kalenderMap.entrySet()) {
			alKalender.add(new Botanikkalender(e.getValue(), e.getKey(), 0)); // ID auf 0, nicht notwendig
		}
		return alKalender;
	}
}
