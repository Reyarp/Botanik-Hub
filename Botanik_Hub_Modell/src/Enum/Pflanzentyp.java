package Enum;

public enum Pflanzentyp {
	ZIERPFLANZE("Zierpflanze"),
	HEILPFLANZE("Heilpflanze"),
	DUFTPFLANZE("Duftpflanze"),
	KLETTERPFLANZE("Kletterpflanze"),
	WILDPFLANZE("Wildpflanze"),
	ZIMMERPFLANZE("Zimmerpflanze"),
	GEWUERZPFLANZE("Gewürzpflanze"),
	WASSERPFLANZE("Wasserpflanze");

	private String beschreibung;

	Pflanzentyp(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public String toString() {
		return beschreibung;
	}

	// Methode für Enum rückwandlung -> sonst IllegalArgumentException
	public static Pflanzentyp fromBeschreibung(String beschreibung) {
		for (Pflanzentyp m : Pflanzentyp.values()) {
			if (m.getBeschreibung().equalsIgnoreCase(beschreibung.trim())) {
				return m;
			}
		}
		throw new IllegalArgumentException("Unbekannter Pflanzentyp");
	}
}
