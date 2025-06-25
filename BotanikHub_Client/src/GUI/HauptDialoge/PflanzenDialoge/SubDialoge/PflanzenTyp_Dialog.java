package GUI.HauptDialoge.PflanzenDialoge.SubDialoge;

import java.util.ArrayList;

import Client.BotanikHub_Client;
import Enum.Pflanzentyp;
import ModellFX.PflanzeFX;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class PflanzenTyp_Dialog extends Dialog<ButtonType> {

	// Liste zur Speicherung der Pflanzentypen
	private ArrayList<Pflanzentyp> alTyp = new ArrayList<>();

	public PflanzenTyp_Dialog(PflanzeFX p) {

		/*
		 * Subdialog für Pflanzentyp auswahl
		 */

		// Buttons & Co
		ButtonType anwenden = new ButtonType("Anwenden", ButtonData.OK_DONE);
		ButtonType abbrechen = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
		this.getDialogPane().getButtonTypes().addAll(anwenden, abbrechen);

		Button apply = (Button) this.getDialogPane().lookupButton(anwenden);
		Button cancel = (Button) this.getDialogPane().lookupButton(abbrechen);
		apply.getStyleClass().add("dialog-button-ok");
		cancel.getStyleClass().add("dialog-button-cancel");

		// Headerbild
		ImageView header = new ImageView(new Image(BotanikHub_Client.class.getResource("/Pflanzentyp_Dialog_Headerbild.jpg").toString()));
		header.setFitWidth(220);
		header.setPreserveRatio(true);

		// Enum zur dynamischen Erstellung der Checkboxen (wartungsfreundlich)
		Pflanzentyp[] typ = Pflanzentyp.values();
		// Checkboxen befüllt mit EnumTyp
		CheckBox[] buttons = new CheckBox[typ.length];

		// Layout: GridPane
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(15);

		// Labels & Buttons: anlegen & befüllen
		for (int i = 0; i < typ.length; i++) {
			Label lbl = new Label(typ[i].toString());
			buttons[i] = new CheckBox();

			grid.add(lbl, 1, i);
			grid.add(buttons[i], 11, i);
		}

		// Vorauswahl über setSelected machen -> beim erneuten Dialog öffnen
		ArrayList<Pflanzentyp> vorhandeneTypen = p.getAppPflanze().getPflanzenTyp();
		if (vorhandeneTypen != null) {
			for (int i = 0; i < typ.length; i++) {
				Pflanzentyp typEnum = Pflanzentyp.fromBeschreibung(typ[i].toString());
				if (vorhandeneTypen.contains(typEnum)) {
					buttons[i].setSelected(true);
				}
			}
		}

		// Resultconverter: zum Speichern
		this.setResultConverter(new Callback<>() {
			@Override
			public ButtonType call(ButtonType arg0) {
				if (arg0 == anwenden) {
					alTyp.clear();
					// Selektierte Pflanzentypen sammeln und zur Arraylist hinzufügen
					for (int i = 0; i < typ.length; i++) {
						if (buttons[i].isSelected()) {
							// ArrayList mit pflanzentypen befüllen
							alTyp.add(Pflanzentyp.fromBeschreibung(typ[i].toString()));
						}
					}
					// ins Modell speichern
					p.getAppPflanze().setPflanzenTyp(alTyp);
				}
				return anwenden;
			}
		});

		// Zusammenbau & Dialogeinstellungen
		HBox banner = new HBox(header);
		banner.setPadding(new Insets(5));

		VBox vb = new VBox(grid);
		vb.setPadding(new Insets(5));

		VBox vb1 = new VBox(banner, vb);
		vb1.setPadding(new Insets(5));

		this.setTitle("Bitte Typ auswählen");
		this.getDialogPane().setContent(vb1);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("dialog-layout");
		// Stage holen zum Icon setzen, da ich direkt im Dialog keins setzen kann
		Stage arg1 = (Stage) this.getDialogPane().getScene().getWindow();
		arg1.getIcons().add(new Image(BotanikHub_Client.class.getResource("/Window_Icon_Lebensbaum.jpg").toString()));
	}


	// Getter für ausgewählte Pflanzentypen
	public ArrayList<Pflanzentyp> getAlTyp() {
		return alTyp;
	}
}
