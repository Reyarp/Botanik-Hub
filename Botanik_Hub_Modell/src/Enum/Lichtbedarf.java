package Enum;

public enum Lichtbedarf {
	SONNIG("Sonnig"),
	HALBSCHATTIG("Halbschattig"),
	SCHATTIG("Schattig");

	private String beschreibung;

	Lichtbedarf(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public String toString() {
		return beschreibung;
	}
	// Methode für Enum rückwandlung -> sonst IllegalArgumentException
	public static Lichtbedarf fromBeschreibung(String beschreibung) {
		for (Lichtbedarf m : Lichtbedarf.values()) {
			if (m.getBeschreibung().equalsIgnoreCase(beschreibung.trim())) {
				return m;
			}
		}
		return null;
	}
}
