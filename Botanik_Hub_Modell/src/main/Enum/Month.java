package Enum;

public enum Month {
	JANUAR("Januar"),
	FEBRUAR("Februar"),
	MAERZ("März"),
	APRIL("April"),
	MAI("Mai"),
	JUNI("Juni"),
	JULI("Juli"),
	AUGUST("August"),
	SEPTEMBER("September"),
	OKTOBER("Oktober"),
	NOVEMBER("November"),
	DEZEMBER("Dezember");

	private String beschreibung;

	Month(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public String toString() {
		return beschreibung;
	}

	// Methode für Enum rückwandlung -> sonst IllegalArgumentException
	public static Month fromBeschreibung(String beschreibung) {
		for (Month m : Month.values()) {
			if (m.getBeschreibung().equalsIgnoreCase(beschreibung.trim())) {
				return m;
			}
		}
		return null;
	}
}
