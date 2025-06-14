package Modell;


public class BotanikHub {

	private Benutzer benutzer;
	private Pflanze pflanze;
	private String notiz;
	
	public BotanikHub() {}
	
	public BotanikHub(Benutzer benutzer, Pflanze pflanze, String notiz) {
		super();
		this.benutzer = benutzer;
		this.pflanze = pflanze;
		this.notiz = notiz;
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

	public String getNotiz() {
		return notiz;
	}

	public void setNotiz(String notiz) {
		this.notiz = notiz;
	}
}
