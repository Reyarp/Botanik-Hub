/* 
 * Dialog zur Anzeige der Wunschliste 
 * - Benutzer kann aus Wunschliste entfernen oder von WUnschliste in Botanikhub kopieren
 * - Klasse hat einen changelistener und eventfilter
 * */

package GUI.HauptDialoge.PflanzenDialoge;

import java.sql.SQLException;
import java.util.ArrayList;

import GUI.BotanikHub_Client;
import GUI.Utilitys.Util;
import Modell.Benutzer;
import Modell.MeineWunschliste;
import ModellFX.MeineWunschlisteFX;
import TEST_DB.DB_BotanikHub;
import TEST_DB.DB_Wunschliste;
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

	public Pflanze_Wunschliste_Dialog(MeineWunschlisteFX wunsch, Benutzer benutzer) {

		

		// Buttons erstellen
		Button abbrechen = new Button("Abbrechen");
		Button zuBotnikHub = new Button("Zu Botnik-Hub hinzufügen");
		zuBotnikHub.setDisable(true);
		Button entfernen = new Button("Aus Wunschliste entfernen");
		entfernen.setDisable(true);

		// Header-Bild setzen
		ImageView headerBild = new ImageView(new Image(BotanikHub_Client.class.getResource("/wunschliste.jpg").toString()));
		headerBild.setCache(true);
		headerBild.setFitHeight(90);
		headerBild.setFitWidth(725);
		headerBild.setSmooth(true);

		/* CSS Styling */
		abbrechen.getStyleClass().add("kalender-dialog-button-cancel");
		zuBotnikHub.getStyleClass().add("kalender-dialog-button-ok");
		entfernen.getStyleClass().add("kalender-dialog-button-ok");

		/*--------------------------------------------
		 * TableView & Columns
		 *-------------------------------------------- */
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

		/*--------------------------------------------
		 * Eventhandler
		 *-------------------------------------------- */
		abbrechen.setOnAction(e -> this.setResult(ButtonType.CANCEL));
		this.getDialogPane().getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, e -> {
			this.setResult(ButtonType.CANCEL);
		});

		readPflanze();

		zuBotnikHub.setOnAction(e -> {
			try {
				DB_BotanikHub.insertBotanikHub(tvWunsch.getSelectionModel().getSelectedItem().getPflanze(), benutzer);
				DB_Wunschliste.deleteWunschliste(tvWunsch.getSelectionModel().getSelectedItem().getPflanze(), benutzer);
				Util.alertWindow(AlertType.INFORMATION, "Info", "Pflanze erfolgreich zu Botanik-Hub hinzugefügt").showAndWait();
				readPflanze();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});

		entfernen.setOnAction(e -> {
			try {
				DB_Wunschliste.deleteWunschliste(tvWunsch.getSelectionModel().getSelectedItem().getPflanze(), benutzer);
				Util.alertWindow(AlertType.INFORMATION, "Info", "Pflanze aus Wunschliste entfernt").showAndWait();
				readPflanze();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});

		/*--------------------------------------------
		 * Changelistener
		 *-------------------------------------------- */
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

		/*--------------------------------------------
		 * Layout zusammensetzen
		 *-------------------------------------------- */
		AnchorPane button = new AnchorPane();
		button.getChildren().addAll(zuBotnikHub, entfernen, abbrechen);

		Util.anchorpane(abbrechen, 10.0, null, null, 1.0);
		Util.anchorpane(entfernen, 10.0, null, 175.0, null);
		Util.anchorpane(zuBotnikHub, 10.0, null, 1.0, null);

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

	/*--------------------------------------------
	 * Datenbank: Pflanzen einlesen
	 *-------------------------------------------- */
	private void readPflanze() {
		Benutzer benutzer = BotanikHub_Client.getBenutzer();
		try {
			ArrayList<MeineWunschliste> alPflanze = DB_Wunschliste.readWunschlistePflanze(benutzer);
			olWunsch.clear();
			for (MeineWunschliste ml : alPflanze) {
				olWunsch.add(new MeineWunschlisteFX(ml));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}
