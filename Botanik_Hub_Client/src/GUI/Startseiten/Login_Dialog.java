package GUI.Startseiten;

import java.sql.SQLException;

import Enum.BenutzerTyp;
import GUI.BotanikHub_Client;
import GUI.Utilitys.Util;
import Modell.Benutzer;
import TEST_DB.DB_Benutzer;
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

public class Login_Dialog extends Dialog<ButtonType> {

	public Login_Dialog(StackPane stack, Stage stage) {


		/*--------------------------------------------
		 * GUI-Elemente: Eingabe & Buttons
		 *-------------------------------------------- */
		TextField nameTxt = new TextField();
		PasswordField pwTxt = new PasswordField();

		ButtonType anmeldeBtn = new ButtonType("Einloggen", ButtonData.OK_DONE);
		ButtonType abbrechen = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
		this.getDialogPane().getButtonTypes().addAll(anmeldeBtn, abbrechen);

		Button login = (Button) this.getDialogPane().lookupButton(anmeldeBtn);
		Button cancel = (Button) this.getDialogPane().lookupButton(abbrechen);

		nameTxt.setPromptText("Benutzername eingeben");
		pwTxt.setPromptText("Passwort eingeben");

		login.getStyleClass().add("dialog-button-ok");
		cancel.getStyleClass().add("dialog-button-cancel");

		/*--------------------------------------------
		 * Layout für Login-Eingabe (GridPane)
		 *-------------------------------------------- */
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.add(new Label("Benutzername"), 0, 0);
		grid.add(nameTxt, 1, 0);
		grid.add(new Label("Passwort"), 0, 1);
		grid.add(pwTxt, 1, 1);

		/*--------------------------------------------
		 * Listener zu Loginbutton
		 *-------------------------------------------- */
		ChangeListener<String> loginListner = (arg0, arg1, arg2) -> {
			boolean name = nameTxt.getText().isEmpty();
			boolean pw = pwTxt.getText().isEmpty();
			// login.setDisable(name || pw); // Freischalten bei Bedarf
		};
		nameTxt.textProperty().addListener(loginListner);
		pwTxt.textProperty().addListener(loginListner);

		/*--------------------------------------------
		 * Eventfilter für Login: Prüfen und Weiterleitung
		 *-------------------------------------------- */
		login.addEventFilter(ActionEvent.ACTION, e -> {
			try {
				Benutzer gefundenerBenutzer = DB_Benutzer.loginBenutzer(nameTxt.getText(), pwTxt.getText());

				if (gefundenerBenutzer != null) {
					BotanikHub_Client.setBenutzer(gefundenerBenutzer);

					if (gefundenerBenutzer.getTyp() == BenutzerTyp.ADMIN) {
						Util.alertWindow(AlertType.INFORMATION, "Login erfolgreich", "Willkommen " + gefundenerBenutzer.getBenutzerName()).showAndWait();
						Admin_Startseite.oeffneAdminSeite(stack, stage, gefundenerBenutzer);
					} else {
						Util.alertWindow(AlertType.INFORMATION, "Login erfolgreich", "Willkommen " + gefundenerBenutzer.getBenutzerName()).showAndWait();
						Benutzer_Startseite.oeffneBenutzerSeite(stack, stage, gefundenerBenutzer);
					}

					this.setResult(ButtonType.OK);
				} else {
					Util.alertWindow(AlertType.ERROR, "Anmeldung fehlgeschlagen", "Benutzername oder Passwort falsch").showAndWait();
					pwTxt.setText("");
					e.consume();
				}

			} catch (SQLException ex) {
				Util.alertWindow(AlertType.ERROR, "Fehler", ex.getMessage()).showAndWait();
				e.consume();
			}
		});

		/*--------------------------------------------
		 * Layout-Zusammenstellung 
		 *-------------------------------------------- */
		VBox vb = new VBox(grid);
		vb.setPadding(new Insets(5));

		/*--------------------------------------------
		 * Dialog-Eigenschaften
		 *-------------------------------------------- */
		this.setTitle("Login");
		this.getDialogPane().setContent(vb);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("dialog-layout");
	}
}
