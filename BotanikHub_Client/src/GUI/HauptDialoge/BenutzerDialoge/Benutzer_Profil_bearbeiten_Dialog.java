package GUI.HauptDialoge.BenutzerDialoge;

import java.sql.SQLException;
import Client.BotanikHub_Client;
import GUI.Utilitys.Util_Animations;
import GUI.Utilitys.Util_Help;
import Modell.Benutzer;
import ModellFX.BenutzerFX;
import ServiceFunctions.Service_Benutzer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class Benutzer_Profil_bearbeiten_Dialog extends Dialog<ButtonType> {

	public Benutzer_Profil_bearbeiten_Dialog(BenutzerFX b) {

		/*
		 * Dieser Dialog ist für den Benutzer selbst
		 * Hier kann er seinen Namen und Passwort ändern
		 * Name wird wieder auf Duplikat überprüft
		 */

		// Buttons & Co
		ButtonType cancel = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
		ButtonType save = new ButtonType("Speichern", ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(cancel, save);

		Button abbrechen = (Button) this.getDialogPane().lookupButton(cancel);
		abbrechen.setPrefWidth(80);
		Button speichern = (Button) this.getDialogPane().lookupButton(save);

		TextField nameTxt = new TextField(b.getBenutzerName());
		nameTxt.setPromptText("Benutzername");

		PasswordField pw = new PasswordField();
		pw.setPromptText("Passwort eingeben");
		pw.setDisable(true);

		PasswordField pwRep = new PasswordField();
		pwRep.setPromptText("Passwort eingeben");
		pwRep.setDisable(true);
		
		CheckBox pwAktivieren = new CheckBox("Passwort ändern aktivieren");
		pwAktivieren.setSelected(false);
		nameTxt.setDisable(false);
		pw.setDisable(true);
		pwRep.setDisable(true);

		abbrechen.getStyleClass().add("benutzer-dialog-button-cancel");
		speichern.getStyleClass().add("benutzer-dialog-button-ok");

		// Layout: GridPane
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);

		grid.add(new Label("Name:"), 0, 0);
		grid.add(nameTxt, 1, 0);

		grid.add(new Label("Passwort ändern"), 0, 1);
		grid.add(pwAktivieren, 1, 1);

		grid.add(new Label("Passwort:"), 0, 2);
		grid.add(pw, 1, 2);

		grid.add(new Label("Passwort wiederholen:"), 0, 3);
		grid.add(pwRep, 1, 3);

		// Changelistener: pwAktivieren
		pwAktivieren.selectedProperty().addListener(new ChangeListener<>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				nameTxt.setDisable(arg2);
				pw.setDisable(!arg2);
				pwRep.setDisable(!arg2);
			}
		});
		
		// Eventfilter: speichern -> validieren und speichern
		speichern.addEventFilter(ActionEvent.ACTION, e -> {
			Benutzer benutzer = b.getAppBenutzer();
			String neuerName = nameTxt.getText().trim();
			String pw1 = pw.getText();
			String pw2 = pwRep.getText();

			// benutzermodell setzen
			benutzer.setBenutzerName(neuerName);
			
			if(neuerName.isEmpty()) {
				Util_Animations.pauseAnimation(nameTxt, Duration.seconds(3));
				e.consume();
				return;
			}

			// Passwortlogik
			if (!pw1.isBlank() || !pw2.isBlank()) {
				if (!pw1.equals(pw2)) {
					Util_Help.alertWindow(AlertType.ERROR, "Fehler: Benutzerprofil", "Passwörter stimmen nicht überein").showAndWait();
					pw.setText("");
					pwRep.setText("");
					e.consume();
					return;
				}
				if (!pw1.equals(benutzer.getPasswort())) {
					benutzer.setPasswort(pw1);
					Util_Help.alertWindow(AlertType.INFORMATION, "Info: Benutzerprofil", "Passwort geändert").showAndWait();
				}
			}

			// Speichern
			try {
				Service_Benutzer.putBenutzer(benutzer);
				Util_Help.alertWindow(AlertType.INFORMATION, "Info: Benutzer", "Benutzer bearbeitet").showAndWait();
				e.consume();
				// Bestätigt den Dialog mit OK (wie im ResultConverter)
				this.setResult(ButtonType.OK);
			} catch (SQLException ex) {
				Util_Help.alertWindow(AlertType.ERROR, "Fehler: Benutzerprofil", ex.getMessage()).showAndWait();
				e.consume();
			}
		});

		// Zusammenbau & Dialogeinstellungen
		VBox gesamt = new VBox(grid);
		gesamt.setPadding(new Insets(5));

		this.setTitle("Profil bearbeiten");
		this.getDialogPane().setContent(gesamt);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("benutzer-dialog-layout");
	}
}
