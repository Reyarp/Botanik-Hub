package ModellFX;

import Modell.Benutzer;
import Modell.Pflanze;
import Modell.PflanzenEntdecken;
import javafx.beans.property.SimpleObjectProperty;

public class PflanzeEntdeckenFX {
	
	private PflanzenEntdecken appEntdecken;
	private SimpleObjectProperty<Benutzer> benutzer;
	private SimpleObjectProperty<Pflanze> pflanze;
	
	public PflanzeEntdeckenFX(PflanzenEntdecken appEntdecken) {
		this.appEntdecken = appEntdecken;
		benutzer = new SimpleObjectProperty<>(appEntdecken.getBenutzer());
		pflanze = new SimpleObjectProperty<>(appEntdecken.getPflanze());
	}

	public PflanzenEntdecken getAppHub() {
		return appEntdecken;
	}

	public final SimpleObjectProperty<Benutzer> benutzerProperty() {
		return this.benutzer;
	}
	
	public final Benutzer getBenutzer() {
		return this.benutzerProperty().get();
	}
	
	public final void setBenutzer(final Benutzer benutzer) {
		this.benutzerProperty().set(benutzer);
		appEntdecken.setBenutzer(benutzer);
	}
	
	public final SimpleObjectProperty<Pflanze> pflanzeProperty() {
		return this.pflanze;
	}
	
	public final Pflanze getPflanze() {
		return this.pflanzeProperty().get();
	}
	
	public final void setPflanze(final Pflanze pflanze) {
		this.pflanzeProperty().set(pflanze);
		appEntdecken.setPflanze(pflanze);
	}
}
