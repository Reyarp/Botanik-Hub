package Enum;

public enum VerwendeteTeile {
	BLUETE("Blüte"),
	STIEL("Stiel"),
	WURZEL("Wurzel");

	private String beschreibung;

	private VerwendeteTeile(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public String toString() {
		return beschreibung;
	}
	// Methode für Enum rückwandlung -> sonst IllegalArgumentException
	public static VerwendeteTeile fromBeschreibung(String beschreibung) {
		for (VerwendeteTeile m : VerwendeteTeile.values()) {
			if (m.getBeschreibung().equalsIgnoreCase(beschreibung.trim())) {
				return m;
			}
		}
		return null;
	}
}
