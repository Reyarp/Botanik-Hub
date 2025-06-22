
package GUI.HauptDialoge.PflanzenDialoge;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

import Client.BotanikHub_Client;
import Enum.*;
import GUI.HauptDialoge.PflanzenDialoge.SubDialoge.*;
import GUI.Utilitys.Util_Help;
import GUI.Utilitys.Util_Animations;
import Modell.*;
import ModellFX.*;
import ServiceFunctions.Service_BotanikHub;
import ServiceFunctions.Service_Erinnerung;
import ServiceFunctions.Service_Pflanze;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.Duration;

public class Pflanze_Anlegen_Dialog extends Dialog<ButtonType> {

	/* Allgemein */
	private TextField nameTxt, botanTxt, bildPfad;
	private TextArea pflanzTypLabel;

	/* Eigenschaften */
	private ArrayList<Pflanzentyp> alTyp;
	private ArrayList<VerwendeteTeile> alTeile;
	private TextField breiteTxt, hoeheTxt;
	private RadioButton gift1, gift2, leben1, leben2, leben3, win1, win2;
	private ToggleGroup giftigkeit = new ToggleGroup();
	private ToggleGroup lebensdauer = new ToggleGroup();
	private ToggleGroup winterhart = new ToggleGroup();
	private CheckBox[] checkboxen;
	VerwendeteTeile[] teileBox;

	/* Pflege */
	private ArrayList<Month> alRueckschnitt;
	private RadioButton wasser1, wasser2, wasser3, licht1, licht2, licht3, dueng1, dueng2, dueng3;
	private TextArea standortLbl;

	/* Kalender */
	private BotanikkalenderFX aussaatKalender = new BotanikkalenderFX(new Botanikkalender(Kalendertyp.AUSSAAT));
	private BotanikkalenderFX blueteKalender = new BotanikkalenderFX(new Botanikkalender(Kalendertyp.BLUETE));
	private BotanikkalenderFX ernteKalender = new BotanikkalenderFX(new Botanikkalender(Kalendertyp.ERNTE));
	private BotanikkalenderFX rueckschnittKalender = new BotanikkalenderFX(new Botanikkalender(Kalendertyp.RUECKSCHNITT));
	private TreeSet<Month> alAussaat;
	private TreeSet<Month> alBluete;
	private TreeSet<Month> alErnte;
	private ArrayList<Botanikkalender> alleKalender;

	/* Vermehrung */
	private ArrayList<Vermehrungsarten> alVermehrung;
	private TextArea notiz;

	/* Erinnerung */
	private ErinnerungenFX erinnerung;
	private DatePicker date;

	/* FehlerTab */
	private ArrayList<String> fehlerText;


	public Pflanze_Anlegen_Dialog(PflanzeFX p, Benutzer benutzer) {

		/*
		 * Dieser Dialog ist das Herzstück der Anwendung
		 * Hier werden Pflanzendaten eingetragen mittels methoden (pro Tab)
		 * Hier werden Informationen für die Pflanze, Kalendereinträge und Erinnerungen(Benutzer) in die Modelle eingetragen
		 */

		// Daten initial übernehmen
		alVermehrung = new ArrayList<>(p.getAppPflanze().getVermehrung());
		alTyp = new ArrayList<>(p.getAppPflanze().getPflanzenTyp());
		alTeile = new ArrayList<>();

		// Erinnerungen eintragen
		ArrayList<Erinnerungen> vorhandeneErinnerungen = p.getAppPflanze().getErinnerung();
		if (vorhandeneErinnerungen != null && !vorhandeneErinnerungen.isEmpty() &&
				vorhandeneErinnerungen.get(0).getTyp() != null &&
				vorhandeneErinnerungen.get(0).getIntervall() != null) {
			erinnerung = new ErinnerungenFX(vorhandeneErinnerungen.get(0));
		} else {
			erinnerung = new ErinnerungenFX(new Erinnerungen(LocalDate.now(), null, null, benutzer, 0, p.getAppPflanze()));
		}

		// Kalenderdaten in FX-Modelle übertragen
		alleKalender = new ArrayList<>(p.getAppPflanze().getKalender());
		for (Botanikkalender k : alleKalender) {
			ArrayList<Month> monate = k.getMonat() != null ? new ArrayList<>(k.getMonat()) : new ArrayList<>();
			switch (k.getKalendertyp()) {
			case AUSSAAT -> aussaatKalender.getAppKalender().setMonat(monate);
			case BLUETE -> blueteKalender.getAppKalender().setMonat(monate);
			case ERNTE -> ernteKalender.getAppKalender().setMonat(monate);
			case RUECKSCHNITT -> rueckschnittKalender.getAppKalender().setMonat(monate);
			}
		}


		/* -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/


		// Button-Typen
		ButtonType speichern = new ButtonType("Speichern", ButtonData.OK_DONE);
		ButtonType abbrechen = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
		this.getDialogPane().getButtonTypes().addAll(speichern, abbrechen);

		Button save = (Button) this.getDialogPane().lookupButton(speichern);
		Button cancel = (Button) this.getDialogPane().lookupButton(abbrechen);


		// CSS-Styling
		cancel.getStyleClass().add("dialog-button-cancel");
		save.getStyleClass().add("dialog-button-ok");

		// TabPane & Tabs hinzufügen
		TabPane tab = new TabPane();
		Tab erinnerungTab = erinnerung(p);
		tab.getTabs().addAll(allgemein(p, benutzer), eigenschaften(p), pflege(p), kalender(p), vermehrung(p), erinnerungTab);
		tab.getStylesheets().add(getClass().getResource("/style.css").toString());

		// Standardwerte setzen bei neuer Pflanze
		if (p.getAppPflanze().getPflanzenID() == 0) {
			leben1.setSelected(true);
			win1.setSelected(true);
			gift1.setSelected(true);
			dueng1.setSelected(true);
			wasser1.setSelected(true);
			licht1.setSelected(true);
		}

		// Notiz deaktivieren -> bei AdminLogin
		if (benutzer.getTyp() == BenutzerTyp.ADMIN) {
			notiz.setDisable(true);
		}

		// Erinnerung deaktivieren nur für bereits gespeicherte Pflanzen
		if (p.getAppPflanze().getPflanzenID() > 0 || benutzer.getTyp() == BenutzerTyp.ADMIN) {
			erinnerungTab.setDisable(true);
		}

		// Resultconverter: 
		this.setResultConverter(new Callback<ButtonType, ButtonType>() {
			@Override
			public ButtonType call(ButtonType arg0) {
				if (arg0 == speichern) {

					/*
					 * ---------------Allgemein---------------
					 */

					// Name, Botanikname & Bildpfad
					p.getAppPflanze().setPflanzenName(nameTxt.getText());
					p.getAppPflanze().setBotanikName(botanTxt.getText());
					//					p.getAppPflanze().setBildPfad(bildPfad.getText());
					// Nur Pfad setzen, wenn Base64 NICHT gesetzt ist (für Benutzer eher nicht notwendig)
					if (p.getAppPflanze().getBildBase64() == null || p.getAppPflanze().getBildBase64().isBlank()) {
						p.getAppPflanze().setBildPfad(bildPfad.getText());
					} else {
						p.getAppPflanze().setBildPfad(""); // Leer setzen oder ganz ignorieren
					}


					// Pflanzentyp
					p.getAppPflanze().setPflanzenTyp(alTyp);

					// Wuchs
					p.getAppPflanze().setWuchsbreite(Double.parseDouble(breiteTxt.getText()));
					p.getAppPflanze().setWuchshoehe(Double.parseDouble(hoeheTxt.getText()));

					// Vermehrung
					p.getAppPflanze().setVermehrung(alVermehrung);

					// Giftigkeit
					p.getAppPflanze().setGiftig(gift1.isSelected());

					// Lebensdauer
					if (leben1.isSelected()) p.getAppPflanze().setLebensdauer(Lebensdauer.EINAEHRIG);
					else if (leben2.isSelected()) p.getAppPflanze().setLebensdauer(Lebensdauer.ZWEIJAEHRIG);
					else if (leben3.isSelected()) p.getAppPflanze().setLebensdauer(Lebensdauer.MEHRJAEHRIG);

					// Verträglichkeit (Winterhart)
					if (win1.isSelected()) p.setVertraeglichkeit(Vertraeglichkeit.WINTERHART);
					else if (win2.isSelected()) p.setVertraeglichkeit(Vertraeglichkeit.NICHT_WINTERHART);

					// Verwendete Pflanzenteile
					alTeile.clear();
					if(checkboxen != null) {
						for(int i = 0; i < checkboxen.length; i++) {
							if(checkboxen[i].isSelected()) {
								alTeile.add(teileBox[i]);
							}
						}
					}
					p.getAppPflanze().setVerwendeteTeile(alTeile);

					/*
					 * ---------------Pflege---------------
					 */

					// Wasserbedarf
					if (wasser1.isSelected()) p.getAppPflanze().setWasserbedarf(Wasserbedarf.GERING);
					else if (wasser2.isSelected()) p.getAppPflanze().setWasserbedarf(Wasserbedarf.MITTEL);
					else if (wasser3.isSelected()) p.getAppPflanze().setWasserbedarf(Wasserbedarf.HOCH);

					// Lichtbedarf
					if (licht1.isSelected()) p.getAppPflanze().setLichtbedarf(Lichtbedarf.SONNIG);
					else if (licht2.isSelected()) p.getAppPflanze().setLichtbedarf(Lichtbedarf.HALBSCHATTIG);
					else if (licht3.isSelected()) p.getAppPflanze().setLichtbedarf(Lichtbedarf.SCHATTIG);

					// Düngung
					if (dueng1.isSelected()) p.getAppPflanze().setDuengung(Intervall.WOECHENTLICH);
					else if (dueng2.isSelected()) p.getAppPflanze().setDuengung(Intervall.MONATLICH);
					else if (dueng3.isSelected()) p.getAppPflanze().setDuengung(Intervall.JAEHRLICH);

					/*
					 * ---------------Kalender---------------
					 */

					// Leere ArrayList zum Verpacken aller Kalender
					alleKalender = new ArrayList<>();
					alleKalender.add(aussaatKalender.getAppKalender());
					alleKalender.add(blueteKalender.getAppKalender());
					alleKalender.add(ernteKalender.getAppKalender());
					alleKalender.add(rueckschnittKalender.getAppKalender());
					p.getAppPflanze().setKalender(alleKalender);

					// Sicherstellen: Rückschnittmonate als TreeSet -> wegen Duplikate
					p.getAppPflanze().getKalender().stream()
					// Nach kalendertyp filtern
					.filter(k -> k.getKalendertyp() == Kalendertyp.RUECKSCHNITT)
					// Abflachen auf einen stream
					.flatMap(k -> k.getMonat().stream())
					// Sammeln und zum neuen TreeSet hinzufügen
					.collect(Collectors.toCollection(TreeSet::new));

					/*
					 * ---------------Notiz---------------
					 */
					p.getAppPflanze().setNotiz(notiz.getText());

					/*
					 * ---------------Erinnerung---------------
					 */

					// Datum, Intervall & Erinnerungstyp
					erinnerung.setDatum(date.getValue());
					erinnerung.setIntervall(erinnerung.getAppErinnerung().getIntervall());
					erinnerung.setTyp(erinnerung.getAppErinnerung().getTyp());

					// Leere ArrayList zum befüllen der erstellten Erinnerung
					ArrayList<Erinnerungen> alErin = new ArrayList<>();
					alErin.add(erinnerung.getAppErinnerung());
					p.getAppPflanze().setErinnerung(alErin);

					/*
					 * ---------------Benutzer---------------
					 */
					p.getAppPflanze().setBenutzer(benutzer);

					/*
					 * ---------------DB Insert---------------
					 */

					try {
						// Bei bestehender Pflanze -> Update
						if (p.getAppPflanze().getPflanzenID() > 0) {
							Service_Pflanze.putPflanze(p.getAppPflanze());

							// Wenn es sich zusätzlich um Benutzer handelt -> Update notiz
							if(benutzer.getTyp() == BenutzerTyp.BENUTZER) {
								BotanikHub hub = new BotanikHub(benutzer, p.getAppPflanze());
								Service_BotanikHub.putNotiz(hub);
								Service_BotanikHub.putUserBase64(hub);
							}

							// Bei neuer Pflanze -> Insert
						} else {
							// Nach dem POST: die vom Server generierte Pflanzen-ID ins Modell übernehmen
							// Notwendig, damit spätere Verknüpfungen -> z.b Kalender, Vermehrung, Wunschliste, korrekt funktionieren
							Pflanze serverPflanze = Service_Pflanze.postPflanze(p.getAppPflanze());
							p.getAppPflanze().setPflanzenID(serverPflanze.getPflanzenID());
							// Erinnerung vollständig verknüpfen:
							// Benutzer und Pflanze setzen, da beide als FK in der Erinnerungs-Tabelle definiert sind
							// -> verhindert SQL-Fehler durch fehlende Referenzen beim Speichern
							erinnerung.getAppErinnerung().setBenutzer(benutzer);
							erinnerung.getAppErinnerung().setPflanze(p.getAppPflanze());

							// Wenn es sich zusätzlich um Benutzer handelt -> Update Botanik-Hub
							if (benutzer.getTyp() == BenutzerTyp.BENUTZER) {
								BotanikHub hub = new BotanikHub(benutzer, p.getAppPflanze());
								Service_BotanikHub.postBotanikHub(hub);
							}
						}

						// Benutzer -> Neue Erinnerung setzen, nur wenn sie auch gewählt wurde
						if (benutzer.getTyp() == BenutzerTyp.BENUTZER &&
								erinnerung.getAppErinnerung().getTyp() != null &&
								erinnerung.getAppErinnerung().getIntervall() != null &&
								erinnerung.getAppErinnerung().getDatum() != null &&
								erinnerung.getAppErinnerung().getPflanze() != null &&
								erinnerung.getAppErinnerung().getBenutzer() != null) {

							Service_Erinnerung.postErinnerung(erinnerung.getAppErinnerung());
						}
					} catch (SQLException ex) {
						Util_Help.alertWindow(AlertType.ERROR, "Fehler: Pflanze anlegen", ex.getMessage()).showAndWait();
					}
					return null;
				}
				return speichern;
			}
		});

		/* -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

		// Eventfilter: zur Validierung

		save.addEventFilter(ActionEvent.ACTION, e -> {

			// ArrayList für die Fehlertexte
			fehlerText = new ArrayList<>();
			double breite = Double.parseDouble(breiteTxt.getText());
			double hoehe = Double.parseDouble(hoeheTxt.getText());

			if (nameTxt == null || nameTxt.getText() == null || nameTxt.getText().trim().isEmpty()) {
				Util_Animations.pauseAnimation(nameTxt, Duration.seconds(10));
				// Nach jedem fehler den der Eventfilter auffängt speichert es automatisch in die Arraylist
				fehlerText.add("- Pflanzenname fehlt (Allgemein)");
				e.consume();
			}

			if (hoehe <= 0 || hoeheTxt.getText().equals(".")) {
				Util_Animations.pauseAnimation(hoeheTxt, Duration.seconds(10));
				fehlerText.add("- Höhe fehlt oder ungültig (Eigenschaften)");
				e.consume();
			}

			
			if (breite <= 0 || breiteTxt.getText().equals(".")) {
				Util_Animations.pauseAnimation(breiteTxt, Duration.seconds(10));
				fehlerText.add("- Breite fehlt oder ungültig (Eigenschaften)");
				e.consume();
			}

			if (p.getAppPflanze().getPflanzenTyp() == null || p.getAppPflanze().getPflanzenTyp().isEmpty()) {
				Util_Animations.pauseAnimation(pflanzTypLabel, Duration.seconds(10));
				fehlerText.add("- Pflanzentyp fehlt (Eigenschaften)");
				e.consume();
			}
			
			if (p.getAppPflanze().getStandort() == null) {
				Util_Animations.pauseAnimation(standortLbl, Duration.seconds(10));
				fehlerText.add("- Standort nicht gewählt (Pflege)");
				e.consume();
			}

			// TabPane referenzieren
			if (!fehlerText.isEmpty()) {
				Tab fehlerTab = fehler();
				// boolean variable um zu Prüfen ob Tab bereits in der TabPane enthalten ist
				boolean existiert = tab.getTabs().stream()
						// Prüfung mit anymatch auf graphic(instanceof ImageView)
						.anyMatch(t -> t.getGraphic() instanceof ImageView 
								// Zusätzlich noch auf die URL
								&& ((ImageView) t.getGraphic()).getImage().getUrl().contains("fehler.png"));

				if (!existiert) {
					// Wenn es nicht existiert -> zur TabPane hinzufügen
					tab.getTabs().add(fehlerTab);
					// Automatisch zu Fehlertab springen -> coole Funktion
					tab.getSelectionModel().select(fehlerTab);
				}
				
				// Funktion um bei erneuten Fehlerauftritten der Tab neu geladen wird
				tab.getTabs().removeIf(t -> t.getGraphic() instanceof ImageView &&
					    ((ImageView) t.getGraphic()).getImage().getUrl().contains("fehler.png"));
					tab.getTabs().add(fehlerTab);
					tab.getSelectionModel().select(fehlerTab);
			}
			
			
			try {
				// Duplikate prüfen bei neuer Pflanze
				if(p.getAppPflanze().getPflanzenID() == 0) {
					List<Pflanze> duplikate = Service_Pflanze.getPflanze().stream()
							// Aktuelle pflanze == eingegeben Pflanze
							.filter(pf -> pf.getPflanzenName().equalsIgnoreCase(nameTxt.getText()))
							// Pflanzenbesitzer == aktueller Benutzer
							.filter(b -> b.getBenutzer().getBenutzerId() == benutzer.getBenutzerId())
							.toList();

					// Bei neuer Pflanze
					if(!duplikate.isEmpty()) {
						// Verhindert das schliessen des Dialog -> Verhindert die Rückgabe an Resultconverter
						this.setResult(null);
						Util_Help.alertWindow(AlertType.ERROR, "Fehler: Pflanze anlegen", "Pflanze existiert bereits").showAndWait();	
						e.consume();
						return;
					}
				}
				
				if (benutzer.getTyp() == BenutzerTyp.BENUTZER) {
					List<Pflanze> userPflanzen = Service_BotanikHub.getBHPflanzen(benutzer.getBenutzerId());
					List<Pflanze> duplikateImHub = userPflanzen.stream()
						.filter(pf -> pf.getPflanzenName().equalsIgnoreCase(nameTxt.getText()))
						// gleiche ID auslassen beim Bearbeiten
						.filter(pf -> pf.getPflanzenID() != p.getAppPflanze().getPflanzenID())
						.toList();

					if (!duplikateImHub.isEmpty()) {
						this.setResult(null);
						Util_Help.alertWindow(AlertType.ERROR, "Fehler: Pflanze anlegen", "Pflanze existiert bereits in deinem Botanikhub").showAndWait();
						e.consume();
						return;
					}
				}

				// Duplikate prüfen bei vorhandener Pflanze
				if(p.getAppPflanze().getPflanzenID() > 0) {
					List<Pflanze> vorhandenePflanze = Service_Pflanze.getPflanze().stream()
							// besitzer Pflanze == eingegebenr Pflanzenname -> Fehler
							.filter(pf -> pf.getPflanzenName().equalsIgnoreCase(nameTxt.getText()))
							// besitzer Pflanze ID != aktuelle Pflanze
							.filter(pf -> pf.getPflanzenID() != p.getAppPflanze().getPflanzenID())
							.toList();

					if(!vorhandenePflanze.isEmpty()) {
						e.consume();
						Util_Help.alertWindow(AlertType.ERROR, "Fehler: Pflanze anlegen", "Pflanze existiert bereits").showAndWait();	
						return;
					}
				}

			} catch (SQLException e1) {
				Util_Help.alertWindow(AlertType.ERROR, "Fehler: Pflanze anlegen", e1.getMessage()).showAndWait();			}
		});

		// Zusammenbau & Dialogeinstellungen
		this.setTitle(p.getAppPflanze().getPflanzenID() == 0 
				? "Neue Pflanze anlegen" 
						: p.getAppPflanze().getPflanzenName() + " bearbeiten");
		this.getDialogPane().setContent(tab);
		this.getDialogPane().setPrefSize(580, 450);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("dialog-layout");
	}

	public Tab allgemein(PflanzeFX p, Benutzer b) {

		// Buttons & Co
		Button suchen = new Button("Suchen");
		suchen.setPrefWidth(80);
		Button loeschen = new Button("Löschen");

		// Name & Botanikname
		nameTxt = new TextField(p.getAppPflanze().getPflanzenName());
		nameTxt.setPromptText("z.B. Lavendel, Rosmarin...");
		nameTxt.setPrefWidth(250);
		Label nameLbl = new Label("Pflanzenname:");

		botanTxt = new TextField(p.getAppPflanze().getBotanikName());
		botanTxt.setPromptText("z.B. Lavandula angustifolia");
		botanTxt.setPrefWidth(250);
		Label botLbl = new Label("Botanischer Name:");

		// Bildpfad & Vorschau
		bildPfad = new TextField(p.getAppPflanze().getBildPfad());
		bildPfad.setPromptText("Bildpfad angeben");
		bildPfad.setPrefWidth(250);
		Label uploadLbl = new Label("Bild hochladen");

		// Löschen Icon
		ImageView loeschIcon = new ImageView(new Image(BotanikHub_Client.class.getResource("/bin.png").toString()));
		loeschIcon.setFitWidth(65);
		loeschIcon.setFitHeight(60);
		loeschIcon.setOpacity(0.4);
		loeschen.setGraphic(loeschIcon);
		Util_Help.tip(loeschIcon, "Bild löschen", Duration.millis(200), Duration.seconds(3));

		// Vorschau ImageView
		ImageView pflanzenBild = new ImageView();
		pflanzenBild.setFitWidth(185);
		pflanzenBild.setFitHeight(210);
		loeschen.setDisable(pflanzenBild == null);



		// Nur wenn Base64 gesetzt ist, dann anzeigen
		String base64 = p.getAppPflanze().getBildBase64();
		String userBase64 = p.getAppPflanze().getUserBase64();

		if ((base64 != null && !base64.isBlank()) || (userBase64 != null && !userBase64.isBlank())) {
			try {
				if(b.getTyp() == BenutzerTyp.ADMIN) {
					byte[] imageBytes = Base64.getDecoder().decode(base64);
					Image base64Image = new Image(new ByteArrayInputStream(imageBytes));
					pflanzenBild.setImage(base64Image);
				} else if(b.getTyp() == BenutzerTyp.BENUTZER) {
					byte[] imageBytes = Base64.getDecoder().decode(userBase64);
					Image userBase64Image = new Image(new ByteArrayInputStream(imageBytes));
					pflanzenBild.setImage(userBase64Image);
				}

			} catch (IllegalArgumentException ex) {
				Util_Help.alertWindow(AlertType.ERROR, "Fehler: Bild Base64", ex.getMessage());
			}
		}

		// Layout: StackPane -> für die ImageView
		StackPane bildBox = new StackPane(pflanzenBild);
		bildBox.setPrefSize(100, 150);
		bildBox.getStyleClass().add("image-vorschau");

		// CSS Styling
		suchen.getStyleClass().add("dialog-button-ok");
		loeschen.getStyleClass().add("dialog-button-ok");

		// Layout: AnchorPane -> Leichtere Positionierung der Elemente
		AnchorPane anchor = new AnchorPane();
		anchor.getChildren().addAll(nameLbl, nameTxt, botLbl, botanTxt, uploadLbl, bildPfad, suchen, loeschIcon, bildBox);

		// Eigene Methode für GUI-Positionierung
		Util_Help.anchorpane(nameLbl, 20.0, null, 15.0, null);
		Util_Help.anchorpane(nameTxt, 15.0, null, 120.0, null);
		Util_Help.anchorpane(botLbl, 60.0, null, 15.0, null);
		Util_Help.anchorpane(botanTxt, 55.0, null, 120.0, null);
		Util_Help.anchorpane(uploadLbl, 100.0, null, 15.0, null);
		Util_Help.anchorpane(bildPfad, 95.0, null, 120.0, null);
		Util_Help.anchorpane(suchen, 95.0, null, 380.0, null);
		Util_Help.anchorpane(loeschIcon, null, 1.0, null, 155.0);
		Util_Help.anchorpane(bildBox, 125.0, null, 120.0, null);

		/*
		 * Der Grundgedanke ist, den Bildpfad zur lokalen Anzeige zu verwenden (z. B. ".../rose.jpg"),
		 * und den Base64-String (bildBase64) zur Übertragung an den Server zu nutzen.
		 * Da es sich beim Bild um Binärdaten handelt, verwende ich Files.readAllBytes(...) und 
		 * Base64 Methoden zur Codierung / Decodierung
		 * Quelle: https://www.baeldung.com/java-base64-encode-and-decode
		 */

		suchen.setOnAction(e -> {

			// FileChooser für Windows Fenster
			FileChooser fc = new FileChooser();
			// Nur explizite Dateiformate
			fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", "*.jpeg"));
			File file = fc.showOpenDialog(null);


			if (file != null) {
				// Pfad und Bild setzen
				String pfad = file.getAbsolutePath();
				bildPfad.setText(pfad);

				// byte[] array zum lesen der Datei über Files.readAllBytes
				try {
					byte[] bildBytes = Files.readAllBytes(file.toPath());
					// String variable zum setzen des Base64 in die Pflanze
					String bildbase64 = Base64.getEncoder().encodeToString(bildBytes);

					// Bild aus byte[] Array anzeigen
					Image image = new Image(new ByteArrayInputStream(bildBytes));
					pflanzenBild.setImage(image);

					if(b.getTyp() == BenutzerTyp.ADMIN) {
						p.getAppPflanze().setBildBase64(bildbase64);
					} else if(b.getTyp() == BenutzerTyp.BENUTZER) {
						p.getAppPflanze().setUserBase64(bildbase64);
					}
				} catch (IOException e1) {
					Util_Help.alertWindow(AlertType.ERROR, "Fehler: Bildupload", e1.getMessage());
				}
			}
		});

		loeschIcon.setOnMousePressed(e -> {
			bildPfad.clear();
			pflanzenBild.setImage(null);

			if(b.getTyp() == BenutzerTyp.ADMIN) {
				p.getAppPflanze().setBildBase64(null);
				p.getAppPflanze().setBildPfad(null);
			} else if(b.getTyp() == BenutzerTyp.BENUTZER) {
				p.getAppPflanze().setUserBase64(null);
				p.getAppPflanze().setBildPfad(null);
			}

		});

		// Changelistener: löschIcon
		pflanzenBild.imageProperty().addListener(new ChangeListener<>() {

			@Override
			public void changed(ObservableValue<? extends Image> arg0, Image arg1, Image arg2) {
				if(arg2 != null) {
					loeschIcon.setDisable(false);
					loeschIcon.setOpacity(1);
					loeschIcon.setStyle("-fx-cursor:hand");
				} else {
					loeschIcon.setDisable(true);
					loeschIcon.setOpacity(0.4);
					loeschIcon.setStyle("");
				}
			}
		});

		// Zusammenbau & Dialogeinstellungen
		VBox vb = new VBox(anchor);
		vb.setPadding(new Insets(5));

		Tab tabAllg = new Tab("Allgemein");
		tabAllg.setClosable(false);
		tabAllg.setContent(vb);

		return tabAllg;
	}

	public Tab eigenschaften(PflanzeFX p) {

		// Pflanzenbreite & Höhe
		breiteTxt = new TextField(String.valueOf(p.getAppPflanze().getWuchsbreite()));
		breiteTxt.setPromptText("Breite in cm");
		breiteTxt.setPrefWidth(130);
		breiteTxt.setTextFormatter(new TextFormatter<>(Util_Help.gibNurZiffernFilter()));

		hoeheTxt = new TextField(String.valueOf(p.getAppPflanze().getWuchshoehe()));
		hoeheTxt.setPromptText("Höhe in cm");
		hoeheTxt.setPrefWidth(130);
		hoeheTxt.setTextFormatter(new TextFormatter<>(Util_Help.gibNurZiffernFilter()));

		// Lebensdauer
		leben1 = new RadioButton("Einjährig");
		leben2 = new RadioButton("Zweijährig");
		leben3 = new RadioButton("Mehrjährig");

		leben1.setToggleGroup(lebensdauer);
		leben2.setToggleGroup(lebensdauer);
		leben3.setToggleGroup(lebensdauer);

		// Vorauswahl setzen -> Bei bearbeiten der Pflanze
		if (p.getAppPflanze().getLebensdauer() != null) {
			switch (p.getAppPflanze().getLebensdauer()) {
			case EINAEHRIG -> leben1.setSelected(true);
			case ZWEIJAEHRIG -> leben2.setSelected(true);
			case MEHRJAEHRIG -> leben3.setSelected(true);
			}
		}

		// Verträglichkeit
		win1 = new RadioButton("Winterhart");
		win2 = new RadioButton("Nicht winterhart");

		win1.setToggleGroup(winterhart);
		win2.setToggleGroup(winterhart);

		// Vorauswahl setzen -> Bei bearbeiten der Pflanze
		if (p.getAppPflanze().getVertraeglichkeit() != null) {
			switch (p.getAppPflanze().getVertraeglichkeit()) {
			case WINTERHART -> win1.setSelected(true);
			case NICHT_WINTERHART -> win2.setSelected(true);
			}
		}

		// Giftig
		gift1 = new RadioButton("Giftig");
		gift2 = new RadioButton("Nicht giftig");

		gift1.setToggleGroup(giftigkeit);
		gift2.setToggleGroup(giftigkeit);

		// Vorauswahl setzen -> Bei bearbeiten der Pflanze
		giftigkeit.selectToggle(p.getAppPflanze().isGiftig() ? gift1 : gift2);

		// Pflanzentyp
		Button pflanzTyp = new Button("Pflanztyp auswählen");
		String typTxt = (p.getAppPflanze().getPflanzenTyp() != null && 
				!p.getAppPflanze().getPflanzenTyp().isEmpty())
				? p.getAppPflanze().getPflanzenTyp().stream()
						.map(Pflanzentyp::getBeschreibung)
						.distinct()
						.collect(Collectors.joining(", "))
						: "Noch kein Pflanzentyp ausgewählt";
		pflanzTypLabel = new TextArea(typTxt);
		pflanzTypLabel.setEditable(false);
		pflanzTypLabel.setPrefWidth(400);
		pflanzTypLabel.setPrefHeight(60);

		// CSS Styling
		pflanzTyp.getStyleClass().add("dialog-button-ok");
		pflanzTypLabel.getStyleClass().add("dialog-label");

		// Layout GridPane
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(15);
		grid.setPadding(new Insets(10));

		// Verwendete Teile
		// Enum zur dynamischen Erstellung der Checkboxen (wartungsfreundlich)
		teileBox = VerwendeteTeile.values();	
		// CheckBoxen befüllt mit EnumTyp
		checkboxen = new CheckBox[teileBox.length];

		// Grid Startposition
		int spalte = 1;
		int zeile = 7;

		// Über das Enum iterieren und für jeden Wert eine CheckBox erstellen
		for (int i = 0; i < teileBox.length; i++) {
			CheckBox cb = new CheckBox(teileBox[i].toString());
			checkboxen[i] = cb;

			// Vorauswahl -> bearbeiten
			if(p.getAppPflanze().getVerwendeteTeile().contains(teileBox[i])) {
				cb.setSelected(true);
			}

			// zu GridPane hinzufügen
			grid.add(cb, spalte, zeile);
			spalte++;
			// Maximal 3 CheckBoxen pro Zeile
			if(spalte > 3) {
				spalte = 1;
				zeile++;
			}
		}

		grid.add(new Label("Breite (cm):"), 0, 0);
		grid.add(breiteTxt, 1, 0);
		grid.add(new Label("Höhe (cm):"), 2, 0);
		grid.add(hoeheTxt, 3, 0);

		grid.add(new Label("Lebensdauer:"), 0, 1);
		grid.add(leben1, 1, 1);
		grid.add(leben2, 2, 1);
		grid.add(leben3, 3, 1);

		grid.add(new Label("Verträglichkeit:"), 0, 2);
		grid.add(win1, 1, 2);
		grid.add(win2, 2, 2);

		grid.add(new Label("Giftigkeit:"), 0, 3);
		grid.add(gift1, 1, 3);
		grid.add(gift2, 2, 3);

		grid.add(new Label("Pflanzentyp:"), 0, 4);
		grid.add(pflanzTyp, 1, 4);
		grid.add(pflanzTypLabel, 1, 5, 3, 1);

		grid.add(new Label("Verwendete \nPflanzenteile:"), 0, 7);


		// Eventhandler: Pflanzentyp
		pflanzTyp.setOnAction(e -> {
			Pflanze_PflanzenTyp_Dialog dialog = new Pflanze_PflanzenTyp_Dialog(p);
			Optional<ButtonType> o = dialog.showAndWait();

			if (o.isPresent() && o.get().getButtonData() == ButtonData.OK_DONE) {
				alTyp = p.getAppPflanze().getPflanzenTyp();
				if (alTyp != null && !alTyp.isEmpty()) {

					String text = alTyp.stream()
							.map(Pflanzentyp::getBeschreibung)
							.collect(Collectors.joining(", "));
					pflanzTypLabel.setText(text);
				} else {
					pflanzTypLabel.setText("Noch kein Pflanzentyp ausgewählt");
				}
			}
		});

		// Zusammenbau & Dialogeinstellungen
		VBox vb = new VBox(grid);
		vb.setPadding(new Insets(5));

		Tab tabEig = new Tab("Eigenschaften");
		tabEig.setClosable(false);
		tabEig.setContent(vb);

		return tabEig;
	}

	@SuppressWarnings("incomplete-switch")
	public Tab pflege(PflanzeFX p) {

		// Wasserbedarf
		ToggleGroup wasserbedarf = new ToggleGroup();
		wasser1 = new RadioButton("Gering");
		wasser2 = new RadioButton("Mittel");
		wasser3 = new RadioButton("Hoch");
		wasser1.setToggleGroup(wasserbedarf);
		wasser2.setToggleGroup(wasserbedarf);
		wasser3.setToggleGroup(wasserbedarf);

		// Vorauswahl setzen -> Bei bearbeiten der Pflanze
		if (p.getAppPflanze().getWasserbedarf() != null) {
			switch (p.getAppPflanze().getWasserbedarf()) {
			case GERING -> wasser1.setSelected(true);
			case MITTEL -> wasser2.setSelected(true);
			case HOCH -> wasser3.setSelected(true);
			}
		}

		// Lichtbedarf
		ToggleGroup lichtbedarf = new ToggleGroup();
		licht1 = new RadioButton("Sonne");
		licht2 = new RadioButton("Halbschatten");
		licht3 = new RadioButton("Schatten");
		licht1.setToggleGroup(lichtbedarf);
		licht2.setToggleGroup(lichtbedarf);
		licht3.setToggleGroup(lichtbedarf);

		// Vorauswahl setzen -> Bei bearbeiten der Pflanze
		if (p.getAppPflanze().getLichtbedarf() != null) {
			switch (p.getAppPflanze().getLichtbedarf()) {
			case SONNIG -> licht1.setSelected(true);
			case HALBSCHATTIG -> licht2.setSelected(true);
			case SCHATTIG -> licht3.setSelected(true);
			}
		}

		// Standort
		Button standort = new Button("Standort");
		standortLbl = new TextArea((p.getAppPflanze().getStandort() != null)
				? p.getAppPflanze().getStandort().getBeschreibung()
						: "Noch kein Standort gewählt");
		standortLbl.setPrefHeight(50);
		standortLbl.setPrefWidth(400);
		standortLbl.setEditable(false);

		// Rückschnitt
		Button rueckschnitt = new Button("Rückschnitt");
		String rueckText = (rueckschnittKalender.getAppKalender().getMonat() != null &&
				!rueckschnittKalender.getAppKalender().getMonat().isEmpty())
				? rueckschnittKalender.getAppKalender().getMonat().stream()
						.map(Month::getBeschreibung)
						.distinct()
						.collect(Collectors.joining(", "))
						: "Noch keine Rückschnittzeit ausgewählt";

		TextArea rueckschnittLbl = new TextArea(rueckText);
		rueckschnittLbl.setPrefHeight(50);
		rueckschnittLbl.setPrefWidth(400);
		rueckschnittLbl.setEditable(false);

		// Düngung
		ToggleGroup duengung = new ToggleGroup();
		dueng1 = new RadioButton("Wöchentlich");
		dueng2 = new RadioButton("Monatlich");
		dueng3 = new RadioButton("Jährlich");
		dueng1.setToggleGroup(duengung);
		dueng2.setToggleGroup(duengung);
		dueng3.setToggleGroup(duengung);

		// Vorauswahl setzen -> Bei bearbeiten der Pflanze
		if (p.getAppPflanze().getDuengung() != null) {
			switch (p.getAppPflanze().getDuengung()) {
			case WOECHENTLICH -> dueng1.setSelected(true);
			case MONATLICH -> dueng2.setSelected(true);
			case JAEHRLICH -> dueng3.setSelected(true);
			}
		}

		// CSS Styling
		rueckschnittLbl.getStyleClass().add("dialog-label");
		standortLbl.getStyleClass().add("dialog-label");
		rueckschnitt.getStyleClass().add("dialog-button-ok");
		standort.getStyleClass().add("dialog-button-ok");

		// Layout: GridPane
		GridPane grid = new GridPane();
		grid.setVgap(15);
		grid.setHgap(10);
		grid.setPadding(new Insets(10));

		grid.add(new Label("Düngung:"), 0, 0);
		grid.add(dueng1, 1, 0);
		grid.add(dueng2, 2, 0);
		grid.add(dueng3, 3, 0);

		grid.add(new Label("Wasserbedarf:"), 0, 1);
		grid.add(wasser1, 1, 1);
		grid.add(wasser2, 2, 1);
		grid.add(wasser3, 3, 1);

		grid.add(new Label("Lichtbedarf:"), 0, 2);
		grid.add(licht1, 1, 2);
		grid.add(licht2, 2, 2);
		grid.add(licht3, 3, 2);

		grid.add(new Label("Standort:"), 0, 3);
		grid.add(standort, 1, 3);
		grid.add(standortLbl, 1, 4, 4, 2);

		grid.add(new Label("Rückschnitt:"), 0, 6);
		grid.add(rueckschnitt, 1, 6);
		grid.add(rueckschnittLbl, 1, 7, 4, 2);

		// Eventhandler: standort, rückschnitt
		standort.setOnAction(e -> {
			Pflanze_Standort_Dialog dialog = new Pflanze_Standort_Dialog(p);
			Optional<ButtonType> result = dialog.showAndWait();
			if (result.isPresent() && result.get().getButtonData() == ButtonData.OK_DONE) {
				if (p.getAppPflanze().getStandort() != null) {
					standortLbl.setText(p.getAppPflanze().getStandort().getBeschreibung());
				}
			}
		});

		rueckschnitt.setOnAction(e -> {
			KalenderTyp_Dialog dialog = new KalenderTyp_Dialog(rueckschnittKalender, Kalendertyp.RUECKSCHNITT);
			Optional<ButtonType> result = dialog.showAndWait();
			if (result.isPresent() && result.get().getButtonData() == ButtonData.OK_DONE) {
				alRueckschnitt = dialog.getAlMonth();
				if (alRueckschnitt != null && !alRueckschnitt.isEmpty()) {
					String text = alRueckschnitt.stream()
							.map(Month::getBeschreibung)
							.collect(Collectors.joining(", "));
					rueckschnittLbl.setText(text);
				}
			}
		});

		// Zusammenbau & Dialogeinstellungen
		VBox vb = new VBox(grid);
		vb.setPadding(new Insets(5));

		Tab tabPflege = new Tab("Pflege");
		tabPflege.setClosable(false);
		tabPflege.setContent(vb);

		return tabPflege;
	}

	public Tab kalender(PflanzeFX p) {

		// Aussaatzeit
		Button aussaat = new Button("Aussaat");
		String aussaatText = (aussaatKalender.getAppKalender().getMonat() != null && 
				!aussaatKalender.getAppKalender().getMonat().isEmpty())
				? aussaatKalender.getAppKalender().getMonat().stream()
						.map(Month::getBeschreibung)
						.distinct()
						.collect(Collectors.joining(", "))
						: "Noch keine Aussaatzeit ausgewählt";

		TextArea ausLbl = new TextArea(aussaatText);
		ausLbl.setPrefWidth(400);
		ausLbl.setPrefHeight(50);
		ausLbl.setEditable(false);

		// Blütezeit
		Button bluete = new Button("Blütezeit");
		String blueteText = (blueteKalender.getAppKalender().getMonat() != null && 
				!blueteKalender.getAppKalender().getMonat().isEmpty())
				? blueteKalender.getAppKalender().getMonat().stream()
						.map(Month::getBeschreibung)
						.distinct()
						.collect(Collectors.joining(", "))
						: "Noch keine Blütezeit ausgewählt";

		TextArea blueteLbl = new TextArea(blueteText);
		blueteLbl.setPrefWidth(400);
		blueteLbl.setPrefHeight(50);
		blueteLbl.setEditable(false);

		// Erntezeit
		Button ernte = new Button("Erntezeit");
		String ernteText = (ernteKalender.getAppKalender().getMonat() != null && 
				!ernteKalender.getAppKalender().getMonat().isEmpty())
				? ernteKalender.getAppKalender().getMonat().stream()
						.map(Month::getBeschreibung)
						.distinct()
						.collect(Collectors.joining(", "))
						: "Noch keine Erntezeit ausgewählt";

		TextArea ernteLbl = new TextArea(ernteText);
		ernteLbl.setPrefWidth(400);
		ernteLbl.setPrefHeight(50);
		ernteLbl.setEditable(false);

		// CSS Styling
		aussaat.getStyleClass().add("dialog-button-ok");
		ausLbl.getStyleClass().add("dialog-label");

		bluete.getStyleClass().add("dialog-button-ok");
		blueteLbl.getStyleClass().add("dialog-label");

		ernte.getStyleClass().add("dialog-button-ok");
		ernteLbl.getStyleClass().add("dialog-label");

		// Layout: GridPane
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);

		grid.add(new Label("Aussaat:"), 0, 0);
		grid.add(aussaat, 1, 0);
		grid.add(ausLbl, 1, 1);

		grid.add(new Label("Blütezeit:"), 0, 2);
		grid.add(bluete, 1, 2);
		grid.add(blueteLbl, 1, 3);

		grid.add(new Label("Erntezeit:"), 0, 4);
		grid.add(ernte, 1, 4);
		grid.add(ernteLbl, 1, 5);

		// Eventhandler: aussat, bluete & ernte
		aussaat.setOnAction(e -> {
			KalenderTyp_Dialog dialog = new KalenderTyp_Dialog(aussaatKalender, Kalendertyp.AUSSAAT);
			Optional<ButtonType> result = dialog.showAndWait();
			if (result.isPresent() && result.get().getButtonData() == ButtonData.OK_DONE) {
				alAussaat = new TreeSet<>(dialog.getAlMonth());
				if (alAussaat != null && !alAussaat.isEmpty()) {
					String text = alAussaat.stream()
							.map(Month::getBeschreibung)
							.collect(Collectors.joining(", "));
					ausLbl.setText(text);
				}
			}
		});

		bluete.setOnAction(e -> {
			KalenderTyp_Dialog dialog = new KalenderTyp_Dialog(blueteKalender, Kalendertyp.BLUETE);
			Optional<ButtonType> result = dialog.showAndWait();
			if (result.isPresent() && result.get().getButtonData() == ButtonData.OK_DONE) {
				alBluete = new TreeSet<>(dialog.getAlMonth());
				if (alBluete != null && !alBluete.isEmpty()) {
					String text = alBluete.stream()
							.map(Month::getBeschreibung)
							.collect(Collectors.joining(", "));
					blueteLbl.setText(text);
				}
			}
		});

		ernte.setOnAction(e -> {
			KalenderTyp_Dialog dialog = new KalenderTyp_Dialog(ernteKalender, Kalendertyp.ERNTE);
			Optional<ButtonType> result = dialog.showAndWait();
			if (result.isPresent() && result.get().getButtonData() == ButtonData.OK_DONE) {
				alErnte = new TreeSet<>(dialog.getAlMonth());
				if (alErnte != null && !alErnte.isEmpty()) {
					String text = alErnte.stream()
							.map(Month::getBeschreibung)
							.collect(Collectors.joining(", "));
					ernteLbl.setText(text);
				}
			}
		});

		// Zusammenbau & Dialogeinstellungen
		VBox vb = new VBox(grid);
		vb.setPadding(new Insets(30, 10, 10, 10));

		Tab tabKal = new Tab("Kalender");
		tabKal.setClosable(false);
		tabKal.setContent(vb);

		return tabKal;
	}

	public Tab vermehrung(PflanzeFX p) {

		// Vermehrung
		Button methoden = new Button("Methoden");
		String vermText = (p.getAppPflanze().getVermehrung() != null && 
				!p.getAppPflanze().getVermehrung().isEmpty())
				? p.getAppPflanze().getVermehrung().stream()
						.map(Vermehrungsarten::getBeschreibung)
						.distinct()
						.collect(Collectors.joining(", "))
						: "Noch keine Vermehrungsart ausgewählt";

		TextArea methodenLbl = new TextArea(vermText);
		methodenLbl.setPrefWidth(400);
		methodenLbl.setPrefHeight(50);
		methodenLbl.setEditable(false);

		// Notiz
		notiz = new TextArea(p.getAppPflanze().getNotiz());
		notiz.setPrefWidth(400);
		notiz.setPrefHeight(200);
		notiz.setPromptText("Notizen hinzufügen");

		// CSS Styling
		methoden.getStyleClass().add("dialog-button-ok");
		methodenLbl.getStyleClass().add("dialog-label");
		notiz.getStyleClass().add("dialog-label");

		// Layout: GridPane
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(15);

		grid.add(new Label("Methoden:"), 0, 0);
		grid.add(methoden, 1, 0);
		grid.add(methodenLbl, 1, 1);

		grid.add(new Label("Notiz:"), 0, 2);
		grid.add(notiz, 1, 2);

		// Evenhandler: methoden
		methoden.setOnAction(e -> {
			Pflanze_Vermehrungsmethoden_Dialog dialog = new Pflanze_Vermehrungsmethoden_Dialog(p);
			Optional<ButtonType> o = dialog.showAndWait();

			if (o.isPresent() && o.get().getButtonData() == ButtonData.OK_DONE) {
				alVermehrung = dialog.getAlVermehrung();
				if (alVermehrung != null && !alVermehrung.isEmpty()) {
					String text = alVermehrung.stream()
							.map(Vermehrungsarten::getBeschreibung)
							.collect(Collectors.joining(", "));
					methodenLbl.setText(text);
				} else {
					methodenLbl.setText("Noch keine Vermehrungsart ausgewählt");
				}
			}
		});

		// Zusammenbau & Dialogeinstellungen
		VBox vb = new VBox(grid);
		vb.setPadding(new Insets(30, 10, 10, 10));

		Tab vermTab = new Tab("Vermehrung");
		vermTab.setClosable(false);
		vermTab.setContent(vb);

		return vermTab;
	}

	public Tab erinnerung(PflanzeFX p) {

		// Erinnerungen
		Button erTyp = new Button("Erinnerungstyp");
		TextArea erTypLbl = new TextArea(
				(erinnerung.getAppErinnerung().getTyp() != null) 
				? erinnerung.getAppErinnerung().getTyp().getBeschreibung()
						: "Erinnerungstyp");
		erTypLbl.setEditable(false);
		erTypLbl.setPrefWidth(400);
		erTypLbl.setPrefHeight(50);

		// Vorauswahl setzen -> Bei bearbeiten der Pflanze
		if (erinnerung.getAppErinnerung().getTyp() != null) {
			erTypLbl.setText(erinnerung.getAppErinnerung().getTyp().getBeschreibung());
		}

		// Intervall
		Button intervall = new Button("Intervall");
		TextArea intervallLbl = new TextArea(
				(erinnerung.getAppErinnerung().getIntervall() != null) 
				? erinnerung.getAppErinnerung().getIntervall().getBeschreibung()
						: "Noch kein Intervall gewählt");
		intervallLbl.setEditable(false);
		intervallLbl.setPrefWidth(400);
		intervallLbl.setPrefHeight(50);

		// Vorauswahl setzen -> Bei bearbeiten der Pflanze
		if (erinnerung.getAppErinnerung().getIntervall() != null) {
			intervallLbl.setText(erinnerung.getAppErinnerung().getIntervall().getBeschreibung());
		}

		// Datum -> Wochentag auswahl
		date = new DatePicker();
		if (erinnerung.getDatum() != null) {
			date.setValue(erinnerung.getDatum());
		}

		// CSS Styling
		erTyp.getStyleClass().add("dialog-button-ok");
		intervall.getStyleClass().add("dialog-button-ok");
		erTypLbl.getStyleClass().add("dialog-label");
		intervallLbl.getStyleClass().add("dialog-label");
		date.getStyleClass().add("date-picker");

		// Layout: GridPane
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(15);

		grid.add(new Label("Erinnerungstyp:"), 0, 0);
		grid.add(erTyp, 1, 0);
		grid.add(erTypLbl, 1, 1, 4, 2);

		grid.add(new Label("Tag wählen:"), 0, 3);
		grid.add(date, 1, 3);

		grid.add(new Label("Intervall:"), 0, 4);
		grid.add(intervall, 1, 4);
		grid.add(intervallLbl, 1, 5, 4, 2);

		// Eventhandler: erty, intervall
		erTyp.setOnAction(e -> {
			ErinnerungsTyp_Dialog dialog = new ErinnerungsTyp_Dialog(erinnerung);
			Optional<ButtonType> o = dialog.showAndWait();
			if (o.isPresent() && o.get().getButtonData() == ButtonData.OK_DONE) {
				if (erinnerung.getAppErinnerung().getTyp() != null) {
					erTypLbl.setText(erinnerung.getAppErinnerung().getTyp().getBeschreibung());
				}
			}
		});

		intervall.setOnAction(e -> {
			Pflanze_Intervall_Dialog dialog = new Pflanze_Intervall_Dialog(erinnerung);
			Optional<ButtonType> o = dialog.showAndWait();
			if (o.isPresent() && o.get().getButtonData() == ButtonData.OK_DONE) {
				if (erinnerung.getAppErinnerung().getIntervall() != null) {
					intervallLbl.setText(erinnerung.getAppErinnerung().getIntervall().getBeschreibung());
				}
			}
		});

		// Zusammenbau & Dialog
		VBox vb = new VBox(grid);
		vb.setPadding(new Insets(30, 10, 10, 10));

		Tab erTab = new Tab("Erinnerung");
		erTab.setClosable(false);
		erTab.setContent(vb);

		return erTab;
	}

	public Tab fehler() {

		TextArea fehlerLbl = new TextArea(String.join("\n", 
				(fehlerText != null) ? fehlerText : new ArrayList<>()));
		fehlerLbl.setWrapText(true);
		fehlerLbl.setEditable(false);
		fehlerLbl.setPrefSize(300, 400);

		ImageView fehlerIcon = new ImageView((BotanikHub_Client.class.getResource("/fehler.png").toString()));
		fehlerIcon.setFitHeight(35);
		fehlerIcon.setFitWidth(25);
		fehlerIcon.setPreserveRatio(true);
		fehlerIcon.setSmooth(true);

		VBox vb = new VBox(fehlerLbl);
		vb.setPadding(new Insets(5));
		vb.setAlignment(Pos.CENTER);

		Tab fehlerTab = new Tab();
		fehlerTab.setGraphic(fehlerIcon);
		fehlerTab.setClosable(true);
		fehlerTab.setContent(vb);
		fehlerTab.getStyleClass().add("fehler-tab");

		return fehlerTab;

	}
}

