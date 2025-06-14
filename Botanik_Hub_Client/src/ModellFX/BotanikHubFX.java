package ModellFX;

import Modell.Benutzer;
import Modell.BotanikHub;
import Modell.Pflanze;
import javafx.beans.property.SimpleObjectProperty;

public class BotanikHubFX {

	private BotanikHub appHub;
	private SimpleObjectProperty<Benutzer> benutzer;
	private SimpleObjectProperty<Pflanze> pflanze;
	
	public BotanikHubFX(BotanikHub appHub) {
		this.appHub = appHub;
		benutzer = new SimpleObjectProperty<>(appHub.getBenutzer());
		pflanze = new SimpleObjectProperty<>(appHub.getPflanze());
	}

	public BotanikHub getAppHub() {
		return appHub;
	}

	public final SimpleObjectProperty<Benutzer> benutzerProperty() {
		return this.benutzer;
	}
	
	public final Benutzer getBenutzer() {
		return this.benutzerProperty().get();
	}
	
	public final void setBenutzer(final Benutzer benutzer) {
		this.benutzerProperty().set(benutzer);
		appHub.setBenutzer(benutzer);
	}
	
	public final SimpleObjectProperty<Pflanze> pflanzeProperty() {
		return this.pflanze;
	}
	
	public final Pflanze getPflanze() {
		return this.pflanzeProperty().get();
	}
	
	public final void setPflanze(final Pflanze pflanze) {
		this.pflanzeProperty().set(pflanze);
		appHub.setPflanze(pflanze);
	}
}
