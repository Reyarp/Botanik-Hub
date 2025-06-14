package ModellFX;

import java.util.ArrayList;
import Enum.Intervall;
import Enum.Lebensdauer;
import Enum.Lichtbedarf;
import Enum.Pflanzentyp;
import Enum.Standort;
import Enum.Vertraeglichkeit;
import Enum.Wasserbedarf;
import Modell.Benutzer;
import Modell.Pflanze;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class PflanzeFX {

	private Pflanze appPflanze;

	private SimpleStringProperty pflanzenName;
	private SimpleStringProperty botanikName;
	private SimpleStringProperty bildPfad;
	private SimpleIntegerProperty pflanzenID;
	private SimpleBooleanProperty giftig;
	private SimpleDoubleProperty wuchsbreite;
	private SimpleDoubleProperty wuchshoehe;
	private SimpleStringProperty notiz;

	private SimpleObjectProperty<Wasserbedarf> wasserbedarf;
	private SimpleObjectProperty<Lichtbedarf> lichtbedarf;
	private SimpleObjectProperty<Intervall> duengung;
	private SimpleObjectProperty<Vertraeglichkeit> vertraeglichkeit;
	private SimpleObjectProperty<Standort> standort;
	private SimpleObjectProperty<Lebensdauer> lebensdauer;
	private SimpleObjectProperty<Benutzer> benutzer;

	public PflanzeFX() {}

	public PflanzeFX(Pflanze appPflanze) {
		this.appPflanze = appPflanze;
		pflanzenName = new SimpleStringProperty(appPflanze.getPflanzenName());
		botanikName = new SimpleStringProperty(appPflanze.getBotanikName());
		bildPfad = new SimpleStringProperty(appPflanze.getBildPfad());
		pflanzenID = new SimpleIntegerProperty(appPflanze.getPflanzenID());
		giftig = new SimpleBooleanProperty(appPflanze.isGiftig());
		wuchsbreite = new SimpleDoubleProperty(appPflanze.getWuchsbreite());
		wuchshoehe = new SimpleDoubleProperty(appPflanze.getWuchshoehe());
		notiz = new SimpleStringProperty(appPflanze.getNotiz());
		wasserbedarf = new SimpleObjectProperty<>(appPflanze.getWasserbedarf());
		lichtbedarf = new SimpleObjectProperty<>(appPflanze.getLichtbedarf());
		duengung = new SimpleObjectProperty<>(appPflanze.getDuengung());
		vertraeglichkeit = new SimpleObjectProperty<>(appPflanze.getVertraeglichkeit());
		standort = new SimpleObjectProperty<>(appPflanze.getStandort());
		lebensdauer = new SimpleObjectProperty<>(appPflanze.getLebensdauer());
		benutzer = new SimpleObjectProperty<>(appPflanze.getBenutzer());
	}

	public Pflanze getAppPflanze() {
		return appPflanze;
	}

	public SimpleStringProperty pflanzenNameProperty() {
		return this.pflanzenName;
	}

	public String getPflanzenName() {
		return this.pflanzenName.get();
	}

	public void setPflanzenName(String pflanzenName) {
		this.pflanzenName.set(pflanzenName);
		appPflanze.setPflanzenName(pflanzenName);
	}

	public SimpleStringProperty botanikNameProperty() {
		return this.botanikName;
	}

	public String getBotanikName() {
		return this.botanikName.get();
	}

	public void setBotanikName(String botanikName) {
		this.botanikName.set(botanikName);
		appPflanze.setBotanikName(botanikName);
	}

	public SimpleStringProperty bildPfadProperty() {
		return this.bildPfad;
	}

	public String getBildPfad() {
		return this.bildPfad.get();
	}

	public void setBildPfad(String bildPfad) {
		this.bildPfad.set(bildPfad);
		appPflanze.setBildPfad(bildPfad);
	}

	public SimpleIntegerProperty pflanzenIDProperty() {
		return this.pflanzenID;
	}

	public int getPflanzenID() {
		return this.pflanzenID.get();
	}

	public void setPflanzenID(int pflanzenID) {
		this.pflanzenID.set(pflanzenID);
		appPflanze.setPflanzenID(pflanzenID);
	}

	public SimpleBooleanProperty giftigProperty() {
		return this.giftig;
	}

	public boolean isGiftig() {
		return this.giftig.get();
	}

	public void setGiftig(boolean giftig) {
		this.giftig.set(giftig);
		appPflanze.setGiftig(giftig);
	}

	public SimpleDoubleProperty wuchsbreiteProperty() {
		return this.wuchsbreite;
	}

	public double getWuchsbreite() {
		return this.wuchsbreite.get();
	}

	public void setWuchsbreite(double wuchsbreite) {
		this.wuchsbreite.set(wuchsbreite);
		appPflanze.setWuchsbreite(wuchsbreite);
	}

	public SimpleDoubleProperty wuchshoeheProperty() {
		return this.wuchshoehe;
	}

	public double getWuchshoehe() {
		return this.wuchshoehe.get();
	}

	public void setWuchshoehe(double wuchshoehe) {
		this.wuchshoehe.set(wuchshoehe);
		appPflanze.setWuchshoehe(wuchshoehe);
	}

	public SimpleStringProperty notizProperty() {
		return this.notiz;
	}

	public String getNotiz() {
		return this.notiz.get();
	}

	public void setNotiz(String notiz) {
		this.notiz.set(notiz);
		appPflanze.setNotiz(notiz);
	}

	public SimpleObjectProperty<Wasserbedarf> wasserbedarfProperty() {
		return this.wasserbedarf;
	}

	public Wasserbedarf getWasserbedarf() {
		return this.wasserbedarf.get();
	}

	public void setWasserbedarf(Wasserbedarf wasserbedarf) {
		this.wasserbedarf.set(wasserbedarf);
		appPflanze.setWasserbedarf(wasserbedarf);
	}

	public SimpleObjectProperty<Lichtbedarf> lichtbedarfProperty() {
		return this.lichtbedarf;
	}

	public Lichtbedarf getLichtbedarf() {
		return this.lichtbedarf.get();
	}

	public void setLichtbedarf(Lichtbedarf lichtbedarf) {
		this.lichtbedarf.set(lichtbedarf);
		appPflanze.setLichtbedarf(lichtbedarf);
	}

	public SimpleObjectProperty<Intervall> duengungProperty() {
		return this.duengung;
	}

	public Intervall getDuengung() {
		return this.duengung.get();
	}

	public void setDuengung(Intervall duengung) {
		this.duengung.set(duengung);
		appPflanze.setDuengung(duengung);
	}

	public SimpleObjectProperty<Vertraeglichkeit> vertraeglichkeitProperty() {
		return this.vertraeglichkeit;
	}

	public Vertraeglichkeit getVertraeglichkeit() {
		return this.vertraeglichkeit.get();
	}

	public void setVertraeglichkeit(Vertraeglichkeit vertraeglichkeit) {
		this.vertraeglichkeit.set(vertraeglichkeit);
		appPflanze.setVertraeglichkeit(vertraeglichkeit);
	}

	public SimpleObjectProperty<Standort> standortProperty() {
		return this.standort;
	}

	public Standort getStandort() {
		return this.standort.get();
	}

	public void setStandort(Standort standort) {
		this.standort.set(standort);
		appPflanze.setStandort(standort);
	}

	public SimpleObjectProperty<Lebensdauer> lebensdauerProperty() {
		return this.lebensdauer;
	}

	public Lebensdauer getLebensdauer() {
		return this.lebensdauer.get();
	}

	public void setLebensdauer(Lebensdauer lebensdauer) {
		this.lebensdauer.set(lebensdauer);
		appPflanze.setLebensdauer(lebensdauer);
	}

	public final SimpleObjectProperty<Benutzer> benutzerProperty() {
		return this.benutzer;
	}

	public final Benutzer getBenutzer() {
		return this.benutzerProperty().get();
	}

	public final void setBenutzer(final Benutzer benutzer) {
		this.benutzerProperty().set(benutzer);
	}

	/* Eigene getPflanzentyp methode für die Tableview
	 * Falls mehrere Pflanzentypen gewählt wurden dann wird der text mit ... erweitert
	 */
	public String getPflanzenTyp() {
		ArrayList<Pflanzentyp> typen = appPflanze.getPflanzenTyp();
		if (typen == null || typen.isEmpty()) return "";

		String first = typen.get(0).getBeschreibung();
		if (typen.size() > 1) {
			return first + " …";
		} else {
			return first;
		}
	}
	
	/*
	 *  Eigene getBenutzerName methode für die Tableview -> Dynamische anzeige
	 */
	public String getBenutzerName() {
		return appPflanze.getBenutzer().getBenutzerId() == 1 ? "Admin" : appPflanze.getBenutzer().getBenutzerName();
		
	}

	@Override
	public String toString() {
		return appPflanze.getPflanzenName();
	}
	
}

