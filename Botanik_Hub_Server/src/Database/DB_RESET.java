package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DB_RESET {

    public static void resetDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
             Statement stmt = conn.createStatement()) {

            // Verbindungstabellen zuerst (wegen FK-Abhängigkeiten)
            drop(stmt, DB_Util.BOTANIKKALENDER_TABLE);
            drop(stmt, DB_Util.PFLANZE_VERWENDETE_TEILE_TABLE);
            drop(stmt, DB_Util.PFLANZE_PFLANZENTYP_TABLE);
            drop(stmt, DB_Util.PFLANZE_VERMEHRUNG_TABLE);
            drop(stmt, DB_Util.WUNSCHLISTE_TABLE);
            drop(stmt, DB_Util.BOTANIK_HUB_TABLE);
            drop(stmt, DB_Util.PFLANZE_ENTDECKEN_TABLE);
            drop(stmt, DB_Util.ERINNERUNG_TABLE);

            // Haupttabellen zuletzt
            drop(stmt, DB_Util.PFLANZE_TABLE);
            drop(stmt, DB_Util.BENUTZER_TABLE);

            System.out.println("✅ Alle Tabellen erfolgreich gelöscht.");

        } catch (SQLException e) {
            System.err.println("❌ Fehler beim Zurücksetzen der Datenbank:");
            e.printStackTrace();
        }
    }

    // Hilfsmethode zum Dropen
    private static void drop(Statement stmt, String tableName) {
        try {
            stmt.executeUpdate("DROP TABLE " + tableName);
            System.out.println("Tabelle gelöscht: " + tableName);
        } catch (SQLException e) {
            System.out.println("Tabelle nicht vorhanden oder bereits gelöscht: " + tableName);
        }
    }
}
