package Modell;

import java.util.ArrayList;

import Enum.Intervall;
import Enum.Lebensdauer;
import Enum.Lichtbedarf;
import Enum.Month;
import Enum.Pflanzentyp;
import Enum.Standort;
import Enum.Vermehrungsarten;
import Enum.Vertraeglichkeit;
import Enum.VerwendeteTeile;
import Enum.Wasserbedarf;

public class Pflanze {

	private Benutzer benutzer;
	private String pflanzenName;
	private String botanikName;
	private String bildPfad;
	private int pflanzenID;
	private boolean giftig;
	private double wuchsbreite;
	private double wuchshoehe;
	private String notiz;
	
	private Wasserbedarf wasserbedarf;
	private Lichtbedarf lichtbedarf;
	private Intervall duengung;
	private Vertraeglichkeit vertraeglichkeit;
	private Standort standort;
	private Lebensdauer lebensdauer;
	
	private ArrayList<Vermehrungsarten> vermehrung = new ArrayList<>();
	private ArrayList<Erinnerungen> erinnerung = new ArrayList<>();
	private ArrayList<Botanikkalender> kalender = new ArrayList<>();
	private ArrayList<Month> monat = new ArrayList<>();
	private ArrayList<VerwendeteTeile> verwendeteTeile = new ArrayList<>();
	private ArrayList<Pflanzentyp> pflanzenTyp = new ArrayList<>();
	
	
	public Pflanze() {}

	public Pflanze(Benutzer benutzer) {
		super();
		this.benutzer = benutzer;
	}

	public Pflanze(int pflanzenID, String pflanzenName, ArrayList<Pflanzentyp> pflanzenTyp) {
		super();
		this.pflanzenID = pflanzenID;
		this.pflanzenName = pflanzenName;
		this.pflanzenTyp = pflanzenTyp;
	}

	// Datenbank Konstruktor
	public Pflanze(String pflanzenName, String botanikName, String bildPfad, int pflanzenID,
			boolean giftig, double wuchsbreite, double wuchshoehe, String notiz, Wasserbedarf wasserbedarf,
			Lichtbedarf lichtbedarf, Intervall duengung, Vertraeglichkeit vertraeglichkeit, Standort standort,
			Lebensdauer lebensdauer, ArrayList<Vermehrungsarten> vermehrung, ArrayList<Erinnerungen> erinnerung,
			ArrayList<Botanikkalender> kalender, ArrayList<Month> monat, ArrayList<VerwendeteTeile> verwendeteTeile,
			ArrayList<Pflanzentyp> pflanzenTyp, Benutzer benutzer) {
		super();
		this.pflanzenName = pflanzenName;
		this.botanikName = botanikName;
		this.bildPfad = bildPfad;
		this.pflanzenID = pflanzenID;
		this.giftig = giftig;
		this.wuchsbreite = wuchsbreite;
		this.wuchshoehe = wuchshoehe;
		this.notiz = notiz;
		this.wasserbedarf = wasserbedarf;
		this.lichtbedarf = lichtbedarf;
		this.duengung = duengung;
		this.vertraeglichkeit = vertraeglichkeit;
		this.standort = standort;
		this.lebensdauer = lebensdauer;
		this.vermehrung = vermehrung;
		this.erinnerung = erinnerung;
		this.kalender = kalender;
		this.monat = monat;
		this.verwendeteTeile = verwendeteTeile;
		this.pflanzenTyp = pflanzenTyp;
		this.benutzer = benutzer;
	}

	public Benutzer getBenutzer() {
		return benutzer;
	}

	public void setBenutzer(Benutzer benutzer) {
		this.benutzer = benutzer;
	}

	public String getPflanzenName() {
		return pflanzenName;
	}

	public void setPflanzenName(String pflanzenName) {
		this.pflanzenName = pflanzenName;
	}

	public String getBotanikName() {
		return botanikName;
	}

	public void setBotanikName(String botanikName) {
		this.botanikName = botanikName;
	}

	public String getBildPfad() {
		return bildPfad;
	}

	public void setBildPfad(String bildPfad) {
		this.bildPfad = bildPfad;
	}

	public int getPflanzenID() {
		return pflanzenID;
	}

	public void setPflanzenID(int pflanzenID) {
		this.pflanzenID = pflanzenID;
	}

	public boolean isGiftig() {
		return giftig;
	}

	public void setGiftig(boolean giftig) {
		this.giftig = giftig;
	}

	public double getWuchsbreite() {
		return wuchsbreite;
	}

	public void setWuchsbreite(double wuchsbreite) {
		this.wuchsbreite = wuchsbreite;
	}

	public double getWuchshoehe() {
		return wuchshoehe;
	}

	public void setWuchshoehe(double wuchshoehe) {
		this.wuchshoehe = wuchshoehe;
	}

	public String getNotiz() {
		return notiz;
	}

	public void setNotiz(String notiz) {
		this.notiz = notiz;
	}

	public Wasserbedarf getWasserbedarf() {
		return wasserbedarf;
	}

	public void setWasserbedarf(Wasserbedarf wasserbedarf) {
		this.wasserbedarf = wasserbedarf;
	}

	public Lichtbedarf getLichtbedarf() {
		return lichtbedarf;
	}

	public void setLichtbedarf(Lichtbedarf lichtbedarf) {
		this.lichtbedarf = lichtbedarf;
	}

	public Intervall getDuengung() {
		return duengung;
	}

	public void setDuengung(Intervall duengung) {
		this.duengung = duengung;
	}

	public Vertraeglichkeit getVertraeglichkeit() {
		return vertraeglichkeit;
	}

	public void setVertraeglichkeit(Vertraeglichkeit vertraeglichkeit) {
		this.vertraeglichkeit = vertraeglichkeit;
	}

	public Standort getStandort() {
		return standort;
	}

	public void setStandort(Standort standort) {
		this.standort = standort;
	}

	public Lebensdauer getLebensdauer() {
		return lebensdauer;
	}

	public void setLebensdauer(Lebensdauer lebensdauer) {
		this.lebensdauer = lebensdauer;
	}

	public ArrayList<Vermehrungsarten> getVermehrung() {
		return vermehrung;
	}

	public void setVermehrung(ArrayList<Vermehrungsarten> vermehrung) {
		this.vermehrung = vermehrung;
	}

	public ArrayList<Erinnerungen> getErinnerung() {
		return erinnerung;
	}

	public void setErinnerung(ArrayList<Erinnerungen> erinnerung) {
		this.erinnerung = erinnerung;
	}

	public ArrayList<Botanikkalender> getKalender() {
		return kalender;
	}

	public void setKalender(ArrayList<Botanikkalender> kalender) {
		this.kalender = kalender;
	}

	public ArrayList<Month> getMonat() {
		return monat;
	}

	public void setMonat(ArrayList<Month> monat) {
		this.monat = monat;
	}

	public ArrayList<VerwendeteTeile> getVerwendeteTeile() {
		return verwendeteTeile;
	}

	public void setVerwendeteTeile(ArrayList<VerwendeteTeile> verwendeteTeile) {
		this.verwendeteTeile = verwendeteTeile;
	}

	public ArrayList<Pflanzentyp> getPflanzenTyp() {
		return pflanzenTyp;
	}

	public void setPflanzenTyp(ArrayList<Pflanzentyp> pflanzenTyp) {
		this.pflanzenTyp = pflanzenTyp;
	}

	@Override
	public String toString() {
		return "Pflanze [benutzer=" + benutzer + ", pflanzenName=" + pflanzenName + ", botanikName=" + botanikName
				+ ", bildPfad=" + bildPfad + ", pflanzenID=" + pflanzenID + ", giftig=" + giftig + ", wuchsbreite="
				+ wuchsbreite + ", wuchshoehe=" + wuchshoehe + ", notiz=" + notiz + ", wasserbedarf=" + wasserbedarf
				+ ", lichtbedarf=" + lichtbedarf + ", duengung=" + duengung + ", vertraeglichkeit=" + vertraeglichkeit
				+ ", standort=" + standort + ", lebensdauer=" + lebensdauer + ", vermehrung=" + vermehrung
				+ ", erinnerung=" + erinnerung + ", kalender=" + kalender + ", monat=" + monat + ", verwendeteTeile="
				+ verwendeteTeile + ", pflanzenTyp=" + pflanzenTyp + "]";
	}
}
