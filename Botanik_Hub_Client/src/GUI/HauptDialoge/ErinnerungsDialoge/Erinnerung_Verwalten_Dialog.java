package GUI.HauptDialoge.ErinnerungsDialoge;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import GUI.BotanikHub_Client;
import GUI.Utilitys.Util;
import Modell.Benutzer;
import Modell.Erinnerungen;
import ModellFX.ErinnerungenFX;
import TEST_DB.DB_Erinnerungen;
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
import javafx.stage.WindowEvent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Erinnerung_Verwalten_Dialog extends Dialog<ButtonType> {

	private ObservableList<ErinnerungenFX> olErinnerung = FXCollections.observableArrayList();

	public Erinnerung_Verwalten_Dialog(Benutzer benutzer) {

		/*--------------------------------------------
		 * Initialisierung & GUI-Elemente
		 *-------------------------------------------- */
		Button abbrechen = new Button("Abbrechen");
		Button neueErinnerung = new Button("Neue Erinnerung");
		Button editErinnerung = new Button("Erinnerung bearbeiten");
		Button deleteErinnerung = new Button("Erinnerung löschen");

		ImageView headerBild = new ImageView(new Image(BotanikHub_Client.class.getResource("/erinnerungsdialog.jpg").toString()));
		headerBild.setSmooth(true);
		headerBild.setCache(true);
		headerBild.setFitHeight(120);
		headerBild.setFitWidth(750);

		/*--------------------------------------------
		 * Styling
		 *-------------------------------------------- */
		neueErinnerung.getStyleClass().add("kalender-dialog-button-ok");
		editErinnerung.getStyleClass().add("kalender-dialog-button-ok");
		deleteErinnerung.getStyleClass().add("kalender-dialog-button-ok");
		abbrechen.getStyleClass().add("kalender-dialog-button-cancel");

		/*--------------------------------------------
		 * TableView 
		 *-------------------------------------------- */
		TableColumn<ErinnerungenFX, String> typCol = new TableColumn<>("Erinnerungstyp");
		typCol.setCellValueFactory(new PropertyValueFactory<>("typ"));
		typCol.setPrefWidth(150);
		typCol.setStyle("-fx-alignment:center");

		TableColumn<ErinnerungenFX, LocalDate> dateCol = new TableColumn<>("Wochentag");
		dateCol.setCellValueFactory(new PropertyValueFactory<>("datum"));
		dateCol.setPrefWidth(170);
		dateCol.setStyle("-fx-alignment:center");

		dateCol.setCellFactory(column -> new javafx.scene.control.cell.TextFieldTableCell<ErinnerungenFX, LocalDate>() {
			@Override
			public void updateItem(LocalDate item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
				} else {
					String tag = item.getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.GERMAN);
					setText(tag); // z. B. "Montag"
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

		readErinnerungen();

		/*--------------------------------------------
		 * Eventhandler
		 *-------------------------------------------- */
		abbrechen.setOnAction(e -> this.setResult(ButtonType.CANCEL));
		this.getDialogPane().getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, e -> {
			this.setResult(ButtonType.CANCEL);
		});

		neueErinnerung.setOnAction(e -> {
			Erinnerung_Anlegen_Dialog dialog = new Erinnerung_Anlegen_Dialog(benutzer);
			Optional<ButtonType> result = dialog.showAndWait();
			readErinnerungen();
		});

		editErinnerung.setOnAction(e -> {
			Erinnerung_Bearbeiten_Dialog dialog = new Erinnerung_Bearbeiten_Dialog(tvErinnerung.getSelectionModel().getSelectedItem());
			Optional<ButtonType> result = dialog.showAndWait();
			readErinnerungen();
		});

		deleteErinnerung.setOnAction(e -> {
			Alert alert = Util.alertWindow(AlertType.CONFIRMATION, "Wirklich löschen?", "Möchten Sie die Erinnerung löschen?");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.YES) {
				try {
						DB_Erinnerungen.deleteErinnerungen(tvErinnerung.getSelectionModel().getSelectedItem().getAppErinnerung());
						Util.alertWindow(AlertType.INFORMATION, "Info", "Pflanze erfolgreich gelöscht").showAndWait();
						olErinnerung.remove(tvErinnerung.getSelectionModel().getSelectedItem());
						readErinnerungen();
					
				} catch (SQLException ex) {
					Util.alertWindow(AlertType.ERROR, "Löschen fehlgeschlagen", "Pflanze löschen fehlgeschlagen");
				}
			}
		});

		/*--------------------------------------------
		 * Positionierung via AnchorPane
		 *-------------------------------------------- */
		AnchorPane anchor = new AnchorPane();
		anchor.getChildren().addAll(deleteErinnerung, abbrechen, neueErinnerung, editErinnerung);

		Util.anchorpane(neueErinnerung, 5.0, null, 5.0, null);
		Util.anchorpane(editErinnerung, 5.0, null, 130.0, null);
		Util.anchorpane(deleteErinnerung, 5.0, null, 285.0, null);
		Util.anchorpane(abbrechen, 5.0, null, null, 5.0);

		/*--------------------------------------------
		 * Layout zusammensetzen
		 *-------------------------------------------- */
		HBox header = new HBox(headerBild);
		header.setPadding(new Insets(0, 0, 5, 0));
		VBox gesamt = new VBox(header, tvErinnerung, anchor);
		gesamt.setPadding(new Insets(5));

		this.setTitle("Erinnerungen");
		this.getDialogPane().setContent(gesamt);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("kalender-dialog-layout");
	}

	/*--------------------------------------------
	 * Erinnerungen aus DB lesen
	 *-------------------------------------------- */
	private void readErinnerungen() {
		try {
			int benutzer = BotanikHub_Client.getBenutzer().getBenutzerId();
			ArrayList<Erinnerungen> alErinnerung = DB_Erinnerungen.readErinnerungen(benutzer);
			olErinnerung.clear();
			for (Erinnerungen eineEr : alErinnerung) {
				olErinnerung.add(new ErinnerungenFX(eineEr));
			}
		} catch (SQLException e) {
			Util.alertWindow(AlertType.ERROR, "Fehler", e.toString());
		}
	}
}
