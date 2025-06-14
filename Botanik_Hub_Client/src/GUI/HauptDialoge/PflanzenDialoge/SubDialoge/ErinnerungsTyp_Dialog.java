package GUI.HauptDialoge.PflanzenDialoge.SubDialoge;

import Enum.Erinnerungstyp;
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

public class ErinnerungsTyp_Dialog extends Dialog<ButtonType> {

	public ErinnerungsTyp_Dialog(ErinnerungenFX e) {

		/*--------------------------------------------
		 * Button-Typen definieren und hinzufügen
		 *-------------------------------------------- */
		ButtonType anwenden = new ButtonType("Anwenden", ButtonData.OK_DONE);
		ButtonType abbrechen = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
		this.getDialogPane().getButtonTypes().addAll(anwenden, abbrechen);

		// Lookup für CSS-Styling
		Button apply = (Button) this.getDialogPane().lookupButton(anwenden);
		Button cancel = (Button) this.getDialogPane().lookupButton(abbrechen);

		/*--------------------------------------------
		 * Headerbild anzeigen
		 *-------------------------------------------- */
		ImageView header = new ImageView(new Image(BotanikHub_Client.class.getResource("/erinnerung.jpg").toString()));
		header.setFitWidth(200);
		header.setFitHeight(100);
		header.setPreserveRatio(true);

		/*--------------------------------------------
		 * CSS Styling
		 *-------------------------------------------- */
		apply.getStyleClass().add("dialog-button-ok");
		cancel.getStyleClass().add("dialog-button-cancel");

		/*--------------------------------------------
		 * Erinnerungstypen als Radiobutton-Liste
		 *-------------------------------------------- */
		Erinnerungstyp[] ertyp = Erinnerungstyp.values();						// Alle verfügbaren Typen
		RadioButton[] buttons = new RadioButton[ertyp.length];					// Radiobuttons pro Typ
		ToggleGroup typen = new ToggleGroup();									// ToggleGroup für Auswahl

		GridPane grid = new GridPane();											// Layout-Grid
		grid.setHgap(10);
		grid.setVgap(10);

		for (int i = 0; i < ertyp.length; i++) {
			Label lbl = new Label(ertyp[i].toString());							// Beschriftung
			buttons[i] = new RadioButton();										// RadioButton erzeugen
			buttons[i].setToggleGroup(typen);									// Gruppe zuweisen

			grid.add(lbl, 1, i);												// Label in Spalte 1
			grid.add(buttons[i], 11, i);										// Button in Spalte 11
			buttons[0].setSelected(true);										// Standard: erster ausgewählt
		}

		/*--------------------------------------------
		 * Vorauswahl (bei erneutem Öffnen)
		 *-------------------------------------------- */
		Erinnerungstyp auswahl = e.getAppErinnerung().getTyp();
		if (auswahl != null) {
			for (int i = 0; i < ertyp.length; i++) {
				if (auswahl == Erinnerungstyp.fromBeschreibung(ertyp[i].toString())) {
					buttons[i].setSelected(true);								// Auswahl wiederherstellen
				}
			}
		}

		/*--------------------------------------------
		 * ResultConverter: Auswahl speichern
		 *-------------------------------------------- */
		this.setResultConverter(new Callback<>() {
			@Override
			public ButtonType call(ButtonType arg0) {
				if (arg0 == anwenden) {
					for (int i = 0; i < buttons.length; i++) {
						if (buttons[i].isSelected()) {
							e.getAppErinnerung().setTyp(Erinnerungstyp.fromBeschreibung(ertyp[i].toString()));
						}
					}
				}
				return anwenden;
			}
		});

		/*--------------------------------------------
		 * Layout zusammensetzen (VBox + HBox)
		 *-------------------------------------------- */
		HBox banner = new HBox(header);
		banner.setPadding(new Insets(5));

		VBox vb = new VBox(grid);
		vb.setPadding(new Insets(5));

		VBox vb1 = new VBox(banner, vb);
		vb1.setPadding(new Insets(5));

		/*--------------------------------------------
		 * Dialog-Eigenschaften setzen
		 *-------------------------------------------- */
		this.setTitle("Bitte Erinnerung auswählen");
		this.getDialogPane().setContent(vb1);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("dialog-layout");
	}
}
