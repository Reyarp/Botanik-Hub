package GUI.HauptDialoge.PflanzenDialoge.SubDialoge;

import Client.BotanikHub_Client;
import Enum.Standort;
import ModellFX.PflanzeFX;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class Pflanze_Standort_Dialog extends Dialog<ButtonType> {

	public Pflanze_Standort_Dialog(PflanzeFX p) {

		/*
		 * Subdialog für Standort auswahl
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
		ImageView header = new ImageView(new Image(BotanikHub_Client.class.getResource("/standort.jpg").toString()));
		header.setFitWidth(200);
		header.setPreserveRatio(true);

		// Enum zur dynamischen Erstellung der Checkboxen (wartungsfreundlich)
		Standort[] std = Standort.values();
		// RadioButtons befüllt mit EnumTyp
		RadioButton[] buttons = new RadioButton[std.length];
		ToggleGroup typen = new ToggleGroup();

		// Layout: GridPane
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(15);

		// Labels & Buttons: anlegen & befüllen
		for (int i = 0; i < std.length; i++) {
			Label lbl = new Label(std[i].toString());
			buttons[i] = new RadioButton();
			buttons[i].setToggleGroup(typen);
			
			grid.add(lbl, 1, i);
			grid.add(buttons[i], 11, i);
			buttons[0].setSelected(true);
		}

		// Vorauswahl über setSelected machen -> beim erneuten Dialog öffnen
		Standort auswahl = p.getAppPflanze().getStandort();
		if (auswahl != null) {
			for (int i = 0; i < std.length; i++) {
				if (auswahl == Standort.fromBeschreibung(std[i].toString())) {
					buttons[i].setSelected(true);
				}
			}
		}

		// Resultconver: zum Speichern
		this.setResultConverter(new Callback<>() {
			@Override
			public ButtonType call(ButtonType arg0) {
				if (arg0 == anwenden) {
					for (int i = 0; i < buttons.length; i++) {
						if (buttons[i].isSelected()) {
							// ins Modell speichern
							p.getAppPflanze().setStandort(Standort.fromBeschreibung(std[i].toString()));
						}
					}
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

		this.setTitle("Bitte Standort auswählen");
		this.getDialogPane().setContent(vb1);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("dialog-layout");
	}
}
