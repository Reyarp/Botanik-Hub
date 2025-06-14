package GUI.Startseiten;

import GUI.BotanikHub_Client;
import GUI.HauptDialoge.BenutzerDialoge.Benutzer_Verwalten_Dialog;
import GUI.HauptDialoge.KalenderDialoge.Botanikkalender_Dialog;
import GUI.HauptDialoge.PflanzenDialoge.Pflanzen_Verwalten_Dialog;
import GUI.Utilitys.Util_Animations;
import GUI.Utilitys.Util;
import Modell.Benutzer;
import Modell.Pflanze;
import ModellFX.PflanzeFX;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Admin_Startseite {

	public static void oeffneAdminSeite(StackPane pane, Stage stage, Benutzer benutzer) {

		/*--------------------------------------------
		 * StackPane & Hintergrund
		 *-------------------------------------------- */
		StackPane adminStack = new StackPane();
		Util.hintergrundSetzen(adminStack);

		/*--------------------------------------------
		 * GUI-Elemente (Icons & Label)
		 *-------------------------------------------- */
		ImageView benutzerVerwalten = new ImageView(new Image(BotanikHub_Client.class.getResource("/benutzer.png").toString()));
		ImageView pflanzenVerwalten = new ImageView(new Image(BotanikHub_Client.class.getResource("/plant-icon.png").toString()));
		ImageView abmelden = new ImageView(new Image(BotanikHub_Client.class.getResource("/logout.png").toString()));
		ImageView kalenderIcon = new ImageView(new Image(BotanikHub_Client.class.getResource("/Kalender.png").toString()));
		ImageView chatAlisa = new ImageView(new Image(BotanikHub_Client.class.getResource("/chat.png").toString()));
		Label botanikHub = new Label("Botanik-Hub");

		/*--------------------------------------------
		 * Positionierung der Elemente
		 *-------------------------------------------- */
		Util.guiPlatzieren(benutzerVerwalten, -620, 120, 260, 180, 1, true);
		Util.guiPlatzieren(pflanzenVerwalten, -620, 270, 260, 150, 1, false);
		Util.guiPlatzieren(abmelden, -660, -350, 140, 80, 1, false);
		Util.guiPlatzieren(kalenderIcon, -620, -30, 260, 150, 1, false);
		Util.guiPlatzieren(chatAlisa, 620, 350, 50, 50, 1, false);
		Util.guiPlatzieren(botanikHub, 0, 0, 250, 250, 1, false);

		/*--------------------------------------------
		 * CSS Styling
		 *-------------------------------------------- */
		chatAlisa.getStyleClass().add("version-label");
		botanikHub.getStyleClass().add("willkommen-label");
		kalenderIcon.getStyleClass().add("kalender-label");
		pflanzenVerwalten.getStyleClass().add("kalender-label");
		benutzerVerwalten.getStyleClass().add("kalender-label");
		abmelden.getStyleClass().add("kalender-label");

		/*--------------------------------------------
		 * Hover-Zoom Effekte
		 *-------------------------------------------- */
		pflanzenVerwalten.setOnMouseEntered(e -> Util_Animations.zoomInAnimation(pflanzenVerwalten, 1.3, Duration.seconds(0.25)));
		pflanzenVerwalten.setOnMouseExited(e -> Util_Animations.zoomOutAnimation(pflanzenVerwalten, Duration.seconds(0.25)));

		benutzerVerwalten.setOnMouseEntered(e -> Util_Animations.zoomInAnimation(benutzerVerwalten, 1.3, Duration.seconds(0.25)));
		benutzerVerwalten.setOnMouseExited(e -> Util_Animations.zoomOutAnimation(benutzerVerwalten, Duration.seconds(0.25)));

		kalenderIcon.setOnMouseEntered(e -> Util_Animations.zoomInAnimation(kalenderIcon, 1.3, Duration.seconds(0.25)));
		kalenderIcon.setOnMouseExited(e -> Util_Animations.zoomOutAnimation(kalenderIcon, Duration.seconds(0.25)));

		abmelden.setOnMouseEntered(e -> Util_Animations.zoomInAnimation(abmelden, 1.3, Duration.seconds(0.25)));
		abmelden.setOnMouseExited(e -> Util_Animations.zoomOutAnimation(abmelden, Duration.seconds(0.25)));

		/*--------------------------------------------
		 * Eventhandler
		 *-------------------------------------------- */
		pflanzenVerwalten.setOnMousePressed(e -> {
			Pflanzen_Verwalten_Dialog dialog = new Pflanzen_Verwalten_Dialog(benutzer);
			dialog.showAndWait();
		});

		benutzerVerwalten.setOnMousePressed(e -> {
			Benutzer_Verwalten_Dialog dialog = new Benutzer_Verwalten_Dialog();
			dialog.showAndWait();
		});

		kalenderIcon.setOnMousePressed(e -> {
			Botanikkalender_Dialog dialog = new Botanikkalender_Dialog(new PflanzeFX(new Pflanze()));
			dialog.showAndWait();
		});

		/*--------------------------------------------
		 * Abmelden & Rückkehr zur Startseite
		 *-------------------------------------------- */
		abmelden.setOnMousePressed(e -> {
			Util.alertWindow(AlertType.CONFIRMATION, "Abmelden", "Möchten Sie sich abmelden?").showAndWait();
			StackPane goBack = new StackPane();
			Login_Startseite.erstelleStartSeite(goBack, stage);
			Scene neueScene = new Scene(goBack);
			stage.setScene(neueScene);
			BotanikHub_Client.setBenutzer(null);
		});

		/*--------------------------------------------
		 * Szenenaufbau & Anzeige
		 *-------------------------------------------- */
		adminStack.getChildren().addAll(benutzerVerwalten, pflanzenVerwalten, kalenderIcon, abmelden, chatAlisa, botanikHub);
		adminStack.getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());

		Scene adminScene = new Scene(adminStack);
		stage.setScene(adminScene);
		stage.show();
	}
}
