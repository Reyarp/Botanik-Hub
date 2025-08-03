package ModellFX;


import java.time.LocalDate;

import Enum.BenutzerTyp;
import Modell.Benutzer;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class BenutzerFX {

	private Benutzer appBenutzer;
	private SimpleStringProperty benutzerName;
	private SimpleStringProperty passwort;
	private SimpleIntegerProperty benutzerID;
	private SimpleObjectProperty<BenutzerTyp> typ;
	private SimpleObjectProperty<LocalDate> registriertSeit;
	
	public BenutzerFX(Benutzer appBenutzer) {
		this.appBenutzer = appBenutzer;
		benutzerName = new SimpleStringProperty(appBenutzer.getBenutzerName());
		passwort = new SimpleStringProperty(appBenutzer.getPasswort());
		benutzerID = new SimpleIntegerProperty(appBenutzer.getBenutzerId());
		typ = new SimpleObjectProperty<>(appBenutzer.getTyp());
		registriertSeit = new SimpleObjectProperty<>(appBenutzer.getRegistriertSeit());
	}

	public Benutzer getAppBenutzer() {
		return appBenutzer;
	}

	public final SimpleStringProperty benutzerNameProperty() {
		return this.benutzerName;
	}
	
	public final String getBenutzerName() {
		return this.benutzerNameProperty().get();
	}
	
	public final void setBenutzerName(final String benutzerName) {
		this.benutzerNameProperty().set(benutzerName);
		appBenutzer.setBenutzerName(benutzerName);
	}
	
	public final SimpleStringProperty passwortProperty() {
		return this.passwort;
	}
	
	public final String getPasswort() {
		return this.passwortProperty().get();
	}
	
	public final void setPasswort(final String passwort) {
		this.passwortProperty().set(passwort);
		appBenutzer.setPasswort(passwort);
	}
	
	public final SimpleIntegerProperty benutzerIDProperty() {
		return this.benutzerID;
	}
	
	public final int getBenutzerID() {
		return this.benutzerIDProperty().get();
	}
	
	public final void setBenutzerID(final int benutzerID) {
		this.benutzerIDProperty().set(benutzerID);
		appBenutzer.setBenutzerId(benutzerID);
	}
	
	public final SimpleObjectProperty<BenutzerTyp> typProperty() {
		return this.typ;
	}
	
	public final BenutzerTyp getTyp() {
		return this.typProperty().get();
	}
	
	public final void setTyp(final BenutzerTyp typ) {
		this.typProperty().set(typ);
		appBenutzer.setTyp(typ);
	}

	public final SimpleObjectProperty<LocalDate> registriertSeitProperty() {
		return this.registriertSeit;
	}
	
	public final LocalDate getRegistriertSeit() {
		return this.registriertSeitProperty().get();
	}
	
	public final void setRegistriertSeit(final LocalDate registriertSeit) {
		this.registriertSeitProperty().set(registriertSeit);
		appBenutzer.setRegistriertSeit(registriertSeit);
	}
}
