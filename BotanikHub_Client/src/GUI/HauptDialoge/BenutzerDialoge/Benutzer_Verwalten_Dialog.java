package GUI.HauptDialoge.BenutzerDialoge;

import java.sql.SQLException;
import java.util.ArrayList;

import Client.BotanikHub_Client;
import GUI.Utilitys.Util_Help;
import Modell.Benutzer;
import ModellFX.BenutzerFX;
import ServiceFunctions.Service_Benutzer;
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

public class Benutzer_Verwalten_Dialog extends Dialog<ButtonType> {

	private ObservableList<BenutzerFX> olBenutzer = FXCollections.observableArrayList();

	@SuppressWarnings("unchecked")
	public Benutzer_Verwalten_Dialog() {

		/*
		 * Dialog für den Admin zur Benutzerverwaltung
		 * Er kann hier nur die Benutzer die registriert sind bearbeiten
		 */

		// Buttons & Co
		Button benutzerBearbeiten = new Button("Benutzer bearbeiten");
		benutzerBearbeiten.setDisable(true);
		Button abbrechen = new Button("Abbrechen");
		abbrechen.setPrefWidth(80);

		ImageView headerBild = new ImageView(new Image(BotanikHub_Client.class.getResource("/Benutzer_Verwalten_Headerbild.jpg").toString()));
		headerBild.setSmooth(true);
		headerBild.setCache(true);
		headerBild.setFitHeight(80);
		headerBild.setFitWidth(632);

		// CSS Styling
		benutzerBearbeiten.getStyleClass().add("benutzer-dialog-button-ok");
		abbrechen.getStyleClass().add("benutzer-dialog-button-cancel");

		// TableView: Benutzerobjekte
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
		// Eigene CSS TableView -> TableRowCell muss extra formatiert werden
		tvBenutzer.setRowFactory(tv -> {
			TableRow<BenutzerFX> row = new TableRow<>();
			row.getStyleClass().add("benutzer-table-row-cell");
			return row;
		});

		// readMethode -> unten
		readAlleBenutzer();

		// Changeistener: tvBenutzer
		tvBenutzer.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<>() {
			@Override
			public void changed(ObservableValue<? extends BenutzerFX> arg0, BenutzerFX arg1, BenutzerFX arg2) {
				benutzerBearbeiten.setDisable(arg2 == null);
			}
		});

		// Eventhandler: abbrechen, bearbeiten
		abbrechen.setOnAction(e -> this.setResult(ButtonType.CANCEL));
		this.getDialogPane().getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, e -> {
			this.setResult(ButtonType.CANCEL);
		});

		benutzerBearbeiten.setOnMousePressed(e -> {
			new Benutzer_Bearbeiten_Dialog(tvBenutzer.getSelectionModel().getSelectedItem()).showAndWait();
			readAlleBenutzer();
		});

		// Layout: AnchorPane zur Positionierung der Buttons
		AnchorPane button = new AnchorPane(benutzerBearbeiten, abbrechen);
		Util_Help.anchorpane(benutzerBearbeiten, 10.0, null, 1.0, null);
		Util_Help.anchorpane(abbrechen, 10.0, null, null, 1.0);

		// Zusammenbau & Dialogeinstellungen
		HBox header = new HBox(headerBild);
		header.setPadding(new Insets(5, 0, 5, 0));
		VBox gesamt = new VBox(header, tvBenutzer, button);

		this.setTitle("Benutzerverwaltung");
		this.getDialogPane().setContent(gesamt);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("benutzer-dialog-layout");
		this.getDialogPane().setPrefHeight(450);
		this.getDialogPane().setPrefWidth(550);
		// Stage holen zum Icon setzen, da ich direkt im Dialog keins setzen kann
		Stage arg1 = (Stage) this.getDialogPane().getScene().getWindow();
		arg1.getIcons().add(new Image(BotanikHub_Client.class.getResource("/Window_Icon_Lebensbaum.jpg").toString()));

	}
	private void readAlleBenutzer() {
		try {
			ArrayList<Benutzer> alBenutzer = Service_Benutzer.getBenutzer();
			olBenutzer.clear();
			for(Benutzer einBenutzer : alBenutzer) {
				olBenutzer.add(new BenutzerFX(einBenutzer));
			}
		} catch(SQLException e) {
			Util_Help.alertWindow(AlertType.ERROR, "Fehler", e.toString());
		}
	}
}
