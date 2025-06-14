package GUI.HauptDialoge.BenutzerDialoge;

import java.sql.SQLException;
import java.util.ArrayList;

import GUI.BotanikHub_Client;
import GUI.Utilitys.Util;
import Modell.Benutzer;
import ModellFX.BenutzerFX;
import TEST_DB.DB_Benutzer;
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

public class Benutzer_Verwalten_Dialog extends Dialog<ButtonType> {

	private ObservableList<BenutzerFX> olBenutzer = FXCollections.observableArrayList();

	public Benutzer_Verwalten_Dialog() {

		/*--------------------------------------------
		 * Dialog für Admins zur Benutzerverwaltung
		 * - Anzeige von ID, Benutzername, Registrierungsdatum
		 * - Benutzer kann bearbeitet werden
		 *-------------------------------------------- */

		/*--------------------------------------------
		 * GUI-Elemente: Buttons & Headerbild
		 *-------------------------------------------- */
		Button benutzerBearbeiten = new Button("Benutzer bearbeiten");
		benutzerBearbeiten.setDisable(true);
		Button abbrechen = new Button("Abbrechen");
		abbrechen.setPrefWidth(80);

		ImageView headerBild = new ImageView(new Image(BotanikHub_Client.class.getResource("/benutzerheader.jpg").toString()));
		headerBild.setSmooth(true);
		headerBild.setCache(true);
		headerBild.setFitHeight(80);
		headerBild.setFitWidth(632);

		benutzerBearbeiten.getStyleClass().add("benutzer-dialog-button-ok");
		abbrechen.getStyleClass().add("benutzer-dialog-button-cancel");

		/*--------------------------------------------
		 * Tabellenansicht mit Benutzerinformationen
		 *-------------------------------------------- */
		TableColumn<BenutzerFX, Integer> idCol = new TableColumn<>("ID");
		idCol.setCellValueFactory(new PropertyValueFactory<>("benutzerID"));
		idCol.setPrefWidth(70);
		idCol.setStyle("-fx-alignment:center");

		TableColumn<BenutzerFX, String> nameCol = new TableColumn<>("Benutzer");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("benutzerName"));
		nameCol.setPrefWidth(270);
		nameCol.setStyle("-fx-alignment:center");

		TableColumn<BenutzerFX, Integer> regCol = new TableColumn<>("Registriert seit");
		regCol.setCellValueFactory(new PropertyValueFactory<>("registriertSeit"));
		regCol.setPrefWidth(290);
		regCol.setStyle("-fx-alignment:center");

		TableView<BenutzerFX> tvBenutzer = new TableView<>(olBenutzer);
		tvBenutzer.getColumns().addAll(idCol, nameCol, regCol);
		tvBenutzer.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		tvBenutzer.getStyleClass().add("benutzer-table-view");
		tvBenutzer.setRowFactory(tv -> {
			TableRow<BenutzerFX> row = new TableRow<>();
			row.getStyleClass().add("benutzer-table-row-cell");
			return row;
		});

		readAlleBenutzer();
		
		/*--------------------------------------------
		 * Listener auf Tabellen-Auswahl
		 *-------------------------------------------- */
		tvBenutzer.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<>() {
			@Override
			public void changed(ObservableValue<? extends BenutzerFX> arg0, BenutzerFX alt, BenutzerFX neu) {
				benutzerBearbeiten.setDisable(neu == null);
			}
		});

		/*--------------------------------------------
		 * Eventhandler für Buttons
		 *-------------------------------------------- */
		abbrechen.setOnAction(e -> this.setResult(ButtonType.CANCEL));
		this.getDialogPane().getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, e -> {
			this.setResult(ButtonType.CANCEL);
		});

		benutzerBearbeiten.setOnMousePressed(e -> {
			new Benutzer_Bearbeiten_Dialog(tvBenutzer.getSelectionModel().getSelectedItem()).showAndWait();
			
		});

		/*--------------------------------------------
		 * Layout: AnchorPane zur Positionierung der Buttons
		 *-------------------------------------------- */
		AnchorPane button = new AnchorPane(benutzerBearbeiten, abbrechen);
		Util.anchorpane(benutzerBearbeiten, 10.0, null, 1.0, null);
		Util.anchorpane(abbrechen, 10.0, null, null, 1.0);

		/*--------------------------------------------
		 * Layout: VBox mit Header, Tabelle und Buttons
		 *-------------------------------------------- */
		HBox header = new HBox(headerBild);
		header.setPadding(new Insets(5, 0, 5, 0));

		VBox gesamt = new VBox(header, tvBenutzer, button);

		/*--------------------------------------------
		 * Dialogeinstellungen
		 *-------------------------------------------- */
		this.setTitle("Benutzerverwaltung");
		this.getDialogPane().setContent(gesamt);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("benutzer-dialog-layout");
		this.getDialogPane().setPrefHeight(450);
		this.getDialogPane().setPrefWidth(550);

	}
	
	private void readAlleBenutzer() {
		try {
		ArrayList<Benutzer> alBenutzer = DB_Benutzer.readAlleBenutzer();
		olBenutzer.clear();
		for(Benutzer einBenutzer : alBenutzer) {
			olBenutzer.add(new BenutzerFX(einBenutzer));
		}
		} catch(SQLException e) {
			Util.alertWindow(AlertType.ERROR, "Feler", e.toString());
		}
	}
}
