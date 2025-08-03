package Database;

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
import Modell.PflanzenEntdecken;

public class DB_PflanzenEntdecken {

	public static void createPflanzenEntdecken() throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.createStatement();
			rs = conn.getMetaData().getTables(null, null, DB_Util.PFLANZE_ENTDECKEN_TABLE.toUpperCase(), new String[] {"TABLE"});

			if(rs.next())
				return;


			String create = "CREATE TABLE " +
					DB_Util.PFLANZE_ENTDECKEN_TABLE + " (" +
					DB_Util.PFLANZE_ID + " INTEGER," +
					DB_Util.BENUTZER_ID + " INTEGER," +
					"PRIMARY KEY(" + DB_Util.PFLANZE_ID + "," + DB_Util.BENUTZER_ID + ")," +
					"FOREIGN KEY(" + DB_Util.PFLANZE_ID + ") REFERENCES " + DB_Util.PFLANZE_TABLE + "(" + DB_Util.PFLANZE_ID + ")," +
					"FOREIGN KEY(" + DB_Util.BENUTZER_ID + ") REFERENCES " + DB_Util.BENUTZER_TABLE + "(" + DB_Util.BENUTZER_ID + ")" +
					")";

			stmt.executeUpdate(create);

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
	}

	public static void insertPflanzenEntdecken(PflanzenEntdecken entdecken) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;

		String insert = "INSERT INTO " + DB_Util.PFLANZE_ENTDECKEN_TABLE + " (" +
				DB_Util.PFLANZE_ID + "," +
				DB_Util.BENUTZER_ID + ") VALUES (?, ?)";

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(insert);

			stmt.setInt(1, entdecken.getPflanze().getPflanzenID());
			stmt.setInt(2, entdecken.getBenutzer().getBenutzerId());
			stmt.executeUpdate();

		} catch(SQLException e) {
			throw e;
		} finally {
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

	public static void deletePflanzenEntdecken(int pflanzeID, int benutzerID) throws SQLException{

		Connection conn = null;
		PreparedStatement stmt = null;

		String delete = "DELETE FROM " + DB_Util.PFLANZE_ENTDECKEN_TABLE +
				" WHERE " + DB_Util.PFLANZE_ID + "=? AND " + 
				DB_Util.BENUTZER_ID + "=?";

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(delete);

			stmt.setInt(1, pflanzeID);
			stmt.setInt(2, benutzerID);
			stmt.executeUpdate();

		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if(conn != null) conn.close();
				if(stmt != null) stmt.close();
			} catch(SQLException e) {
				throw e;
			}
		}
	}

	public static ArrayList<Pflanze> readPflanzeEntdecken(int benutzerID) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		// SQL select: Lädt alle Admin-Pflanzen, die noch nicht im BotanikHub des aktuellen Benutzers vorhanden sind.
		// Enthält Daten aus: Pflanze, Benutzer, Vermehrung, Pflanzentyp, verwendete Teile, Botanikkalender.
		// Admin-Pflanzen werden über Benutzer_ID = 1 identifiziert, bereits übernommene über NOT IN ausgeschlossen.
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
						 " ON " + DB_Util.PFLANZE_TABLE + "." + DB_Util.PFLANZE_ID + " = " + DB_Util.BOTANIKKALENDER_TABLE + "." + DB_Util.PFLANZE_ID +

						 " WHERE " + DB_Util.PFLANZE_TABLE + "." + DB_Util.BENUTZER_ID + "=1" +
						 " AND " + DB_Util.PFLANZE_TABLE + "." + DB_Util.PFLANZE_ID +
						 // NOT IN -> Tabelle ausschliessen
						 " NOT IN (" +
						 "SELECT " + DB_Util.PFLANZE_ID + " FROM " + DB_Util.BOTANIK_HUB_TABLE +
						 " WHERE " + DB_Util.BENUTZER_ID + " = ?" +
						 // UNION -> kombiniert beide Teilabfragen (BotanikHub & Wunschliste)
						 " UNION " +
						 "SELECT " + DB_Util.PFLANZE_ID + " FROM " + DB_Util.WUNSCHLISTE_TABLE +
						 " WHERE " + DB_Util.BENUTZER_ID + " = ?" +
						 ")";

		// Leere ArrayList und Pflanzenobjekt zum befüllen
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
			// id für Botanik-Hub
			stmt.setInt(1, benutzerID);
			// id für Wunschliste
			stmt.setInt(2, benutzerID);
			rs = stmt.executeQuery();


			while (rs.next()) {
				// Pflanze ID holen zum Vergleichen -> letzeID != pflanzeID
				int pflanzenId = rs.getInt(DB_Util.PFLANZE_ID);
				int benutzerId = rs.getInt(DB_Util.BENUTZER_ID);

				// admin erstellen um ihn einer pflanze zuzuweisen beim lesen (ID = 1)
				Benutzer admin = new Benutzer();
				admin.setBenutzerId(benutzerId);
				admin.setBenutzerName(rs.getString(DB_Util.BENUTZER_NAME));
				admin.setTyp(BenutzerTyp.fromBeschreibung(rs.getString(DB_Util.BENUTZER_BENUTZERROLLE)));


				// Solange die aktuelle Pflanze != letzer pflanze und != null -> vorherige Pflanze abschliessen und speichern
				if (pflanzenId != letzteId) {	
					if (pflanze != null) {
						// Daten übernehmen
						pflanze.getVermehrung().addAll(vermehrungSet);
						pflanze.getPflanzenTyp().addAll(pflanzentypSet);
						pflanze.getVerwendeteTeile().addAll(teileSet);
						pflanze.getKalender().addAll(kalenderList);

						// Pflanze zur ArrayList hinzufügen
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
							admin,	// admin setzen
							null,	// Keine Notiz bei Admin
							true
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
		return alPflanze;
	}
}
