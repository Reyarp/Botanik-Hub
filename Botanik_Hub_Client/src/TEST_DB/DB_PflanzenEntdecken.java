package TEST_DB;

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

	public static void deletePflanzenEntdecken(PflanzenEntdecken entdecken) throws SQLException{

		Connection conn = null;
		PreparedStatement stmt = null;

		String delete = "DELETE FROM " + DB_Util.PFLANZE_ENTDECKEN_TABLE +
				" WHERE " + DB_Util.PFLANZE_ID + "=? AND " + 
				DB_Util.BENUTZER_ID + "=?";

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(delete);

			stmt.setInt(1, entdecken.getPflanze().getPflanzenID());
			stmt.setInt(2, entdecken.getBenutzer().getBenutzerId());
			stmt.executeUpdate();

		} catch (SQLException e) {
			throw e;
		} 
		finally {
			try {
				if(conn != null) conn.close();
				if(stmt != null) stmt.close();
			} catch(SQLException e) {
				throw e;
			}
		}
	}

	public static ArrayList<Pflanze> readPflanzeEntdecken(Benutzer benutzer) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;


		String select =
				/*
				 * SELECT lädt alle pflanzenbezogenen Daten inkl. Benutzername,
				 * Vermehrung, Typ, verwendete Teile und Kalenderinfos
				 * für die GUI-Anzeige oder Weiterverarbeitung.
				 */
				"SELECT " + DB_Util.PFLANZE_TABLE + ".*, " +
						DB_Util.BENUTZER_TABLE + "." + DB_Util.BENUTZER_NAME + ", " +
						DB_Util.BENUTZER_TABLE + "." + DB_Util.BENUTZER_BENUTZERROLLE + ", " +
						DB_Util.PFLANZE_VERMEHRUNG_TABLE + ".*, " +
						DB_Util.PFLANZE_PFLANZENTYP_TABLE + ".*, " +
						DB_Util.PFLANZE_VERWENDETE_TEILE_TABLE + ".*, " +
						DB_Util.BOTANIKKALENDER_TABLE + ".* " +

						/*
						 * Ein select um NUR Admin pflanzen zu laden -> um sicher zu gehen hab ich NOT IN angewendet um Botanikhub Pflanzen auszuschließen
						 */
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
						 " NOT IN (" + " SELECT " + DB_Util.PFLANZE_ID +
						 " FROM " + DB_Util.BOTANIK_HUB_TABLE +
						 " WHERE " + DB_Util.BENUTZER_ID + "=?" + ")";

		ArrayList<Pflanze> alPflanze = new ArrayList<>();

		try {
			conn = DriverManager.getConnection(DB_Util.CONNECTION_STRING);
			stmt = conn.prepareStatement(select);
			stmt.setInt(1, benutzer.getBenutzerId());
			rs = stmt.executeQuery();

			Pflanze pflanze = null;

			// Temporäre Sets zur Duplikatvermeidung für die alPflanze
			HashSet<Vermehrungsarten> vermehrungSet = new HashSet<>();
			HashSet<Pflanzentyp> pflanzentypSet = new HashSet<>();
			HashSet<VerwendeteTeile> teileSet = new HashSet<>();
			ArrayList<Botanikkalender> kalenderList = new ArrayList<>();

			int letzteId = -1;
			while (rs.next()) {
				int pflanzenId = rs.getInt(DB_Util.PFLANZE_ID);
				int benutzerId = rs.getInt(DB_Util.BENUTZER_ID);
				Benutzer admin;

				// admin erstellen um ihn einer pflanze zuzuweisen beim lesen
				admin = new Benutzer();
				admin.setBenutzerId(benutzerId);
				admin.setBenutzerName(rs.getString(DB_Util.BENUTZER_NAME));
				admin.setTyp(BenutzerTyp.fromBeschreibung(rs.getString(DB_Util.BENUTZER_BENUTZERROLLE)));


				// Wenn wir zu einer neuen Pflanze wechseln, aktuelle Pflanze speichern und neue anlegen
				if (pflanzenId != letzteId) {
					if (pflanze != null) {
						// Daten übernehmen
						pflanze.getVermehrung().addAll(vermehrungSet);
						pflanze.getPflanzenTyp().addAll(pflanzentypSet);
						pflanze.getVerwendeteTeile().addAll(teileSet);
						pflanze.getKalender().addAll(kalenderList);

						alPflanze.add(pflanze);

						// Sets und Listen für neue Pflanze leeren
						vermehrungSet.clear();
						pflanzentypSet.clear();
						teileSet.clear();
						kalenderList.clear();
					}

					// Neue Pflanze anlegen
					pflanze = new Pflanze(
							rs.getString(DB_Util.PFLANZE_NAME),
							rs.getString(DB_Util.PFLANZE_BOTAN_NAME),
							rs.getString(DB_Util.PFLANZE_BILDPFAD),
							pflanzenId,
							rs.getBoolean(DB_Util.PFLANZE_IS_GIFTIG),
							rs.getDouble(DB_Util.PFLANZE_WUCHSBREITE),
							rs.getDouble(DB_Util.PFLANZE_WUCHSHOEHE),
							null,
							Wasserbedarf.fromBeschreibung(rs.getString(DB_Util.PFLANZE_WASSERBEDARF)),
							Lichtbedarf.fromBeschreibung(rs.getString(DB_Util.PFLANZE_LICHTBEDARF)),
							Intervall.fromBeschreibung(rs.getString(DB_Util.PFLANZE_DUENGUNG)),
							Vertraeglichkeit.fromBeschreibung(rs.getString(DB_Util.PFLANZE_VERTRAEGLICHKEIT)),
							Standort.fromBeschreibung(rs.getString(DB_Util.PFLANZE_STANDORT)),
							Lebensdauer.fromBeschreibung(rs.getString(DB_Util.PFLANZE_LEBENSDAUER)),
							new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
							new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
							admin
							);

					letzteId = pflanzenId;
				}

				// Vermehrung
				String vermehrung = rs.getString(DB_Util.VERMEHRUNG_ARTEN);
				if (vermehrung != null) {
					try {
						vermehrungSet.add(Vermehrungsarten.fromBeschreibung(vermehrung));
					} catch (IllegalArgumentException ignored) {}
				}

				// Pflanzentyp
				String typ = rs.getString(DB_Util.PFLANZEN_TYPEN);
				if (typ != null) {
					try {
						pflanzentypSet.add(Pflanzentyp.fromBeschreibung(typ));
					} catch (IllegalArgumentException ignored) {}
				}

				// Verwendete Teile
				String teil = rs.getString(DB_Util.VERWENDETE_TEILE);
				if (teil != null) {
					try {
						teileSet.add(VerwendeteTeile.fromBeschreibung(teil));
					} catch (IllegalArgumentException ignored) {}
				}

				// Botanikkalender
				int id = rs.getInt(DB_Util.BOTANIKKALENDER_ID);
				String monat = rs.getString(DB_Util.BOTANIKKALENDER_MONAT);
				String typKalender = rs.getString(DB_Util.BOTANIKKALENDER_KALENDERTYP);

				if (monat != null && typKalender != null) {
					try {
						Kalendertyp kaltyp = Kalendertyp.fromBeschreibung(typKalender);
						Month m = Month.fromBeschreibung(monat);

						// Versuche bestehenden Kalender dieses Typs zu finden
						Botanikkalender vorhandener = kalenderList.stream()
								.filter(k -> k.getKalendertyp() == kaltyp)
								.findFirst()
								.orElse(null);

						if (vorhandener == null) {
							// Erstellen wenn nicht vorhanden
							vorhandener = new Botanikkalender(new ArrayList<>(), kaltyp, id);
							kalenderList.add(vorhandener);
						}

						// Monat hinzufügen, wenn nicht schon drin
						if (!vorhandener.getMonat().contains(m)) {
							vorhandener.getMonat().add(m);
						}
					} catch (IllegalArgumentException ignored) {}
				}
			}

			// Letzte Pflanze noch hinzufügen
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
