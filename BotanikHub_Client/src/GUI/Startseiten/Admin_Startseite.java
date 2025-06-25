package GUI.Startseiten;

import java.util.Optional;

import ChatBot.ChatBot_Alisa_Dialog;
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

	public static void oeffneAdminSeite(StackPane pane, Stage arg0, Benutzer benutzer) {

		/*
		 * Dieser Dialog ist die Admin Startseite
		 * Hier hat er Zugriff auf: Botanikkalender, Benutzerverwalung & Pflanzenverwaltung
		 * chatAlisa -> war ein versuch, wird aber nicht in der aktuellen Version enthalten sein
		 */


		// Buttons & Co
		ImageView benutzerVerwaltenBtn = new ImageView(new Image(BotanikHub_Client.class.getResource("/Startseite_Benutzer_Button.png").toString()));
		ImageView pflanzenVerwaltenBtn = new ImageView(new Image(BotanikHub_Client.class.getResource("/Startseite_Pflanzen_Button.png").toString()));
		ImageView abmeldenBtn = new ImageView(new Image(BotanikHub_Client.class.getResource("/Startseite_Logout_Button.png").toString()));
		ImageView botanikkalenderBtn = new ImageView(new Image(BotanikHub_Client.class.getResource("/Startseite_Botanikkalender_Button.png").toString()));
		ImageView chatAlisa = new ImageView(new Image(BotanikHub_Client.class.getResource("/Startseite_Chatbot_Button.png").toString()));	
		ImageView lebensBaumLogo = new ImageView(new Image(BotanikHub_Client.class.getResource("/Login_Startseite_Lebensbaum_Logo.png").toString()));


		Label willkommenLbl = new Label("Willkommen im");
		Label botanikHubLbl = new Label("Botanik-Hub");
		Label benutzerVerwaltenLbl = new Label("Profil verwalten");
		Label botanikkalenderLbl = new Label("Botanikkalender");
		Label pflanzenVerwaltenLbl = new Label("Botanik-Hub");
		Label chatbotLbl = new Label("ChatBot Alisa");
		Label abmeldeLbl = new Label("Abmelden");
		Label aktuellerBenutzer = new Label("Angemeldeter Benutzer: " + benutzer.getBenutzerName());



		// Eigene Methode zum Platzieren der GUI 
		Util_Help.guiPlatzieren(benutzerVerwaltenBtn, -200, 120, 300, 200, 0, true);
		Util_Help.guiPlatzieren(botanikkalenderBtn, -67.5, 120, 300, 165, 0, false);
		Util_Help.guiPlatzieren(pflanzenVerwaltenBtn, 67.5, 120, 300, 167, 0, false);
		Util_Help.guiPlatzieren(chatAlisa, 200, 120, 293, 168, 0, false);

		// Abmeldebutton & Aktueller Benutzer 
		Util_Help.guiPlatzieren(abmeldenBtn, -650, -340, 140, 80, 0, false);
		Util_Help.guiPlatzieren(aktuellerBenutzer, -600, 370, 0, 0, 0, false);

		// Bild & Begrüssungstext
		Util_Help.guiPlatzieren(lebensBaumLogo, 0, -200, 350, 0, 0, true);
		Util_Help.guiPlatzieren(willkommenLbl, 0, -50, 250, 250, 0, false);
		Util_Help.guiPlatzieren(botanikHubLbl, 0, 0, 250, 250, 0, false);

		// Texte für die Buttons
		Util_Help.guiPlatzieren(benutzerVerwaltenLbl, -200, 200, 1, 1, 0, false);
		Util_Help.guiPlatzieren(botanikkalenderLbl, -67.5, 200, 1, 1, 0, false);
		Util_Help.guiPlatzieren(pflanzenVerwaltenLbl, 67.5, 200, 1, 1, 0, false);
		Util_Help.guiPlatzieren(chatbotLbl, 200, 200, 1, 1, 0, false);
		
		Util_Help.guiPlatzieren(abmeldeLbl, -650, -295, 1, 1, 0, false);

		// CSS Styling
		pflanzenVerwaltenBtn.getStyleClass().add("kalender-label");
		benutzerVerwaltenBtn.getStyleClass().add("kalender-label");
		botanikkalenderBtn.getStyleClass().add("kalender-label");
		
		chatAlisa.getStyleClass().add("kalender-label");
		abmeldenBtn.getStyleClass().add("kalender-label");
		
		botanikHubLbl.getStyleClass().add("willkommen-label");
		willkommenLbl.getStyleClass().add("willkommen-label");
		aktuellerBenutzer.getStyleClass().add("version-label");
		
		benutzerVerwaltenLbl.getStyleClass().add("startseite-label");
		botanikkalenderLbl.getStyleClass().add("startseite-label");
		pflanzenVerwaltenLbl.getStyleClass().add("startseite-label");
		chatbotLbl.getStyleClass().add("startseite-label");
		abmeldeLbl.getStyleClass().add("startseite-label");

		// Animationseffekte -> Util_Animations
				pflanzenVerwaltenBtn.setOnMouseEntered(e -> {
					Util_Animations.zoomInAnimation(pflanzenVerwaltenBtn, 1.3, Duration.seconds(0.25));
					pflanzenVerwaltenLbl.setOpacity(1);
				});
				pflanzenVerwaltenBtn.setOnMouseExited(e -> {
					Util_Animations.zoomOutAnimation(pflanzenVerwaltenBtn, Duration.seconds(0.25));
					pflanzenVerwaltenLbl.setOpacity(0);
				});

				benutzerVerwaltenBtn.setOnMouseEntered(e -> {
					Util_Animations.zoomInAnimation(benutzerVerwaltenBtn, 1.3, Duration.seconds(0.25));
					benutzerVerwaltenLbl.setOpacity(1);
				});
				benutzerVerwaltenBtn.setOnMouseExited(e -> {
					Util_Animations.zoomOutAnimation(benutzerVerwaltenBtn, Duration.seconds(0.25));
					benutzerVerwaltenLbl.setOpacity(0);
				});

				botanikkalenderBtn.setOnMouseEntered(e -> {
					Util_Animations.zoomInAnimation(botanikkalenderBtn, 1.3, Duration.seconds(0.25));
					botanikkalenderLbl.setOpacity(1);
				});
				botanikkalenderBtn.setOnMouseExited(e -> {
					Util_Animations.zoomOutAnimation(botanikkalenderBtn, Duration.seconds(0.25));
					botanikkalenderLbl.setOpacity(0);
				});

				abmeldenBtn.setOnMouseEntered(e -> {
					Util_Animations.zoomInAnimation(abmeldenBtn, 1.3, Duration.seconds(0.25));
					abmeldeLbl.setOpacity(1);
				});
				abmeldenBtn.setOnMouseExited(e -> {
					Util_Animations.zoomOutAnimation(abmeldenBtn, Duration.seconds(0.25));
					abmeldeLbl.setOpacity(0);
				});

				chatAlisa.setOnMouseEntered(e -> {
					Util_Animations.zoomInAnimation(chatAlisa, 1.3, Duration.seconds(0.25));
					chatbotLbl.setOpacity(1);
				});
				chatAlisa.setOnMouseExited(e -> {
					Util_Animations.zoomOutAnimation(chatAlisa, Duration.seconds(0.25));
					chatbotLbl.setOpacity(0);
				});
				
				Util_Animations.fadeInAnimation(lebensBaumLogo, Duration.seconds(1), Duration.seconds(1), 0.75);
				Util_Animations.fadeInAnimation(willkommenLbl, Duration.seconds(1), Duration.seconds(1.5), 1);
				Util_Animations.fadeInAnimation(botanikHubLbl, Duration.seconds(1), Duration.seconds(2), 1);
				
				Util_Animations.fadeInAnimation(benutzerVerwaltenBtn, Duration.seconds(1), Duration.seconds(1.25), 0.75);
				Util_Animations.fadeInAnimation(botanikkalenderBtn, Duration.seconds(1), Duration.seconds(1.5), 0.75);
				Util_Animations.fadeInAnimation(pflanzenVerwaltenBtn, Duration.seconds(1), Duration.seconds(1.75), 0.75);
				Util_Animations.fadeInAnimation(chatAlisa, Duration.seconds(1), Duration.seconds(2), 0.75);
				
				
				Util_Animations.fadeInAnimation(abmeldenBtn, Duration.seconds(1), Duration.seconds(1), 0.75);
				Util_Animations.fadeInAnimation(aktuellerBenutzer, Duration.seconds(1), Duration.seconds(1), 0.75);

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

		chatAlisa.setOnMousePressed(e ->{
			new ChatBot_Alisa_Dialog(benutzer).showAndWait();
		});

		/*--------------------------------------------
		 * Abmeldefunktion:
		 * Öffnet ein Bestätigungsfenster ("Möchten Sie sich abmelden?")
		 * Wenn bestätigt:
		 * - Neue StackPane wird erstellt
		 * - Login-Startseite wird auf dieser StackPane aufgebaut
		 * - Neue Scene mit dieser Startseite erstellt
		 * - Scene in die aktuelle Stage geladen (arg0)
		 * - Benutzer wird auf null gesetzt (abgemeldet)
		 *--------------------------------------------*/
		abmeldenBtn.setOnMousePressed(e -> {
			Alert alert = Util_Help.alertWindow(AlertType.CONFIRMATION, "Info: Abmelden", "Möchten Sie sich abmelden?");
			Optional<ButtonType> result = alert.showAndWait();
			if(result.isPresent() && result.get().getButtonData() == ButtonData.YES) {
				StackPane goBack = new StackPane(); 				// Neue leere Oberfläche
				Login_Startseite.erstelleStartSeite(goBack, arg0); 	// Startseite darauf erstellen
				Scene neueScene = new Scene(goBack); 				// Neue Scene mit Startseite
				arg0.setScene(neueScene); 							// Scene setzen (Wechsel)
				BotanikHub_Client.setBenutzer(null); 				// Benutzer-Logout
			}
		});


		// Layout: StackPane -> Robuster bei Elementüberlappungen
		StackPane adminStack = new StackPane();
		// Hintergrund methode -> Util_Help
		Util_Help. hintergrundSetzenAdmin(adminStack);

		// Zusammenbau & Dialogeinstellungen
		adminStack.getChildren().addAll(
				benutzerVerwaltenBtn, benutzerVerwaltenLbl, 
				pflanzenVerwaltenBtn, pflanzenVerwaltenLbl,
				botanikkalenderBtn, botanikkalenderLbl,
				abmeldenBtn, abmeldeLbl,
				botanikHubLbl, 
				willkommenLbl, aktuellerBenutzer,
				chatAlisa, chatbotLbl,
				lebensBaumLogo);
		adminStack.getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());

		Scene adminScene = new Scene(adminStack);
		arg0.setScene(adminScene);
		arg0.getIcons().add(new Image(BotanikHub_Client.class.getResource("/Window_Icon_Lebensbaum.jpg").toString()));
		arg0.show();
	}
}
