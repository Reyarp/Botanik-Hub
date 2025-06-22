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

import Client.BotanikHub_Client;
import Enum.BenutzerTyp;
import GUI.Utilitys.Util_Help;
import Modell.Benutzer;
import Modell.MeineWunschliste;
import Modell.Pflanze;
import ModellFX.MeineWunschlisteFX;
import ModellFX.PflanzeFX;
import ServiceFunctions.Service_BotanikHub;
import ServiceFunctions.Service_Pflanze;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

	@SuppressWarnings("unchecked")
	public Pflanzen_Verwalten_Dialog(Benutzer benutzer) {
		
		/*
		 * Dieser Dialog verwaltet alle Pflanzen
		 * Hier kann man neue anlegen, bearbeite, löschen oder ansehen
		 * Zusätzlich gibt es für den Benutzer noch die 
		 * Notizfunktion, Wunschliste und PflanzeEntdecken
		 */

		// Buttons & Co
		Button neuePflanze = new Button("Neue Pflanze anlegen");
		Button pflanzeBearbeiten = new Button("Pflanze bearbeiten");
		Button pflanzeLoeschen = new Button("Pflanze löschen");
		Button pflanzeAnsehen = new Button("Pflanze ansehen");
		Button abbrechen = new Button("Abbrechen"); 

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

		// Icon einstellungen
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

		// CSS Styling
		neuePflanze.getStyleClass().add("dialog-button-ok");
		pflanzeBearbeiten.getStyleClass().add("dialog-button-ok");
		pflanzeLoeschen.getStyleClass().add("dialog-button-ok");
		pflanzeAnsehen.getStyleClass().add("dialog-button-ok");
		abbrechen.getStyleClass().add("dialog-button-cancel");
		wunschlisteIcon.getStyleClass().add("kalender-label");
		entdecken.getStyleClass().add("kalender-label");
		notiz.getStyleClass().add("kalender-label");

		// Eigene ToolTip Methode
		Util_Help.tip(wunschlisteIcon, "Meine Wunschliste", Duration.millis(200), Duration.seconds(5));
		Util_Help.tip(entdecken, "Pflanzen entdecken", Duration.millis(200), Duration.seconds(5));
		Util_Help.tip(notiz, "Notiz bearbeiten", Duration.millis(200), Duration.seconds(5));

		// TableView: tvPflanze
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


		/* Da ich keine ButtonTypes hier verwendet habe musste ich eine andere Lösung zum schliessen finden
		 * über this.setResult kann ich dem Fenster sagen -> ButtonType.Cancel = Schliesse das fenster
		 */
		abbrechen.setOnAction(e -> this.setResult(ButtonType.CANCEL));
		// Dieser befehl ist ähnlich wie oben nur für das 'x' beim Fenster
		this.getDialogPane().getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, e -> {
			this.setResult(ButtonType.CANCEL);
		});

		// Eventhandler: neuePflanze, pflanzeBearbeiten, pflanzeLoeschen, pflanzeAnsehen
		neuePflanze.setOnAction(e -> {
			PflanzeFX pfx = new PflanzeFX(new Pflanze(benutzer));
			Pflanze_Anlegen_Dialog dialog = new Pflanze_Anlegen_Dialog(pfx, benutzer);
			Optional<ButtonType> result = dialog.showAndWait();
			if (result.isPresent() && result.get().getButtonData() == ButtonData.OK_DONE) {
				readPflanzen(benutzer);	
			}	
		});

		pflanzeBearbeiten.setOnAction(e -> {
			new Pflanze_Anlegen_Dialog(tvPflanze.getSelectionModel().getSelectedItem(), benutzer).showAndWait();
			readPflanzen(benutzer);
		});



		pflanzeLoeschen.setOnAction(e -> {
			Alert alert = Util_Help.alertWindow(AlertType.CONFIRMATION, "Wirklich löschen", "Möchten Sie die Pflanze wirklich löschen?");
			Optional<ButtonType> result = alert.showAndWait();

			if(result.isPresent() && result.get() == ButtonType.YES) {
				try {
					Pflanze pflanze = tvPflanze.getSelectionModel().getSelectedItem().getAppPflanze();

					// Unterscheide: Admin / Benutzer
					if(benutzer.getTyp() == BenutzerTyp.ADMIN) {
						Service_Pflanze.deletePflanze(pflanze.getPflanzenID());
						Util_Help.alertWindow(AlertType.INFORMATION, "Erfolgreich gelöscht", "Pflanze erfolgreich aus Datenbank gelöscht").showAndWait();
					} else {
						Service_BotanikHub.deleteBotanikHub(pflanze.getPflanzenID(), benutzer.getBenutzerId());
						Util_Help.alertWindow(AlertType.INFORMATION, "Erfolgreich entfernt", "Pflanze erfolgreich aus Botanik-Hub entfernt").showAndWait();
					}

					olPflanze.remove(tvPflanze.getSelectionModel().getSelectedItem()); // Direkt aus Tabelle entfernen
					readPflanzen(benutzer); // Neu laden zur Sicherheit

				} catch(SQLException ex) {
					Util_Help.alertWindow(AlertType.ERROR, "Fehler", ex.getMessage()).showAndWait();
				}
			}
		});

		pflanzeAnsehen.setOnMousePressed(e -> {
			new Pflanze_Ansehen_Dialog(tvPflanze.getSelectionModel().getSelectedItem(), benutzer).showAndWait();
			readPflanzen(benutzer);
		});


		// Zusätzliche Funktionen für Benutzer
		notiz.setOnMousePressed(e -> {
			new Pflanzen_Notiz_Bearbeiten_Dialog(tvPflanze.getSelectionModel().getSelectedItem(), benutzer).showAndWait();
			readPflanzen(benutzer);
		});

		wunschlisteIcon.setOnMousePressed(e -> {
			new Pflanze_Wunschliste_Dialog(new MeineWunschlisteFX(new MeineWunschliste()), benutzer).showAndWait();
			readPflanzen(benutzer);
		});

		entdecken.setOnMousePressed(e -> {
			new Pflanzen_Entdecken_Dialog(new PflanzeFX(new Pflanze()), benutzer).showAndWait();
			readPflanzen(benutzer);
		});

		// Rechteverwaltung: Admin/Benutzer
		if(benutzer.getTyp() == BenutzerTyp.ADMIN) {
			wunschlisteIcon.setDisable(true);
			wunschlisteIcon.setOpacity(0.4);
			entdecken.setDisable(true);
			entdecken.setOpacity(0.4);
			notiz.setDisable(true);
			notiz.setOpacity(0.4);
		}

		// readMethode -> unten
		readPflanzen(benutzer);

		// changelistener
		tvPflanze.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<>() {

			@Override
			public void changed(ObservableValue<? extends PflanzeFX> arg0, PflanzeFX arg1, PflanzeFX arg2) {
				if(arg2 == null) {
					pflanzeBearbeiten.setDisable(true);
					pflanzeAnsehen.setDisable(true);
					pflanzeLoeschen.setDisable(true);
					notiz.setDisable(true);
					notiz.setOpacity(0.4);
					return;
				}
				
				// Admin Booleans
				boolean adminPflanze = arg2.getAppPflanze().getBenutzer().getBenutzerId() == 1;
				boolean adminLogin = benutzer.getTyp() == BenutzerTyp.ADMIN;
				boolean adminDarfBearbeiten = adminPflanze && adminLogin;
				
				// Pflanze des aktuellen Benutzers
				boolean userLogin = !adminLogin;
				boolean userPflanze = arg2.getAppPflanze().getBenutzer().equals(benutzer);
				boolean userDarfBearbeiten = userLogin && userPflanze;
				boolean userNotizBearbeiten = userLogin && userPflanze;
				
				// Buttons aktivieren
				pflanzeAnsehen.setDisable(false);
				pflanzeLoeschen.setDisable(false);
				
				if(adminLogin) {
					pflanzeBearbeiten.setDisable(!adminDarfBearbeiten);
					notiz.setDisable(true);
					notiz.setOpacity(0.4);
				}
				if(userLogin) {
					pflanzeBearbeiten.setDisable(!userDarfBearbeiten);
					
					notiz.setDisable(!userNotizBearbeiten);
					notiz.setOpacity(userNotizBearbeiten ? 1:0.4);
				}
				
			}
		});
		

		// Layout: AnchorPane
		AnchorPane buttonPane = new AnchorPane(neuePflanze, pflanzeBearbeiten, pflanzeLoeschen, pflanzeAnsehen, wunschlisteIcon, entdecken, abbrechen, notiz);
		Util_Help.anchorpane(neuePflanze, null, 0.0, 0.0, null);
		Util_Help.anchorpane(pflanzeBearbeiten, null, 0.0, 150.0, null);
		Util_Help.anchorpane(pflanzeLoeschen, null, 0.0, 285.0, null);
		Util_Help.anchorpane(pflanzeAnsehen, null, 0.0, 403.0, null);
		Util_Help.anchorpane(wunschlisteIcon, null, -2.0, null, 145.0);
		Util_Help.anchorpane(entdecken, null, -7.5, null, 100.0);
		Util_Help.anchorpane(notiz, null, -2.0, null, 185.0);
		Util_Help.anchorpane(abbrechen, null, 0.0, null, 5.0);

		// Zusammenbau & Dialogeinstellungen
		VBox layout = new VBox(new HBox(headerBild), tvPflanze, buttonPane);
		layout.setSpacing(10);
		layout.setPadding(new Insets(5));

		this.setTitle(benutzer.getTyp() == BenutzerTyp.ADMIN ? "Globale Pflanzenverwaltung" : "Botanik-Hub");
		this.getDialogPane().setContent(layout);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("dialog-layout");
		this.getDialogPane().setPrefHeight(450);
		this.getDialogPane().setPrefWidth(750);
	}

	private void readPflanzen(Benutzer benutzer) {
		try {
			ArrayList<Pflanze> alPflanze = new ArrayList<>();
			if(benutzer.getTyp() == BenutzerTyp.ADMIN) {
				alPflanze = Service_Pflanze.getPflanze();
			} else if(benutzer.getTyp() == BenutzerTyp.BENUTZER) {
				alPflanze = Service_BotanikHub.getBHPflanzen(benutzer.getBenutzerId());
			}
			olPflanze.clear();
			for(Pflanze einePflanze : alPflanze) {
				olPflanze.add(new PflanzeFX(einePflanze));
			}
		} catch(SQLException e) {
			Util_Help.alertWindow(AlertType.ERROR, "Fehler", e.getMessage());
		}
	}
}
