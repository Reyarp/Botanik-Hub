package Client;

import GUI.Startseiten.Login_Startseite;
import Modell.Benutzer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BotanikHub_Client extends Application {

	/* Globale Benutzer-Variable – wird nach dem Login gesetzt und zur Benutzeridentifikation im gesamten Programm verwendet */
	public static Benutzer aktuellerBenutzer;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage arg0) throws Exception {

		
		// Layout & Zusammenbau & Dialogeinstellungen
		StackPane stack = new StackPane(); 						
		Login_Startseite.erstelleStartSeite(stack, arg0); 		
		VBox vb = new VBox(stack);								

		arg0.setResizable(false); 	// Fenstergröße fixieren
		arg0.getIcons().add(new Image(BotanikHub_Client.class.getResource("/lebensbaum-sticker.jpg").toString()));
		arg0.setTitle("BotanikHub");

		Scene scene = new Scene(vb);
		scene.getStylesheets().add(getClass().getResource("/style.css").toString());

		arg0.setScene(scene);
		arg0.show();
	}

	// setter für eingeloggten Benutzer
	public static void setBenutzer(Benutzer b) {
		aktuellerBenutzer = b;
	}

	// getter für eingeloggten Benutzer
	public static Benutzer getBenutzer() {
		return aktuellerBenutzer;
	}
}
