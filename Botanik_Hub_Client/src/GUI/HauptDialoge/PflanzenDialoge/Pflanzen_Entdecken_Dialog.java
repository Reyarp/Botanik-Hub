package GUI.HauptDialoge.PflanzenDialoge;

import java.sql.SQLException;
import java.util.ArrayList;

import GUI.BotanikHub_Client;
import GUI.Utilitys.Util;
import Modell.Benutzer;
import Modell.MeineWunschliste;
import Modell.Pflanze;
import ModellFX.PflanzeFX;
import TEST_DB.DB_BotanikHub;
import TEST_DB.DB_PflanzenEntdecken;
import TEST_DB.DB_Wunschliste;
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
import javafx.stage.WindowEvent;

public class Pflanzen_Entdecken_Dialog extends Dialog<ButtonType> {

	private ObservableList<PflanzeFX> olPflanze = FXCollections.observableArrayList();

	public Pflanzen_Entdecken_Dialog(PflanzeFX p, Benutzer benutzer) {

		/*--------------------------------------------
		 * Buttons & Initialisierung
		 *-------------------------------------------- */
		Button abbrechen = new Button("Abbrechen");
		Button zuBotnikHub = new Button("Zu Botnik-Hub hinzufügen");
		zuBotnikHub.setDisable(true);
		Button zuWunschliste = new Button("Zu Wunschliste hinzufügen");
		zuWunschliste.setDisable(true);
		Button pflanzeAnsehen = new Button("Pflanze ansehen");
		pflanzeAnsehen.setDisable(true);

		ImageView headerBild = new ImageView(new Image(BotanikHub_Client.class.getResource("/PflanzenEntdecken.png").toString()));
		headerBild.setCache(true);
		headerBild.setFitHeight(90);
		headerBild.setFitWidth(725);
		headerBild.setSmooth(true);

		/*--------------------------------------------
		 * CSS Styling
		 *-------------------------------------------- */
		abbrechen.getStyleClass().add("kalender-dialog-button-cancel");
		zuBotnikHub.getStyleClass().add("kalender-dialog-button-ok");
		zuWunschliste.getStyleClass().add("kalender-dialog-button-ok");
		pflanzeAnsehen.getStyleClass().add("kalender-dialog-button-ok");

		/*--------------------------------------------
		 * TableView & Spalten
		 *-------------------------------------------- */
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

		/*--------------------------------------------
		 * Eventhandler – Buttons
		 *-------------------------------------------- */
		abbrechen.setOnAction(e -> this.setResult(ButtonType.CANCEL));
		this.getDialogPane().getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, e -> {
			this.setResult(ButtonType.CANCEL);
		});

		zuBotnikHub.setOnAction(e -> {
			try {
				DB_BotanikHub.insertBotanikHub(tvPflanze.getSelectionModel().getSelectedItem().getAppPflanze(), benutzer);
				DB_Wunschliste.deleteWunschliste(tvPflanze.getSelectionModel().getSelectedItem().getAppPflanze(), benutzer);
				Util.alertWindow(AlertType.INFORMATION, "Info", "Pflanze erfolgreich zum Botanik-Hub hinzugefügt").showAndWait();
				readPflanzen();
			} catch (SQLException e1) {
				Util.alertWindow(AlertType.ERROR, "Fehler", e1.toString());
			}
		});

		zuWunschliste.setOnAction(e -> {
			try {
				DB_Wunschliste.insertWunschliste(new MeineWunschliste(benutzer, tvPflanze.getSelectionModel().getSelectedItem().getAppPflanze()));
				Util.alertWindow(AlertType.INFORMATION, "Info", "Pflanze erfolgreich zur Wunschliste hinzugefügt").showAndWait();
				readPflanzen();
			} catch (SQLException e1) {
				Util.alertWindow(AlertType.ERROR, "Fehler", e1.toString());
			}
		});

		pflanzeAnsehen.setOnMousePressed(e -> {
			new Pflanze_Ansehen_Dialog(tvPflanze.getSelectionModel().getSelectedItem()).showAndWait();
			readPflanzen();
		});

		/*--------------------------------------------
		 * ChangeListener: Auswahl Pflanze
		 *-------------------------------------------- */
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

		/*--------------------------------------------
		 * AnchorPane: Button-Positionierung
		 *-------------------------------------------- */
		AnchorPane button = new AnchorPane();
		button.getChildren().addAll(zuBotnikHub, zuWunschliste, pflanzeAnsehen, abbrechen);

		Util.anchorpane(abbrechen, 10.0, null, null, 1.0);
		Util.anchorpane(zuWunschliste, 10.0, null, 175.0, null);
		Util.anchorpane(zuBotnikHub, 10.0, null, 1.0, null);
		Util.anchorpane(pflanzeAnsehen, 10.0, null, 350.0, null);

		/*--------------------------------------------
		 * Layout zusammensetzen
		 *-------------------------------------------- */
		HBox header = new HBox(headerBild);
		header.setPadding(new Insets(5, 0, 5, 0));
		VBox gesamt = new VBox(header, tvPflanze, button);

		this.setTitle("Pflanzen entdecken");
		this.getDialogPane().setContent(gesamt);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("kalender-dialog-layout");
		this.getDialogPane().setPrefHeight(500);
		this.getDialogPane().setPrefWidth(725);

		/*--------------------------------------------
		 * Daten initial laden
		 *-------------------------------------------- */
		readPflanzen();
	}

	/*--------------------------------------------
	 * Pflanzen aus DB laden
	 *-------------------------------------------- */
	private void readPflanzen() {
		Benutzer benutzer = BotanikHub_Client.getBenutzer();
		try {
			ArrayList<Pflanze> alPflanze = DB_PflanzenEntdecken.readPflanzeEntdecken(benutzer);
			olPflanze.clear();
			for (Pflanze einePflanze : alPflanze) {
				olPflanze.add(new PflanzeFX(einePflanze));
			}
		} catch (SQLException ex) {
			Util.alertWindow(AlertType.ERROR, "Fehler", ex.toString());
		}
	}
}
