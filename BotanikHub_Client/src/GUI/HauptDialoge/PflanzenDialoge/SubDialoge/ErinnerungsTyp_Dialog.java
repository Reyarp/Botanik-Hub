package GUI.HauptDialoge.PflanzenDialoge.SubDialoge;

import Client.BotanikHub_Client;
import Enum.Erinnerungstyp;
import ModellFX.ErinnerungenFX;
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

public class ErinnerungsTyp_Dialog extends Dialog<ButtonType> {

	public ErinnerungsTyp_Dialog(ErinnerungenFX e) {

		/*
		 * Subdialog für Erinnerungstyp auswahl
		 */

		// Buttons & Co
		ButtonType anwenden = new ButtonType("Anwenden", ButtonData.OK_DONE);
		ButtonType abbrechen = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
		this.getDialogPane().getButtonTypes().addAll(anwenden, abbrechen);

		Button apply = (Button) this.getDialogPane().lookupButton(anwenden);
		Button cancel = (Button) this.getDialogPane().lookupButton(abbrechen);

		// Headerbild
		ImageView header = new ImageView(new Image(BotanikHub_Client.class.getResource("/Erinnerungstyp_Dialog_Headerbild.jpg").toString()));
		header.setFitWidth(200);
		header.setFitHeight(100);
		header.setPreserveRatio(true);

		// CSS Styling
		apply.getStyleClass().add("dialog-button-ok");
		cancel.getStyleClass().add("dialog-button-cancel");

		// Enum zur dynamischen Erstellung der Checkboxen (wartungsfreundlich)
		Erinnerungstyp[] ertyp = Erinnerungstyp.values();		
		// RadioButtons befüllt mit EnumTyp
		RadioButton[] buttons = new RadioButton[ertyp.length];					
		ToggleGroup typen = new ToggleGroup();									

		// Layout: GridPane
		GridPane grid = new GridPane();											
		grid.setHgap(10);
		grid.setVgap(10);

		// Labels & Buttons: anlegen & befüllen
		for (int i = 0; i < ertyp.length; i++) {
			Label lbl = new Label(ertyp[i].toString());							
			buttons[i] = new RadioButton();										
			buttons[i].setToggleGroup(typen);									

			grid.add(lbl, 1, i);												
			grid.add(buttons[i], 11, i);	
			// Standardauswahl
			buttons[0].setSelected(true);										
		}

		// Vorauswahl über setSelected machen -> beim erneuten Dialog öffnen
		Erinnerungstyp auswahl = e.getAppErinnerung().getTyp();
		if (auswahl != null) {
			for (int i = 0; i < ertyp.length; i++) {
				if (auswahl == Erinnerungstyp.fromBeschreibung(ertyp[i].toString())) {
					buttons[i].setSelected(true);								
				}
			}
		}

		// Resultconverter: zum Speichern
		this.setResultConverter(new Callback<>() {
			@Override
			public ButtonType call(ButtonType arg0) {
				if (arg0 == anwenden) {
					for (int i = 0; i < buttons.length; i++) {
						if (buttons[i].isSelected()) {
							// ins Modell speichern
							e.getAppErinnerung().setTyp(Erinnerungstyp.fromBeschreibung(ertyp[i].toString()));
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

		this.setTitle("Bitte Erinnerung auswählen");
		this.getDialogPane().setContent(vb1);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("dialog-layout");
		// Stage holen zum Icon setzen, da ich direkt im Dialog keins setzen kann
		Stage arg1 = (Stage) this.getDialogPane().getScene().getWindow();
		arg1.getIcons().add(new Image(BotanikHub_Client.class.getResource("/Window_Icon_Lebensbaum.jpg").toString()));
	}
}
