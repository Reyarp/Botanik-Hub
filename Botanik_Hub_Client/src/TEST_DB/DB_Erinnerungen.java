package TEST_DB;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import Enum.Erinnerungstyp;
import Enum.Intervall;
import Modell.Benutzer;
import Modell.Erinnerungen;
import Modell.Pflanze;

public class DB_Erinnerungen {

	public static void createErinnerung() throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.createStatement();
			rs = conn.getMetaData().getTables(null, null, DB_Util.ERINNERUNG_TABLE.toUpperCase(), new String[] {"TABLE"});

			if(rs.next())
				return;

			String create = "CREATE TABLE " +
					DB_Util.ERINNERUNG_TABLE + " (" +
					DB_Util.ERINNERUNG_ID + " INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
					DB_Util.ERINNERUNG_DATUM + " DATE," +
					DB_Util.ERINNERUNG_ERINNERUNGTYP + " VARCHAR(100)," +
					DB_Util.ERINNERUNG_INTERVALL + " VARCHAR(100)," +
					DB_Util.PFLANZE_ID + " INTEGER," +
					DB_Util.BENUTZER_ID + " INTEGER," +
					"PRIMARY KEY(" + DB_Util.ERINNERUNG_ID + ")," +
					"FOREIGN KEY(" + DB_Util.PFLANZE_ID + ") REFERENCES " + DB_Util.PFLANZE_TABLE + "(" + DB_Util.PFLANZE_ID + ")," +
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

	public static void insertErinnerung(Erinnerungen erinnerung) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null; 	// ein Resultset um die ID von Erinnerung zu holen

		String insert = "INSERT INTO " + DB_Util.ERINNERUNG_TABLE + " (" +
				DB_Util.ERINNERUNG_DATUM + "," +
				DB_Util.ERINNERUNG_ERINNERUNGTYP + "," +
				DB_Util.ERINNERUNG_INTERVALL + "," +
				DB_Util.PFLANZE_ID + "," +
				DB_Util.BENUTZER_ID + ") VALUES (?, ?, ?, ?, ?)";

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
			Date datum = Date.valueOf(erinnerung.getDatum());

			stmt.setDate(1, datum);
			stmt.setString(2, erinnerung.getTyp().getBeschreibung());
			stmt.setString(3, erinnerung.getIntervall().getBeschreibung());
			stmt.setInt(4, erinnerung.getPflanze().getPflanzenID());
			stmt.setInt(5, erinnerung.getBenutzer().getBenutzerId());

			stmt.executeUpdate();

			rs = stmt.getGeneratedKeys();

			int erinnerungID = -1;			// default wert (keine 0 für klarheit und um zu prüfen ob tatsächlich eine ID gesetzt wurde)				
			if(rs.next()) {
				erinnerungID = rs.getInt(1);	// ID wurde geneiert
				erinnerung.setErinnerungID(erinnerungID); // id wurde ins Objekt übertragen
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
	}

	public static void updateErinnerungen(Erinnerungen erinnerung) throws SQLException{
		Connection conn =  null;
		PreparedStatement stmt = null;

		String update = "UPDATE " + DB_Util.ERINNERUNG_TABLE + " SET " + 
				DB_Util.ERINNERUNG_DATUM + "=?, " +
				DB_Util.ERINNERUNG_ERINNERUNGTYP + "=?, " +
				DB_Util.ERINNERUNG_INTERVALL + "=? WHERE " + DB_Util.ERINNERUNG_ID + "=?";

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(update);
			Date datum = Date.valueOf(erinnerung.getDatum());
			
			stmt.setObject(1, datum);
			stmt.setString(2, erinnerung.getTyp().getBeschreibung());
			stmt.setString(3, erinnerung.getIntervall().getBeschreibung());
			stmt.setInt(4, erinnerung.getErinnerungID());
			
			stmt.executeUpdate();
			
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

	public static void deleteErinnerungen(Erinnerungen erinnerung) throws SQLException {
	    Connection conn = null;
	    PreparedStatement stmt = null;

	    String delete = "DELETE FROM " + DB_Util.ERINNERUNG_TABLE +
	        " WHERE " + DB_Util.ERINNERUNG_ID + "=?";

	    try {
	        conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
	        stmt = conn.prepareStatement(delete);
	        stmt.setInt(1, erinnerung.getErinnerungID());
	        stmt.executeUpdate();

	    } catch (SQLException e) {
	        throw e;
	    } finally {
	        if (stmt != null) stmt.close();
	        if (conn != null) conn.close();
	    }
	}

	public static ArrayList<Erinnerungen> readErinnerungen(int benutzerID) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		// PFLANZE_NAME zusätzlich mitladen
		String select = 
				"SELECT " + DB_Util.ERINNERUNG_TABLE + ".*, " + 
						DB_Util.PFLANZE_TABLE + "." + DB_Util.PFLANZE_NAME + 
						" FROM " + DB_Util.ERINNERUNG_TABLE +
						" JOIN " + DB_Util.PFLANZE_TABLE +
						" ON " + DB_Util.ERINNERUNG_TABLE + "." + DB_Util.PFLANZE_ID + " = " + DB_Util.PFLANZE_TABLE + "." + DB_Util.PFLANZE_ID +
						" WHERE " + DB_Util.ERINNERUNG_TABLE + "." + DB_Util.BENUTZER_ID + "=?";


		ArrayList<Erinnerungen> alErinnerung = new ArrayList<>();

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(select);
			stmt.setInt(1, benutzerID);
			rs = stmt.executeQuery();

			while (rs.next()) {
				// Benutzer setzen
				Benutzer benutzer = new Benutzer();
				benutzer.setBenutzerId(rs.getInt(DB_Util.BENUTZER_ID));

				// Pflanze mit ID und Name setzen
				Pflanze pflanze = new Pflanze();
				pflanze.setPflanzenID(rs.getInt(DB_Util.PFLANZE_ID));
				pflanze.setPflanzenName(rs.getString(DB_Util.PFLANZE_NAME));

				// Erinnerung erzeugen
				Erinnerungen e = new Erinnerungen(
						rs.getDate(DB_Util.ERINNERUNG_DATUM).toLocalDate(),
						Erinnerungstyp.fromBeschreibung(rs.getString(DB_Util.ERINNERUNG_ERINNERUNGTYP)),
						Intervall.fromBeschreibung(rs.getString(DB_Util.ERINNERUNG_INTERVALL)),
						benutzer,
						rs.getInt(DB_Util.ERINNERUNG_ID),
						pflanze
						);

				alErinnerung.add(e);
			}
		} catch(SQLException e) {
			throw e;
		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}

		return alErinnerung;
	}

}
