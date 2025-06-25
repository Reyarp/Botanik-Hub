package GUI.HauptDialoge.BenutzerDialoge;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import Client.BotanikHub_Client;
import Enum.BenutzerTyp;
import GUI.Utilitys.Util_Help;
import Modell.Benutzer;
import ModellFX.BenutzerFX;
import ServiceFunctions.Service_Benutzer;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

public class Benutzer_Erstellen_Dialog extends Dialog<ButtonType> {

	// Fixe Konstante zum Prüfen
	public static final String ADMIN_NAME = "admin";

	public Benutzer_Erstellen_Dialog(BenutzerFX bFX, StackPane stack, Stage stage) {

		/*
		 * Dieser Dialog ist zum erstellen eines Benutzers
		 * Man kann einen Namen eingeben -> wird auf Duplikat überprüft in der DB
		 * und man kann ein Passwort setzen -> wird über einen Regex gesteuert
		 */

		// Buttons & Co
		ButtonType save = new ButtonType("Speichern", ButtonData.OK_DONE);
		ButtonType cancel = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
		this.getDialogPane().getButtonTypes().addAll(save, cancel);

		Button speichern = (Button) this.getDialogPane().lookupButton(save);
		Button abbrechen = (Button) this.getDialogPane().lookupButton(cancel);

		Label nameLbl = new Label("Name eingeben:");
		TextField nameTxt = new TextField();

		Label pwLbl = new Label("Passwort:");
		PasswordField pwTxt = new PasswordField();
		Label pwRepLbl = new Label("Passwort wiederholen:");
		PasswordField pwRepTxt = new PasswordField();

		TextField pwEinblendenTxt = new TextField();
		ImageView pwLogo = new ImageView(new Image(BotanikHub_Client.class.getResource("/Benutzer_Erstellen_Auge.png").toString()));
		Button pwEinblendeBtn = new Button();


		// Settings
		speichern.setDisable(true);
		// Unsichtbar machen
		pwRepTxt.setPromptText("Passwort eingeben");
		nameTxt.setPromptText("Benutzername eingeben");

		pwEinblendeBtn.setGraphic(pwLogo);
		pwEinblendenTxt.setVisible(false);
		// setManaged(false): Element wird vollständig aus der Layoutberechnung entfernt (nimmt keinen Platz mehr ein)
		pwEinblendenTxt.setManaged(false);
		// Fokus weglassen
		pwEinblendenTxt.setEditable(false);

		// Beide Passwortfelder snychronisieren
		pwEinblendenTxt.textProperty().bindBidirectional(pwTxt.textProperty());
		// Eigene Tooltip Methode
		Util_Help.tip(pwEinblendeBtn, "Passwort anzeigen", Duration.millis(200), Duration.seconds(3));

		pwLogo.setFitHeight(15);
		pwLogo.setFitWidth(18);
		pwLogo.setSmooth(true);
		pwLogo.setPreserveRatio(true);

		pwTxt.setVisible(true);
		pwTxt.setManaged(true);
		pwTxt.setPromptText("Passwort eingeben");

		// CSS Styling
		abbrechen.getStyleClass().add("dialog-button-cancel");
		speichern.getStyleClass().add("dialog-button-ok");
		pwEinblendeBtn.getStyleClass().add("auge-button");


		//ChangeListener: Speichern nur aktiv wenn alle Felder ausgefüllt wurden
		ChangeListener<String> loginListner = (arg0, arg1, arg2) -> {
			boolean name = nameTxt.getText().isEmpty();
			boolean pass = pwTxt.getText().isEmpty();
			boolean pass2 = pwRepTxt.getText().isEmpty();
			speichern.setDisable(name || pass || pass2);
		};
		// Listener setzen
		nameTxt.textProperty().addListener(loginListner);
		pwTxt.textProperty().addListener(loginListner);
		pwRepTxt.textProperty().addListener(loginListner);

		// Switch für pw felder
		// Umschalten zwischen verdecktem und sichtbarem Passwortfeld
		// Sichtbares Feld aktivieren: unsichtbares (pw) ausblenden, sichtbares (pwSichtbar) anzeigen
		// Dabei sicherstellen, dass das Layout korrekt aktualisiert wird
		pwEinblendeBtn.setOnMousePressed(e -> {
			pwTxt.setVisible(false);
			pwTxt.setManaged(false);
			pwEinblendenTxt.setVisible(true);
			pwEinblendenTxt.setManaged(true);
		});

		pwEinblendeBtn.setOnMouseReleased(e -> {
			pwTxt.setVisible(true);
			pwTxt.setManaged(true);
			pwEinblendenTxt.setVisible(false);
			pwEinblendenTxt.setManaged(false);
		});

		this.setResultConverter(new Callback<ButtonType, ButtonType>() {

			@Override
			public ButtonType call(ButtonType arg0) {
				if(arg0 == save) {
					// Benutzer erstellen für POST anfrage
					Benutzer benutzer = new Benutzer(
							nameTxt.getText(),
							pwTxt.getText(),
							0,	// ID auf 0 -> Wird in der DB generiert				
							BenutzerTyp.BENUTZER,
							LocalDate.now()
							);

					try {
						Service_Benutzer.postBenutzer(benutzer);
						return save;
					} catch (SQLException e) {
						Util_Help.alertWindow(AlertType.ERROR, "Fehler: Benutzer erstellen", e.getMessage()).showAndWait();
						return null;
					}
				}
				return null;
			}
		});

		// Eventhandler: speichern
		speichern.addEventFilter(ActionEvent.ACTION, e -> {
			try {
				if (!pwTxt.getText().equals(pwRepTxt.getText())) {
					Util_Help.alertWindow(AlertType.ERROR, "Fehler", "Passwörter stimmen nicht überein").showAndWait();
					e.consume(); 
					pwTxt.setText("");
					pwRepTxt.setText("");
					pwTxt.requestFocus();
					return;
				}
				// Regex zum Passwort validieren
				// Erklärung: das ?=.* -> LookAhead - Quelle: https://stackoverflow.com/questions/2973436/regex-lookahead-lookbehind-and-atomic-groups
				// Bedeutet: „es muss irgendwo danach das Muster in (...) vorkommen, aber es wird nicht verbraucht" -> ?= = es folgt irgentwo ...
				// (?=.*\d) → mindestens eine Zahl
				// [A-Z] → beginnt mit Großbuchstaben
				// [a-z\d!@#%&*_]{7,49} → 7–49 weitere gültige Zeichen (gesamt also 8–50 inkl. Startbuchstabe)
				if(!pwTxt.getText().matches("^(?=.*\\d)([A-Z])([A-Za-z\\d!@#%&*_]{7,49})$")) {
					Util_Help.alertWindow(AlertType.ERROR, "Fehler: Passwort setzen", 
							"Das Passwort muss mindestens 8 Zeichen lang sein, mit einem Großbuchstaben am Anfang und mindestens einem Sonderzeichen").showAndWait();
					e.consume();
					pwTxt.requestFocus();
				}

				// Liste erstellen, durch streamen und mit eingegeben Namen vergleichen -> auf Duplikat
				List<Benutzer> duplikat = Service_Benutzer.getBenutzer().stream()
						.filter(k -> k.getBenutzerName().equals(nameTxt.getText()))
						.toList();

				// Wenn treffer oder admin -> fehler
				if(!duplikat.isEmpty() || nameTxt.getText().equalsIgnoreCase(ADMIN_NAME)) {
					Util_Help.alertWindow(AlertType.ERROR, "Fehler: Benutzer erstellen", "Benutzer existiert bereits").showAndWait();	
					e.consume();
					pwTxt.setText("");
					pwRepTxt.setText("");
					nameTxt.requestFocus();
					return;
				}
			} catch (SQLException ex) {
				Util_Help.alertWindow(AlertType.ERROR, "Fehler: Benutzer erstellen", ex.getMessage()).showAndWait();
			}
		});


		// Layout: AnchroPane 
		AnchorPane layout = new AnchorPane();
		layout.getChildren().addAll(nameLbl, nameTxt, pwTxt, pwEinblendenTxt, pwLbl, pwEinblendeBtn, pwRepLbl, pwRepTxt);

		Util_Help.anchorpane(nameLbl, 2.5, null, 1.0, null);
		Util_Help.anchorpane(nameTxt, 1.0, null, 125.0, null);

		Util_Help.anchorpane(pwLbl, 37.5, null, 1.0, null);
		Util_Help.anchorpane(pwTxt, 36.0, null, 125.0, null);
		Util_Help.anchorpane(pwEinblendenTxt, 36.0, null, 125.0, null);
		Util_Help.anchorpane(pwEinblendeBtn, 36.0, null, 90.0, null);

		Util_Help.anchorpane(pwRepLbl, 72.5, null, 1.0, null);
		Util_Help.anchorpane(pwRepTxt, 71.0, null, 125.0, null);

		// Zusammenbau & Dialogeinstellungen
		VBox vb = new VBox(layout);
		vb.setPadding(new Insets(5));

		this.setTitle("Neuen Benutzer erstellen");
		this.getDialogPane().setContent(vb);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("dialog-layout");
		// Stage holen zum Icon setzen, da ich direkt im Dialog keins setzen kann
		Stage arg1 = (Stage) this.getDialogPane().getScene().getWindow();
		arg1.getIcons().add(new Image(BotanikHub_Client.class.getResource("/Window_Icon_Lebensbaum.jpg").toString()));
	}
}
