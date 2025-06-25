package GUI.HauptDialoge.PflanzenDialoge;

import java.sql.SQLException;
import java.util.ArrayList;

import Client.BotanikHub_Client;
import GUI.Utilitys.Util_Help;
import Modell.Benutzer;
import Modell.BotanikHub;
import Modell.MeineWunschliste;
import Modell.Pflanze;
import ModellFX.PflanzeFX;
import ServiceFunctions.Service_BotanikHub;
import ServiceFunctions.Service_Pflanze;
import ServiceFunctions.Service_PflanzeEntdecken;
import ServiceFunctions.Service_Wunschliste;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Pflanzen_Entdecken_Dialog extends Dialog<ButtonType> {

	private ObservableList<PflanzeFX> olPflanze = FXCollections.observableArrayList();

	@SuppressWarnings("unchecked")
	public Pflanzen_Entdecken_Dialog(PflanzeFX p, Benutzer benutzer) {

		/*
		 * Dieser Dialog ist für den Benutzer
		 * Hier kann er Pflanzen erstellt vom Admin ansehen 
		 * entweder er tut sie in seine Sammlung oder in die Wunschliste
		 */

		// Buttons & Co
		Button abbrechen = new Button("Abbrechen");
		Button zuBotnikHub = new Button("Zu Botnik-Hub hinzufügen");
		zuBotnikHub.setDisable(true);
		Button zuWunschliste = new Button("Zu Wunschliste hinzufügen");
		zuWunschliste.setDisable(true);
		Button pflanzeAnsehen = new Button("Pflanze ansehen");
		pflanzeAnsehen.setDisable(true);

		// Header Bild
		ImageView headerBild = new ImageView(new Image(BotanikHub_Client.class.getResource("/Pflanzen_Entdecken_Headerbild.png").toString()));
		headerBild.setCache(true);
		headerBild.setFitHeight(90);
		headerBild.setFitWidth(725);
		headerBild.setSmooth(true);

		// Suchfeld
		TextField suchTxt = new TextField();
		suchTxt.setPromptText("Nach Pflanzenname suchen");
		suchTxt.setPrefWidth(150);

		// CSS Styling
		abbrechen.getStyleClass().add("kalender-dialog-button-cancel");
		zuBotnikHub.getStyleClass().add("kalender-dialog-button-ok");
		zuWunschliste.getStyleClass().add("kalender-dialog-button-ok");
		pflanzeAnsehen.getStyleClass().add("kalender-dialog-button-ok");

		// TableView: tvPflanze
		TableColumn<PflanzeFX, Integer> idCol = new TableColumn<>("ID");
		idCol.setCellValueFactory(new PropertyValueFactory<>("pflanzenID"));
		idCol.setPrefWidth(100);
		idCol.setStyle("-fx-alignment:center");

		TableColumn<PflanzeFX, String> nameCol = new TableColumn<>("Pflanzename");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("pflanzenName"));
		nameCol.setPrefWidth(300);
		nameCol.setStyle("-fx-alignment:center");

		TableColumn<PflanzeFX, String> typCol = new TableColumn<>("Pflanzentyp");
		typCol.setCellValueFactory(new PropertyValueFactory<>("pflanzenTyp"));
		typCol.setPrefWidth(330);
		typCol.setStyle("-fx-alignment:center");

		TableView<PflanzeFX> tvPflanze = new TableView<>(olPflanze);
		tvPflanze.getColumns().addAll(idCol, nameCol, typCol);
		tvPflanze.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		// readMethode -> unten
		readPflanzen(benutzer);


		/* Da ich keine ButtonTypes hier verwendet habe musste ich eine andere Lösung zum schliessen finden
		 * über this.setResult kann ich dem Fenster sagen -> ButtonType.Cancel = Schliesse das fenster
		 */
		abbrechen.setOnAction(e -> this.setResult(ButtonType.CANCEL));
		// Dieser befehl ist ähnlich wie oben nur für das 'x' beim Fenster
		this.getDialogPane().getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, e -> {
			this.setResult(ButtonType.CANCEL);
		});

		// Changelistener auf das Suchfeld -> aktualisiert die pflanzenliste nach jeder Suche -> methode unten
		suchTxt.textProperty().addListener(e ->{
			try {
				ArrayList<Pflanze> filter = Service_Pflanze.getPflanze(suchTxt.getText());
				updateSuchfeld(filter);
			} catch (SQLException e1) {
				Util_Help.alertWindow(AlertType.ERROR, "Fehler Pflanze Entdecken:", e1.getMessage());
			}
		});

		// Eventhandler: zuBotanikHub, zuWunschliste, pflanzeAnsehen
		zuBotnikHub.setOnAction(e -> {
			try {
				Pflanze selected = tvPflanze.getSelectionModel().getSelectedItem().getAppPflanze();
				// 1. Zu Botanik-Hub hinzufügen -> Benutzer als besitzer
				BotanikHub hub = new BotanikHub(benutzer, selected);
				Service_BotanikHub.postBotanikHub(hub);

				// 2. Aus Wunschliste entfernen
				Service_Wunschliste.deleteWunschliste(benutzer.getBenutzerId(),selected.getPflanzenID());
				Util_Help.alertWindow(AlertType.INFORMATION, "Info: Botanik-Hub", "Pflanze erfolgreich zu Botanik-Hub hinzugefügt").showAndWait();
				readPflanzen(benutzer);
			} catch (SQLException e1) {
				Util_Help.alertWindow(AlertType.ERROR, "Fehler: Zu BotanikHub hinzufügen", e1.getMessage());
			}
		});

		zuWunschliste.setOnAction(e -> {
			try {
				Pflanze selected = tvPflanze.getSelectionModel().getSelectedItem().getAppPflanze();
				MeineWunschliste wunsch = new MeineWunschliste(benutzer, selected);
				Service_Wunschliste.postWunschliste(wunsch);
				Util_Help.alertWindow(AlertType.INFORMATION, "Info: Wunschliste", "Pflanze erfolgreich zur Wunschliste hinzugefügt").showAndWait();
				readPflanzen(benutzer);
			} catch (SQLException e1) {
				Util_Help.alertWindow(AlertType.ERROR, "Fehler: Zu Wunschliste hinzufügen", e1.getMessage());
			}
		});

		pflanzeAnsehen.setOnMousePressed(e -> {
			new Pflanze_Ansehen_Dialog(tvPflanze.getSelectionModel().getSelectedItem(), benutzer).showAndWait();
			readPflanzen(benutzer);
		});

		// Changelistener: tvPflanze
		tvPflanze.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<>() {
			@Override
			public void changed(ObservableValue<? extends PflanzeFX> arg0, PflanzeFX alt, PflanzeFX neu) {
				if (neu != null) {
					zuBotnikHub.setDisable(false);
					zuWunschliste.setDisable(false);
					pflanzeAnsehen.setDisable(false);
				} else {
					zuBotnikHub.setDisable(true);
					zuWunschliste.setDisable(true);
					pflanzeAnsehen.setDisable(true);
				}
			}
		});

		// Layout: AnchorPane -> für Buttons
		AnchorPane button = new AnchorPane();
		button.getChildren().addAll(zuBotnikHub, zuWunschliste, pflanzeAnsehen, abbrechen, suchTxt);

		Util_Help.anchorpane(abbrechen, 10.0, null, null, 1.0);
		Util_Help.anchorpane(suchTxt, 10.0, null, null, 100.0); 
		Util_Help.anchorpane(zuWunschliste, 10.0, null, 175.0, null);
		Util_Help.anchorpane(zuBotnikHub, 10.0, null, 1.0, null);
		Util_Help.anchorpane(pflanzeAnsehen, 10.0, null, 350.0, null);

		// Zusammenbau & Dialogeinstellungen
		HBox header = new HBox(headerBild);
		header.setPadding(new Insets(5, 0, 5, 0));
		VBox gesamt = new VBox(header, tvPflanze, button);

		this.setTitle("Pflanzen entdecken");
		this.getDialogPane().setContent(gesamt);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("kalender-dialog-layout");
		this.getDialogPane().setPrefHeight(500);
		this.getDialogPane().setPrefWidth(725);
		// Stage holen zum Icon setzen, da ich direkt im Dialog keins setzen kann
		Stage arg1 = (Stage) this.getDialogPane().getScene().getWindow();
		arg1.getIcons().add(new Image(BotanikHub_Client.class.getResource("/Window_Icon_Lebensbaum.jpg").toString()));
	}

	private void updateSuchfeld(ArrayList<Pflanze> filter) {
		// Tableview clear
		olPflanze.clear();
		// Durch filter iterieren
		for(Pflanze p : filter) {
			// gefilterte Pflanze anzeigen
			olPflanze.add(new PflanzeFX(p));
		}
	}

	private void readPflanzen(Benutzer benutzer) {
		try {
			// Readmethode für Pflanzen
			ArrayList<Pflanze> alPflanze = Service_PflanzeEntdecken.getPEPflanzen(benutzer.getBenutzerId());
			olPflanze.clear();
			for(Pflanze einePflanze : alPflanze) {
				olPflanze.add(new PflanzeFX(einePflanze));
			}
		} catch(SQLException e) {
			Util_Help.alertWindow(AlertType.ERROR, "Fehler", e.getMessage());
		}
	}
}
