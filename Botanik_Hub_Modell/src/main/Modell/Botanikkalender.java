package Modell;

import java.util.ArrayList;

import Enum.Kalendertyp;
import Enum.Month;

public class Botanikkalender {

	private ArrayList<Month> monat = new ArrayList<>();
	private Kalendertyp kalendertyp;
	private int	kalenderID;
	
	public Botanikkalender() {}
	
	public Botanikkalender(Kalendertyp typ) {
		this.kalendertyp = typ;
	}
	
	public Botanikkalender(ArrayList<Month> monat, Kalendertyp kalendertyp, int kalenderID) {
		super();
		this.monat = monat;
		this.kalendertyp = kalendertyp;
		this.kalenderID = kalenderID;
	}

	public ArrayList<Month> getMonat() {
		return monat;
	}

	public void setMonat(ArrayList<Month> monat) {
		this.monat = monat;
	}

	public Kalendertyp getKalendertyp() {
		return kalendertyp;
	}

	public void setKalendertyp(Kalendertyp kalendertyp) {
		this.kalendertyp = kalendertyp;
	}

	public int getKalenderID() {
		return kalenderID;
	}

	public void setKalenderID(int kalenderID) {
		this.kalenderID = kalenderID;
	}
}
