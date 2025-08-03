package Enum;

public enum Kalendertyp {
	ERNTE("Ernte"),
	BLUETE("Blüte"),
	AUSSAAT("Aussaat"),
	RUECKSCHNITT("RÜckschnitt"); /* Es gibt Rückschnitt der für die Rückschnitts Monate gedacht ist 
									und es Gibt im ErinnerungsTyp ENUM den Pflegeschnitt der für die ErinnerungDialog gedacht ist!!!*/

	private String beschreibung;

	private Kalendertyp(String beschreibung) { 
		this.beschreibung = beschreibung;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public String toString() {
		return beschreibung;
	}

	// Methode für Enum rückwandlung -> sonst IllegalArgumentException
	public static Kalendertyp fromBeschreibung(String beschreibung) {
		for (Kalendertyp m : Kalendertyp.values()) {
			if (m.getBeschreibung().equalsIgnoreCase(beschreibung.trim())) {
				return m;
			}
		}
		return null;
	}
}
