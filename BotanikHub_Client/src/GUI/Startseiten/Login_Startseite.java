package GUI.Startseiten;

import java.util.Optional;

import Client.BotanikHub_Client;
import GUI.HauptDialoge.BenutzerDialoge.Benutzer_Erstellen_Dialog;
import GUI.Utilitys.Util_Animations;
import GUI.Utilitys.Util_Help;
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

		/*
		 * Das ist der Hauptdialog wo man am Anfang reinkommt
		 * Hier kann man sich entweder Anmelden oder ein neues Benutzerkonto erstellen
		 */


		// Buttons & Co
		Label version = new Label("v 1.0");
		Label willkommen = new Label("Willkommen");
		Label oder = new Label("Oder");
		Button login = new Button("Anmelden");
		Button accountErstellen = new Button("Neues Konto erstellen");

		// Hintergrund setzen & Image
		Image lebensBaumImage = new Image(BotanikHub_Client.class.getResource("/Lebensbaum.png").toString());
		ImageView lebensBaumLogo = new ImageView(lebensBaumImage);
		Util_Help.hintergrundSetzen(stack);
		
		// CSS Styling
		version.getStyleClass().add("version-label");
		willkommen.getStyleClass().add("willkommen-label");
		oder.getStyleClass().add("version-label");
		login.getStyleClass().add("login-button");
		accountErstellen.getStyleClass().add("neues-konto-button");

		// Animationseffekte -> Util_Animations
		Util_Animations.fadeInAnimation(willkommen, Duration.seconds(1), Duration.seconds(1.5), 1);
		Util_Animations.fadeInAnimation(oder, Duration.seconds(1), Duration.seconds(2.35), 1);
		Util_Animations.fadeInAnimation(lebensBaumLogo, Duration.seconds(1), Duration.seconds(1), 0.75);
		Util_Animations.fadeInAnimation(login, Duration.seconds(1), Duration.seconds(2.3), 1);
		Util_Animations.fadeInAnimation(accountErstellen, Duration.seconds(1), Duration.seconds(2.4), 1);

		// Eigene Methode zum Platzieren der GUI 
		Util_Help.guiPlatzieren(lebensBaumLogo, 0, -165, 350, 0, 0, true);
		Util_Help.guiPlatzieren(willkommen, 0, 0, 0, 0, 0, false);
		Util_Help.guiPlatzieren(login, 0, 70, 0, 0, 0, false);
		Util_Help.guiPlatzieren(oder, 0, 107, 20, 20, 0, false);
		Util_Help.guiPlatzieren(accountErstellen, 0, 140, 0, 0, 0, false);
		Util_Help.guiPlatzieren(version, 675, 375, 0, 0, 1, false);

		// Animationseffekte -> Util_Animations
		login.setOnMouseEntered(e -> Util_Animations.zoomInAnimation(login, 1.2, Duration.seconds(0.3)));
		login.setOnMouseExited(e -> Util_Animations.zoomOutAnimation(login, Duration.seconds(0.5)));
		accountErstellen.setOnMouseEntered(e -> Util_Animations.zoomInAnimation(accountErstellen, 1.2, Duration.seconds(0.3)));
		accountErstellen.setOnMouseExited(e -> Util_Animations.zoomOutAnimation(accountErstellen, Duration.seconds(0.5)));

		// Eventhandler: login & accountErstellen
		login.setOnAction(e -> {
			new Login_Dialog(stack, stage).showAndWait();
		});

		accountErstellen.setOnAction(e -> {
			Benutzer_Erstellen_Dialog dialog = new Benutzer_Erstellen_Dialog(new BenutzerFX(new Benutzer()), stack, stage);
			Optional<ButtonType> result = dialog.showAndWait();
			if (result.isPresent() && result.get().getButtonData() == ButtonData.OK_DONE) {
				Util_Help.alertWindow(AlertType.INFORMATION, "Info: Account erstellen", "Benutzer erfolgreich erstellt").showAndWait();
			}
		});

		// Zusammenbau & Dialogeinstellungen
		stack.getChildren().addAll(lebensBaumLogo, willkommen, login, version, accountErstellen, oder);
		stack.getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
	}
}
