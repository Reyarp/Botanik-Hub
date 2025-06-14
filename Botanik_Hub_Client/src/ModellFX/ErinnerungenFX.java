package ModellFX;

import java.time.LocalDate;

import Enum.Erinnerungstyp;
import Enum.Intervall;
import Modell.Benutzer;
import Modell.Erinnerungen;
import Modell.Pflanze;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ErinnerungenFX {

	private Erinnerungen appErinnerung;
	private SimpleObjectProperty<LocalDate> datum;
	private SimpleObjectProperty<Erinnerungstyp> typ;
	private SimpleObjectProperty<Intervall> intervall;
	private SimpleObjectProperty<Benutzer> benutzer;
	private SimpleIntegerProperty erinnerungsID;
	private SimpleObjectProperty<Pflanze> pflanze;
	
	public ErinnerungenFX(Erinnerungen appErinnerung) {
		this.appErinnerung = appErinnerung;
		datum = new SimpleObjectProperty<>(appErinnerung.getDatum());
		typ = new SimpleObjectProperty<>(appErinnerung.getTyp());
		intervall = new SimpleObjectProperty<>(appErinnerung.getIntervall());
		benutzer = new SimpleObjectProperty<>(appErinnerung.getBenutzer());
		erinnerungsID = new SimpleIntegerProperty(appErinnerung.getErinnerungID());
		pflanze = new SimpleObjectProperty<>(appErinnerung.getPflanze());
	}

	public Erinnerungen getAppErinnerung() {
		return appErinnerung;
	}

	public final SimpleObjectProperty<LocalDate> datumProperty() {
		return this.datum;
	}
	
	public final LocalDate getDatum() {
		return this.datumProperty().get();
	}
	
	public final void setDatum(final LocalDate datum) {
		this.datumProperty().set(datum);
		appErinnerung.setDatum(datum);
	}
	
	public final SimpleObjectProperty<Erinnerungstyp> typProperty() {
		return this.typ;
	}
	
	public final Erinnerungstyp getTyp() {
		return this.typProperty().get();
	}
	
	public final void setTyp(final Erinnerungstyp typ) {
		this.typProperty().set(typ);
		appErinnerung.setTyp(typ);
	}
	
	public final SimpleObjectProperty<Intervall> intervallProperty() {
		return this.intervall;
	}
	
	public final Intervall getIntervall() {
		return this.intervallProperty().get();
	}
	
	public final void setIntervall(final Intervall intervall) {
		this.intervallProperty().set(intervall);
		appErinnerung.setIntervall(intervall);
	}
	
	public final SimpleObjectProperty<Benutzer> benutzerProperty() {
		return this.benutzer;
	}
	
	public final Benutzer getBenutzer() {
		return this.benutzerProperty().get();
	}

	public final void setBenutzer(final Benutzer benutzer) {
		this.benutzerProperty().set(benutzer);
		appErinnerung.setBenutzer(benutzer);
	}
	
	public final SimpleIntegerProperty erinnerungsIDProperty() {
		return this.erinnerungsID;
	}
	
	public final int getErinnerungsID() {
		return this.erinnerungsIDProperty().get();
	}
	
	public final void setErinnerungsID(final int erinnerungsID) {
		this.erinnerungsIDProperty().set(erinnerungsID);
		appErinnerung.setErinnerungID(erinnerungsID);
	}

	public final SimpleObjectProperty<Pflanze> pflanzeProperty() {
		return this.pflanze;
	}
	
	public final Pflanze getPflanze() {
		return this.pflanzeProperty().get();
	}
	
	public final void setPflanze(final Pflanze pflanze) {
		this.pflanzeProperty().set(pflanze);
	}
	
	public String getPflanzenName() {
		return appErinnerung.getPflanze().getPflanzenName();
	}
}
