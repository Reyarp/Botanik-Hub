package Enum;

public enum Vertraeglichkeit {
	WINTERHART("Winterhart"),
	NICHT_WINTERHART("Nicht Winterhart");

	private String beschreibung;

	private Vertraeglichkeit(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public String toString() {
		return beschreibung;
	}
	// Methode für Enum rückwandlung -> sonst IllegalArgumentException
	public static Vertraeglichkeit fromBeschreibung(String beschreibung) {
		for (Vertraeglichkeit m : Vertraeglichkeit.values()) {
			if (m.getBeschreibung().equalsIgnoreCase(beschreibung.trim())) {
				return m;
			}
		}
		return null;
	}
}
