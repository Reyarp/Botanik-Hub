package GUI.HauptDialoge.PflanzenDialoge.SubDialoge;

import Enum.Standort;
import GUI.BotanikHub_Client;
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

		/*--------------------------------------------
		 * Dialog-Buttons erstellen & stylen
		 *-------------------------------------------- */
		ButtonType anwenden = new ButtonType("Anwenden", ButtonData.OK_DONE);
		ButtonType abbrechen = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
		this.getDialogPane().getButtonTypes().addAll(anwenden, abbrechen);

		Button apply = (Button) this.getDialogPane().lookupButton(anwenden);
		Button cancel = (Button) this.getDialogPane().lookupButton(abbrechen);
		apply.getStyleClass().add("dialog-button-ok");
		cancel.getStyleClass().add("dialog-button-cancel");

		/*--------------------------------------------
		 * Headerbild einbinden
		 *-------------------------------------------- */
		ImageView header = new ImageView(new Image(BotanikHub_Client.class.getResource("/standort.jpg").toString()));
		header.setFitWidth(200);
		header.setPreserveRatio(true);

		/*--------------------------------------------
		 * GridPane: Standortauswahl mit RadioButtons
		 *-------------------------------------------- */
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(15);

		Standort[] std = Standort.values();
		RadioButton[] buttons = new RadioButton[std.length];
		ToggleGroup typen = new ToggleGroup();

		for (int i = 0; i < std.length; i++) {
			Label lbl = new Label(std[i].toString());
			buttons[i] = new RadioButton();
			buttons[i].setToggleGroup(typen);
			grid.add(lbl, 1, i);
			grid.add(buttons[i], 11, i);
		}

		/*--------------------------------------------
		 * Vorauswahl setzen (z. B. beim Bearbeiten)
		 *-------------------------------------------- */
		Standort auswahl = p.getAppPflanze().getStandort();
		if (auswahl != null) {
			for (int i = 0; i < std.length; i++) {
				if (auswahl == Standort.fromBeschreibung(std[i].toString())) {
					buttons[i].setSelected(true);
				}
			}
		} else {
			buttons[0].setSelected(true); // Fallback
		}

		/*--------------------------------------------
		 * Auswahl speichern (ResultConverter)
		 *-------------------------------------------- */
		this.setResultConverter(new Callback<>() {
			@Override
			public ButtonType call(ButtonType arg0) {
				if (arg0 == anwenden) {
					for (int i = 0; i < buttons.length; i++) {
						if (buttons[i].isSelected()) {
							p.getAppPflanze().setStandort(Standort.fromBeschreibung(std[i].toString()));
						}
					}
				}
				return anwenden;
			}
		});

		/*--------------------------------------------
		 * Layoutaufbau
		 *-------------------------------------------- */
		HBox banner = new HBox(header);
		banner.setPadding(new Insets(5));

		VBox vb = new VBox(grid);
		vb.setPadding(new Insets(5));

		VBox vb1 = new VBox(banner, vb);
		vb1.setPadding(new Insets(5));

		/*--------------------------------------------
		 * Dialogeigenschaften setzen
		 *-------------------------------------------- */
		this.setTitle("Bitte Standort auswählen");
		this.getDialogPane().setContent(vb1);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("dialog-layout");
	}
}
