package GUI.HauptDialoge.BenutzerDialoge;

import java.sql.SQLException;
import java.time.LocalDate;

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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Benutzer_Erstellen_Dialog extends Dialog<ButtonType> {

	public Benutzer_Erstellen_Dialog(BenutzerFX bFX, StackPane stack, Stage stage) {

		/*
		 * Dieser Dialog ist zum erstellen eines Benutzers
		 * Man kann einen Namen eingeben -> wird auf Duplikat überprüft in der DB
		 * und man kann ein Passwort setzen
		 */

		// Buttons & Co
		ButtonType save = new ButtonType("Speichern", ButtonData.OK_DONE);
		ButtonType cancel = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
		this.getDialogPane().getButtonTypes().addAll(save, cancel);

		Button speichern = (Button) this.getDialogPane().lookupButton(save);
		speichern.setDisable(true);
		Button abbrechen = (Button) this.getDialogPane().lookupButton(cancel);

		TextField nameTxt = new TextField();
		nameTxt.setPromptText("Benutzername eingeben");

		PasswordField pw = new PasswordField();
		pw.setPromptText("Passwort eingeben");

		PasswordField pwRep = new PasswordField();
		pwRep.setPromptText("Passwort eingeben");

		abbrechen.getStyleClass().add("dialog-button-cancel");
		speichern.getStyleClass().add("dialog-button-ok");


		//ChangeListener: Speichern nur aktiv bei Eingabe
		ChangeListener<String> loginListner = (arg0, oldVal, newVal) -> {
			boolean name = nameTxt.getText().isEmpty();
			boolean pass = pw.getText().isEmpty();
			boolean pass2 = pwRep.getText().isEmpty();
			speichern.setDisable(name || pass || pass2);
		};
		// Listener setzen
		nameTxt.textProperty().addListener(loginListner);
		pw.textProperty().addListener(loginListner);
		pwRep.textProperty().addListener(loginListner);

		// Eventhandler: speichern
		speichern.addEventFilter(ActionEvent.ACTION, e -> {
			try {
				if (!pw.getText().equals(pwRep.getText())) {
					Util_Help.alertWindow(AlertType.ERROR, "Fehler", "Passwörter stimmen nicht überein").showAndWait();
					e.consume(); 
					pw.setText("");
					pwRep.setText("");
					return;
				} else {
					// Benutzer erstellen für POST anfrage
					Benutzer benutzer = new Benutzer(
							nameTxt.getText(),
							pw.getText(),
							0,	// ID auf 0 -> Wird in der DB generiert				
							BenutzerTyp.BENUTZER,
							LocalDate.now()
							);
					
					Service_Benutzer.postBenutzer(benutzer);
				}
			} catch (SQLException ex) {
				Util_Help.alertWindow(AlertType.ERROR, "Fehler: Benutzer erstellen", ex.getMessage()).showAndWait();
				e.consume(); 
				pw.setText("");
				pwRep.setText("");
			}
		});

		// Layout: GridPane
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);

		grid.add(new Label("Benutzername eingeben"), 0, 0);
		grid.add(nameTxt, 1, 0);

		grid.add(new Label("Passwort"), 0, 1);
		grid.add(pw, 1, 1);

		grid.add(new Label("Passwort wiederholen"), 0, 2);
		grid.add(pwRep, 1, 2);

		// Zusammenbau & Dialogeinstellungen
		VBox vb = new VBox(grid);
		vb.setPadding(new Insets(5));

		this.setTitle("Neuen Benutzer erstellen");
		this.getDialogPane().setContent(vb);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("dialog-layout");
	}
}
