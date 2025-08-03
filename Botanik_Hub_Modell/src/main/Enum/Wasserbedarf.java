package Enum;

public enum Wasserbedarf {
	GERING("Gering"),
	MITTEL("Mittel"),
	HOCH("Hoch");

	private String beschreibung;

	private Wasserbedarf(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public String toString() {
		return beschreibung;
	}
	
	// Methode für Enum rückwandlung -> sonst IllegalArgumentException
	public static Wasserbedarf fromBeschreibung(String beschreibung) {
		for (Wasserbedarf m : Wasserbedarf.values()) {
			if (m.getBeschreibung().equalsIgnoreCase(beschreibung.trim())) {
				return m;
			}
		}
		return null;
	}
}
