package Enum;

public enum Intervall {
	TAEGLICH("Täglich"),
	WOECHENTLICH("Wöchentlich"),
	MONATLICH("Monatlich"),
	JAEHRLICH("Jährlich");

	private String beschreibung;

	private Intervall(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public String toString() {
		return beschreibung;
	}

	// Methode für Enum rückwandlung -> sonst IllegalArgumentException
	public static Intervall fromBeschreibung(String beschreibung) {
		for (Intervall m : Intervall.values()) {
			if (m.getBeschreibung().equalsIgnoreCase(beschreibung.trim())) {
				return m;
			}
		}
		return null;
	}
}
