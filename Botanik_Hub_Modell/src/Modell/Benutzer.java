package Modell;

import java.time.LocalDate;

import Enum.BenutzerTyp;

public class Benutzer {

	private String benutzerName;
	private String passwort;
	private int benutzerId;
	private BenutzerTyp typ;
	private LocalDate registriertSeit;

	public Benutzer() {}

	public Benutzer(String benutzerName, String passwort, int benutzerId, BenutzerTyp typ, LocalDate registriertSeit) {
		super();
		this.benutzerName = benutzerName;
		this.passwort = passwort;
		this.benutzerId = benutzerId;
		this.typ = typ;
		this.registriertSeit = LocalDate.now();	// Aktuelles Datum, später für die Tableview
	}

	public String getBenutzerName() {
		return benutzerName;
	}

	public void setBenutzerName(String benutzerName) {
		this.benutzerName = benutzerName;
	}

	public String getPasswort() {
		return passwort;
	}

	public void setPasswort(String passwort) {
		this.passwort = passwort;
	}

	public int getBenutzerId() {
		return benutzerId;
	}

	public void setBenutzerId(int benutzerId) {
		this.benutzerId = benutzerId;
	}

	public BenutzerTyp getTyp() {
		return typ;
	}

	public void setTyp(BenutzerTyp typ) {
		this.typ = typ;
	}

	public LocalDate getRegistriertSeit() {
		return registriertSeit;
	}

	public void setRegistriertSeit(LocalDate registriertSeit) {
		this.registriertSeit = registriertSeit;
	}
}
