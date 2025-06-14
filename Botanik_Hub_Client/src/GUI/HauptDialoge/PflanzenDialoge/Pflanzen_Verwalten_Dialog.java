/*
 * Dialog zur Pflanzenverwaltung für Admins und Benutzer
 * - Anzeige aller Pflanzen (je nach Rolle unterschiedlich)
 * - Neue Pflanze anlegen, bearbeiten, löschen, ansehen
 * - Wunschliste und Notizen für Benutzer
 */

package GUI.HauptDialoge.PflanzenDialoge;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import Enum.BenutzerTyp;
import GUI.BotanikHub_Client;
import GUI.Utilitys.Util;
import Modell.Benutzer;
import Modell.MeineWunschliste;
import Modell.Pflanze;
import ModellFX.MeineWunschlisteFX;
import ModellFX.PflanzeFX;
import TEST_DB.DB_BotanikHub;
import TEST_DB.DB_Erinnerungen;
import TEST_DB.DB_Pflanze;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class Pflanzen_Verwalten_Dialog extends Dialog<ButtonType> {

	// Liste zur Anzeige der Pflanzen
	private ObservableList<PflanzeFX> olPflanze = FXCollections.observableArrayList();

	public Pflanzen_Verwalten_Dialog(Benutzer benutzer) {

		/*
		 * -----------------------------------------------
		 * GUI-Elemente: Buttons und Icons
		 * -----------------------------------------------
		 */
		Button neuePflanze = new Button("Neue Pflanze anlegen");
		Button pflanzeBearbeiten = new Button("Pflanze bearbeiten");
		Button pflanzeLoeschen = new Button("Pflanze löschen");
		Button pflanzeAnsehen = new Button("Pflanze ansehen");
		Button abbrechen = new Button("Abbrechen");

		// Voreinstellungen
		pflanzeBearbeiten.setDisable(true);
		pflanzeLoeschen.setDisable(true);
		pflanzeAnsehen.setDisable(true);
		abbrechen.setPrefWidth(80);

		// Icons
		ImageView wunschlisteIcon = new ImageView(new Image(BotanikHub_Client.class.getResource("/wishlist.png").toString()));
		ImageView entdecken = new ImageView(new Image(BotanikHub_Client.class.getResource("/suchen.png").toString()));
		ImageView notiz = new ImageView(new Image(BotanikHub_Client.class.getResource("/notiz.png").toString()));
		ImageView headerBild = new ImageView(benutzer.getTyp() == BenutzerTyp.ADMIN
				? new Image(BotanikHub_Client.class.getResource("/global.jpeg").toString())
						: new Image(BotanikHub_Client.class.getResource("/botanikhub.jpg").toString()));

		/*
		 * -----------------------------------------------
		 * Icon-Einstellungen (Größe, Stil, Tooltip)
		 * -----------------------------------------------
		 */
		wunschlisteIcon.setFitHeight(28);
		wunschlisteIcon.setFitWidth(28);
		entdecken.setFitHeight(37);
		entdecken.setFitWidth(37);
		notiz.setFitHeight(28);
		notiz.setFitWidth(28);
		notiz.setDisable(true);
		notiz.setOpacity(0.4);
		headerBild.setFitHeight(110);
		headerBild.setFitWidth(725);

		neuePflanze.getStyleClass().add("dialog-button-ok");
		pflanzeBearbeiten.getStyleClass().add("dialog-button-ok");
		pflanzeLoeschen.getStyleClass().add("dialog-button-ok");
		pflanzeAnsehen.getStyleClass().add("dialog-button-ok");
		abbrechen.getStyleClass().add("dialog-button-cancel");
		wunschlisteIcon.getStyleClass().add("kalender-label");
		entdecken.getStyleClass().add("kalender-label");
		notiz.getStyleClass().add("kalender-label");

		Util.tip(wunschlisteIcon, "Meine Wunschliste", Duration.millis(200), Duration.seconds(5));
		Util.tip(entdecken, "Pflanzen entdecken", Duration.millis(200), Duration.seconds(5));
		Util.tip(notiz, "Notiz bearbeiten", Duration.millis(200), Duration.seconds(5));

		/*
		 * -----------------------------------------------
		 * Tabellenansicht mit Spalten
		 * -----------------------------------------------
		 */
		TableColumn<PflanzeFX, Integer> idCol = new TableColumn<>("ID");
		idCol.setCellValueFactory(new PropertyValueFactory<>("pflanzenID"));
		idCol.setPrefWidth(100);
		idCol.setStyle("-fx-alignment:center");

		TableColumn<PflanzeFX, String> nameCol = new TableColumn<>("Pflanzename");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("pflanzenName"));
		nameCol.setPrefWidth(230);
		nameCol.setStyle("-fx-alignment:center");

		TableColumn<PflanzeFX, String> typCol = new TableColumn<>("Pflanzentyp");
		typCol.setCellValueFactory(new PropertyValueFactory<>("pflanzenTyp"));
		typCol.setPrefWidth(200);
		typCol.setStyle("-fx-alignment:center");

		TableColumn<PflanzeFX, String> benCol = new TableColumn<>(benutzer.getTyp() == BenutzerTyp.ADMIN ? "Erstellt von" : "Benutzer");
		benCol.setCellValueFactory(new PropertyValueFactory<>("benutzerName"));
		benCol.setPrefWidth(200);
		benCol.setStyle("-fx-alignment:center");

		TableView<PflanzeFX> tvPflanze = new TableView<>(olPflanze);
		tvPflanze.getColumns().addAll(idCol, nameCol, typCol, benCol);
		tvPflanze.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		/*
		 * -----------------------------------------------
		 * Button-Events
		 * -----------------------------------------------
		 */
		abbrechen.setOnAction(e -> this.setResult(ButtonType.CANCEL));
		this.getDialogPane().getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, e -> this.setResult(ButtonType.CANCEL));

		neuePflanze.setOnAction(e -> {
			PflanzeFX pfx = new PflanzeFX(new Pflanze(benutzer));
			Pflanze_Anlegen_Dialog dialog = new Pflanze_Anlegen_Dialog(pfx, benutzer);
			if (dialog.showAndWait().filter(bt -> bt.getButtonData() == ButtonData.OK_DONE).isPresent()) readPflanzen();
		});

		pflanzeBearbeiten.setOnAction(e -> {
			new Pflanze_Anlegen_Dialog(tvPflanze.getSelectionModel().getSelectedItem(), benutzer).showAndWait();
			readPflanzen();
		});

		pflanzeLoeschen.setOnAction(e -> {
			
			/*
			 * Löscht eine Pflanze:
			 * - Vorher Sicherheitsabfrage mit Bestätigungsdialog
			 * - Admin: löscht Pflanze + zugehörige Erinnerungen
			 * - Benutzer: entfernt Pflanze nur aus seinem BotanikHub (nicht global!)
			 */
			
			Alert alert = Util.alertWindow(AlertType.CONFIRMATION, "Wirklich löschen", "Möchten Sie die Pflanze wirklich löschen?");
			Optional<ButtonType> result = alert.showAndWait();

			if(result.isPresent() && result.get() == ButtonType.YES) {
				try {
					Pflanze pflanze = tvPflanze.getSelectionModel().getSelectedItem().getAppPflanze();

					if(benutzer.getTyp() == BenutzerTyp.ADMIN) {
						DB_Pflanze.deletePflanze(pflanze);
					} else {
						// Benutzer löscht nur seine Beziehung in der BotanikHub-Tabelle
						DB_BotanikHub.deleteBotanikHub(pflanze, benutzer);
					}

					Util.alertWindow(AlertType.INFORMATION, "Info", "Pflanze erfolgreich gelöscht").showAndWait();
					olPflanze.remove(tvPflanze.getSelectionModel().getSelectedItem()); // Direkt aus Tabelle entfernen
					readPflanzen(); // Neu laden zur Sicherheit

				} catch(SQLException ex) {
					Util.alertWindow(AlertType.ERROR, "Löschen fehlgeschlagen", "Pflanze löschen fehlgeschlagen").showAndWait();
				}
			}
		});


		/*
		 * -----------------------------------------------
		 * Zusätzliche Funktionen
		 * -----------------------------------------------
		 */
		pflanzeAnsehen.setOnMousePressed(e -> {
			new Pflanze_Ansehen_Dialog(tvPflanze.getSelectionModel().getSelectedItem()).showAndWait();
			readPflanzen();
		});

		notiz.setOnMousePressed(e -> {
			new Pflanzen_Notiz_Bearbeiten_Dialog(tvPflanze.getSelectionModel().getSelectedItem()).showAndWait();
			readPflanzen();
		});

		wunschlisteIcon.setOnMousePressed(e -> {
			new Pflanze_Wunschliste_Dialog(new MeineWunschlisteFX(new MeineWunschliste()), benutzer).showAndWait();
			readPflanzen();
		});

		entdecken.setOnMousePressed(e -> {
			new Pflanzen_Entdecken_Dialog(new PflanzeFX(new Pflanze()), benutzer).showAndWait();
			readPflanzen();
		});

		/*
		 * -----------------------------------------------
		 * Rechteverwaltung für Benutzer/Admin
		 * -----------------------------------------------
		 */
		if(benutzer.getTyp() == BenutzerTyp.ADMIN) {
			wunschlisteIcon.setDisable(true);
			wunschlisteIcon.setOpacity(0.4);
			entdecken.setDisable(true);
			entdecken.setOpacity(0.4);
			notiz.setDisable(true);
			notiz.setOpacity(0.4);
		}


		/*
		 * -----------------------------------------------
		 * Changelistener für tvPflanze
		 * -----------------------------------------------
		 */
		tvPflanze.getSelectionModel().selectedItemProperty().addListener((arg0, arg1, arg2) -> {
			
			/* Listener auf die Tabellen-Auswahl:
			 * - Aktiviert / deaktiviert Buttons je nach Pflanzenbesitzer & Benutzerrolle
			 * - Admin darf nur Admin-Pflanzen bearbeiten
			 * - Benutzer dürfen nur eigene Pflanzen bearbeiten & Notiz verwenden
			 */

			// Wenn keine gültige Pflanze ausgewählt wurde
			if (arg2 == null || arg2.getAppPflanze() == null || arg2.getAppPflanze().getBenutzer() == null) {
				pflanzeBearbeiten.setDisable(true);
				pflanzeLoeschen.setDisable(true);
				pflanzeAnsehen.setDisable(true);
				notiz.setDisable(true);
				notiz.setOpacity(0.4);
				return;
			}

			// Pflanze & eingeloggter Benutzer extrahieren
			Pflanze aktuellePflanze = arg2.getAppPflanze();
			Benutzer eingeloggterBenutzer = BotanikHub_Client.getBenutzer();
			int pflanzenBesitzerID = aktuellePflanze.getBenutzer().getBenutzerId();

			// Logikbooleans erstellen
			boolean isAdmin = eingeloggterBenutzer.getTyp() == BenutzerTyp.ADMIN;
			boolean istEigenePflanze = pflanzenBesitzerID == eingeloggterBenutzer.getBenutzerId();
			boolean istAdminPflanze = pflanzenBesitzerID == 1;

			pflanzeAnsehen.setDisable(false);
			pflanzeLoeschen.setDisable(false);

			if (isAdmin) {
				// Admins dürfen nur Admin-Pflanzen bearbeiten, keine Notizen
				pflanzeBearbeiten.setDisable(!istAdminPflanze);
				notiz.setDisable(true);
			} else {
				// Benutzer dürfen eigene Pflanzen bearbeiten & Notiz verwenden
				pflanzeBearbeiten.setDisable(!istEigenePflanze);
				notiz.setDisable(!istEigenePflanze);
				notiz.setOpacity(1);
			}
		});


		/*
		 * -----------------------------------------------
		 * Layout-Zusammenstellung
		 * -----------------------------------------------
		 */
		AnchorPane buttonPane = new AnchorPane(neuePflanze, pflanzeBearbeiten, pflanzeLoeschen, pflanzeAnsehen, wunschlisteIcon, entdecken, abbrechen, notiz);
		Util.anchorpane(neuePflanze, 5.0, 0.0, 0.0, null);
		Util.anchorpane(pflanzeBearbeiten, null, 0.0, 150.0, null);
		Util.anchorpane(pflanzeLoeschen, null, 0.0, 285.0, null);
		Util.anchorpane(pflanzeAnsehen, null, 0.0, 403.0, null);
		Util.anchorpane(wunschlisteIcon, null, -2.0, null, 145.0);
		Util.anchorpane(entdecken, null, -7.5, null, 100.0);
		Util.anchorpane(notiz, null, -2.0, null, 185.0);
		Util.anchorpane(abbrechen, null, 0.0, null, 5.0);

		VBox layout = new VBox(new HBox(headerBild), tvPflanze, buttonPane);
		layout.setPadding(new Insets(5));

		// Dialog-Eigenschaften
		this.setTitle(benutzer.getTyp() == BenutzerTyp.ADMIN ? "Globale Pflanzenverwaltung" : "Botanik-Hub");
		this.getDialogPane().setContent(layout);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("dialog-layout");
		this.getDialogPane().setPrefHeight(450);
		this.getDialogPane().setPrefWidth(750);

		readPflanzen();
	}


	private void readPflanzen() {
		/*
		 * Pflanzen aus DB laden, je nach Benutzertyp
		 */
		try {
			ArrayList<Pflanze> alPflanze;
			Benutzer aktuellerBenutzer = BotanikHub_Client.getBenutzer();
			if (aktuellerBenutzer.getTyp() == BenutzerTyp.ADMIN) {
				alPflanze = DB_Pflanze.readAllePflanzen();
			} else {
				alPflanze = DB_BotanikHub.readBotanikHubPflanzen();
			}
			olPflanze.clear();
			for (Pflanze einePflanze : alPflanze) {
				olPflanze.add(new PflanzeFX(einePflanze));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
