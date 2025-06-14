package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DB_Verbindungstabellen {

	public static void createPflanzenTyp() throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.createStatement();
			rs = conn.getMetaData().getTables(null, null, DB_Util.PFLANZE_PFLANZENTYP_TABLE.toUpperCase(), null);
			

			if(rs.next())
				return;

			String create = "CREATE TABLE " +
					DB_Util.PFLANZE_PFLANZENTYP_TABLE + " (" +
					DB_Util.PFLANZE_ID + " INTEGER," +
					DB_Util.PFLANZEN_TYPEN + " VARCHAR(100)," +
					"PRIMARY KEY(" + DB_Util.PFLANZE_ID + "," + DB_Util.PFLANZEN_TYPEN + ")," +
					"FOREIGN KEY(" + DB_Util.PFLANZE_ID + ") REFERENCES " + DB_Util.PFLANZE_TABLE + "(" + DB_Util.PFLANZE_ID + ")" +
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
	
	public static void createVermehrung() throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.createStatement();
			rs = conn.getMetaData().getTables(null, null, DB_Util.PFLANZE_VERMEHRUNG_TABLE.toUpperCase(), null);


			if(rs.next())
				return;

			String create = "CREATE TABLE " +
					DB_Util.PFLANZE_VERMEHRUNG_TABLE + " (" +
					DB_Util.PFLANZE_ID + " INTEGER," +
					DB_Util.VERMEHRUNG_ARTEN + " VARCHAR(100)," +
					"PRIMARY KEY(" + DB_Util.PFLANZE_ID + "," + DB_Util.VERMEHRUNG_ARTEN + ")," +
					"FOREIGN KEY(" + DB_Util.PFLANZE_ID + ") REFERENCES " + DB_Util.PFLANZE_TABLE + "(" + DB_Util.PFLANZE_ID + ")" +
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
	
	public static void createVerwendeteTeile() throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.createStatement();
			rs = conn.getMetaData().getTables(null, null, DB_Util.PFLANZE_VERWENDETE_TEILE_TABLE.toUpperCase(), null);
			
			
			if(rs.next())
				return;

			String create = "CREATE TABLE " +
					DB_Util.PFLANZE_VERWENDETE_TEILE_TABLE + " (" +
					DB_Util.PFLANZE_ID + " INTEGER," +
					DB_Util.VERWENDETE_TEILE + " VARCHAR(100)," +
					"PRIMARY KEY(" + DB_Util.PFLANZE_ID + "," + DB_Util.VERWENDETE_TEILE + ")," +
					"FOREIGN KEY(" + DB_Util.PFLANZE_ID + ") REFERENCES " + DB_Util.PFLANZE_TABLE + "(" + DB_Util.PFLANZE_ID + ")" +
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
}
