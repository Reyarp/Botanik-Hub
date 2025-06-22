package GUI.HauptDialoge.ErinnerungsDialoge;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import Client.BotanikHub_Client;
import GUI.HauptDialoge.PflanzenDialoge.SubDialoge.ErinnerungsTyp_Dialog;
import GUI.HauptDialoge.PflanzenDialoge.SubDialoge.Pflanze_Intervall_Dialog;
import GUI.Utilitys.Util_Animations;
import GUI.Utilitys.Util_Help;
import Modell.Benutzer;
import Modell.Erinnerungen;
import Modell.Pflanze;
import ModellFX.ErinnerungenFX;
import ModellFX.PflanzeFX;
import ServiceFunctions.Service_BotanikHub;
import ServiceFunctions.Service_Erinnerung;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class Erinnerung_Anlegen_Dialog extends Dialog<ButtonType> {

	// Container für Eingabedaten der Erinnerung (FX + Modell)
	private ErinnerungenFX erinnerung = new ErinnerungenFX(new Erinnerungen());
	private ObservableList<PflanzeFX> olPflanze = FXCollections.observableArrayList();

	@SuppressWarnings("unchecked")
	public Erinnerung_Anlegen_Dialog(Benutzer benutzer) {

		// Buttons & Co
		ButtonType cancel = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
		ButtonType save = new ButtonType("Speichern", ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(cancel, save);
		Button abbrechen = (Button) this.getDialogPane().lookupButton(cancel);
		Button speichern = (Button) this.getDialogPane().lookupButton(save);
		speichern.setDisable(true);

		Button erTyp = new Button("Erinnerungstyp");
		TextArea erTypLbl = new TextArea("Noch keinen Typ ausgewählt");
		erTypLbl.setEditable(false);
		erTypLbl.setPrefSize(400, 50);

		Button intervall = new Button("Intervall");
		TextArea intervallLbl = new TextArea("Noch kein Intervall ausgewählt");
		intervallLbl.setEditable(false);
		intervallLbl.setPrefSize(400, 50);

		DatePicker date = new DatePicker(LocalDate.now());

		// CSS Styling
		erTyp.getStyleClass().add("dialog-button-ok");
		intervall.getStyleClass().add("dialog-button-ok");
		erTypLbl.getStyleClass().add("dialog-label");
		intervallLbl.getStyleClass().add("dialog-label");
		date.getStyleClass().add("date-picker");
		abbrechen.getStyleClass().add("kalender-dialog-button-cancel");
		speichern.getStyleClass().add("kalender-dialog-button-ok");

		// Layout: GridPane
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(15);

		grid.add(new Label("Erinnerungstyp:"), 0, 0);
		grid.add(erTyp, 1, 0);
		grid.add(erTypLbl, 1, 1, 4, 2);

		grid.add(new Label("Tag wählen:"), 0, 3);
		grid.add(date, 1, 3);

		grid.add(new Label("Intervall:"), 0, 4);
		grid.add(intervall, 1, 4);
		grid.add(intervallLbl, 1, 5, 4, 2);

		// TableView: Pflanzenobjekte
		TableColumn<PflanzeFX, Integer> idCol = new TableColumn<>("ID");
		idCol.setCellValueFactory(new PropertyValueFactory<>("pflanzenID"));
		idCol.setPrefWidth(100);
		idCol.setStyle("-fx-alignment:center");

		TableColumn<PflanzeFX, String> nameCol = new TableColumn<>("Pflanzenname");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("pflanzenName"));
		nameCol.setPrefWidth(200);
		nameCol.setStyle("-fx-alignment:center");

		TableColumn<PflanzeFX, String> typCol = new TableColumn<>("Pflanzentyp");
		typCol.setCellValueFactory(new PropertyValueFactory<>("pflanzenTyp"));
		typCol.setPrefWidth(200);
		typCol.setStyle("-fx-alignment:center");

		TableView<PflanzeFX> tvPflanze = new TableView<>(olPflanze);
		tvPflanze.getColumns().addAll(idCol, nameCol, typCol);
		tvPflanze.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		// Layout verpacken für TitledPane
		HBox pflanzeBox = new HBox(tvPflanze);
		HBox erinnerungBox = new HBox(grid);
		pflanzeBox.getStyleClass().add("dialog-layout");
		erinnerungBox.getStyleClass().add("dialog-layout");

		TitledPane tpPflanze = new TitledPane("Pflanzenauswahl", pflanzeBox);
		TitledPane tpErinnerung = new TitledPane("Erinnerungsdetails", erinnerungBox);
		tpPflanze.setExpanded(true);
		tpErinnerung.setDisable(true);
		tpErinnerung.setExpanded(true);

		// readMethode -> unten
		readPflanzen();

		// Changelistener: tvPflanze
		tvPflanze.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<>() {
			@Override
			public void changed(ObservableValue<? extends PflanzeFX> obs, PflanzeFX alt, PflanzeFX neu) {
				if (neu != null) {
					tpErinnerung.setDisable(false);
					speichern.setDisable(false);
					erinnerung.getAppErinnerung().setPflanze(neu.getAppPflanze());
				} else {
					tpErinnerung.setDisable(true);
					speichern.setDisable(true);
				}
			}
		});

		// Eventhandler: erinnerungsTyp, intervall, abbrechen & speichern
		erTyp.setOnAction(e -> {
			ErinnerungsTyp_Dialog dialog = new ErinnerungsTyp_Dialog(erinnerung);
			Optional<ButtonType> result = dialog.showAndWait();
			result.ifPresent(r -> {
				if (r.getButtonData() == ButtonData.OK_DONE && erinnerung.getAppErinnerung().getTyp() != null) {
					erTypLbl.setText(erinnerung.getAppErinnerung().getTyp().toString());
				}
			});
		});

		intervall.setOnAction(e -> {
			Pflanze_Intervall_Dialog dialog = new Pflanze_Intervall_Dialog(erinnerung);
			Optional<ButtonType> result = dialog.showAndWait();
			result.ifPresent(r -> {
				if (r.getButtonData() == ButtonData.OK_DONE && erinnerung.getAppErinnerung().getIntervall() != null) {
					intervallLbl.setText(erinnerung.getAppErinnerung().getIntervall().toString());
				}
			});
		});

		abbrechen.setOnAction(e -> this.setResult(ButtonType.CANCEL));
		this.getDialogPane().getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, e -> {
			this.setResult(ButtonType.CANCEL);
		});

		speichern.setOnAction(e -> {
			if (date != null) {
				erinnerung.setPflanze(tvPflanze.getSelectionModel().getSelectedItem().getAppPflanze());
				erinnerung.setBenutzer(benutzer);
				erinnerung.setDatum(date.getValue());
				erinnerung.setIntervall(erinnerung.getAppErinnerung().getIntervall());
				erinnerung.setTyp(erinnerung.getAppErinnerung().getTyp());

				try {
					Service_Erinnerung.postErinnerung(erinnerung.getAppErinnerung());
				} catch (SQLException e1) {
					Util_Help.alertWindow(AlertType.ERROR, "Fehler", e1.getMessage());
				}
			}
		});

		// Eventfilter: validierung vor speichern
		speichern.addEventFilter(ActionEvent.ACTION, e -> {
			if (date == null || date.getValue() == null) {
				Util_Animations.pauseAnimation(date, Duration.seconds(4));
				e.consume();
			}
			if (erinnerung.getAppErinnerung().getIntervall() == null) {
				Util_Animations.pauseAnimation(intervallLbl, Duration.seconds(4));
				e.consume();
			}
			if (erinnerung.getAppErinnerung().getTyp() == null) {
				Util_Animations.pauseAnimation(erTypLbl, Duration.seconds(4));
				e.consume();
			}
		});

		// Zusammenbau & Dialogeinstellungen
		VBox vbTV = new VBox(tpPflanze);
		vbTV.setPrefSize(350, 250);
		VBox vb = new VBox(vbTV, tpErinnerung, speichern, abbrechen);
		vb.setPadding(new Insets(20, 15, 15, 15));
		vb.setSpacing(10);

		this.setTitle("Neue Erinnerung anlegen");
		this.getDialogPane().setContent(vb);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("kalender-dialog-layout");
	}

	private void readPflanzen() {
		try {
			Benutzer benutzer = BotanikHub_Client.getBenutzer();
			ArrayList<Pflanze> alPflanze = Service_BotanikHub.getBHPflanzen(benutzer.getBenutzerId());
			olPflanze.clear();
			for(Pflanze einePflanze : alPflanze) {
				olPflanze.add(new PflanzeFX(einePflanze));
			}
		} catch(SQLException e) {
			Util_Help.alertWindow(AlertType.ERROR, "Fehler", e.getMessage());
		}
	}
}
