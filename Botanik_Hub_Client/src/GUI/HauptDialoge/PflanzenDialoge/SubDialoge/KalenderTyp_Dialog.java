package GUI.HauptDialoge.PflanzenDialoge.SubDialoge;

import java.util.ArrayList;

import Enum.Kalendertyp;
import Enum.Month;
import GUI.BotanikHub_Client;
import ModellFX.BotanikkalenderFX;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class KalenderTyp_Dialog extends Dialog<ButtonType> {

	private ArrayList<Month> alMonth = new ArrayList<>();

	public KalenderTyp_Dialog(BotanikkalenderFX b, Kalendertyp kTyp) {

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
		 * Headerbild (Design)
		 *-------------------------------------------- */
		ImageView header = new ImageView(new Image(BotanikHub_Client.class.getResource("/kalender1.jpg").toString()));
		header.setFitWidth(200);
		header.setPreserveRatio(true);

		/*--------------------------------------------
		 * GridPane für Monats-Checkboxen
		 *-------------------------------------------- */
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);

		Month[] monate = Month.values();							// Enum-Werte (JAN–DEZ)
		CheckBox[] checkboxen = new CheckBox[monate.length];		// CheckBox pro Monat

		for (int i = 0; i < monate.length; i++) {
			Label lbl = new Label(monate[i].toString());
			checkboxen[i] = new CheckBox();

			grid.add(lbl, 1, i);				// Monat links
			grid.add(checkboxen[i], 12, i);		// Checkbox rechts
		}

		/*--------------------------------------------
		 * Vorauswahl aktivieren (z. B. beim Bearbeiten)
		 *-------------------------------------------- */
		ArrayList<Month> vorhandeneMonate = b.getAppKalender().getMonat();
		if (vorhandeneMonate != null) {
			for (int i = 0; i < monate.length; i++) {
				Month typEnum = Month.fromBeschreibung(monate[i].toString());
				if (vorhandeneMonate.contains(typEnum)) {
					checkboxen[i].setSelected(true);
				}
			}
		}

		/*--------------------------------------------
		 * ResultConverter: Auswahl übernehmen
		 *-------------------------------------------- */
		this.setResultConverter(new Callback<ButtonType, ButtonType>() {
			@Override
			public ButtonType call(ButtonType arg0) {
				if (arg0 == anwenden) {
					alMonth.clear();

					// Selektierte Monate sammeln
					for (int i = 0; i < monate.length; i++) {
						if (checkboxen[i].isSelected()) {
							alMonth.add(Month.fromBeschreibung(monate[i].toString()));
						}
					}

					// In FX-Klasse speichern
					b.getAppKalender().setMonat(alMonth);
				}
				return anwenden;
			}
		});

		/*--------------------------------------------
		 * Layoutaufbau (VBox, HBox)
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
		this.setTitle("Bitte Monate auswählen");
		this.getDialogPane().setContent(vb1);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("dialog-layout");
	}

	/*--------------------------------------------
	 * Getter für ausgewählte Monate
	 *-------------------------------------------- */
	public ArrayList<Month> getAlMonth() {
		return alMonth;
	}
}
