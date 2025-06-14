package GUI.HauptDialoge.PflanzenDialoge.SubDialoge;

import java.util.ArrayList;

import Enum.Pflanzentyp;
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

public class Pflanze_PflanzenTyp_Dialog extends Dialog<ButtonType> {

	private ArrayList<Pflanzentyp> alTyp = new ArrayList<>();

	public Pflanze_PflanzenTyp_Dialog(PflanzeFX p) {

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
		 * Headerbild
		 *-------------------------------------------- */
		ImageView header = new ImageView(new Image(BotanikHub_Client.class.getResource("/pflanzentyp.jpg").toString()));
		header.setFitWidth(220);
		header.setPreserveRatio(true);

		/*--------------------------------------------
		 * GridPane für Pflanzentypen-Checkboxen
		 *-------------------------------------------- */
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(15);

		Pflanzentyp[] typ = Pflanzentyp.values();
		CheckBox[] buttons = new CheckBox[typ.length];

		for (int i = 0; i < typ.length; i++) {
			Label lbl = new Label(typ[i].toString());
			buttons[i] = new CheckBox();

			grid.add(lbl, 1, i);
			grid.add(buttons[i], 11, i);
		}

		/*--------------------------------------------
		 * Vorauswahl (wenn Pflanze bereits Typen hat)
		 *-------------------------------------------- */
		ArrayList<Pflanzentyp> vorhandeneTypen = p.getAppPflanze().getPflanzenTyp();
		if (vorhandeneTypen != null) {
			for (int i = 0; i < typ.length; i++) {
				Pflanzentyp typEnum = Pflanzentyp.fromBeschreibung(typ[i].toString());
				if (vorhandeneTypen.contains(typEnum)) {
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
					alTyp.clear();
					for (int i = 0; i < typ.length; i++) {
						if (buttons[i].isSelected()) {
							alTyp.add(Pflanzentyp.fromBeschreibung(typ[i].toString()));
						}
					}
					p.getAppPflanze().setPflanzenTyp(alTyp);
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
		this.setTitle("Bitte Typ auswählen");
		this.getDialogPane().setContent(vb1);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("dialog-layout");
	}

	/*--------------------------------------------
	 * Getter für ausgewählte Pflanzentypen
	 *-------------------------------------------- */
	public ArrayList<Pflanzentyp> getAlTyp() {
		return alTyp;
	}
}
