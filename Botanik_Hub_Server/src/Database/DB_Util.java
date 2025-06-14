package Database;

public class DB_Util {
	
	/*
	 * Hilfsklasse für die Datenbank um auf die Konstanten zuzugreifen
	 */

	private static final String DB_LOCATION = "G:\\Derby";
	public static final String CONNECTION_STRING = "jdbc:derby:" + DB_LOCATION + ";create=true";
	
	public static final String PFLANZE_TABLE = "PFLANZE_TABLE";
	public static final String PFLANZE_ID = "PFLANZE_ID";
	public static final String PFLANZE_NAME = "PFLANZE_NAME";
	public static final String PFLANZE_BOTAN_NAME = "PFLANZE_BOTAN_NAME";
	public static final String PFLANZE_IS_GIFTIG = "PFLANZE_IS_GIFTIG";
	public static final String PFLANZE_WUCHSBREITE = "PFLANZE_WUCHSBREITE";
	public static final String PFLANZE_WUCHSHOEHE = "PFLANZE_WUCHSHOEHE";
	public static final String PFLANZE_WASSERBEDARF = "PFLANZE_WASSERBEDARF";
	public static final String PFLANZE_LICHTBEDARF = "PFLANZE_LICHTBEDARF";
	public static final String PFLANZE_DUENGUNG = "PFLANZE_DUENGUNG";
	public static final String PFLANZE_VERTRAEGLICHKEIT = "PFLANZE_VERTRAEGLICHKEIT";
	public static final String PFLANZE_STANDORT = "PFLANZE_STANDORT";
	public static final String PFLANZE_LEBENSDAUER = "PFLANZE_LEBENSDAUER";
	public static final String PFLANZE_BILDPFAD = "PFLANZE_BILDPFAD";
	
	//Verbindungstabelle
	public static final String PFLANZE_VERMEHRUNG_TABLE = "PFLANZE_VERMEHRUNG_TABLE";
	public static final String VERMEHRUNG_ARTEN = "VERMEHRUNG_ARTEN";

	//Verbindungstabelle
	public static final String PFLANZE_PFLANZENTYP_TABLE = "PFLANZE_PFLANZENTYP_TABLE";
	public static final String PFLANZEN_TYPEN = "PFLANZEN_TYPEN";

	//Verbindungstabelle
	public static final String PFLANZE_VERWENDETE_TEILE_TABLE = "PFLANZE_VERWENDETE_TEILE_TABLE";
	public static final String VERWENDETE_TEILE = "VERWENDETE_TEILE";

	//Verbindungstabelle
	public static final String WUNSCHLISTE_TABLE = "WUNSCHLISTE_TABLE";
	
	//Verbindungstabelle
	public static final String BOTANIK_HUB_TABLE = "BOTANIK_HUB_TABLE";
	public static final String BOTANIK_HUB_PFLANZE_NOTIZ = "BOTANIK_HUB_PFLANZE_NOTIZ";
	
	//Verbindungstabelle
	public static final String PFLANZE_ENTDECKEN_TABLE = "PFLANZE_ENTDECKEN_TABLE";
	
	
	public static final String BOTANIKKALENDER_TABLE = "BOTANIKKALENDER_TABLE";
	public static final String BOTANIKKALENDER_ID = "BOTANIKKALENDER_ID";
	public static final String BOTANIKKALENDER_MONAT = "BOTANIKKALENDER_MONAT";
	public static final String BOTANIKKALENDER_KALENDERTYP = "BOTANIKKALENDER_KALENDERTYP";
	

	public static final String ERINNERUNG_TABLE = "ERINNERUNG_TABLE";
	public static final String ERINNERUNG_ID = "ERINNERUNG_ID";
	public static final String ERINNERUNG_DATUM = "ERINNERUNG_DATUM";
	public static final String ERINNERUNG_ERINNERUNGTYP = "ERINNERUNG_ERINNERUNGTYP";
	public static final String ERINNERUNG_INTERVALL = "ERINNERUNG_INTERVALL";

	public static final String BENUTZER_TABLE = "BENUTZER_TABLE";
	public static final String BENUTZER_ID = "BENUTZER_ID";
	public static final String BENUTZER_REGISTRIERT_SEIT = "BENUTZER_REGISTRIERT_SEIT";
	public static final String BENUTZER_NAME = "BENUTZER_NAME";
	public static final String BENUTZER_PASSWORT = "BENUTZER_PASSWORT";
	public static final String BENUTZER_BENUTZERROLLE = "BENUTZER_BENUTZERROLLE";

	public static final String ADMIN_NAME = "admin";
	public static final String ADMIN_PASSWORT = "1234";
	
}
