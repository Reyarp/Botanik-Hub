package GUI.Startseiten;

import java.util.Optional;

import GUI.BotanikHub_Client;
import GUI.HauptDialoge.BenutzerDialoge.Benutzer_Erstellen_Dialog;
import GUI.Utilitys.Util_Animations;
import GUI.Utilitys.Util;
import Modell.Benutzer;
import ModellFX.BenutzerFX;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Login_Startseite {

	public static void erstelleStartSeite(StackPane stack, Stage stage) {

		/*--------------------------------------------
		 * Bilder & Hintergrund
		 *-------------------------------------------- */
		Image lebensBaumImage = new Image(BotanikHub_Client.class.getResource("/Lebensbaum.png").toString());
		ImageView lebensBaumLogo = new ImageView(lebensBaumImage);
		Util.hintergrundSetzen(stack);

		/*--------------------------------------------
		 * GUI-Elemente (Labels & Buttons)
		 *-------------------------------------------- */
		Label version = new Label("v 1.0");
		Label willkommen = new Label("Willkommen");
		Label oder = new Label("Oder");
		Button login = new Button("Anmelden");
		Button accountErstellen = new Button("Neues Konto erstellen");

		/*--------------------------------------------
		 * CSS Styling
		 *-------------------------------------------- */
		version.getStyleClass().add("version-label");
		willkommen.getStyleClass().add("willkommen-label");
		oder.getStyleClass().add("version-label");
		login.getStyleClass().add("login-button");
		accountErstellen.getStyleClass().add("neues-konto-button");

		/*--------------------------------------------
		 * Animationen
		 *-------------------------------------------- */
		Util_Animations.fadeInAnimation(willkommen, Duration.seconds(1), Duration.seconds(1.5), 1);
		Util_Animations.fadeInAnimation(oder, Duration.seconds(1), Duration.seconds(2.35), 1);
		Util_Animations.fadeInAnimation(lebensBaumLogo, Duration.seconds(1), Duration.seconds(1), 0.75);
		Util_Animations.fadeInAnimation(login, Duration.seconds(1), Duration.seconds(2.3), 1);
		Util_Animations.fadeInAnimation(accountErstellen, Duration.seconds(1), Duration.seconds(2.4), 1);

		/*--------------------------------------------
		 * GUI Platzierung
		 *-------------------------------------------- */
		Util.guiPlatzieren(lebensBaumLogo, 0, -165, 350, 0, 0, true);
		Util.guiPlatzieren(willkommen, 0, 0, 0, 0, 0, false);
		Util.guiPlatzieren(login, 0, 70, 0, 0, 0, false);
		Util.guiPlatzieren(oder, 0, 107, 20, 20, 0, false);
		Util.guiPlatzieren(accountErstellen, 0, 140, 0, 0, 0, false);
		Util.guiPlatzieren(version, 675, 375, 0, 0, 1, false);

		/*--------------------------------------------
		 * Hover-Zoom Effekte
		 *-------------------------------------------- */
		login.setOnMouseEntered(e -> Util_Animations.zoomInAnimation(login, 1.2, Duration.seconds(0.3)));
		login.setOnMouseExited(e -> Util_Animations.zoomOutAnimation(login, Duration.seconds(0.5)));
		accountErstellen.setOnMouseEntered(e -> Util_Animations.zoomInAnimation(accountErstellen, 1.2, Duration.seconds(0.3)));
		accountErstellen.setOnMouseExited(e -> Util_Animations.zoomOutAnimation(accountErstellen, Duration.seconds(0.5)));

		/*--------------------------------------------
		 * Eventhandler
		 *-------------------------------------------- */
		login.setOnAction(e -> {
			Login_Dialog dialog = new Login_Dialog(stack, stage);
			Optional<ButtonType> result = dialog.showAndWait();
		});

		accountErstellen.setOnAction(e -> {
			Benutzer_Erstellen_Dialog dialog = new Benutzer_Erstellen_Dialog(new BenutzerFX(new Benutzer()), stack, stage);
			Optional<ButtonType> result = dialog.showAndWait();
			if (result.isPresent() && result.get().getButtonData() == ButtonData.OK_DONE) {
				Util.alertWindow(AlertType.INFORMATION, "Glückwunsch", "Benutzer erfolgreich erstellt").showAndWait();
			}
		});

		/*--------------------------------------------
		 * Aufbau der Szene
		 *-------------------------------------------- */
		stack.getChildren().addAll(lebensBaumLogo, willkommen, login, version, accountErstellen, oder);
		stack.getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
	}
}
