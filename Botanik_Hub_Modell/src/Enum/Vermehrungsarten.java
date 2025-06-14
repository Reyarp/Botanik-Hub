package Enum;

public enum Vermehrungsarten {
	STECKLING("Steckling"),
	TEILUNG("Teilung"),
	ABLEGER("Ableger"),
	WURZELAUSLAEUFER("Wurzelausläufer"),
	KNOLLEN("Knollen"),
	VEREDLUNG("Veredlung"),
	ZIEHBULBEN("Ziehbulben"),
	AUSSAAT("Aussaat");

	private String beschreibung;

	private Vermehrungsarten(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public String toString() {
		return beschreibung;
	}
	
	// Methode für Enum rückwandlung -> sonst IllegalArgumentException
	public static Vermehrungsarten fromBeschreibung(String beschreibung) {
		for (Vermehrungsarten m : Vermehrungsarten.values()) {
			if (m.getBeschreibung().equalsIgnoreCase(beschreibung.trim())) {
				return m;
			}
		}
		return null;
	}
}
