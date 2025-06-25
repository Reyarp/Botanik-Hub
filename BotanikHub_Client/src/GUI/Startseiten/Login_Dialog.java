package GUI.Startseiten;

import java.sql.SQLException;

import Client.BotanikHub_Client;
import Enum.BenutzerTyp;
import GUI.Utilitys.Util_Animations;
import GUI.Utilitys.Util_Help;
import Modell.Benutzer;
import ServiceFunctions.Service_Benutzer;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Login_Dialog extends Dialog<ButtonType> {

	public Login_Dialog(StackPane stack, Stage arg0) {

		/*
		 * Dies ist der Logindialog
		 * Überprüfungen finden hier statt und zu sicherheit auch auf der Serverseite
		 */

		// Buttons & Co
		TextField nameTxt = new TextField();
		PasswordField pwTxt = new PasswordField();

		ButtonType anmeldeBtn = new ButtonType("Einloggen", ButtonData.OK_DONE);
		ButtonType abbrechen = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
		this.getDialogPane().getButtonTypes().addAll(anmeldeBtn, abbrechen);

		Button login = (Button) this.getDialogPane().lookupButton(anmeldeBtn);
		Button cancel = (Button) this.getDialogPane().lookupButton(abbrechen);

		nameTxt.setPromptText("Benutzername eingeben");
		login.getStyleClass().add("dialog-button-ok");
		
		// CSS Styling
		pwTxt.setPromptText("Passwort eingeben");
		cancel.getStyleClass().add("dialog-button-cancel");

		// Layout: GridPane
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		
		grid.add(new Label("Benutzername"), 0, 0);
		grid.add(nameTxt, 1, 0);
		grid.add(new Label("Passwort"), 0, 1);
		grid.add(pwTxt, 1, 1);


		// Eventfilter: validierung -> weiterleitung zum Server
		login.addEventFilter(ActionEvent.ACTION, e -> {
			try {
				String name = nameTxt.getText();	
				String pw = pwTxt.getText();		
				
				if((name == null || name.isEmpty()) || pw == null || pw.isEmpty()) {
					Util_Animations.pauseAnimation(nameTxt, Duration.seconds(2));
					Util_Animations.pauseAnimation(pwTxt, Duration.seconds(2));
					e.consume();
					return;
				}

				// Eigene Server Methode zum Prüfen -> DB_Benutzer_loginBenutzer
				Benutzer benutzer = Service_Benutzer.loginBenutzer(name, pw); 	
				// Benutzer zum validieren setzen
				BotanikHub_Client.setBenutzer(benutzer);

				if (benutzer.getTyp() == BenutzerTyp.ADMIN) {
					Admin_Startseite.oeffneAdminSeite(stack, arg0, benutzer);
				} else {
					Benutzer_Startseite.oeffneBenutzerSeite(stack, arg0, benutzer);
				}
				
				Util_Help.alertWindow(AlertType.INFORMATION, "Info: Login Erfolgreich", "Willkommen " + benutzer.getBenutzerName()).showAndWait();
				this.setResult(ButtonType.OK);

			} catch (SQLException ex) {
				Util_Help.alertWindow(AlertType.ERROR, "Fehler: Login", ex.getMessage()).showAndWait();
				pwTxt.setText("");
				e.consume();
			}
		});


		// Zusammenbau & Dialogeinstellungen
		VBox vb = new VBox(grid);
		vb.setPadding(new Insets(5));

		this.setTitle("Login");
		this.getDialogPane().setContent(vb);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("dialog-layout");
		// Stage holen zum Icon setzen, da ich direkt im Dialog keins setzen kann
		Stage arg1 = (Stage) this.getDialogPane().getScene().getWindow();
		arg1.getIcons().add(new Image(BotanikHub_Client.class.getResource("/Window_Icon_Lebensbaum.jpg").toString()));
	}
}
