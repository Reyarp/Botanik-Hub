package GUI.Startseiten;

import java.util.Optional;

import Client.BotanikHub_Client;
import GUI.HauptDialoge.BenutzerDialoge.Benutzer_Verwalten_Dialog;
import GUI.HauptDialoge.KalenderDialoge.Botanikkalender_Dialog;
import GUI.HauptDialoge.PflanzenDialoge.Pflanzen_Verwalten_Dialog;
import GUI.Utilitys.Util_Animations;
import GUI.Utilitys.Util_Help;
import Modell.Benutzer;
import Modell.Pflanze;
import ModellFX.PflanzeFX;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Admin_Startseite {

	public static void oeffneAdminSeite(StackPane pane, Stage stage, Benutzer benutzer) {

		/*
		 * Dieser Dialog ist die Admin Startseite
		 * Hier hat er Zugriff auf: Botanikkalender, Benutzerverwalung & Pflanzenverwaltung
		 * chatAlisa -> war ein versuch, wird aber nicht in der aktuellen Version enthalten sein
		 */


		// Buttons & Co
		ImageView benutzerVerwaltenBtn = new ImageView(new Image(BotanikHub_Client.class.getResource("/benutzer.png").toString()));
		ImageView pflanzenVerwaltenBtn = new ImageView(new Image(BotanikHub_Client.class.getResource("/plant-icon.png").toString()));
		ImageView abmeldenBtn = new ImageView(new Image(BotanikHub_Client.class.getResource("/logout.png").toString()));
		ImageView botanikkalenderBtn = new ImageView(new Image(BotanikHub_Client.class.getResource("/Kalender.png").toString()));
//		ImageView chatAlisa = new ImageView(new Image(BotanikHub_Client.class.getResource("/chat.png").toString()));	
		Label botanikHubLbl = new Label("Botanik-Hub");
		
		// Layout: StackPane -> Robuster bei Elementüberlappungen
		StackPane adminStack = new StackPane();
		// Hintergrund methode -> Util_Help
		Util_Help.hintergrundSetzen(adminStack);

		// Eigene Methode zum Platzieren der GUI 
		Util_Help.guiPlatzieren(benutzerVerwaltenBtn, -620, 120, 260, 180, 1, true);
		Util_Help.guiPlatzieren(pflanzenVerwaltenBtn, -620, 270, 260, 150, 1, false);
		Util_Help.guiPlatzieren(abmeldenBtn, -660, -350, 140, 80, 1, false);
		Util_Help.guiPlatzieren(botanikkalenderBtn, -620, -30, 260, 150, 1, false);
//		Util_Help.guiPlatzieren(chatAlisa, 620, 350, 50, 50, 1, false);
		Util_Help.guiPlatzieren(botanikHubLbl, 0, 0, 250, 250, 1, false);

		// CSS Styling
//		chatAlisa.getStyleClass().add("version-label");
		botanikHubLbl.getStyleClass().add("willkommen-label");
		botanikkalenderBtn.getStyleClass().add("kalender-label");
		pflanzenVerwaltenBtn.getStyleClass().add("kalender-label");
		benutzerVerwaltenBtn.getStyleClass().add("kalender-label");
		abmeldenBtn.getStyleClass().add("kalender-label");

		// Animationseffekte -> Util_Animations
		pflanzenVerwaltenBtn.setOnMouseEntered(e -> Util_Animations.zoomInAnimation(pflanzenVerwaltenBtn, 1.3, Duration.seconds(0.25)));
		pflanzenVerwaltenBtn.setOnMouseExited(e -> Util_Animations.zoomOutAnimation(pflanzenVerwaltenBtn, Duration.seconds(0.25)));

		benutzerVerwaltenBtn.setOnMouseEntered(e -> Util_Animations.zoomInAnimation(benutzerVerwaltenBtn, 1.3, Duration.seconds(0.25)));
		benutzerVerwaltenBtn.setOnMouseExited(e -> Util_Animations.zoomOutAnimation(benutzerVerwaltenBtn, Duration.seconds(0.25)));

		botanikkalenderBtn.setOnMouseEntered(e -> Util_Animations.zoomInAnimation(botanikkalenderBtn, 1.3, Duration.seconds(0.25)));
		botanikkalenderBtn.setOnMouseExited(e -> Util_Animations.zoomOutAnimation(botanikkalenderBtn, Duration.seconds(0.25)));

		abmeldenBtn.setOnMouseEntered(e -> Util_Animations.zoomInAnimation(abmeldenBtn, 1.3, Duration.seconds(0.25)));
		abmeldenBtn.setOnMouseExited(e -> Util_Animations.zoomOutAnimation(abmeldenBtn, Duration.seconds(0.25)));

		// Eventhandler: pflanzeVerwalten, benutzerVerwalten & botanikkalender
		pflanzenVerwaltenBtn.setOnMousePressed(e -> {
			Pflanzen_Verwalten_Dialog dialog = new Pflanzen_Verwalten_Dialog(benutzer);
			dialog.showAndWait();
		});

		benutzerVerwaltenBtn.setOnMousePressed(e -> {
			Benutzer_Verwalten_Dialog dialog = new Benutzer_Verwalten_Dialog();
			dialog.showAndWait();
		});

		botanikkalenderBtn.setOnMousePressed(e -> {
			Botanikkalender_Dialog dialog = new Botanikkalender_Dialog(new PflanzeFX(new Pflanze()));
			dialog.showAndWait();
		});

		/*
		 * Abmeldefunktion:
		 * Neue StackPane erstellen für die LoginSeite
		 * Neue Scene mit Login erstellen
		 * Scene in die Stage setzen
		 * Benutzer auf null setzen
		 */
		abmeldenBtn.setOnMousePressed(e -> {
			Alert alert = Util_Help.alertWindow(AlertType.CONFIRMATION, "Info: Abmelden", "Möchten Sie sich abmelden?");
			Optional<ButtonType> result = alert.showAndWait();
			if(result.isPresent() && result.get().getButtonData() == ButtonData.YES) {
				StackPane goBack = new StackPane();
				Login_Startseite.erstelleStartSeite(goBack, stage);
				Scene neueScene = new Scene(goBack);
				stage.setScene(neueScene);
				BotanikHub_Client.setBenutzer(null);
			}
		});

		// Zusammenbau & Dialogeinstellungen
		adminStack.getChildren().addAll(benutzerVerwaltenBtn, pflanzenVerwaltenBtn, botanikkalenderBtn, abmeldenBtn, botanikHubLbl);
		adminStack.getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());

		Scene adminScene = new Scene(adminStack);
		stage.setScene(adminScene);
		stage.show();
	}
}
