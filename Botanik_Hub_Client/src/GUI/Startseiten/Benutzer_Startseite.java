package GUI.Startseiten;

import GUI.BotanikHub_Client;
import GUI.HauptDialoge.BenutzerDialoge.Benutzer_Profil_bearbeiten_Dialog;
import GUI.HauptDialoge.ErinnerungsDialoge.Erinnerung_Verwalten_Dialog;
import GUI.HauptDialoge.KalenderDialoge.Botanikkalender_Dialog;
import GUI.HauptDialoge.PflanzenDialoge.Pflanzen_Verwalten_Dialog;
import GUI.Utilitys.Util;
import GUI.Utilitys.Util_Animations;
import Modell.Benutzer;
import Modell.Pflanze;
import ModellFX.BenutzerFX;
import ModellFX.PflanzeFX;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Benutzer_Startseite {

	public static void oeffneBenutzerSeite(StackPane pane, Stage stage, Benutzer benutzer) {

		/*--------------------------------------------
		 * StackPane & Hintergrund
		 *-------------------------------------------- */
		StackPane userStack = new StackPane();
		Util.hintergrundSetzen(userStack);

		/*--------------------------------------------
		 * GUI-Elemente (Icons & Label)
		 *-------------------------------------------- */
		ImageView profilVerwalten = new ImageView(new Image(BotanikHub_Client.class.getResource("/benutzer.png").toString()));
		ImageView BotanikHub = new ImageView(new Image(BotanikHub_Client.class.getResource("/plant-icon.png").toString()));
		ImageView abmelden = new ImageView(new Image(BotanikHub_Client.class.getResource("/logout.png").toString()));
		ImageView kalenderIcon = new ImageView(new Image(BotanikHub_Client.class.getResource("/Kalender.png").toString()));
		ImageView chatAlisa = new ImageView(new Image(BotanikHub_Client.class.getResource("/chat.png").toString()));
		ImageView erinnerungIcon = new ImageView(new Image(BotanikHub_Client.class.getResource("/erinnerungicon.png").toString()));
		Label botanikHub = new Label("Botanik-Hub");

		/*--------------------------------------------
		 * Positionierung der Elemente
		 *-------------------------------------------- */
		Util.guiPlatzieren(profilVerwalten, -620, 120, 260, 180, 1, true);
		Util.guiPlatzieren(BotanikHub, -620, 270, 260, 150, 1, false);
		Util.guiPlatzieren(abmelden, -660, -350, 140, 80, 1, false);
		Util.guiPlatzieren(kalenderIcon, -620, -30, 260, 150, 1, false);
		Util.guiPlatzieren(chatAlisa, 620, 350, 50, 50, 1, false);
		Util.guiPlatzieren(botanikHub, 0, 0, 250, 250, 1, false);
		Util.guiPlatzieren(erinnerungIcon, -615, -160, 295, 160, 1, false);

		/*--------------------------------------------
		 * CSS Styling
		 *-------------------------------------------- */
		chatAlisa.getStyleClass().add("version-label");
		botanikHub.getStyleClass().add("willkommen-label");
		kalenderIcon.getStyleClass().add("kalender-label");
		BotanikHub.getStyleClass().add("kalender-label");
		profilVerwalten.getStyleClass().add("kalender-label");
		abmelden.getStyleClass().add("kalender-label");
		erinnerungIcon.getStyleClass().add("kalender-label");

		/*--------------------------------------------
		 * Hover-Zoom Animationen
		 *-------------------------------------------- */
		BotanikHub.setOnMouseEntered(e -> Util_Animations.zoomInAnimation(BotanikHub, 1.3, Duration.seconds(0.25)));
		BotanikHub.setOnMouseExited(e -> Util_Animations.zoomOutAnimation(BotanikHub, Duration.seconds(0.25)));

		profilVerwalten.setOnMouseEntered(e -> Util_Animations.zoomInAnimation(profilVerwalten, 1.3, Duration.seconds(0.25)));
		profilVerwalten.setOnMouseExited(e -> Util_Animations.zoomOutAnimation(profilVerwalten, Duration.seconds(0.25)));

		kalenderIcon.setOnMouseEntered(e -> Util_Animations.zoomInAnimation(kalenderIcon, 1.3, Duration.seconds(0.25)));
		kalenderIcon.setOnMouseExited(e -> Util_Animations.zoomOutAnimation(kalenderIcon, Duration.seconds(0.25)));

		abmelden.setOnMouseEntered(e -> Util_Animations.zoomInAnimation(abmelden, 1.3, Duration.seconds(0.25)));
		abmelden.setOnMouseExited(e -> Util_Animations.zoomOutAnimation(abmelden, Duration.seconds(0.25)));

		erinnerungIcon.setOnMouseEntered(e -> Util_Animations.zoomInAnimation(erinnerungIcon, 1.3, Duration.seconds(0.25)));
		erinnerungIcon.setOnMouseExited(e -> Util_Animations.zoomOutAnimation(erinnerungIcon, Duration.seconds(0.25)));

		/*--------------------------------------------
		 * Eventhandler
		 *-------------------------------------------- */
		BotanikHub.setOnMousePressed(e -> {
			Pflanzen_Verwalten_Dialog dialog = new Pflanzen_Verwalten_Dialog(benutzer);
			dialog.showAndWait();
		});

		profilVerwalten.setOnMousePressed(e -> {
			Benutzer_Profil_bearbeiten_Dialog dialog = new Benutzer_Profil_bearbeiten_Dialog(new BenutzerFX(benutzer));
			dialog.showAndWait();
		});

		kalenderIcon.setOnMousePressed(e -> {
			Botanikkalender_Dialog dialog = new Botanikkalender_Dialog(new PflanzeFX(new Pflanze()));
			dialog.showAndWait();
		});

		erinnerungIcon.setOnMousePressed(e -> {
			Erinnerung_Verwalten_Dialog dialog = new Erinnerung_Verwalten_Dialog(benutzer);
			dialog.showAndWait();
		});

		abmelden.setOnMousePressed(e -> {
			Util.alertWindow(AlertType.CONFIRMATION, "Abmelden", "Möchten Sie sich abmelden?").showAndWait();
			StackPane goBack = new StackPane();
			Login_Startseite.erstelleStartSeite(goBack, stage);
			Scene neueScene = new Scene(goBack);
			stage.setScene(neueScene);
			BotanikHub_Client.setBenutzer(null);
		});

		/*--------------------------------------------
		 * Aufbau & Szenenwechsel
		 *-------------------------------------------- */
		userStack.getChildren().addAll(profilVerwalten, BotanikHub, kalenderIcon, abmelden, chatAlisa, botanikHub, erinnerungIcon);
		userStack.getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());

		Scene adminScene = new Scene(userStack);
		stage.setScene(adminScene);
		stage.show();
	}
}
