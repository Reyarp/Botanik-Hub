package GUI.HauptDialoge.ErinnerungsDialoge;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;

import Client.BotanikHub_Client;
import GUI.Utilitys.Util_Help;
import Modell.Benutzer;
import Modell.Erinnerungen;
import ModellFX.ErinnerungenFX;
import ServiceFunctions.Service_Erinnerung;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;

public class Erinnerung_Verwalten_Dialog extends Dialog<ButtonType> {

	private ObservableList<ErinnerungenFX> olErinnerung = FXCollections.observableArrayList();

	@SuppressWarnings("unchecked")
	public Erinnerung_Verwalten_Dialog(Benutzer benutzer) {

		/*
		 * Dieser Dialog gibt informationen über die Erinnerungen von Benutzern 
		 * Jede pflanze kann mehrere Erinnerungen haben
		 * Es wird eine TableView angezeigt mit den Inhalten
		 */

		// Buttons & Cp
		Button abbrechen = new Button("Abbrechen");
		Button neueErinnerung = new Button("Neue Erinnerung");
		Button editErinnerung = new Button("Erinnerung bearbeiten");
		editErinnerung.setDisable(true);
		Button deleteErinnerung = new Button("Erinnerung löschen");
		deleteErinnerung.setDisable(true);

		ImageView headerBild = new ImageView(new Image(BotanikHub_Client.class.getResource("/Erinnerungen_Verwalten_Headerbild.jpg").toString()));
		headerBild.setSmooth(true);
		headerBild.setCache(true);
		headerBild.setFitHeight(120);
		headerBild.setFitWidth(750);

		// CSS Styling
		neueErinnerung.getStyleClass().add("kalender-dialog-button-ok");
		editErinnerung.getStyleClass().add("kalender-dialog-button-ok");
		deleteErinnerung.getStyleClass().add("kalender-dialog-button-ok");
		abbrechen.getStyleClass().add("kalender-dialog-button-cancel");

		// TableView: Erinnerungsobjekte
		TableColumn<ErinnerungenFX, String> typCol = new TableColumn<>("Erinnerungstyp");
		typCol.setCellValueFactory(new PropertyValueFactory<>("typ"));
		typCol.setPrefWidth(150);
		typCol.setStyle("-fx-alignment:center");

		TableColumn<ErinnerungenFX, LocalDate> dateCol = new TableColumn<>("Wochentag");
		dateCol.setCellValueFactory(new PropertyValueFactory<>("datum"));
		dateCol.setPrefWidth(170);
		dateCol.setStyle("-fx-alignment:center");

		// Eigene Formatierung für den Wochentag
		dateCol.setCellFactory(column -> new javafx.scene.control.cell.TextFieldTableCell<ErinnerungenFX, LocalDate>() {
			@Override
			public void updateItem(LocalDate item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
				} else {
					// DateTimeFormatter mit dem format (Wochentag == EEEE)
					DateTimeFormatter format = DateTimeFormatter.ofPattern("EEEE", Locale.GERMAN);
					String tag = item.format(format);
					setText(tag); // z. B montag
				}
			}
		});

		TableColumn<ErinnerungenFX, Integer> intCol = new TableColumn<>("Intervall");
		intCol.setCellValueFactory(new PropertyValueFactory<>("intervall"));
		intCol.setPrefWidth(123);
		intCol.setStyle("-fx-alignment:center");

		TableColumn<ErinnerungenFX, Integer> pflCol = new TableColumn<>("Pflanze");
		pflCol.setCellValueFactory(new PropertyValueFactory<>("pflanzenName"));
		pflCol.setPrefWidth(300);
		pflCol.setStyle("-fx-alignment:center");

		TableView<ErinnerungenFX> tvErinnerung = new TableView<>(olErinnerung);
		tvErinnerung.getColumns().addAll(typCol, pflCol, dateCol, intCol);
		tvErinnerung.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		// readMethode -> unten
		readErinnerungen(benutzer);

		// Changelistener: tvErinnerung
		tvErinnerung.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<>() {

			@Override
			public void changed(ObservableValue<? extends ErinnerungenFX> arg0, ErinnerungenFX arg1,ErinnerungenFX arg2) {
				if(arg2 != null) {
					editErinnerung.setDisable(false);
					deleteErinnerung.setDisable(false);
				} else {
					editErinnerung.setDisable(true);
					deleteErinnerung.setDisable(true);
				}
			}
		});

		/* Da ich keine ButtonTypes hier verwendet habe musste ich eine andere Lösung zum schliessen finden
		 * über this.setResult kann ich dem Fenster sagen -> ButtonType.Cancel = Schliesse das fenster
		 */
		abbrechen.setOnAction(e -> this.setResult(ButtonType.CANCEL));
		// Dieser befehl ist ähnlich wie oben nur für das 'x' beim Fenster
		this.getDialogPane().getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, e -> {
			this.setResult(ButtonType.CANCEL);
		});

		// Eventhandler:
		neueErinnerung.setOnAction(e -> {
			Erinnerung_Anlegen_Dialog dialog = new Erinnerung_Anlegen_Dialog(benutzer);
			Optional<ButtonType> result = dialog.showAndWait();
			if(result.isPresent() && result.get().getButtonData() == ButtonData.OK_DONE) {
				readErinnerungen(benutzer);
			}
		});

		editErinnerung.setOnAction(e -> {
			new Erinnerung_Bearbeiten_Dialog(tvErinnerung.getSelectionModel().getSelectedItem()).showAndWait();
			readErinnerungen(benutzer);
		});

		deleteErinnerung.setOnAction(e -> {
			Alert alert = Util_Help.alertWindow(AlertType.CONFIRMATION, "Wirklich löschen?", "Möchten Sie die Erinnerung löschen?");
			Optional<ButtonType> result = alert.showAndWait();
			Erinnerungen erinnerung = tvErinnerung.getSelectionModel().getSelectedItem().getAppErinnerung();			
			if (result.isPresent() && result.get() == ButtonType.YES) {
				try {
					Service_Erinnerung.deleteErinnerung(erinnerung.getErinnerungID());
					Util_Help.alertWindow(AlertType.INFORMATION, "Info", "Erinnerung erfolgreich gelöscht").showAndWait();
					olErinnerung.remove(tvErinnerung.getSelectionModel().getSelectedItem());
				} catch (SQLException e1) {
					Util_Help.alertWindow(AlertType.ERROR, "Fehler", e1.getMessage());
				}
				readErinnerungen(benutzer);
			}
		});

		// Layout: AnchroPane für die Buttons
		AnchorPane anchor = new AnchorPane();
		anchor.getChildren().addAll(deleteErinnerung, abbrechen, neueErinnerung, editErinnerung);

		// Eigene Methode zum GUI Platzieren
		Util_Help.anchorpane(neueErinnerung, 5.0, null, 5.0, null);
		Util_Help.anchorpane(editErinnerung, 5.0, null, 130.0, null);
		Util_Help.anchorpane(deleteErinnerung, 5.0, null, 285.0, null);
		Util_Help.anchorpane(abbrechen, 5.0, null, null, 5.0);

		// Zusammenbau & Dialogeinstellungen
		HBox header = new HBox(headerBild);
		header.setPadding(new Insets(0, 0, 5, 0));
		VBox gesamt = new VBox(header, tvErinnerung, anchor);
		gesamt.setPadding(new Insets(5));
		gesamt.setSpacing(5);

		this.setTitle("Erinnerungen");
		this.getDialogPane().setContent(gesamt);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("kalender-dialog-layout");
		// Stage holen zum Icon setzen, da ich direkt im Dialog keins setzen kann
		Stage arg1 = (Stage) this.getDialogPane().getScene().getWindow();
		arg1.getIcons().add(new Image(BotanikHub_Client.class.getResource("/Window_Icon_Lebensbaum.jpg").toString()));
	}

	private void readErinnerungen(Benutzer benutzer) {
		try {
			// Readmethode für Erinnerungen
			ArrayList<Erinnerungen> alErinnerung = Service_Erinnerung.getErinnerung(benutzer.getBenutzerId());
			olErinnerung.clear();
			for(Erinnerungen er : alErinnerung) {
				olErinnerung.add(new ErinnerungenFX(er));
			}
		} catch(SQLException e) {
			Util_Help.alertWindow(AlertType.ERROR, "Fehler", e.getMessage());
		}
	}	
}
