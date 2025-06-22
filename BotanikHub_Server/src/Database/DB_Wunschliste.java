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
import Modell.MeineWunschliste;
import Modell.Pflanze;

public class DB_Wunschliste {

	public static void createWunschliste() throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.createStatement();
			rs = conn.getMetaData().getTables(null, null, DB_Util.WUNSCHLISTE_TABLE.toUpperCase(), new String[] {"TABLE"});

			if(rs.next())
				return;

			// SQL create: Erstellt die Verbindungstabelle zwischen Pflanze und Benutzer für die Wunschliste
			// Die Tabelle speichert, welche Pflanze sich welcher Benutzer vorgemerkt hat
			// Primärschlüssel = Kombination aus PFLANZE_ID und BENUTZER_ID
			// Enthält zwei Fremdschlüssel auf PFLANZE_TABLE und BENUTZER_TABLE
			String create = "CREATE TABLE " +
					DB_Util.WUNSCHLISTE_TABLE + " (" +
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

	public static void insertWunschliste(MeineWunschliste wunschliste) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;

		// SQL insert: Fügt einen Datensatz in die Wunschliste ein (PFLANZE_ID, BENUTZER_ID)
		// Wird aufgerufen, wenn ein Benutzer eine Pflanze zur Wunschliste hinzufügt
		String insert = "INSERT INTO " + DB_Util.WUNSCHLISTE_TABLE + " (" +
				DB_Util.PFLANZE_ID + "," +
				DB_Util.BENUTZER_ID + ") VALUES (?, ?)";

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(insert);

			stmt.setInt(1, wunschliste.getPflanze().getPflanzenID());
			stmt.setInt(2, wunschliste.getBenutzer().getBenutzerId());
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

	public static void deleteWunschliste(int pflanzeID, int benutzerID) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;

		// SQL delete: Entfernt einen Datensatz aus der Wunschliste
		// Kombination aus PFLANZE_ID und BENUTZER_ID muss übereinstimmen
		String delete = "DELETE FROM " + DB_Util.WUNSCHLISTE_TABLE +
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

	public static ArrayList<MeineWunschliste> readWunschlistePflanze(Benutzer benutzer) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		// SQL select: Liest alle Pflanzen eines Benutzers aus der Wunschliste
		// Beinhaltet JOINs auf Pflanze, Benutzer, Vermehrung, Typ, verwendete Teile und Botanikkalender
		// NOT IN: Schließt Pflanzen aus, die bereits im BotanikHub des Benutzers sind
		String select =

				// SQL select: Lädt alle pflanzenbezogenen Daten für die Wunschliste
				// JOINs auf:
				// PFLANZE_TABLE: um Pflanzendaten zu erhalten
				// BENUTZER_TABLE: um den ursprünglichen Ersteller der Pflanze (Admin) zu ermitteln
				// LEFT JOINs auf Verbindungstabellen: Vermehrung, Typ, Teile, Kalender
				// WHERE: Nur Pflanzen, die zur Wunschliste des Benutzers gehören, aber noch nicht im BotanikHub stehen

				"SELECT " + DB_Util.PFLANZE_TABLE + ".*, " +
				DB_Util.BENUTZER_TABLE + "." + DB_Util.BENUTZER_NAME + ", " +
				DB_Util.BENUTZER_TABLE + "." + DB_Util.BENUTZER_BENUTZERROLLE + ", " +
				DB_Util.PFLANZE_VERMEHRUNG_TABLE + ".*, " +
				DB_Util.PFLANZE_PFLANZENTYP_TABLE + ".*, " +
				DB_Util.PFLANZE_VERWENDETE_TEILE_TABLE + ".*, " +
				DB_Util.BOTANIKKALENDER_TABLE + ".* " +

				" FROM " + DB_Util.WUNSCHLISTE_TABLE +
				" JOIN " + DB_Util.PFLANZE_TABLE +
				" ON " + DB_Util.WUNSCHLISTE_TABLE + "." + DB_Util.PFLANZE_ID + " = " + DB_Util.PFLANZE_TABLE + "." + DB_Util.PFLANZE_ID +
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

				" WHERE " + DB_Util.WUNSCHLISTE_TABLE + "." + DB_Util.BENUTZER_ID + "=?" +
				" AND " + DB_Util.PFLANZE_TABLE + "." + DB_Util.PFLANZE_ID +
				" NOT IN (" +
				" SELECT " + DB_Util.PFLANZE_ID +
				" FROM " + DB_Util.BOTANIK_HUB_TABLE +
				" WHERE " + DB_Util.BENUTZER_ID + "=?" +
				")";

		// Leere ArrayList und Pflanzenobjekt zum befüllen
		ArrayList<MeineWunschliste> alPflanze = new ArrayList<>();
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
			// Benutzer-ID aus der Wunschliste in das SQL einsetzen
			stmt.setInt(1, benutzer.getBenutzerId());
			stmt.setInt(2, benutzer.getBenutzerId());	// Für NOT IN
			rs = stmt.executeQuery();


			while (rs.next()) {
				// Pflanze ID holen zum Vergleichen -> letzeID != pflanzeID
				int pflanzenId = rs.getInt(DB_Util.PFLANZE_ID);
				int benutzerId = rs.getInt(DB_Util.BENUTZER_ID);

				// Admin-Benutzer erstellen (der Ersteller der Pflanze, nicht der Wunschlistenbesitzer)
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

						// Pflanze zur Wunschliste hinzufügen
						alPflanze.add(new MeineWunschliste(benutzer, pflanze));

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
							null,
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
							null	// Keine Notiz für Admin
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

				// Füge die vollständig aufgebaute Pflanze der alPflanze hinzu,
				// verknüpft mit dem Benutzer, dem die Wunschliste gehört.
				// Der übergebene Benutzer (Wunschlistenbesitzer) != Ersteller der Pflanze (Admin)
				alPflanze.add(new MeineWunschliste(benutzer, pflanze));
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
