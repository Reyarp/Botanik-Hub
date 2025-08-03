package Enum;

public enum Lebensdauer {
	EINAEHRIG("Einjährig"),
	ZWEIJAEHRIG("Zweijährig"),
	MEHRJAEHRIG("Mehrjährig");

	private String beschreibung;

	private Lebensdauer(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public String getBeschreibung() {
		return beschreibung; 
	}

	public String toString() {
		return beschreibung;
	}

	// Methode für Enum rückwandlung -> sonst IllegalArgumentException
	public static Lebensdauer fromBeschreibung(String beschreibung) {
		for (Lebensdauer m : Lebensdauer.values()) {
			if (m.getBeschreibung().equalsIgnoreCase(beschreibung.trim())) {
				return m;
			}
		}
		return null;
	}
}
