package GUI;

import GUI.Startseiten.Login_Startseite;
import Modell.Benutzer;
import TEST_DB.DB_Benutzer;
import TEST_DB.DB_BotanikHub;
import TEST_DB.DB_BotanikKalender;
import TEST_DB.DB_Erinnerungen;
import TEST_DB.DB_Pflanze;
import TEST_DB.DB_PflanzenEntdecken;
import TEST_DB.DB_Verbindungstabellen;
import TEST_DB.DB_Wunschliste;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BotanikHub_Client extends Application {

	/* Globale Benutzer variable -> Für Login Mechanismes und Benutzer weitergabe */
	public static Benutzer aktuellerBenutzer;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage arg0) throws Exception {

		DB_Benutzer.createBenutzer();
		DB_Pflanze.createPflanze();
		
		DB_BotanikHub.createBotanikHub();
		DB_PflanzenEntdecken.createPflanzenEntdecken();
		DB_Wunschliste.createWunschliste();
		
		DB_Erinnerungen.createErinnerung();
		DB_BotanikKalender.createBotanikkalender();
		
		
		
		DB_Verbindungstabellen.createPflanzenTyp();
		DB_Verbindungstabellen.createVermehrung();
		DB_Verbindungstabellen.createVerwendeteTeile();
		
		
		
		/*--------------------------------------------
		 * GUI-Struktur: StackPane als Hauptcontainer
		 *-------------------------------------------- */
		StackPane stack = new StackPane(); 						// Für übereinanderliegende GUI-Elemente
		Login_Startseite.erstelleStartSeite(stack, arg0); 		// Startseite laden
		VBox vb = new VBox(stack);								// VBox um StackPane zu kapseln

		/*--------------------------------------------
		 * Fenster-/Szeneneinstellungen
		 *-------------------------------------------- */
		arg0.setResizable(false); 	// Fenstergröße fixieren
		arg0.getIcons().add(new Image(BotanikHub_Client.class.getResource("/lebensbaum-sticker.jpg").toString()));
		arg0.setTitle("BotanikHub");

		Scene scene = new Scene(vb);
		scene.getStylesheets().add(getClass().getResource("/style.css").toString());

		arg0.setScene(scene);
		arg0.show();
	}

	/*--------------------------------------------
	 * Setter für den global eingeloggten Benutzer
	 *-------------------------------------------- */
	public static void setBenutzer(Benutzer b) {
		aktuellerBenutzer = b;
	}

	/*--------------------------------------------
	 * Getter für den aktuell eingeloggten Benutzer
	 *-------------------------------------------- */
	public static Benutzer getBenutzer() {
		return aktuellerBenutzer;
	}
}
