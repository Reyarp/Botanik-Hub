package Modell;

import java.time.LocalDate;
import java.util.Objects;

import Enum.BenutzerTyp;
import jakarta.json.bind.annotation.JsonbDateFormat;

public class Benutzer {

	private String benutzerName;
	private String passwort;
	private int benutzerId;
	private BenutzerTyp typ;
	
	@JsonbDateFormat("yyyy-MM-dd") // Sonst erkennt JSON das format nicht -> Exception
	private LocalDate registriertSeit;

	public Benutzer() {}

	public Benutzer(String benutzerName, String passwort, int benutzerId, BenutzerTyp typ, LocalDate registriertSeit) {
		super();
		this.benutzerName = benutzerName;
		this.passwort = passwort;
		this.benutzerId = benutzerId;
		this.typ = typ;
		this.registriertSeit = registriertSeit;
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

	@Override
	public int hashCode() {
		return Objects.hash(benutzerId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Benutzer other = (Benutzer) obj;
		return benutzerId == other.benutzerId;
	}
}
