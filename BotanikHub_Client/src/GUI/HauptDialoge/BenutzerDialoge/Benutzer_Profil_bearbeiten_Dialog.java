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
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Benutzer_Profil_bearbeiten_Dialog extends Dialog<ButtonType> {

	public Benutzer_Profil_bearbeiten_Dialog(BenutzerFX b) {

		/*
		 * Dieser Dialog ist für den Benutzer selbst
		 * Hier kann er seinen Namen und Passwort ändern
		 * Name wird wieder auf Duplikat überprüft
		 * & Passwort wird über Regex gesteuert/validiert
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
		nameTxt.setDisable(false);

		PasswordField pwTxt = new PasswordField();
		pwTxt.setPromptText("Passwort eingeben");
		pwTxt.setDisable(true);

		PasswordField pwRepTxt = new PasswordField();
		pwRepTxt.setPromptText("Passwort eingeben");
		pwRepTxt.setDisable(true);

		CheckBox pwAktivieren = new CheckBox("Passwort ändern aktivieren");
		pwAktivieren.setSelected(false);
		
		// CSS Styling
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
		grid.add(pwTxt, 1, 2);

		grid.add(new Label("Passwort wiederholen:"), 0, 3);
		grid.add(pwRepTxt, 1, 3);

		// Changelistener: pwAktivieren
		pwAktivieren.selectedProperty().addListener(new ChangeListener<>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				nameTxt.setDisable(arg2);
				pwTxt.setDisable(!arg2);
				pwRepTxt.setDisable(!arg2);
			}
		});

		speichern.setOnAction(e ->{
			// Speichern
			try {
				Service_Benutzer.putBenutzer(b.getAppBenutzer());
				// Bestätigt den Dialog mit OK (wie im ResultConverter)
				this.setResult(ButtonType.OK);
			} catch (SQLException ex) {
				Util_Help.alertWindow(AlertType.ERROR, "Fehler: Benutzerprofil", ex.getMessage()).showAndWait();
				e.consume();
			}
		});

		// Eventfilter: speichern -> validieren
		speichern.addEventFilter(ActionEvent.ACTION, e -> {
			Benutzer benutzer = b.getAppBenutzer();
			String neuerName = nameTxt.getText().trim();
			String pw1 = pwTxt.getText();
			String pw2 = pwRepTxt.getText();

			// benutzermodell setzen
			benutzer.setBenutzerName(neuerName);

			if(!neuerName.equals(b.getBenutzerName())) {
				Util_Help.alertWindow(AlertType.INFORMATION, "Info: Benutzerprofil", "Benutzername erfolgreich geändert").showAndWait();
			}

			if(neuerName.isEmpty()) {
				Util_Animations.pauseAnimation(nameTxt, Duration.seconds(3));
				e.consume();
				return;
			}

			if(neuerName.equals(b.getBenutzerName()) && !pwAktivieren.isSelected()) {
				Util_Help.alertWindow(AlertType.INFORMATION, "Info: Benutzerprofil", "Benutzername wurde nicht verändert").showAndWait();
			}


			// Regex zum Passwort validieren
			// Erklärung: das ?=.* -> LookAhead - Quelle: https://stackoverflow.com/questions/2973436/regex-lookahead-lookbehind-and-atomic-groups
			// Bedeutet: „es muss irgendwo danach das Muster in (...) vorkommen, aber es wird nicht verbraucht" -> ?= = es folgt irgentwo ...
			// (?=.*\d) → mindestens eine Zahl
			// [A-Z] → beginnt mit Großbuchstaben
			// [a-z\d!@#%&*_]{7,49} → 7–49 weitere gültige Zeichen (gesamt also 8–50 inkl. Startbuchstabe)

			// Passwortlogik
			if (!pw1.isBlank() || !pw2.isBlank()) {
				// 1. Gleichheit prüfen
				if (!pw1.equals(pw2)) {
					Util_Help.alertWindow(AlertType.ERROR, "Fehler: Benutzerprofil", "Passwörter stimmen nicht überein").showAndWait();
					pwTxt.setText("");
					pwRepTxt.setText("");
					e.consume();
					pwTxt.requestFocus();
					return;
				}

				// 2. Format prüfen -> regex oben erklärt
				if (!pw1.matches("^(?=.*\\d)(?=.*[!@#%&*_])(?=.*[A-Z])[A-Za-z\\d!@#%&*_]{8,50}$")) {
					Util_Help.alertWindow(AlertType.ERROR, "Fehler: Passwort setzen", 
							"Das Passwort muss mindestens 8 Zeichen lang sein, mit einem Großbuchstaben, einer Ziffer und einem Sonderzeichen (!@#%&*_)").showAndWait();
					e.consume();
					pwTxt.setText("");
					pwRepTxt.setText("");
					pwTxt.requestFocus();
					return;
				}

				// 3. Altes -> neues Passwort vergleichen
				if (pw1.equals(benutzer.getPasswort())) {
					Util_Help.alertWindow(AlertType.ERROR, "Fehler: Benutzerprofil", "Sie haben das alte Passwort eingegeben, bitte ein neues wählen").showAndWait();
					e.consume();
					pwTxt.setText("");
					pwRepTxt.setText("");
					pwTxt.requestFocus();
					return;
				}

				// 4. Neues Passwort setzen
				benutzer.setPasswort(pw1);
				Util_Help.alertWindow(AlertType.INFORMATION, "Info: Benutzerprofil", 
						"Passwort geändert").showAndWait();
			}
			if(pw1.isEmpty() && pw2.isEmpty() && pwAktivieren.isSelected()) {
				Util_Help.alertWindow(AlertType.INFORMATION, "Info: Benutzerprofil", "Passwort wurde nicht verändert").showAndWait();
			}
		});

		// Zusammenbau & Dialogeinstellungen
		VBox gesamt = new VBox(grid);
		gesamt.setPadding(new Insets(5));

		this.setTitle("Profil bearbeiten");
		this.getDialogPane().setContent(gesamt);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("benutzer-dialog-layout");
		// Stage holen zum Icon setzen, da ich direkt im Dialog keins setzen kann
		Stage arg1 = (Stage) this.getDialogPane().getScene().getWindow();
		arg1.getIcons().add(new Image(BotanikHub_Client.class.getResource("/Window_Icon_Lebensbaum.jpg").toString()));
	}
}
