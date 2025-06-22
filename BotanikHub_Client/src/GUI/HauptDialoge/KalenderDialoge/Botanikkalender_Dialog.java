package GUI.HauptDialoge.KalenderDialoge;

import java.sql.SQLException;
import java.util.ArrayList;

import Client.BotanikHub_Client;
import Enum.BenutzerTyp;
import Enum.Month;
import GUI.Utilitys.Util_Help;
import Modell.Benutzer;
import Modell.Botanikkalender;
import Modell.Pflanze;
import ModellFX.PflanzeFX;
import ServiceFunctions.Service_Botaikkalender;
import ServiceFunctions.Service_BotanikHub;
import ServiceFunctions.Service_Pflanze;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Botanikkalender_Dialog extends Dialog<ButtonType> {

	/*
	 * Dieser Dialog ist für Admin und Benutzer
	 * Hier kann man eine Pflanze auswählen die einem die Aussaat,-Blüte und Erntemonate anzeigt
	 */
	
	// GridPanes für die Kalenderansicht
	private GridPane infoGrid = new GridPane();
	private GridPane aussaatGrid = new GridPane();
	private GridPane blueteGrid = new GridPane();
	private GridPane ernteGrid = new GridPane();

	// Monatseinträge & Pflanzenauswahl
	private ObservableList<PflanzeFX> ol = FXCollections.observableArrayList();
	private Month[] monate = Month.values();

	public Botanikkalender_Dialog(PflanzeFX p) {

		// Buttons & Co
		ButtonType cancel = new ButtonType("Schließen", ButtonData.CANCEL_CLOSE);
		this.getDialogPane().getButtonTypes().add(cancel);
		Button abbrechen = (Button) this.getDialogPane().lookupButton(cancel);

		ComboBox<PflanzeFX> cbPflanze = new ComboBox<>(ol);
		cbPflanze.setPrefSize(111, 20);
		cbPflanze.setPromptText("Bitte Pflanze wählen");
		

		ImageView headerBild = new ImageView(new Image(BotanikHub_Client.class.getResource("/Botanik.jpg").toString()));
		headerBild.setFitWidth(1095);
		headerBild.setFitHeight(110);
		headerBild.setCache(true);
		headerBild.setSmooth(true);

		ImageView aussaatImage = new ImageView(new Image(BotanikHub_Client.class.getResource("/kalendersamen.png").toString()));
		aussaatImage.setFitWidth(60);
		aussaatImage.setFitHeight(60);

		ImageView blueteImage = new ImageView(new Image(BotanikHub_Client.class.getResource("/kalenderbluete.png").toString()));
		blueteImage.setFitWidth(60);
		blueteImage.setFitHeight(60);

		ImageView ernteImage = new ImageView(new Image(BotanikHub_Client.class.getResource("/kalenderernte.png").toString()));
		ernteImage.setFitWidth(60);
		ernteImage.setFitHeight(60);

		Label iconAussaat = new Label("", aussaatImage);
		iconAussaat.setPrefSize(84, 150);
		iconAussaat.setAlignment(Pos.CENTER);

		Label iconBluete = new Label("", blueteImage);
		iconBluete.setPrefSize(84, 150);
		iconBluete.setAlignment(Pos.CENTER);

		Label iconErnte = new Label("", ernteImage);
		iconErnte.setPrefSize(84, 150);
		iconErnte.setAlignment(Pos.CENTER);

		abbrechen.getStyleClass().add("kalender-dialog-button-cancel");
		cbPflanze.getStyleClass().add("combo-box");

		
		 // Kalender-Layout mit Monatslabels befüllen
		for (int i = 0; i < monate.length; i++) {
			Label leerLbl = new Label();
			leerLbl.setPrefSize(84, 20);
			infoGrid.add(leerLbl, 0, 0);

			Label infoLbl = new Label(monate[i].getBeschreibung());
			infoLbl.setPrefSize(84, 20);
			infoLbl.getStyleClass().add("label-kalender-monate");
			infoGrid.add(infoLbl, i + 1, 0);

			Label ausLbl = new Label();
			ausLbl.setPrefSize(84, 150);
			ausLbl.getStyleClass().add("label-kalender");
			aussaatGrid.add(ausLbl, i + 1, 0);

			Label blueLbl = new Label();
			blueLbl.setPrefSize(84, 150);
			blueLbl.getStyleClass().add("label-kalender");
			blueteGrid.add(blueLbl, i + 1, 0);

			Label ernLbl = new Label();
			ernLbl.setPrefSize(84, 150);
			ernLbl.getStyleClass().add("label-kalender");
			ernteGrid.add(ernLbl, i + 1, 0);
		}

		// In die GridPane einfügen
		aussaatGrid.add(iconAussaat, 0, 0);
		blueteGrid.add(iconBluete, 0, 0);
		ernteGrid.add(iconErnte, 0, 0);

		// readMethode -> unten
		readPflanzen();
		cbPflanze.setDisable(ol.isEmpty());
		// Changelistener: cbPflanze -> hole die Pflanzeninformation aus arg2 über readKalender methode
		cbPflanze.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<>() {
			@Override
			public void changed(ObservableValue<? extends PflanzeFX> arg0, PflanzeFX arg1, PflanzeFX arg2) {
				if (arg2 != null) {
					readKalender(arg2.getAppPflanze().getPflanzenID());
					cbPflanze.setDisable(false);
				} else {
					cbPflanze.setDisable(true);
				}
			}
		});

		// Zusammenbau & Dialogeinstellungen
		HBox header = new HBox(headerBild);
		HBox hboxCombo = new HBox(cbPflanze);
		hboxCombo.setPadding(new Insets(15, 0, 5, 0));

		VBox vb = new VBox(header,hboxCombo, new HBox(infoGrid), new HBox(aussaatGrid), new HBox(blueteGrid), new HBox(ernteGrid));
		vb.setSpacing(5);

		this.setTitle("Botanik Kalender");
		this.getDialogPane().setContent(vb);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("kalender-dialog-layout");
	}

	private void readKalender(int id) {
		try {
			ArrayList<Botanikkalender> alB = Service_Botaikkalender.getBotanikkalender(id);
			updateKalender1(alB);
		} catch (SQLException e) {
			Util_Help.alertWindow(AlertType.ERROR, "Fehler", e.getMessage());
		}
	}
	
	private void readPflanzen() {
		try {
			Benutzer benutzer = BotanikHub_Client.getBenutzer();
			ArrayList<Pflanze> alPflanze = new ArrayList<>();
			if(benutzer.getTyp() == BenutzerTyp.ADMIN) {
				alPflanze = Service_Pflanze.getPflanze();
			} else if(benutzer.getTyp() == BenutzerTyp.BENUTZER) {
				alPflanze = Service_BotanikHub.getBHPflanzen(benutzer.getBenutzerId());
			}
			ol.clear();
			for(Pflanze einePflanze : alPflanze) {
				ol.add(new PflanzeFX(einePflanze));
			}
		} catch(SQLException e) {
			Util_Help.alertWindow(AlertType.ERROR, "Fehler", e.getMessage());
		}
	}
	
	/*
	 * Setzt alle markierten Monatszellen eines bestimmten Kalenderrasters
	 * (z. B. Aussaat, Blüte, Ernte) wieder zurück auf weißen Hintergrund.
	 */
	private void resetGrid(GridPane grid) {
		for (Node node : grid.getChildren()) {
			Integer spalte = GridPane.getColumnIndex(node);
			if (node instanceof Label && spalte != null && spalte > 0) {
				((Label) node).setStyle("-fx-background-color: white;");
			}
		}
	}

	// Alle GridPanes auf Anfangszustand setzen
	private void resetKalenderLabels() {
		resetGrid(aussaatGrid);
		resetGrid(blueteGrid);
		resetGrid(ernteGrid);
	}

	/*
	 * Markiert einen bestimmten Monat im übergebenen Kalender-Grid farbig,
	 * abhängig vom Kalendertyp (Grün, Rosa oder Gelb).
	 */
	private void labelMarkieren(GridPane grid, int monat) {
		for (Node node : grid.getChildren()) {
			Integer spalte = GridPane.getColumnIndex(node);
			if (node instanceof Label && spalte != null && spalte == monat + 1) {
				Label lbl = (Label) node;
				if (grid.equals(aussaatGrid)) {
					lbl.setStyle("-fx-background-color: lightgreen;");
				} else if (grid.equals(blueteGrid)) {
					lbl.setStyle("-fx-background-color: lightpink;");
				} else if (grid.equals(ernteGrid)) {
					lbl.setStyle("-fx-background-color: lightyellow;");
				}
				break;
			}
		}
	}

	/*
	 * Aktualisiert die gesamte Kalenderansicht:
	 * Setzt alle Monatsmarkierungen zurück
	 * Iteriert über alle Kalendereinträge der gewählten Pflanze
	 * Markiert die jeweiligen Monate im entsprechenden Grid (Aussaat, Blüte, Ernte) anhand des Kalendertyps und der Monatsposition (Month.ordinal())
	 */
	@SuppressWarnings("incomplete-switch")
	private void updateKalender1(ArrayList<Botanikkalender> alKal) {
		resetKalenderLabels();
		for (Botanikkalender b : alKal) {
			for (Month m : b.getMonat()) {
				int index = m.ordinal();
				switch (b.getKalendertyp()) {
				case AUSSAAT -> labelMarkieren(aussaatGrid, index);
				case BLUETE  -> labelMarkieren(blueteGrid, index);
				case ERNTE   -> labelMarkieren(ernteGrid, index);
				}
			}
		}
	}
}
