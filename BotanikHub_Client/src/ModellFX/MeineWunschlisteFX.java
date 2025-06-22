package ModellFX;

import java.util.ArrayList;
import Enum.Pflanzentyp;
import Modell.Benutzer;
import Modell.MeineWunschliste;
import Modell.Pflanze;
import javafx.beans.property.SimpleObjectProperty;

public class MeineWunschlisteFX {

	private MeineWunschliste appWunsch;
	private SimpleObjectProperty<Benutzer> benutzer;
	private SimpleObjectProperty<Pflanze> pflanze;
	
	public MeineWunschlisteFX(MeineWunschliste appWunsch) {
		this.appWunsch = appWunsch;
		benutzer = new SimpleObjectProperty<>(appWunsch.getBenutzer());
		pflanze = new SimpleObjectProperty<>(appWunsch.getPflanze());
	}

	public MeineWunschliste getAppWunsch() {
		return appWunsch;
	}

	public final SimpleObjectProperty<Benutzer> benutzerProperty() {
		return this.benutzer;
	}
	
	public final Benutzer getBenutzer() {
		return this.benutzerProperty().get();
	}
	
	public final void setBenutzer(final Benutzer benutzer) {
		this.benutzerProperty().set(benutzer);
		appWunsch.setBenutzer(benutzer);
	}
	
	public final SimpleObjectProperty<Pflanze> pflanzeProperty() {
		return this.pflanze;
	}
	
	public final Pflanze getPflanze() {
		return this.pflanzeProperty().get();
	}
	
	public final void setPflanze(final Pflanze pflanze) {
		this.pflanzeProperty().set(pflanze);
		appWunsch.setPflanze(pflanze);
	}
	
	public String getPflanzenName() {
		return appWunsch.getPflanze().getPflanzenName();
	}
	
	public int getPflanzenID() {
		return appWunsch.getPflanze().getPflanzenID();
	}
	
	public String getPflanzenTyp() {
		ArrayList<Pflanzentyp> typen = appWunsch.getPflanze().getPflanzenTyp();
		if (typen == null || typen.isEmpty()) return "";

		String first = typen.get(0).getBeschreibung();
		if (typen.size() > 1) {
			return first + " …";
		} else {
			return first;
		}
	}
}
