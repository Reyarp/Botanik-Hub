package ChatBot;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import Client.BotanikHub_Client;
import Enum.BenutzerTyp;
import Enum.Kalendertyp;
import Enum.Month;
import Enum.Pflanzentyp;
import Enum.Vermehrungsarten;
import Enum.Vertraeglichkeit;
import Enum.VerwendeteTeile;
import GUI.Utilitys.Util_Help;
import Modell.Benutzer;
import Modell.Pflanze;
import ServiceFunctions.Service_Benutzer;
import ServiceFunctions.Service_BotanikHub;
import ServiceFunctions.Service_Pflanze;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class ChatBot_Alisa_Dialog extends Dialog<ButtonType>{

	private ArrayList<Pflanze> alPflanze;
	

	public ChatBot_Alisa_Dialog(Benutzer benutzer) {

		/*
		 * Dies ist mein Chatbot
		 * Es befindet sich eine methode die mit String und Arraylist arbeitet
		 * String ist für die Frage und die ArrayList dient um durch zu iterieren um die passende Pflanze zu finden
		 */


		// Buttons & Co
		Button abbrechen = new Button("Schließen");
		Button senden = new Button("Senden");
		senden.setDisable(true);

		TextField chatTxt = new TextField();
		chatTxt.setPromptText("Frage stellen");

		TextArea chatArea = new TextArea();
		chatArea.setPrefSize(400, 450);
		chatArea.setEditable(false);
		chatArea.setWrapText(true);
		
		// String array mit Befehlen
		String[] hilfeLbls = {
				"hilfe", "funktion", "was kannst du", "fragen", "hallo alisa", "hi", "hey", "hallöchen",
				"wieviele pflanzen", "meine pflanzen", "botanikhub", "besitze", "aktueller stand", 
				"wieviele benutzer", "benutzer",
				"wuchsbreite", "wuchshöhe", "höhe", "lebensdauer", "lichtbedarf", "wasserbedarf", 
				"giftig", "nicht giftig", "pflanzentyp", "typ pflanze", "verwendete teile", 
				"teile", "winterhart", "frost", "verträglichkeit", "düngung", "düngen", 
				"vermehrung", "vermehrungsart", "vermehrungsmethode",
				"aussaat", "aussäen", "blüte", "blüht", "ernte", "ernten", 
				"rückschnitt", "zurückschneiden", "pflegeschnitt",
				"botanischer name", "botanik", "was weißt du über", "informationen", "erzähl mir was über", "info"
			};
		
		Random random = new Random();
		// Hashset für Zufallszahlen erstellen
		HashSet<Integer> zufallSet = new HashSet<>();
		
		// 9 zufallszahlen erzeugen
		while(zufallSet.size() < 9) {
			int zz = random.nextInt(hilfeLbls.length);
			zufallSet.add(zz);
		}
		
		// in eine Liste konventieren -> streamen
		List<String> zufallsList = zufallSet.stream()
				.map(i -> hilfeLbls[i]).toList();
		
		// VBox für die Anzeige
		VBox helpBox = new VBox();
		helpBox.setSpacing(5);
		
		// Label aufbau -> 4 pro zeile
		HBox zeile = null;
		for(int i = 0; i < zufallsList.size(); i++) {
			if(i % 4 == 0) {
				// neue Zeile pro 4 Labels
				zeile = new HBox();
				zeile.setSpacing(10);
			}
			// Labels erzeugen -> mit List befüllen
			Label helpLbl = new Label(zufallsList.get(i));
			helpLbl.getStyleClass().add("help-label");
			zeile.getChildren().add(helpLbl);
			
			if (i % 4 == 3 || i == zufallsList.size() - 1) {
				helpBox.getChildren().add(zeile);
			}
		}

		// CSS Styling
		abbrechen.getStyleClass().add("kalender-dialog-button-cancel");
		senden.getStyleClass().add("kalender-dialog-button-ok");
		chatArea.getStyleClass().add("textarea-chatbot");

		// Pflanzendaten aus der Datenbank holen
		try {
			alPflanze = Service_Pflanze.getPflanze();
		} catch (SQLException e) {
			Util_Help.alertWindow(AlertType.ERROR, "Fehler: ChatBot (GET Pflanze)", e.getMessage());
		}

		// Eventhandler
		senden.setOnAction(e ->{
			String frage = chatTxt.getText().trim();

			// Frage anzeigen
			chatArea.appendText(benutzer.getBenutzerName() + ": " + frage + "\n");

			// Chatbot denkt nach :D
			chatArea.appendText("Alisa denkt nach...");

			// textfield löschen
			chatTxt.clear();

			// Animation einbauen
			PauseTransition pause = new PauseTransition(Duration.seconds(2));
			pause.setOnFinished(ev ->{
				// Methode aufrufen -> unten
				String antwort = frage(frage, alPflanze, benutzer);

				// Letzte Zeile löschen (Alisa denkt nach...) -> für mehr Übersicht
				ArrayList<String> line = new ArrayList<>(List.of(chatArea.getText().split("\n")));
				if(!line.isEmpty() && line.get(line.size() - 1).startsWith("Alisa denkt")) {
					// Letze zeile entfernen
					line.remove(line.size() -1); 
					chatArea.clear();
					chatArea.appendText(String.join("\n", line) + "\n");
				}

				// Amtwort
				chatArea.appendText("Alisa: " + antwort + "\n\n"); 
			});
			// Animation starten
			pause.play();
		});

		// Eventhandler: abbrechen
		abbrechen.setOnAction(e -> this.setResult(ButtonType.CANCEL));
		this.getDialogPane().getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, e -> {
			this.setResult(ButtonType.CANCEL);
		});

		// Enter taste -> Senden
		chatTxt.setOnAction(e -> senden.fire());

		// Changelistener: senden -> wenn text leer ist
		chatTxt.textProperty().addListener(new ChangeListener<>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				if(arg2 != null) 
					senden.setDisable(arg2 == null || arg2.isEmpty());
			}
		});

		// Layout: AnchorPane
		AnchorPane anchor = new AnchorPane();
		anchor.getChildren().addAll(senden, abbrechen);

		Util_Help.anchorpane(senden, 10.0, null, null, 80.0);
		Util_Help.anchorpane(abbrechen, 10.0, null, null, 1.0);

		// Zusammenbau & Dialogeinstellungen
		VBox vb = new VBox(helpBox, chatArea, chatTxt, anchor);
		vb.setPadding(new Insets(5));
		vb.setSpacing(5);

		this.setTitle("Chatbot Alisa");
		this.getDialogPane().setContent(vb);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("kalender-dialog-layout");
		// Stage holen zum Icon setzen, da ich direkt im Dialog keins setzen kann
		Stage arg1 = (Stage) this.getDialogPane().getScene().getWindow();
		arg1.getIcons().add(new Image(BotanikHub_Client.class.getResource("/Window_Icon_Lebensbaum.jpg").toString()));
	}

	private String frage(String frage, ArrayList<Pflanze> pflanzen, Benutzer benutzer) {


		// << ALLGEMEINER CHAT >> 
		if (frage.toLowerCase().contains("hi alisa") ||
				frage.toLowerCase().contains("hey") ||
				frage.toLowerCase().contains("hallo") ||
				frage.toLowerCase().contains("hallöchen")) {
			return "Hallo " + benutzer.getBenutzerName() + ", wie kann ich dir heute helfen?";
		}

		if(frage.toLowerCase().contains("hilfe") || frage.toLowerCase().contains("helfen")) {
			return """
				Hier ist eine Liste von Befehlen, die ich aktuell verstehe:

				Allgemein:
				• Hi Alisa / Hallo / Hey
				• Was kannst du? / Funktionen / Fragen

				Deine Pflanzen:
				• Wieviele Pflanzen habe ich?
				• Welche Pflanzen besitze ich?
				• Was ist der aktuelle Stand meines Botanikhubs?

				Benutzerinfo:
				• Wieviele Benutzer gibt es?

				Detailfragen zu Pflanzen (z. B. Lavendel):
				• Wie hoch ist Lavendel? / Wuchshöhe von ...
				• Wie breit wird ...? / Wuchsbreite von ...
				• Wie lange lebt ...? / Lebensdauer von ...
				• Ist ... giftig? / Giftigkeit von ...
				• Welcher Pflanzentyp ist ...?
				• Welche Teile werden verwendet? / Verwendete Pflanzenteile
				• Ist ... winterhart? / Frostverträglichkeit
				• Wie oft soll ich ... düngen?
				• Wie kann ich ... vermehren?
				• Was ist der botanische Name von ...?
				• Erzähl mir etwas über ... / Was weißt du über ...

				Kalenderinfos:
				• Wann säe ich ... aus? / Aussaatzeit
				• Wann blüht ...? / Blütezeit
				• Wann kann ich ... ernten? / Erntezeit
				• Wann schneide ich ... zurück? / Rückschnittzeit
				""";
		}


		if (frage.toLowerCase().contains("funktion") || 
				frage.toLowerCase().contains("was kannst du") || 
				frage.toLowerCase().contains("was machst du") || 
				frage.toLowerCase().contains("fragen")) {

			return "Ich kann dir vieles über deine Pflanzen erzählen, von typischen Eigenschaften wie Wuchshöhe oder Lebensdauer über Pflegetipps (Licht -und Wasserbedarf) "
					+ "bis hin zu praktischen Kalenderübersichten: wann säen, wann blühen, wann ernten oder zurückschneiden. Außerdem verrate ich dir, wie du Pflanzen verwenden oder vermehren kannst";
		}

		// << Datenbank abfrage >>
		if(benutzer.getTyp() == BenutzerTyp.BENUTZER) {
			try {
				ArrayList<Pflanze> meinePflanzen = Service_BotanikHub.getBHPflanzen(benutzer.getBenutzerId());

				if(frage.toLowerCase().contains("wieviele pflanzen") || 
						frage.toLowerCase().contains("meine pflanzen") || 
						frage.toLowerCase().contains("datenbank") ||
						frage.toLowerCase().contains("besitze") ||
						frage.toLowerCase().contains("botanikhub")||
						frage.toLowerCase().contains("aktueller stand")) {
					if(meinePflanzen.isEmpty()) {
						return "Du hast noch keine Pflanzen in deinem BotanikHub gespeichert.";
					}

					List<String> pflanzennamen = meinePflanzen.stream()
							.map(Pflanze::getPflanzenName)
							.sorted()
							.toList();

					return "Du hast aktuell " + meinePflanzen.size() + " Pflanze(n) gespeichert: " +
					String.join(", ", pflanzennamen) + ".";
				}
			} catch (SQLException e) {
				Util_Help.alertWindow(AlertType.ERROR, "Fehler: ChatBot (GET BH Pflanze)", e.getMessage());
			}
		} else {
			try {
				ArrayList<Pflanze> meinePflanzen = Service_Pflanze.getPflanze();

				if(frage.toLowerCase().contains("wieviele pflanzen") || 
						frage.toLowerCase().contains("meine pflanzen") ||
						frage.toLowerCase().contains("datenbank") ||
						frage.toLowerCase().contains("besitze") ||
						frage.toLowerCase().contains("aktueller stand")) {
					if(meinePflanzen.isEmpty()) {
						return "Du hast noch keine Pflanzen in der Datenbank gespeichert.";
					}

					List<String> pflanzennamen = meinePflanzen.stream()
							.map(Pflanze::getPflanzenName)
							.sorted()
							.toList();

					return "Es befinden sich aktuell " + meinePflanzen.size() + " Pflanze(n) in der Datenbank: " +
					String.join(", ", pflanzennamen) + ".";
				}
			} catch (SQLException e) {
				Util_Help.alertWindow(AlertType.ERROR, "Fehler: ChatBot (GET BH Pflanze)", e.getMessage());
			}
		}

		// << Benutzer Datenbank >>
		try {
			ArrayList<Benutzer> alleBenutzer = Service_Benutzer.getBenutzer();

			if(frage.toLowerCase().contains("wieviele Benutzer") ||
					frage.toLowerCase().contains("benutzer")){
				if(alleBenutzer.size() == 1 && alleBenutzer.get(0).getBenutzerName().equalsIgnoreCase("admin")) {
					return "Es befinden sich keine anderen Benutzer im System";
				}

				List<String> benutzerNamen = alleBenutzer.stream()
						.map(Benutzer::getBenutzerName)
						.sorted()
						.toList();

				return "Es befinden sich aktuell " + alleBenutzer.size() + " Benutzer im System: " +
				String.join(", ", benutzerNamen) + ".";
			}
		} catch (SQLException e) {
			Util_Help.alertWindow(AlertType.ERROR, "Fehler: ChatBot (GET BH Pflanze)", e.getMessage());
		}


		/*-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

		// Pflanzen streamen um die Pflanzen zu holen die aktuell erstellt wurden
		Optional<Pflanze> gefundenePflanzen = pflanzen.stream()
				.filter(p -> frage.toLowerCase().contains(p.getPflanzenName().toLowerCase()))
				.findFirst();

		// wenn gefunden -> in variable speichern
		if(gefundenePflanzen.isPresent()) {
			Pflanze p = gefundenePflanzen.get();



			/*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

			// << ALLGEMEINE INFO >> 
			if(frage.toLowerCase().matches(".*botan(isch|ischer|ik)?( ?name)? .*")) {
				return "Der botanische Name von " + p.getPflanzenName() + " lautet " + p.getBotanikName() + ".";
			}


			/*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

			// EIGENSCHAFTEN & Pflege

			if(frage.toLowerCase().contains("wuchsbreite") || frage.toLowerCase().contains("breit")) {
				int zufall = (int) (Math.random() * 3) + 1;
				int auswahl = (int) zufall;

				switch(auswahl) {
				case 1:
					return "Die Wuchsbreite von " + p.getPflanzenName() + " beträgt bis zu " + p.getWuchsbreite() + " cm – ideal, wenn du den Platzbedarf im Beet planen möchtest.";
				case 2:
					return p.getPflanzenName() + " kann sich seitlich auf etwa " + p.getWuchsbreite() + " cm ausbreiten – beachte dies bei der Pflanzung.";
				case 3:
					return "Mit einer maximalen Breite von " + p.getWuchsbreite() + " cm gehört " + p.getPflanzenName() + " zu den eher " +
					(p.getWuchsbreite() > 80 ? "ausladenden" : "kompakten") + " Pflanzen.";
				}
			} else if(frage.toLowerCase().contains("wuchshöhe") || frage.toLowerCase().contains("hoch") || frage.toLowerCase().contains("höhe")) {
				int auswahl = (int)(Math.random() * 3) + 1; 

				switch(auswahl) {
				case 1:
					return p.getPflanzenName() + " kann eine beeindruckende Höhe von etwa " + p.getWuchshoehe() + " cm erreichen.";
				case 2:
					return "Die Wuchshöhe dieser Pflanze liegt bei ungefähr " + p.getWuchshoehe() + " cm – das solltest du bei der Standortwahl bedenken.";
				case 3:
					return "Mit bis zu " + p.getWuchshoehe() + " cm zählt " + p.getPflanzenName() + " eher zu den " +
					(p.getWuchshoehe() > 150 ? "größeren" : "kleineren") + " Pflanzen im Garten.";
				}
			} else if(frage.toLowerCase().contains("lebensdauer") || frage.toLowerCase().contains("lebt") || frage.toLowerCase().contains("leben")) {
				int auswahl = (int)(Math.random() * 3) + 1; // Werte 1–3

				switch(auswahl) {
				case 1:
					return p.getPflanzenName() + " zählt zu den " + p.getLebensdauer().toString().toLowerCase() + " Pflanzen – das beeinflusst, wie lange sie im Garten bleibt.";
				case 2:
					return "Die Lebensdauer von " + p.getPflanzenName() + " ist " + p.getLebensdauer().name().toLowerCase() + ", also solltest du sie entsprechend einplanen.";
				case 3:
					return p.getPflanzenName() + " ist eine " + p.getLebensdauer().name().toLowerCase() + " Pflanze – das bedeutet, sie lebt typischerweise " +
					(p.getLebensdauer().name().equalsIgnoreCase("einjährig") ? "nur eine Saison." :
						p.getLebensdauer().name().equalsIgnoreCase("zweijährig") ? "zwei Jahre." :
							"mehrere Jahre und kommt jedes Jahr wieder.");
				}
			} else if(frage.toLowerCase().contains("lichtbedarf") || frage.toLowerCase().contains("licht") || frage.toLowerCase().contains("beleuchtung")) {
				int auswahl = (int)(Math.random() * 3) + 1;

				switch(auswahl) {
				case 1:
					return p.getPflanzenName() + " bevorzugt einen Standort mit " + p.getLichtbedarf().toString().toLowerCase() + "en Bedingungen – das solltest du bei der Platzwahl berücksichtigen.";
				case 2:
					return p.getPflanzenName() + " gedeiht am besten bei " + p.getLichtbedarf().name().toLowerCase() + "en" + " Lichtverhältnissen – also z. B. "
					+ (p.getLichtbedarf().getBeschreibung().equalsIgnoreCase("sonnig") ? "an einem offenen, sonnigen Platz." :
						p.getLichtbedarf().getBeschreibung().equalsIgnoreCase("halbschattig") ? "unter Bäumen oder am Rand einer Hecke." :
							"an einem schattigen Ort, z. B. an der Nordseite des Hauses.");
				}
			} else if(frage.toLowerCase().contains("wasserbedarf") || frage.toLowerCase().contains("wasser") || frage.toLowerCase().contains("bewässerung")) {
				int auswahl = (int)(Math.random() * 3) + 1;

				switch(auswahl) {
				case 1:
					return p.getPflanzenName() + " hat einen " + p.getWasserbedarf().getBeschreibung() + "en Wasserbedarf – bitte beim Gießen berücksichtigen.";
				case 2:
					return "Der Wasserbedarf dieser Pflanze ist als " + p.getWasserbedarf().getBeschreibung() + " einzustufen. Gleichmäßige Feuchtigkeit ist dabei entscheidend.";
				case 3:
					return p.getPflanzenName() + " kommt mit " +
					(p.getWasserbedarf().getBeschreibung().equals("gering") ? "wenig Wasser aus und eignet sich gut für trockene Standorte." :
						p.getWasserbedarf().getBeschreibung().equals("mittel") ? "mäßiger Wasserversorgung gut zurecht – achte auf regelmäßiges Gießen." :
							"einer hohen Feuchtigkeit gut zurecht und sollte nie austrocknen.");
				}
			} else if(frage.toLowerCase().contains("giftig") || frage.toLowerCase().contains("nicht gifitg")) {
				int auswahl = (int)(Math.random() * 3) + 1;

				switch(auswahl) {
				case 1:
					return p.getPflanzenName() + (p.isGiftig() 
							? " ist giftig und sollte von Kindern und Haustieren ferngehalten werden." 
									: " ist nicht giftig und unbedenklich für Mensch und Tier.");
				case 2:
					return "Was die Giftigkeit betrifft: " + p.getPflanzenName() + (p.isGiftig() 
							? " enthält toxische Stoffe und ist nicht zum Verzehr geeignet." 
									: " gilt als ungiftig und ist auch in Familiengärten gut geeignet.");
				case 3:
					return p.getPflanzenName() + (p.isGiftig() 
							? " kann bei Verzehr oder Hautkontakt gesundheitsschädlich wirken – Vorsicht ist geboten." 
									: " stellt keine Gefahr dar und ist auch für Tiere unkritisch.");
				}
			} else if(frage.toLowerCase().contains("pflanzentyp") || frage.toLowerCase().contains("typ pflanze")) {
				int auswahl = (int)(Math.random() * 3) + 1;

				// Pflanzentypen sauber per Beschreibung ausgeben
				List<String> typNamen = p.getPflanzenTyp().stream()
						.map(Pflanzentyp::getBeschreibung)
						.toList();

				// in Variable speichern
				String typText;
				// Wenn nur 1 Pflanzentyp -> eine holen
				if (typNamen.size() == 1) {
					typText = typNamen.get(0);
					// Wenn mehrere -> String.join
				} else {
					typText = String.join(", ", typNamen.subList(0, typNamen.size() - 1)) + " und " + typNamen.get(typNamen.size() - 1);
				}

				switch(auswahl) {
				case 1:
					return p.getPflanzenName() + " zählt zu folgenden Pflanzentypen: " + typText + ".";
				case 2:
					return "Diese Pflanze wird typischerweise als " + typText + " verwendet.";
				case 3:
					return "Typisch für " + p.getPflanzenName() + ": Sie ist eine vielseitige " + typText + ".";
				}
			} else if(frage.toLowerCase().contains("winterhart") || frage.toLowerCase().contains("frost") || frage.toLowerCase().contains("verträglichkeit")) {
				int auswahl = (int)(Math.random() * 3) + 1;

				switch(auswahl) {
				case 1:
					return p.getPflanzenName() + (p.getVertraeglichkeit() == Vertraeglichkeit.WINTERHART
					? " ist winterhart und übersteht auch Minusgrade im Freien."
							: " ist nicht winterhart und sollte vor Frost geschützt werden.");
				case 2:
					return "Was die Winterhärte betrifft: " + p.getPflanzenName() + (p.getVertraeglichkeit() == Vertraeglichkeit.WINTERHART
					? " ist robust gegenüber Kälte und kann draußen überwintern."
							: " verträgt keinen Frost und muss im Winter geschützt oder hereingeholt werden.");
				case 3:
					return p.getPflanzenName() + (p.getVertraeglichkeit() == Vertraeglichkeit.WINTERHART
					? " eignet sich gut für ganzjährige Pflanzung im Freiland."
							: " sollte bei Frostgefahr an einem geschützten Ort stehen.");
				}
			} else if(frage.toLowerCase().contains("verwendete") || frage.toLowerCase().contains("teile") || frage.toLowerCase().contains("teile")) {
				int auswahl = (int)(Math.random() * 3) + 1;

				// Beschreibungen zusammensetzen, z. B. „Blüte, Blatt und Wurzel“
				List<String> teile = p.getVerwendeteTeile().stream()
						.map(VerwendeteTeile::getBeschreibung)
						.toList();

				String teileText;
				if (teile.size() == 1) {
					teileText = teile.get(0);
				} else {
					teileText = String.join(", ", teile.subList(0, teile.size() - 1)) + " und " + teile.get(teile.size() - 1);
				}

				switch(auswahl) {
				case 1:
					return "Bei " + p.getPflanzenName() + " werden folgende Pflanzenteile verwendet: " + teileText + ".";
				case 2:
					return p.getPflanzenName() + " nutzt man typischerweise wegen ihrer/seiner " + teileText + ".";
				case 3:
					return "Verwendet werden vor allem: " + teileText + " – je nach Anwendung und Pflanzentyp.";
				}
			} else if(frage.toLowerCase().contains("düngung") || frage.toLowerCase().contains("düngen")) {
				int auswahl = (int)(Math.random() * 3) + 1;
				String duengrhythmus = p.getDuengung().getBeschreibung();

				switch(auswahl) {
				case 1:
					return p.getPflanzenName() + " sollte " + duengrhythmus + " gedüngt werden, um gesund zu wachsen.";
				case 2:
					return "Für ein optimales Wachstum empfiehlt sich bei " + p.getPflanzenName() + " eine " + duengrhythmus + "e Düngung.";
				case 3:
					return "Der Düngebedarf von " + p.getPflanzenName() + " liegt bei einer " + duengrhythmus + "en Versorgung – bitte regelmäßig beachten.";
				}
			} else if(frage.toLowerCase().contains("vermehrung") || frage.toLowerCase().contains("vermehrungsmethode") || frage.toLowerCase().contains("vermehrungsart")) {
				int auswahl = (int)(Math.random() * 3) + 1;

				List<String> methoden = p.getVermehrung().stream()
						.map(Vermehrungsarten::getBeschreibung)
						.toList();

				String methodenText;
				if (methoden.size() == 1) {
					methodenText = methoden.get(0);
				} else {
					methodenText = String.join(", ", methoden.subList(0, methoden.size() - 1)) + " und " + methoden.get(methoden.size() - 1);
				}

				switch(auswahl) {
				case 1:
					return "Die Pflanze lässt sich durch folgende Methoden vermehren: " + methodenText + ".";
				case 2:
					return p.getPflanzenName() + " kann vermehrt werden über " + methodenText + " – je nach Erfahrung und Saison.";
				case 3:
					return "Mögliche Vermehrungsarten bei " + p.getPflanzenName() + ": " + methodenText + ". Informiere dich je nach Methode über die beste Vorgehensweise.";
				}
			}

			/*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/			

			// << KALENDER >> -> Kalenderinhalte holen über Methode -> unten
			List<Month> aussaat = getMonateZuTyp(p, Kalendertyp.AUSSAAT);
			List<Month> bluete = getMonateZuTyp(p, Kalendertyp.BLUETE);
			List<Month> ernte = getMonateZuTyp(p, Kalendertyp.ERNTE);
			List<Month> rueckschnitt = getMonateZuTyp(p, Kalendertyp.RUECKSCHNITT);

			if(frage.toLowerCase().contains("aussaat") || frage.toLowerCase().contains("aussäen")) {
				if(!aussaat.isEmpty()) {
					// Ersten monat holen
					Month von = aussaat.get(0);
					// Letzen monat holen
					Month bis = aussaat.get(aussaat.size() -1);
					int zufall = (int) (Math.random() * 5) +1;

					switch (zufall) {
					case 1 -> {
						return "Die beste Zeit, um " + p.getPflanzenName() + " auszusäen, ist von " + von + " bis " + bis + ".";
					}
					case 2 -> {
						return "Wenn du " + p.getPflanzenName() + " anbauen möchtest, säe sie am besten zwischen " + von + " und " + bis + " aus.";
					}
					case 3 -> {
						return "Aussaatzeit für " + p.getPflanzenName() + ": " + von + " bis " + bis + ". Viel Erfolg beim Pflanzen!";
					}
					case 4 -> {
						return p.getPflanzenName() + " kannst du idealerweise zwischen " + von + " und " + bis + " aussäen.";
					}
					case 5 -> {
						return "Von " + von + " bis " + bis + " ist der perfekte Zeitraum, um " + p.getPflanzenName() + " auszusäen.";
					}
					}
				}
			} else if(frage.toLowerCase().contains("blüht") || frage.toLowerCase().contains("blüte")) {
				if(!bluete.isEmpty()) {
					Month von = bluete.get(0);
					Month bis = bluete.get(bluete.size() -1);
					int zufall = (int) (Math.random() * 5) + 1; 

					switch (zufall) {
					case 1 -> {
						return p.getPflanzenName() + " blüht von " + von + " bis " + bis + ".";
					}
					case 2 -> {
						return "Die Blütezeit von " + p.getPflanzenName() + " liegt zwischen " + von + " und " + bis + ".";
					}
					case 3 -> {
						return "Von " + von + " bis " + bis + " zeigt " + p.getPflanzenName() + " ihre schönsten Blüten.";
					}
					case 4 -> {
						return "Zwischen " + von + " und " + bis + " kannst du die Blüte von " + p.getPflanzenName() + " bewundern.";
					}
					case 5 -> {
						return p.getPflanzenName() + " entfaltet ihre Blütenpracht von " + von + " bis " + bis + ".";
					}
					}
				}
			} else if(frage.toLowerCase().contains("ernte") || frage.toLowerCase().contains("ernten")) {
				if(!ernte.isEmpty()) {
					Month von = ernte.get(0);
					Month bis = ernte.get(ernte.size() -1);
					int zufall = (int) (Math.random() * 5) +1;

					switch (zufall) {
					case 1 -> {
						return "Du kannst " + p.getPflanzenName() + " zwischen " + von + " und " + bis + " ernten.";
					}
					case 2 -> {
						return p.getPflanzenName() + " ist von " + von + " bis " + bis + " erntereif – ran an die Schere!";
					}
					case 3 -> {
						return "Die Erntezeit von " + p.getPflanzenName() + " liegt zwischen " + von + " und " + bis + ".";
					}
					case 4 -> {
						return "Zwischen " + von + " und " + bis + " darfst du dich auf die Ernte von " + p.getPflanzenName() + " freuen!";
					}
					case 5 -> {
						return "Wenn du " + p.getPflanzenName() + " ernten möchtest, ist die beste Zeit dafür von " + von + " bis " + bis + ".";
					}
					}
				}
			} else if(frage.toLowerCase().contains("rückschnitt") || frage.toLowerCase().contains("zurückschneiden") || frage.toLowerCase().contains("pflegeschnitt")) {
				if(!rueckschnitt.isEmpty()) {
					Month von = rueckschnitt.get(0);
					Month bis = rueckschnitt.get(rueckschnitt.size() - 1);
					int zufall = (int) (Math.random() * 5) + 1;

					switch (zufall) {
					case 1 -> {
						return "Der ideale Zeitraum für den Rückschnitt von " + p.getPflanzenName() + " liegt zwischen " + von + " und " + bis + ".";
					}
					case 2 -> {
						return p.getPflanzenName() + " solltest du am besten zwischen " + von + " und " + bis + " zurückschneiden.";
					}
					case 3 -> {
						return "Ein Pflegeschnitt bei " + p.getPflanzenName() + " ist von " + von + " bis " + bis + " empfehlenswert.";
					}
					case 4 -> {
						return "Zwischen " + von + " und " + bis + " kannst du " + p.getPflanzenName() + " für einen gesunden Wuchs zurückschneiden.";
					}
					case 5 -> {
						return "Die Rückschnittzeit für " + p.getPflanzenName() + " erstreckt sich von " + von + " bis " + bis + ".";
					}
					}
				}
			}

			// Volle Info zur Pflanze :)
			if (frage.toLowerCase().matches(".*(erzähl|sage|sag|was weißt|info|informationen).*(über|zu|von).*")) {

				// Vermehrungstext aufbauen
				String vermehrungText = p.getVermehrung() != null && !p.getVermehrung().isEmpty()
						? p.getVermehrung().stream()
								.map(Vermehrungsarten::getBeschreibung)
								.reduce((a, b) -> a + ", " + b).orElse("keine Angabe")
								: "keine Angabe";

				// verwendete Pflanzenteile aufbereiten
				List<String> teile = p.getVerwendeteTeile() != null ? 
						p.getVerwendeteTeile().stream()
						.map(VerwendeteTeile::getBeschreibung)
						.toList() : List.of();
				String teileText;
				if (teile.isEmpty()) {
					teileText = "keine Angabe";
				} else if (teile.size() == 1) {
					teileText = teile.get(0);
				} else {
					teileText = String.join(", ", teile.subList(0, teile.size() - 1)) + " und " + teile.get(teile.size() - 1);
				}

				// Feste Felder aus RadioButtons usw. (immer gesetzt)
				String lebensdauer = p.getLebensdauer().getBeschreibung();
				String licht = p.getLichtbedarf().getBeschreibung() + "e";
				String wasser = p.getWasserbedarf().getBeschreibung() + "em";
				String duenger = p.getDuengung().getBeschreibung();
				String winterhart = p.getVertraeglichkeit() == Vertraeglichkeit.WINTERHART
						? "sie ist winterhart" : "sie ist nicht winterhart";
				String giftText = p.isGiftig()
						? "du solltest beachten, dass sie giftig ist, "
								: "sie ist ungiftig, ";

				// Pflanzentyp (Liste von Enums als String, z. B. „Heilpflanze, Zierpflanze“)
				String pflanzentypText = p.getPflanzenTyp() != null && !p.getPflanzenTyp().isEmpty()
						? String.join(", ", p.getPflanzenTyp().stream().map(Enum::name).toList())
								: "keine Angabe";

				return p.getPflanzenName() + " ist eine " + lebensdauer +
						" Pflanze mit etwa " + p.getWuchshoehe() + " cm Höhe und " + p.getWuchsbreite() + " cm Breite, " +
						"sie liebt " + licht + " Standorte und kommt mit " + wasser + " Wasserbedarf aus, " +
						"gedüngt wird am besten " + duenger + ", " +
						giftText +
						"vermehrt werden kann sie über " + vermehrungText + ", " +
						"ausgesät wird sie idealerweise zwischen " + aussaat + ", " +
						"blühen tut sie von " + bluete + ", " +
						"geerntet wird sie meist zwischen " + ernte + ", " +
						"zurückschneiden solltest du sie zwischen " + rueckschnitt + ". " +
						"Sie gehört zu den " + pflanzentypText + ", " +
						"verwendet werden häufig " + teileText + ", " +
						winterhart + ".";
			}
		}
		// Default Antwort
		return "Dazu habe ich leider noch keine passende Antwort.";
	}

	public static List<Month> getMonateZuTyp(Pflanze p, Kalendertyp typ) {
		// Methode um die Monate zu extrahieren und zu sortieren
		return p.getKalender().stream()
				.filter(k -> k.getKalendertyp() == typ)
				.flatMap(k -> k.getMonat().stream())
				.sorted((Comparator.comparing(Month::ordinal)))
				.toList();
	}
}
