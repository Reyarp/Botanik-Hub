package Enum;

public enum Erinnerungstyp {
	GIESSEN("Gießen"),
	PFLEGESCHNITT("Pflegeschnitt"),
	UMTOPFEN("Umtopfen"),
	AUSSAAT("Aussaat"),
	BLUETE("Blüte"),
	ERNTE("Ernte");
	
	private String beschreibung;

	private Erinnerungstyp(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public String getBeschreibung() {
		return beschreibung;
	}
	
	public String toString() {
		return beschreibung;
	}
	
	// Methode für Enum rückwandlung -> sonst IllegalArgumentException
	public static Erinnerungstyp fromBeschreibung(String beschreibung) {
	    for (Erinnerungstyp m : Erinnerungstyp.values()) {
	        if (m.getBeschreibung().equalsIgnoreCase(beschreibung.trim())) {
	            return m;
	        }
	    }
		return null;
	}
}
