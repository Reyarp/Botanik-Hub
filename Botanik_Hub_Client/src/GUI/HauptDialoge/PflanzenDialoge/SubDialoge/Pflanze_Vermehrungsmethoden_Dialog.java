package GUI.HauptDialoge.PflanzenDialoge.SubDialoge;

import java.util.ArrayList;

import Enum.Vermehrungsarten;
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

public class Pflanze_Vermehrungsmethoden_Dialog extends Dialog<ButtonType> {

	private ArrayList<Vermehrungsarten> alVermehrung = new ArrayList<>();

	public Pflanze_Vermehrungsmethoden_Dialog(PflanzeFX p) {

		/*--------------------------------------------
		 * Buttons & CSS Styling
		 *-------------------------------------------- */
		ButtonType anwenden = new ButtonType("Anwenden", ButtonData.OK_DONE);
		ButtonType abbrechen = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
		this.getDialogPane().getButtonTypes().addAll(anwenden, abbrechen);

		Button apply = (Button) this.getDialogPane().lookupButton(anwenden);
		Button cancel = (Button) this.getDialogPane().lookupButton(abbrechen);
		apply.getStyleClass().add("dialog-button-ok");
		cancel.getStyleClass().add("dialog-button-cancel");

		/*--------------------------------------------
		 * GridPane: Vermehrungsarten + CheckBoxen
		 *-------------------------------------------- */
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(15);

		Vermehrungsarten[] art = Vermehrungsarten.values();
		CheckBox[] buttons = new CheckBox[art.length];

		for (int i = 0; i < art.length; i++) {
			Label lbl = new Label(art[i].toString());
			buttons[i] = new CheckBox();
			grid.add(lbl, 1, i);
			grid.add(buttons[i], 8, i);
		}

		/*--------------------------------------------
		 * Vorauswahl aktivieren (z. B. beim Bearbeiten)
		 *-------------------------------------------- */
		ArrayList<Vermehrungsarten> auswahl = p.getAppPflanze().getVermehrung();
		if (auswahl != null) {
			for (int i = 0; i < art.length; i++) {
				Vermehrungsarten typEnum = Vermehrungsarten.fromBeschreibung(art[i].toString());
				if (auswahl.contains(typEnum)) {
					buttons[i].setSelected(true);
				}
			}
		}

		/*--------------------------------------------
		 * Auswahl speichern – ResultConverter
		 *-------------------------------------------- */
		this.setResultConverter(new Callback<>() {
			@Override
			public ButtonType call(ButtonType arg0) {
				alVermehrung.clear();
				if (arg0 == anwenden) {
					for (int i = 0; i < buttons.length; i++) {
						if (buttons[i].isSelected()) {
							alVermehrung.add(Vermehrungsarten.fromBeschreibung(art[i].toString()));
						}
					}
					p.getAppPflanze().setVermehrung(alVermehrung);
				}
				return anwenden;
			}
		});

		/*--------------------------------------------
		 * Headerbild
		 *-------------------------------------------- */
		ImageView header = new ImageView(new Image(BotanikHub_Client.class.getResource("/vermehrung.jpg").toString()));
		header.setFitWidth(200);
		header.setPreserveRatio(true);

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
		 * Dialogkonfiguration
		 *-------------------------------------------- */
		this.setTitle("Bitte Methode auswählen");
		this.getDialogPane().setContent(vb1);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("dialog-layout");
	}

	public ArrayList<Vermehrungsarten> getAlVermehrung() {
		return alVermehrung;
	}
}
