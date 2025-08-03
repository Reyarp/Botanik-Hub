package Enum;

public enum Standort {
	GARTEN("Garten"),
	BALKON("Balkon"),
	FENSTERBANK("Fensterbank"),
	TERRASSE("Terrasse"),
	GEWAECHSHAUS("Gewächshaus"),
	WINTERGARTEN("Wintergarten");
	
	private String beschreibung;

	private Standort(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public String getBeschreibung() {
		return beschreibung;
	}
	
	public String toString() {
		return beschreibung;
	}
	
	// Methode für Enum rückwandlung -> sonst IllegalArgumentException
		public static Standort fromBeschreibung(String beschreibung) {
			for (Standort m : Standort.values()) {
				if (m.getBeschreibung().equalsIgnoreCase(beschreibung.trim())) {
					return m;
				}
			}
			return null;
		}
}
