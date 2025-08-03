package ModellFX;

import Enum.Kalendertyp;
import Modell.Botanikkalender;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class BotanikkalenderFX {

	private Botanikkalender appKalender;
	private SimpleObjectProperty<Kalendertyp> typ;
	private SimpleIntegerProperty kalenderID;
	
	public BotanikkalenderFX(Botanikkalender appKalender) {
		this.appKalender = appKalender;
		typ = new SimpleObjectProperty<>(appKalender.getKalendertyp());
		kalenderID = new SimpleIntegerProperty(appKalender.getKalenderID());
	}

	public Botanikkalender getAppKalender() {
		return appKalender;
	}
	
	public final SimpleObjectProperty<Kalendertyp> typProperty() {
		return this.typ;
	}
	
	public final Kalendertyp getTyp() {
		return this.typProperty().get();
	}
	
	public final void setTyp(final Kalendertyp typ) {
		this.typProperty().set(typ);
		appKalender.setKalendertyp(typ);
	}
	
	public final SimpleIntegerProperty kalenderIDProperty() {
		return this.kalenderID;
	}
	
	public final int getKalenderID() {
		return this.kalenderIDProperty().get();
	}
	
	public final void setKalenderID(final int kalenderID) {
		this.kalenderIDProperty().set(kalenderID);
		appKalender.setKalenderID(kalenderID);
	}
}
