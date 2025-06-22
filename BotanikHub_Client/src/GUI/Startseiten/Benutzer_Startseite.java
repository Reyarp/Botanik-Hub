package GUI.Startseiten;

import java.util.Optional;

import Client.BotanikHub_Client;
import GUI.HauptDialoge.BenutzerDialoge.Benutzer_Profil_bearbeiten_Dialog;
import GUI.HauptDialoge.ErinnerungsDialoge.Erinnerung_Verwalten_Dialog;
import GUI.HauptDialoge.KalenderDialoge.Botanikkalender_Dialog;
import GUI.HauptDialoge.PflanzenDialoge.Pflanzen_Verwalten_Dialog;
import GUI.Utilitys.Util_Help;
import GUI.Utilitys.Util_Animations;
import Modell.Benutzer;
import Modell.Pflanze;
import ModellFX.BenutzerFX;
import ModellFX.PflanzeFX;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Benutzer_Startseite {

	public static void oeffneBenutzerSeite(StackPane pane, Stage stage, Benutzer benutzer) {

		/*
		 * Dieser Dialog ist die Benutzer Startseite
		 * Hier hat er Zugriff auf: Botanikkalender, Benutzerverwalung, Botanik-Hub & Erinnerungen
		 * chatAlisa -> war ein versuch, wird aber nicht in der aktuellen Version enthalten sein
		 */
		
		
		// Buttons & Co
		ImageView profilVerwaltenBtn = new ImageView(new Image(BotanikHub_Client.class.getResource("/benutzer.png").toString()));
		ImageView botanikHubBtn = new ImageView(new Image(BotanikHub_Client.class.getResource("/plant-icon.png").toString()));
		ImageView abmeldenBtn = new ImageView(new Image(BotanikHub_Client.class.getResource("/logout.png").toString()));
		ImageView botanikkalenderBtn = new ImageView(new Image(BotanikHub_Client.class.getResource("/Kalender.png").toString()));
//		ImageView chatAlisa = new ImageView(new Image(BotanikHub_Client.class.getResource("/chat.png").toString()));
		ImageView erinnerungBtn = new ImageView(new Image(BotanikHub_Client.class.getResource("/erinnerungicon.png").toString()));
		Label botanikHubLbl = new Label("Botanik-Hub");

		// Layout: StackPane
		StackPane userStack = new StackPane();
		// Hintergrund methode -> Util_Help
		Util_Help.hintergrundSetzen(userStack);
		
		// Eigene Methode zum Platzieren der GUI 
		Util_Help.guiPlatzieren(profilVerwaltenBtn, -620, 120, 260, 180, 1, true);
		Util_Help.guiPlatzieren(botanikHubBtn, -620, 270, 260, 150, 1, false);
		Util_Help.guiPlatzieren(abmeldenBtn, -660, -350, 140, 80, 1, false);
		Util_Help.guiPlatzieren(botanikkalenderBtn, -620, -30, 260, 150, 1, false);
//		Util_Help.guiPlatzieren(chatAlisa, 620, 350, 50, 50, 1, false);
		Util_Help.guiPlatzieren(botanikHubLbl, 0, 0, 250, 250, 1, false);
		Util_Help.guiPlatzieren(erinnerungBtn, -615, -160, 295, 160, 1, false);

		// CSS Styling
//		chatAlisa.getStyleClass().add("version-label");
		botanikHubLbl.getStyleClass().add("willkommen-label");
		botanikkalenderBtn.getStyleClass().add("kalender-label");
		botanikHubBtn.getStyleClass().add("kalender-label");
		profilVerwaltenBtn.getStyleClass().add("kalender-label");
		abmeldenBtn.getStyleClass().add("kalender-label");
		erinnerungBtn.getStyleClass().add("kalender-label");

		// Animationseffekte -> Util_Animations
		botanikHubBtn.setOnMouseEntered(e -> Util_Animations.zoomInAnimation(botanikHubBtn, 1.3, Duration.seconds(0.25)));
		botanikHubBtn.setOnMouseExited(e -> Util_Animations.zoomOutAnimation(botanikHubBtn, Duration.seconds(0.25)));

		profilVerwaltenBtn.setOnMouseEntered(e -> Util_Animations.zoomInAnimation(profilVerwaltenBtn, 1.3, Duration.seconds(0.25)));
		profilVerwaltenBtn.setOnMouseExited(e -> Util_Animations.zoomOutAnimation(profilVerwaltenBtn, Duration.seconds(0.25)));

		botanikkalenderBtn.setOnMouseEntered(e -> Util_Animations.zoomInAnimation(botanikkalenderBtn, 1.3, Duration.seconds(0.25)));
		botanikkalenderBtn.setOnMouseExited(e -> Util_Animations.zoomOutAnimation(botanikkalenderBtn, Duration.seconds(0.25)));

		abmeldenBtn.setOnMouseEntered(e -> Util_Animations.zoomInAnimation(abmeldenBtn, 1.3, Duration.seconds(0.25)));
		abmeldenBtn.setOnMouseExited(e -> Util_Animations.zoomOutAnimation(abmeldenBtn, Duration.seconds(0.25)));

		erinnerungBtn.setOnMouseEntered(e -> Util_Animations.zoomInAnimation(erinnerungBtn, 1.3, Duration.seconds(0.25)));
		erinnerungBtn.setOnMouseExited(e -> Util_Animations.zoomOutAnimation(erinnerungBtn, Duration.seconds(0.25)));

		// Eventhandler: botanikhub, profilVerwalten, botanikkalender, erinnerungen
		botanikHubBtn.setOnMousePressed(e -> {
			Pflanzen_Verwalten_Dialog dialog = new Pflanzen_Verwalten_Dialog(benutzer);
			dialog.showAndWait();
		});

		profilVerwaltenBtn.setOnMousePressed(e -> {
			Benutzer_Profil_bearbeiten_Dialog dialog = new Benutzer_Profil_bearbeiten_Dialog(new BenutzerFX(benutzer));
			dialog.showAndWait();
		});

		botanikkalenderBtn.setOnMousePressed(e -> {
			Botanikkalender_Dialog dialog = new Botanikkalender_Dialog(new PflanzeFX(new Pflanze()));
			dialog.showAndWait();
		});

		erinnerungBtn.setOnMousePressed(e -> {
			Erinnerung_Verwalten_Dialog dialog = new Erinnerung_Verwalten_Dialog(benutzer);
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
		userStack.getChildren().addAll(profilVerwaltenBtn, botanikHubBtn, botanikkalenderBtn, abmeldenBtn, botanikHubLbl, erinnerungBtn);
		userStack.getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());

		Scene adminScene = new Scene(userStack);
		stage.setScene(adminScene);
		stage.show();
	}
}
