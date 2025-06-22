package GUI.HauptDialoge.BenutzerDialoge;

import java.sql.SQLException;

import Client.BotanikHub_Client;
import GUI.Utilitys.Util_Animations;
import GUI.Utilitys.Util_Help;
import Modell.Benutzer;
import ModellFX.BenutzerFX;
import ServiceFunctions.Service_Benutzer;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class Benutzer_Bearbeiten_Dialog extends Dialog<ButtonType> {

	public Benutzer_Bearbeiten_Dialog(BenutzerFX b) {	

		/*
		 * Dieser Dialog ist für den Admin
		 * Hier kann er den Namen jedes Benutzer und die ID einsehen
		 * Änderbar ist nur der Name des Benutzers
		 */
		
		// Buttons & Co
		ButtonType cancel = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
		ButtonType save = new ButtonType("Speichern", ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(cancel, save);

		Button abbrechen = (Button) this.getDialogPane().lookupButton(cancel);
		abbrechen.setPrefWidth(80);
		Button speichern = (Button) this.getDialogPane().lookupButton(save);

		TextField nameTxt = new TextField(b.getBenutzerName());
		Label idLbl = new Label(String.valueOf(b.getAppBenutzer().getBenutzerId()));

		// CSS
		abbrechen.getStyleClass().add("benutzer-dialog-button-cancel");
		speichern.getStyleClass().add("benutzer-dialog-button-ok");

		// Layout: GridPane
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);

		grid.add(new Label("Benutzer ID:"), 0, 0);
		grid.add(idLbl, 1, 0);

		grid.add(new Label("Benutzername:"), 0, 1);
		grid.add(nameTxt, 1, 1);

		// Eventhandler: speichern
		speichern.addEventFilter(ActionEvent.ACTION, e -> {
			Benutzer benutzer = b.getAppBenutzer();
			String neuerName = nameTxt.getText();
			// Model setzen für den Insert
			benutzer.setBenutzerName(neuerName);
			if(neuerName.isEmpty()) {
				Util_Animations.pauseAnimation(nameTxt, Duration.seconds(3));
				e.consume();
				return;
			}
			try {
				Service_Benutzer.putBenutzer(benutzer);
				Util_Help.alertWindow(AlertType.INFORMATION, "Info: Benutzer", "Benutzer bearbeitet").showAndWait();
				e.consume();
				// Bestätigt den Dialog mit OK (wie im ResultConverter)
				this.setResult(ButtonType.OK);
			} catch (SQLException ex) {
				Util_Help.alertWindow(AlertType.ERROR, "Fehler: Benutzer bearbeiten", ex.getMessage()).showAndWait();
				e.consume();
			}
		});

		// Zusammenbau & Dialogeinstellungen
		VBox gesamt = new VBox(grid);
		gesamt.setPadding(new Insets(5));

		this.setTitle("Benutzer bearbeiten");
		this.getDialogPane().setContent(gesamt);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("benutzer-dialog-layout");
	}
}
