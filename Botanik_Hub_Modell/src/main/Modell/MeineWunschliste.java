package Modell;

public class MeineWunschliste {

	private Benutzer benutzer;
	private Pflanze pflanze;
	
	public MeineWunschliste() {}
	
	public MeineWunschliste(Benutzer benutzer, Pflanze pflanze) {
		super();
		this.benutzer = benutzer;
		this.pflanze = pflanze;
	}

	public Benutzer getBenutzer() {
		return benutzer;
	}

	public void setBenutzer(Benutzer benutzer) {
		this.benutzer = benutzer;
	}

	public Pflanze getPflanze() {
		return pflanze;
	}

	public void setPflanze(Pflanze pflanze) {
		this.pflanze = pflanze;
	}
}
