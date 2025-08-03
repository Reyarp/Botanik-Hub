package Modell;

import java.time.LocalDate;

import Enum.Erinnerungstyp;
import Enum.Intervall;
import jakarta.json.bind.annotation.JsonbDateFormat;

public class Erinnerungen {

	
	@JsonbDateFormat("yyyy-MM-dd")
	private LocalDate datum;
	
	private Erinnerungstyp typ;
	private Intervall intervall;
	private Benutzer benutzer;
	private int erinnerungID;
	private Pflanze pflanze;
	
	public Erinnerungen() {}

	public Erinnerungen(LocalDate datum, Erinnerungstyp typ, Intervall intervall, Benutzer benutzer, int erinnerungID, Pflanze pflanze) {
		super();
		this.datum = datum;
		this.typ = typ;
		this.intervall = intervall;
		this.benutzer = benutzer;
		this.erinnerungID = erinnerungID;
		this.pflanze = pflanze;
	}

	public LocalDate getDatum() {
		return datum;
	}

	public void setDatum(LocalDate datum) {
		this.datum = datum;
	}

	public Erinnerungstyp getTyp() {
		return typ;
	}

	public void setTyp(Erinnerungstyp typ) {
		this.typ = typ;
	}

	public Intervall getIntervall() {
		return intervall;
	}

	public void setIntervall(Intervall intervall) {
		this.intervall = intervall;
	}

	public Benutzer getBenutzer() {
		return benutzer;
	}

	public void setBenutzer(Benutzer benutzer) {
		this.benutzer = benutzer;
	}

	public int getErinnerungID() {
		return erinnerungID;
	}

	public void setErinnerungID(int erinnerungID) {
		this.erinnerungID = erinnerungID;
	}

	public Pflanze getPflanze() {
		return pflanze;
	}

	public void setPflanze(Pflanze pflanze) {
		this.pflanze = pflanze;
	}
}
