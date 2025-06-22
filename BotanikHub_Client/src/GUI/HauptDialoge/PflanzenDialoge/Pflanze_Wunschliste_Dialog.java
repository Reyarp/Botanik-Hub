/* 
 * Dialog zur Anzeige der Wunschliste 
 * - Benutzer kann aus Wunschliste entfernen oder von WUnschliste in Botanikhub kopieren
 * - Klasse hat einen changelistener und eventfilter
 * */

package GUI.HauptDialoge.PflanzenDialoge;

import java.sql.SQLException;
import java.util.ArrayList;

import Client.BotanikHub_Client;
import GUI.Utilitys.Util_Help;
import Modell.Benutzer;
import Modell.BotanikHub;
import Modell.MeineWunschliste;
import Modell.Pflanze;
import ModellFX.MeineWunschlisteFX;
import ServiceFunctions.Service_BotanikHub;
import ServiceFunctions.Service_Wunschliste;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;

public class Pflanze_Wunschliste_Dialog extends Dialog<ButtonType> {

	private ObservableList<MeineWunschlisteFX> olWunsch = FXCollections.observableArrayList();

	@SuppressWarnings("unchecked")
	public Pflanze_Wunschliste_Dialog(MeineWunschlisteFX wunsch, Benutzer benutzer) {

		/*
		 * Dieser Dialog ist die Wunschliste eines Benutzers
		 * Hier kann er Pflanzen die er hier vorgemerkt hat auch wieder löschen
		 * oder er fügt eine Pflanze in seine Sammlung hinzu
		 */

		// Buttons && Co
		Button abbrechen = new Button("Abbrechen");
		Button zuBotnikHub = new Button("Zu Botnik-Hub hinzufügen");
		zuBotnikHub.setDisable(true);
		Button entfernen = new Button("Aus Wunschliste entfernen");
		entfernen.setDisable(true);

		// Header-Bild
		ImageView headerBild = new ImageView(new Image(BotanikHub_Client.class.getResource("/wunschliste.jpg").toString()));
		headerBild.setCache(true);
		headerBild.setFitHeight(90);
		headerBild.setFitWidth(725);
		headerBild.setSmooth(true);

		// CSS Styling
		abbrechen.getStyleClass().add("kalender-dialog-button-cancel");
		zuBotnikHub.getStyleClass().add("kalender-dialog-button-ok");
		entfernen.getStyleClass().add("kalender-dialog-button-ok");

		// TableView: tvWunsch
		TableColumn<MeineWunschlisteFX, Integer> idCol = new TableColumn<>("ID");
		idCol.setCellValueFactory(new PropertyValueFactory<>("pflanzenID"));
		idCol.setPrefWidth(100);
		idCol.setStyle("-fx-alignment:center");

		TableColumn<MeineWunschlisteFX, String> nameCol = new TableColumn<>("Pflanzename");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("pflanzenName"));
		nameCol.setPrefWidth(300);
		nameCol.setStyle("-fx-alignment:center");

		TableColumn<MeineWunschlisteFX, String> typCol = new TableColumn<>("Pflanzentyp");
		typCol.setCellValueFactory(new PropertyValueFactory<>("pflanzenTyp"));
		typCol.setPrefWidth(330);
		typCol.setStyle("-fx-alignment:center");

		TableView<MeineWunschlisteFX> tvWunsch = new TableView<>(olWunsch);
		tvWunsch.getColumns().addAll(idCol, nameCol, typCol);
		tvWunsch.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		// readMethode -> unten
		readPflanzen();
		
		
		/* Da ich keine ButtonTypes hier verwendet habe musste ich eine andere Lösung zum schliessen finden
		 * über this.setResult kann ich dem Fenster sagen -> ButtonType.Cancel = Schliesse das fenster
		 */
		abbrechen.setOnAction(e -> this.setResult(ButtonType.CANCEL));
		// Dieser befehl ist ähnlich wie oben nur für das 'x' beim Fenster
		this.getDialogPane().getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, e -> {
			this.setResult(ButtonType.CANCEL);
		});

		// Eventhandler: zuBotanikHub, entfernen
		zuBotnikHub.setOnAction(e -> {
			try {
				Pflanze selected = tvWunsch.getSelectionModel().getSelectedItem().getPflanze();
				
				// 1. Insert in Botanik-Hub
				BotanikHub hub = new BotanikHub(benutzer, selected);
				Service_BotanikHub.postBotanikHub(hub);
				
				// 2. Delete aus Wunschliste -> Sonst exception
				Service_Wunschliste.deleteWunschliste(selected.getPflanzenID(), benutzer.getBenutzerId());
				Util_Help.alertWindow(AlertType.INFORMATION, "Info: Wunschliste", "Pflanze erfolgreich zu Botanik-Hub hinzugefügt").showAndWait();
				readPflanzen();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});

		entfernen.setOnAction(e -> {
			try {
				Service_Wunschliste.deleteWunschliste(tvWunsch.getSelectionModel().getSelectedItem().getPflanze().getPflanzenID(), benutzer.getBenutzerId());
				Util_Help.alertWindow(AlertType.INFORMATION, "Info: Wunschliste", "Pflanze aus Wunschliste entfernt").showAndWait();
				readPflanzen();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});

		// Changelistener: tvWunsch
		tvWunsch.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<>() {
			@Override
			public void changed(ObservableValue<? extends MeineWunschlisteFX> arg0, MeineWunschlisteFX arg1, MeineWunschlisteFX arg2) {
				if(arg2 != null) {
					zuBotnikHub.setDisable(false);
					entfernen.setDisable(false);
				} else {
					zuBotnikHub.setDisable(true);
					entfernen.setDisable(true);
				}
			}
		});

		// Layout: AnchorPane -> für Buttons
		AnchorPane button = new AnchorPane();
		button.getChildren().addAll(zuBotnikHub, entfernen, abbrechen);

		Util_Help.anchorpane(abbrechen, 10.0, null, null, 1.0);
		Util_Help.anchorpane(entfernen, 10.0, null, 175.0, null);
		Util_Help.anchorpane(zuBotnikHub, 10.0, null, 1.0, null);

		// Zusammenbau & Dialogeinstellungen
		HBox header = new HBox(headerBild);
		header.setPadding(new Insets(5, 0, 5, 0));
		VBox gesamt = new VBox(header, tvWunsch, button);

		this.setTitle("Meine Wunschliste");
		this.getDialogPane().setContent(gesamt);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("kalender-dialog-layout");
		this.getDialogPane().setPrefHeight(500);
		this.getDialogPane().setPrefWidth(725);
	}
	
	private void readPflanzen() {
		try {
			Benutzer benutzer = BotanikHub_Client.getBenutzer();
			ArrayList<MeineWunschliste> alW = Service_Wunschliste.getWLPflanzen(benutzer.getBenutzerId());
			olWunsch.clear();
			for(MeineWunschliste einWunsch : alW) {
				olWunsch.add(new MeineWunschlisteFX(einWunsch));
			}
		} catch(SQLException e) {
			Util_Help.alertWindow(AlertType.ERROR, "Fehler", e.getMessage());
		}
	}
}
