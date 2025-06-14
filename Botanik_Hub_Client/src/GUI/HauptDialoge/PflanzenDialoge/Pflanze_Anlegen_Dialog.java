
package GUI.HauptDialoge.PflanzenDialoge;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

import Enum.*;
import GUI.BotanikHub_Client;
import GUI.HauptDialoge.PflanzenDialoge.SubDialoge.*;
import GUI.Utilitys.Util;
import GUI.Utilitys.Util_Animations;
import Modell.*;
import ModellFX.*;
import TEST_DB.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
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
	private CheckBox teil1, teil2, teil3;
	private TextField breiteTxt, hoeheTxt;
	private RadioButton gift1, gift2, leben1, leben2, leben3, win1, win2;
	private ToggleGroup giftigkeit = new ToggleGroup();
	private ToggleGroup lebensdauer = new ToggleGroup();
	private ToggleGroup winterhart = new ToggleGroup();

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
	private ArrayList<Erinnerungen> alErinnerung;
	private ErinnerungenFX erinnerung;
	private DatePicker date;

	/* Fehleranzeige */
	private ImageView fehler;


	public Pflanze_Anlegen_Dialog(PflanzeFX p, Benutzer benutzer) {

		/* --------------------------------------------
		 * Werte vorbereiten & Tabs setzen
		 * -------------------------------------------- */

		// Daten initial übernehmen
		alVermehrung = new ArrayList<>(p.getAppPflanze().getVermehrung());
		alTyp = new ArrayList<>(p.getAppPflanze().getPflanzenTyp());
		alTeile = new ArrayList<>(p.getAppPflanze().getVerwendeteTeile());

		// Erinnerung übernehmen oder neu initialisieren
		alErinnerung = new ArrayList<>(p.getAppPflanze().getErinnerung());
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

		/* --------------------------------------------
		 * UI SETUP: Tabs, Standardwerte, Styles
		 * --------------------------------------------*/

		// Button-Typen
		ButtonType speichern = new ButtonType("Speichern", ButtonData.OK_DONE);
		ButtonType abbrechen = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
		this.getDialogPane().getButtonTypes().addAll(speichern, abbrechen);

		// CSS-Styling
		Button save = (Button) this.getDialogPane().lookupButton(speichern);
		save.getStyleClass().add("dialog-button-ok");

		Button cancel = (Button) this.getDialogPane().lookupButton(abbrechen);
		cancel.getStyleClass().add("dialog-button-cancel");

		// TabPane + Tabs hinzufügen
		TabPane tab = new TabPane();
		Tab erinnerungTab = erinnerung(p);
		tab.getTabs().addAll(allgemein(p), eigenschaften(p), pflege(p), kalender(p), vermehrung(p), erinnerungTab);
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

		// Notiz deaktivieren nur für Admin
		if (benutzer.getTyp() == BenutzerTyp.ADMIN) {
		    notiz.setDisable(true);
		}

		// Erinnerung deaktivieren nur für bereits gespeicherte Pflanzen
		if (p.getAppPflanze().getPflanzenID() > 0 || benutzer.getTyp() == BenutzerTyp.ADMIN) {
		    erinnerungTab.setDisable(true);
		}


		/* --------------------------------------------
		 * esultconverter: Pflanze speichern
		 * -------------------------------------------- */

		this.setResultConverter(new Callback<ButtonType, ButtonType>() {
			@Override
			public ButtonType call(ButtonType clickedButton) {
				if (clickedButton == speichern) {

					/* ------------------------
					 * Allgemein
					 * ------------------------ */

					p.getAppPflanze().setPflanzenName(nameTxt.getText());
					p.getAppPflanze().setBotanikName(botanTxt.getText());
					p.getAppPflanze().setBildPfad(bildPfad.getText());

					/* ------------------------
					 * Node Eigenschaften
					 * ------------------------ */

					p.getAppPflanze().setPflanzenTyp(alTyp);
					p.getAppPflanze().setWuchsbreite(Double.parseDouble(breiteTxt.getText()));
					p.getAppPflanze().setWuchshoehe(Double.parseDouble(hoeheTxt.getText()));
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
					if (teil1.isSelected()) alTeile.add(VerwendeteTeile.BLUETE);
					if (teil2.isSelected()) alTeile.add(VerwendeteTeile.STIEL);
					if (teil3.isSelected()) alTeile.add(VerwendeteTeile.WURZEL);
					p.getAppPflanze().setVerwendeteTeile(alTeile);

					/* ------------------------
					 * Pflege
					 * ------------------------ */

					if (wasser1.isSelected()) p.getAppPflanze().setWasserbedarf(Wasserbedarf.GERING);
					else if (wasser2.isSelected()) p.getAppPflanze().setWasserbedarf(Wasserbedarf.MITTEL);
					else if (wasser3.isSelected()) p.getAppPflanze().setWasserbedarf(Wasserbedarf.HOCH);

					if (licht1.isSelected()) p.getAppPflanze().setLichtbedarf(Lichtbedarf.SONNIG);
					else if (licht2.isSelected()) p.getAppPflanze().setLichtbedarf(Lichtbedarf.HALBSCHATTIG);
					else if (licht3.isSelected()) p.getAppPflanze().setLichtbedarf(Lichtbedarf.SCHATTIG);

					if (dueng1.isSelected()) p.getAppPflanze().setDuengung(Intervall.WOECHENTLICH);
					else if (dueng2.isSelected()) p.getAppPflanze().setDuengung(Intervall.MONATLICH);
					else if (dueng3.isSelected()) p.getAppPflanze().setDuengung(Intervall.JAEHRLICH);

					/* ------------------------
					 * Kalenderdaten
					 * ------------------------ */

					alleKalender = new ArrayList<>();
					alleKalender.add(aussaatKalender.getAppKalender());
					alleKalender.add(blueteKalender.getAppKalender());
					alleKalender.add(ernteKalender.getAppKalender());
					alleKalender.add(rueckschnittKalender.getAppKalender());
					p.getAppPflanze().setKalender(alleKalender);

					// Sicherstellen: Rückschnittmonate als TreeSet
					p.getAppPflanze().getKalender().stream()
					.filter(k -> k.getKalendertyp() == Kalendertyp.RUECKSCHNITT)
					.flatMap(k -> k.getMonat().stream())
					.collect(Collectors.toCollection(TreeSet::new));

					/* ------------------------
					 * Notiz
					 * ------------------------ */

					p.getAppPflanze().setNotiz(notiz.getText());

					/* ------------------------
					 * Erinnerung
					 * ------------------------ */

					erinnerung.setDatum(date.getValue());
					erinnerung.setIntervall(erinnerung.getAppErinnerung().getIntervall());
					erinnerung.setTyp(erinnerung.getAppErinnerung().getTyp());
					ArrayList<Erinnerungen> alErin = new ArrayList<>();
					alErin.add(erinnerung.getAppErinnerung());
					p.getAppPflanze().setErinnerung(alErin);

					// Benutzer setzen
					p.getAppPflanze().setBenutzer(benutzer);

					/* ------------------------
					 * DB INSERT / UPDATE
					 * ------------------------ */
					try {
						if (p.getAppPflanze().getPflanzenID() > 0) {
							DB_Pflanze.updatePflanze(p.getAppPflanze());
						} else {
							DB_Pflanze.insertPflanze(p.getAppPflanze());
							erinnerung.getAppErinnerung().setBenutzer(benutzer);
							erinnerung.getAppErinnerung().setPflanze(p.getAppPflanze());

							if (benutzer.getTyp() == BenutzerTyp.BENUTZER) {
								DB_BotanikHub.insertBotanikHub(p.getAppPflanze(), benutzer);
							}
						}

						// Erinnerung separat speichern
						if (benutzer.getTyp() == BenutzerTyp.BENUTZER &&
								erinnerung.getAppErinnerung().getTyp() != null &&
								erinnerung.getAppErinnerung().getIntervall() != null &&
								erinnerung.getAppErinnerung().getDatum() != null &&
								erinnerung.getAppErinnerung().getPflanze() != null &&
								erinnerung.getAppErinnerung().getBenutzer() != null) {

							DB_Erinnerungen.insertErinnerung(erinnerung.getAppErinnerung());
						}

					} catch (SQLException ex) {
						Util.alertWindow(AlertType.ERROR, "Fehler", ex.toString()).showAndWait();
					}

					// Dialog schließen
					return null;
				}

				// Bei Abbruch → Speichern nicht ausführen
				return speichern;
			}
		});

		/* --------------------------------------------
		 * EVENTFILTER: Pflichtfelder validieren
		 * -------------------------------------------- */

		save.addEventFilter(ActionEvent.ACTION, e -> {

			// Name prüfen
			if (nameTxt == null || nameTxt.getText() == null || nameTxt.getText().trim().isEmpty()) {
				Util_Animations.pauseAnimation(nameTxt, Duration.seconds(5));
				fehler.setOpacity(1);
				Util_Animations.pauseAnimation(fehler, Duration.seconds(5));
				e.consume();
			}

			// Botanischer Name prüfen
			else if (botanTxt == null || botanTxt.getText() == null || botanTxt.getText().trim().isEmpty()) {
				Util_Animations.pauseAnimation(botanTxt, Duration.seconds(5));
				fehler.setOpacity(1);
				Util_Animations.pauseAnimation(fehler, Duration.seconds(5));
				e.consume();
			}

			// Höhe prüfen
			else if (hoeheTxt == null || hoeheTxt.getText() == null || hoeheTxt.getText().trim().isEmpty()) {
				Util_Animations.pauseAnimation(hoeheTxt, Duration.seconds(5));
				fehler.setOpacity(1);
				Util_Animations.pauseAnimation(fehler, Duration.seconds(5));
				e.consume();
			}

			// Breite prüfen
			else if (breiteTxt == null || breiteTxt.getText() == null || breiteTxt.getText().trim().isEmpty()) {
				Util_Animations.pauseAnimation(breiteTxt, Duration.seconds(5));
				fehler.setOpacity(1);
				Util_Animations.pauseAnimation(fehler, Duration.seconds(5));
				e.consume();
			}

			// Standort prüfen
			else if (p.getAppPflanze().getStandort() == null) {
				Util_Animations.pauseAnimation(standortLbl, Duration.seconds(5));
				fehler.setOpacity(1);
				Util_Animations.pauseAnimation(fehler, Duration.seconds(5));
				e.consume();
			}

			// Pflanzentyp prüfen
			else if (p.getAppPflanze().getPflanzenTyp() == null || p.getAppPflanze().getPflanzenTyp().isEmpty()) {
				Util_Animations.pauseAnimation(pflanzTypLabel, Duration.seconds(5));
				fehler.setOpacity(1);
				Util_Animations.pauseAnimation(fehler, Duration.seconds(5));
				e.consume();
			}

			// Duplikatsprüfung (Insert / Update unterscheiden!)
			try {
				if (p.getAppPflanze().getPflanzenID() == 0) {
					if (DB_Pflanze.insertNameExistiert(p.getAppPflanze().getPflanzenName())) {
						Util.alertWindow(AlertType.ERROR, "Fehler", "Pflanzenname existiert bereits").showAndWait();
						e.consume();
					}
				} else {
					if (DB_Pflanze.updateNameExistiert(p.getAppPflanze().getPflanzenName(), p.getAppPflanze().getPflanzenID())) {
						Util.alertWindow(AlertType.ERROR, "Fehler", "Pflanzenname existiert bereits").showAndWait();
						e.consume();
					}
				}
			} catch (SQLException ex) {
				Util.alertWindow(AlertType.ERROR, "Fehler", "Namensprüfung fehlgeschlagen").showAndWait();
				e.consume();
			}
		});

		/* --------------------------------------------
		 * Dialog-Fenster konfigurieren
		 * -------------------------------------------- */

		this.setTitle(p.getAppPflanze().getPflanzenID() == 0 
				? "Neue Pflanze anlegen" 
						: p.getAppPflanze().getPflanzenName() + " bearbeiten");

		this.getDialogPane().setContent(tab);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("dialog-layout");
	}

	public Tab allgemein(PflanzeFX p) {

		/* --------------------------------------------
		 * Pflanzenname & Botanischer Name
		 * -------------------------------------------- */
		nameTxt = new TextField(p.getAppPflanze().getPflanzenName());
		nameTxt.setPromptText("z.B. Lavendel, Rosmarin...");
		nameTxt.setPrefWidth(250);
		Label nameLbl = new Label("Pflanzenname:");

		botanTxt = new TextField(p.getAppPflanze().getBotanikName());
		botanTxt.setPromptText("z.B. Lavandula angustifolia");
		botanTxt.setPrefWidth(250);
		Label botLbl = new Label("Botanischer Name:");

		/* --------------------------------------------
		 * Bildpfad & Vorschau
		 * -------------------------------------------- */
		bildPfad = new TextField(p.getAppPflanze().getBildPfad());
		bildPfad.setPromptText("Bildpfad angeben");
		bildPfad.setPrefWidth(250);
		Label uploadLbl = new Label("Bild hochladen");

		Button suchen = new Button("Suchen");
		suchen.setPrefWidth(80);

		Button loeschen = new Button("Löschen");

		// Löschen Icon
		ImageView loeschIcon = new ImageView(new Image(BotanikHub_Client.class.getResource("/bin.png").toString()));
		loeschIcon.setFitWidth(65);
		loeschIcon.setFitHeight(60);
		loeschIcon.setOpacity(0.4);
		loeschen.setGraphic(loeschIcon);
		Util.tip(loeschIcon, "Bild löschen", Duration.millis(200), Duration.seconds(3));
		
		ImageView pflanzenBild = new ImageView();
		pflanzenBild.setFitWidth(185);
		pflanzenBild.setFitHeight(210);
		loeschen.setDisable(pflanzenBild == null);
		
		if (p.getAppPflanze().getBildPfad() != null && !p.getAppPflanze().getBildPfad().isBlank()) {
			pflanzenBild.setImage(new Image("file:" + p.getAppPflanze().getBildPfad()));
		}

		StackPane bildBox = new StackPane(pflanzenBild);
		bildBox.setPrefSize(100, 150);
		bildBox.getStyleClass().add("image-vorschau");

		/* --------------------------------------------
		 * Fehlericon
		 * -------------------------------------------- */
		fehler = new ImageView(new Image(BotanikHub_Client.class.getResource("/fehler.png").toString()));
		fehler.setFitHeight(40);
		fehler.setFitWidth(40);
		fehler.setOpacity(0); // unsichtbar
		Util.tip(fehler, "Bitte alle Pflichtfelder ausfüllen", Duration.millis(200), Duration.seconds(5));

		/* --------------------------------------------
		 * CSS & Layout
		 * -------------------------------------------- */
		suchen.getStyleClass().add("dialog-button-ok");
		loeschen.getStyleClass().add("dialog-button-ok");

		AnchorPane anchor = new AnchorPane();
		anchor.getChildren().addAll(
				nameLbl, nameTxt,
				botLbl, botanTxt,
				uploadLbl, bildPfad,
				suchen, loeschIcon,
				bildBox, fehler
				);

		// Eigene Methode für GUI-Positionierung
		Util.anchorpane(nameLbl, 20.0, null, 15.0, null);
		Util.anchorpane(nameTxt, 15.0, null, 120.0, null);
		Util.anchorpane(botLbl, 60.0, null, 15.0, null);
		Util.anchorpane(botanTxt, 55.0, null, 120.0, null);
		Util.anchorpane(uploadLbl, 100.0, null, 15.0, null);
		Util.anchorpane(bildPfad, 95.0, null, 120.0, null);
		Util.anchorpane(suchen, 95.0, null, 380.0, null);
		Util.anchorpane(loeschIcon, null, 5.0, null, 130.0);
		Util.anchorpane(bildBox, 125.0, null, 120.0, null);
		Util.anchorpane(fehler, 5.0, null, null, 5.0);

		/* --------------------------------------------
		 * Eventhandler für Suchen / Löschen
		 * -------------------------------------------- */
		suchen.setOnAction(e -> {
			FileChooser fc = new FileChooser();
			fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", "*.jpeg"));
			File file = fc.showOpenDialog(null);
			if (file != null) {
				String pfad = file.getAbsolutePath();
				bildPfad.setText(pfad);
				pflanzenBild.setImage(new Image(file.toURI().toString()));
			}
		});

		loeschIcon.setOnMousePressed(e -> {
			bildPfad.clear();
			pflanzenBild.setImage(null);
		});

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

		/* --------------------------------------------
		 * Zusammenbauen & zurückgeben
		 * -------------------------------------------- */
		VBox vb = new VBox(anchor);
		vb.setPadding(new Insets(5));

		Tab tabAllg = new Tab("Allgemein");
		tabAllg.setClosable(false);
		tabAllg.setContent(vb);

		return tabAllg;
	}

	public Tab eigenschaften(PflanzeFX p) {

		/*--------------------------------------------
		 * Breite & Höhe
		 *-------------------------------------------- */
		breiteTxt = new TextField(String.valueOf(p.getAppPflanze().getWuchsbreite()));
		breiteTxt.setPromptText("Breite in cm");
		breiteTxt.setPrefWidth(130);
		breiteTxt.setTextFormatter(new TextFormatter<>(Util.gibNurZiffernFilter()));

		hoeheTxt = new TextField(String.valueOf(p.getAppPflanze().getWuchshoehe()));
		hoeheTxt.setPromptText("Höhe in cm");
		hoeheTxt.setPrefWidth(130);
		hoeheTxt.setTextFormatter(new TextFormatter<>(Util.gibNurZiffernFilter()));

		/*--------------------------------------------
		 * Lebensdauer
		 *-------------------------------------------- */
		leben1 = new RadioButton("Einjährig");
		leben2 = new RadioButton("Zweijährig");
		leben3 = new RadioButton("Mehrjährig");

		leben1.setToggleGroup(lebensdauer);
		leben2.setToggleGroup(lebensdauer);
		leben3.setToggleGroup(lebensdauer);

		if (p.getAppPflanze().getLebensdauer() != null) {
			switch (p.getAppPflanze().getLebensdauer()) {
			case EINAEHRIG -> leben1.setSelected(true);
			case ZWEIJAEHRIG -> leben2.setSelected(true);
			case MEHRJAEHRIG -> leben3.setSelected(true);
			}
		}

		/*--------------------------------------------
		 * Verträglichkeit
		 *-------------------------------------------- */
		win1 = new RadioButton("Winterhart");
		win2 = new RadioButton("Nicht winterhart");

		win1.setToggleGroup(winterhart);
		win2.setToggleGroup(winterhart);

		if (p.getAppPflanze().getVertraeglichkeit() != null) {
			switch (p.getAppPflanze().getVertraeglichkeit()) {
			case WINTERHART -> win1.setSelected(true);
			case NICHT_WINTERHART -> win2.setSelected(true);
			}
		}

		/*--------------------------------------------
		 * Giftigkeit
		 *-------------------------------------------- */
		gift1 = new RadioButton("Giftig");
		gift2 = new RadioButton("Nicht giftig");

		gift1.setToggleGroup(giftigkeit);
		gift2.setToggleGroup(giftigkeit);
		giftigkeit.selectToggle(p.getAppPflanze().isGiftig() ? gift1 : gift2);

		/*--------------------------------------------
		 * Pflanzentyp-Auswahl
		 *-------------------------------------------- */
		pflanzTypLabel = new TextArea(
				(p.getAppPflanze().getPflanzenTyp() != null && !p.getAppPflanze().getPflanzenTyp().isEmpty())
				? p.getAppPflanze().getPflanzenTyp().stream()
						.map(Pflanzentyp::getBeschreibung)
						.collect(Collectors.joining(", "))
						: "Noch kein Typ ausgewählt"
				);
		pflanzTypLabel.setEditable(false);
		pflanzTypLabel.setPrefWidth(400);
		pflanzTypLabel.setPrefHeight(80);

		Button pflanzTyp = new Button("Pflanztyp auswählen");
		pflanzTyp.getStyleClass().add("dialog-button-ok");

		/*--------------------------------------------
		 * Verwendete Pflanzenteile
		 *-------------------------------------------- */
		teil1 = new CheckBox("Blüte");
		teil2 = new CheckBox("Stiel");
		teil3 = new CheckBox("Wurzel");

		alTeile.clear();
		for (VerwendeteTeile t : p.getAppPflanze().getVerwendeteTeile()) {
			switch (t) {
			case BLUETE -> teil1.setSelected(true);
			case STIEL -> teil2.setSelected(true);
			case WURZEL -> teil3.setSelected(true);
			}
		}

		/*--------------------------------------------
		 * Layout: GridPane
		 *-------------------------------------------- */
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(15);
		grid.setPadding(new Insets(10));

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
		grid.add(teil1, 1, 7);
		grid.add(teil2, 2, 7);
		grid.add(teil3, 3, 7);

		/*--------------------------------------------
		 * Eventhandler: Pflanzentyp-Auswahl
		 *-------------------------------------------- */
		pflanzTyp.setOnAction(e -> {
			Pflanze_PflanzenTyp_Dialog dialog = new Pflanze_PflanzenTyp_Dialog(p);
			Optional<ButtonType> o = dialog.showAndWait();

			if (o.isPresent() && o.get().getButtonData() == ButtonData.OK_DONE) {
				alTyp = dialog.getAlTyp();
				if (alTyp != null && !alTyp.isEmpty()) {
					String text = alTyp.stream()
							.map(Pflanzentyp::getBeschreibung)
							.collect(Collectors.joining(", "));
					pflanzTypLabel.setText(text);
				}
			}
		});

		/*--------------------------------------------
		 * Zusammenbauen & zurückgeben
		 *-------------------------------------------- */
		VBox vb = new VBox(grid);
		vb.setPadding(new Insets(5));

		Tab tabEig = new Tab("Eigenschaften");
		tabEig.setClosable(false);
		tabEig.setContent(vb);

		return tabEig;
	}

	public Tab pflege(PflanzeFX p) {

		/*--------------------------------------------
		 * Wasserbedarf
		 *-------------------------------------------- */
		ToggleGroup wasserbedarf = new ToggleGroup();
		wasser1 = new RadioButton("Gering");
		wasser2 = new RadioButton("Mittel");
		wasser3 = new RadioButton("Hoch");
		wasser1.setToggleGroup(wasserbedarf);
		wasser2.setToggleGroup(wasserbedarf);
		wasser3.setToggleGroup(wasserbedarf);

		if (p.getAppPflanze().getWasserbedarf() != null) {
			switch (p.getAppPflanze().getWasserbedarf()) {
			case GERING -> wasser1.setSelected(true);
			case MITTEL -> wasser2.setSelected(true);
			case HOCH -> wasser3.setSelected(true);
			}
		}

		/*--------------------------------------------
		 * Lichtbedarf
		 *-------------------------------------------- */
		ToggleGroup lichtbedarf = new ToggleGroup();
		licht1 = new RadioButton("Sonne");
		licht2 = new RadioButton("Halbschatten");
		licht3 = new RadioButton("Schatten");
		licht1.setToggleGroup(lichtbedarf);
		licht2.setToggleGroup(lichtbedarf);
		licht3.setToggleGroup(lichtbedarf);

		if (p.getAppPflanze().getLichtbedarf() != null) {
			switch (p.getAppPflanze().getLichtbedarf()) {
			case SONNIG -> licht1.setSelected(true);
			case HALBSCHATTIG -> licht2.setSelected(true);
			case SCHATTIG -> licht3.setSelected(true);
			}
		}

		/*--------------------------------------------
		 * Standort
		 *-------------------------------------------- */
		Button standort = new Button("Standort");
		standortLbl = new TextArea((p.getAppPflanze().getStandort() != null)
				? p.getAppPflanze().getStandort().getBeschreibung()
						: "Noch kein Standort gewählt");
		standortLbl.setPrefHeight(50);
		standortLbl.setPrefWidth(400);
		standortLbl.setEditable(false);

		/*--------------------------------------------
		 * Rückschnittzeit
		 *-------------------------------------------- */
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

		/*--------------------------------------------
		 * Düngung
		 *-------------------------------------------- */
		ToggleGroup duengung = new ToggleGroup();
		dueng1 = new RadioButton("Wöchentlich");
		dueng2 = new RadioButton("Monatlich");
		dueng3 = new RadioButton("Jährlich");
		dueng1.setToggleGroup(duengung);
		dueng2.setToggleGroup(duengung);
		dueng3.setToggleGroup(duengung);

		if (p.getAppPflanze().getDuengung() != null) {
			switch (p.getAppPflanze().getDuengung()) {
			case WOECHENTLICH -> dueng1.setSelected(true);
			case MONATLICH -> dueng2.setSelected(true);
			case JAEHRLICH -> dueng3.setSelected(true);
			}
		}

		/*--------------------------------------------
		 * CSS Styling
		 *-------------------------------------------- */
		rueckschnittLbl.getStyleClass().add("dialog-label");
		standortLbl.getStyleClass().add("dialog-label");
		rueckschnitt.getStyleClass().add("dialog-button-ok");
		standort.getStyleClass().add("dialog-button-ok");

		/*--------------------------------------------
		 * Layout: GridPane
		 *-------------------------------------------- */
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

		/*--------------------------------------------
		 * Eventhandler
		 *-------------------------------------------- */
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

		/*--------------------------------------------
		 * Zusammenbauen & zurückgeben
		 *-------------------------------------------- */
		VBox vb = new VBox(grid);
		vb.setPadding(new Insets(5));

		Tab tabPflege = new Tab("Pflege");
		tabPflege.setClosable(false);
		tabPflege.setContent(vb);

		return tabPflege;
	}

	public Tab kalender(PflanzeFX p) {

		/*--------------------------------------------
		 * Aussaat
		 *-------------------------------------------- */
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

		/*--------------------------------------------
		 * Blütezeit
		 *-------------------------------------------- */
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

		/*--------------------------------------------
		 * Erntezeit
		 *-------------------------------------------- */
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

		/*--------------------------------------------
		 * CSS Styling
		 *-------------------------------------------- */
		aussaat.getStyleClass().add("dialog-button-ok");
		ausLbl.getStyleClass().add("dialog-label");

		bluete.getStyleClass().add("dialog-button-ok");
		blueteLbl.getStyleClass().add("dialog-label");

		ernte.getStyleClass().add("dialog-button-ok");
		ernteLbl.getStyleClass().add("dialog-label");

		/*--------------------------------------------
		 * Layout: GridPane
		 *-------------------------------------------- */
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

		/*--------------------------------------------
		 * Eventhandler
		 *-------------------------------------------- */
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

		/*--------------------------------------------
		 * Zusammenbauen & zurückgeben
		 *-------------------------------------------- */
		VBox vb = new VBox(grid);
		vb.setPadding(new Insets(30, 10, 10, 10));

		Tab tabKal = new Tab("Kalender");
		tabKal.setClosable(false);
		tabKal.setContent(vb);

		return tabKal;
	}

	public Tab vermehrung(PflanzeFX p) {

		/*--------------------------------------------
		 * Vermehrungsarten
		 *-------------------------------------------- */
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

		/*--------------------------------------------
		 * Notizfeld
		 *-------------------------------------------- */
		notiz = new TextArea(p.getAppPflanze().getNotiz());
		notiz.setPrefWidth(400);
		notiz.setPrefHeight(200);
		notiz.setPromptText("Notizen hinzufügen");

		/*--------------------------------------------
		 * CSS Styling
		 *-------------------------------------------- */
		methoden.getStyleClass().add("dialog-button-ok");
		methodenLbl.getStyleClass().add("dialog-label");
		notiz.getStyleClass().add("dialog-label");

		/*--------------------------------------------
		 * Layout: GridPane
		 *-------------------------------------------- */
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(15);

		grid.add(new Label("Methoden:"), 0, 0);
		grid.add(methoden, 1, 0);
		grid.add(methodenLbl, 1, 1);

		grid.add(new Label("Notiz:"), 0, 2);
		grid.add(notiz, 1, 2);

		/*--------------------------------------------
		 * Eventhandler
		 *-------------------------------------------- */
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
				}
			}
		});

		/*--------------------------------------------
		 * Zusammenbauen & zurückgeben
		 *-------------------------------------------- */
		VBox vb = new VBox(grid);
		vb.setPadding(new Insets(30, 10, 10, 10));

		Tab vermTab = new Tab("Vermehrung");
		vermTab.setClosable(false);
		vermTab.setContent(vb);

		return vermTab;
	}

	public Tab erinnerung(PflanzeFX p) {

		/*--------------------------------------------
		 * Erinnerungstyp
		 *-------------------------------------------- */
		Button erTyp = new Button("Erinnerungstyp");
		TextArea erTypLbl = new TextArea(
				(erinnerung.getAppErinnerung().getTyp() != null) 
				? erinnerung.getAppErinnerung().getTyp().getBeschreibung()
						: "Erinnerungstyp");
		erTypLbl.setEditable(false);
		erTypLbl.setPrefWidth(400);
		erTypLbl.setPrefHeight(50);

		if (erinnerung.getAppErinnerung().getTyp() != null) {
			erTypLbl.setText(erinnerung.getAppErinnerung().getTyp().getBeschreibung());
		}

		/*--------------------------------------------
		 * Intervall
		 *-------------------------------------------- */
		Button intervall = new Button("Intervall");
		TextArea intervallLbl = new TextArea(
				(erinnerung.getAppErinnerung().getIntervall() != null) 
				? erinnerung.getAppErinnerung().getIntervall().getBeschreibung()
						: "Noch kein Intervall gewählt");
		intervallLbl.setEditable(false);
		intervallLbl.setPrefWidth(400);
		intervallLbl.setPrefHeight(50);

		if (erinnerung.getAppErinnerung().getIntervall() != null) {
			intervallLbl.setText(erinnerung.getAppErinnerung().getIntervall().getBeschreibung());
		}

		/*--------------------------------------------
		 * Datumsauswahl
		 *-------------------------------------------- */
		date = new DatePicker();
		if (erinnerung.getDatum() != null) {
			date.setValue(erinnerung.getDatum());
		}

		/*--------------------------------------------
		 * CSS Styling
		 *-------------------------------------------- */
		erTyp.getStyleClass().add("dialog-button-ok");
		intervall.getStyleClass().add("dialog-button-ok");
		erTypLbl.getStyleClass().add("dialog-label");
		intervallLbl.getStyleClass().add("dialog-label");
		date.getStyleClass().add("date-picker");

		/*--------------------------------------------
		 * Layout: GridPane
		 *-------------------------------------------- */
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

		/*--------------------------------------------
		 * Eventhandler
		 *-------------------------------------------- */
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

		/*--------------------------------------------
		 * Zusammenbauen & zurückgeben
		 *-------------------------------------------- */
		VBox vb = new VBox(grid);
		vb.setPadding(new Insets(30, 10, 10, 10));

		Tab erTab = new Tab("Erinnerung");
		erTab.setClosable(false);
		erTab.setContent(vb);

		return erTab;
	}
}

