package Enum;

public enum BenutzerTyp {
	ADMIN("Admin"),
	BENUTZER("Benutzer");

	private String beschreibung;

	private BenutzerTyp(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public String toString() {
		return beschreibung;
	}

	/*
	 *  Da Enums sehr case sensitive sind hab ich mit String Beschreibung gearbeitet und  
	 *  eine Methode geschrieben die meine Beschreibung als Vergleich hernimmt für Prüfungen da z.B. März oder Gießen nicht in der Datenbank funktioniert!
	 */
	
	// Methode für Enum rückwandlung -> sonst IllegalArgumentException
	public static BenutzerTyp fromBeschreibung(String beschreibung) {
		for (BenutzerTyp m : BenutzerTyp.values()) {
			if (m.getBeschreibung().equalsIgnoreCase(beschreibung.trim())) {
				return m;
			}
		}
		return null;
	}
}
