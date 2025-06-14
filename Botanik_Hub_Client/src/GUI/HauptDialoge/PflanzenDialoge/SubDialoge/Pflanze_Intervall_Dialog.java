package GUI.HauptDialoge.PflanzenDialoge.SubDialoge;

import Enum.Intervall;
import GUI.BotanikHub_Client;
import ModellFX.ErinnerungenFX;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class Pflanze_Intervall_Dialog extends Dialog<ButtonType> {

	public Pflanze_Intervall_Dialog(ErinnerungenFX e) {

		/*--------------------------------------------
		 * Button-Typen & Styling
		 *-------------------------------------------- */
		ButtonType anwenden = new ButtonType("Anwenden", ButtonData.OK_DONE);
		ButtonType abbrechen = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
		this.getDialogPane().getButtonTypes().addAll(anwenden, abbrechen);

		Button apply = (Button) this.getDialogPane().lookupButton(anwenden);
		Button cancel = (Button) this.getDialogPane().lookupButton(abbrechen);
		apply.getStyleClass().add("dialog-button-ok");
		cancel.getStyleClass().add("dialog-button-cancel");

		/*--------------------------------------------
		 * Headerbild für Design
		 *-------------------------------------------- */
		ImageView header = new ImageView(new Image(BotanikHub_Client.class.getResource("/intervall.jpg").toString()));
		header.setFitWidth(200);
		header.setPreserveRatio(true);

		/*--------------------------------------------
		 * Intervall-Auswahl via Radiobuttons
		 *-------------------------------------------- */
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);

		Intervall[] inv = Intervall.values();
		RadioButton[] buttons = new RadioButton[inv.length];
		ToggleGroup ivl = new ToggleGroup();

		for (int i = 0; i < inv.length; i++) {
			Label lbl = new Label(inv[i].toString());
			buttons[i] = new RadioButton();
			buttons[i].setToggleGroup(ivl);

			grid.add(lbl, 1, i);
			grid.add(buttons[i], 11, i);

			buttons[0].setSelected(true); // Standardauswahl
		}

		/*--------------------------------------------
		 * Vorauswahl bei erneutem Öffnen
		 *-------------------------------------------- */
		Intervall auswahl = e.getAppErinnerung().getIntervall();
		if (auswahl != null) {
			for (int i = 0; i < inv.length; i++) {
				if (auswahl == Intervall.fromBeschreibung(inv[i].toString())) {
					buttons[i].setSelected(true);
				}
			}
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
							e.getAppErinnerung().setIntervall(Intervall.fromBeschreibung(inv[i].toString()));
						}
					}
				}
				return anwenden;
			}
		});

		/*--------------------------------------------
		 * Layoutaufbau (VBox/HBox)
		 *-------------------------------------------- */
		HBox banner = new HBox(header);
		banner.setPadding(new Insets(5));

		VBox vb = new VBox(grid);
		vb.setPadding(new Insets(5));

		VBox vb1 = new VBox(banner, vb);
		vb1.setPadding(new Insets(5));

		/*--------------------------------------------
		 * Dialogeigenschaften
		 *-------------------------------------------- */
		this.setTitle("Bitte Zeit auswählen");
		this.getDialogPane().setContent(vb1);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("dialog-layout");
	}
}
