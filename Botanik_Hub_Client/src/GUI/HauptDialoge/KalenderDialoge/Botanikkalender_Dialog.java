package GUI.HauptDialoge.KalenderDialoge;

import java.sql.SQLException;
import java.util.ArrayList;

import Enum.BenutzerTyp;
import Enum.Month;
import GUI.BotanikHub_Client;
import GUI.Utilitys.Util;
import Modell.Benutzer;
import Modell.Botanikkalender;
import Modell.Pflanze;
import ModellFX.PflanzeFX;
import TEST_DB.DB_BotanikHub;
import TEST_DB.DB_BotanikKalender;
import TEST_DB.DB_Pflanze;
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

	// GridPanes für die Kalenderansicht
	private GridPane infoGrid = new GridPane();
	private GridPane aussaatGrid = new GridPane();
	private GridPane blueteGrid = new GridPane();
	private GridPane ernteGrid = new GridPane();

	// Monatseinträge & Pflanzenauswahl
	private ObservableList<PflanzeFX> ol = FXCollections.observableArrayList();
	private Month[] monate = Month.values();

	// Bildobjekt für spätere Verwendung
	private ImageView aussaatBild;

	public Botanikkalender_Dialog(PflanzeFX p) {

		/*
		 * -----------------------------------------------
		 * GUI-Elemente: Header, ComboBox, Icons
		 * -----------------------------------------------
		 */
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

		aussaatBild = new ImageView(new Image(BotanikHub_Client.class.getResource("/aussaat.png").toString()));
		aussaatBild.setFitWidth(84);
		aussaatBild.setFitHeight(150);

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

		/*
		 * -----------------------------------------------
		 * Kalender-Layout mit Monatslabels befüllen
		 * -----------------------------------------------
		 */
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

		aussaatGrid.add(iconAussaat, 0, 0);
		blueteGrid.add(iconBluete, 0, 0);
		ernteGrid.add(iconErnte, 0, 0);

		/*
		 * -----------------------------------------------
		 * Daten laden und ComboBox Listener setzen
		 * -----------------------------------------------
		 */
		readPflanzen();

		cbPflanze.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<>() {
			@Override
			public void changed(ObservableValue<? extends PflanzeFX> arg0, PflanzeFX alt, PflanzeFX neu) {
				if (neu != null) {
					try {
						ArrayList<Botanikkalender> kalender = DB_BotanikKalender.readKalender(neu.getPflanzenID());
						updateKalender1(kalender);
					} catch (SQLException e) {
						Util.alertWindow(AlertType.ERROR, "Fehler", e.toString());
					}
				}
			}
		});

		/*
		 * -----------------------------------------------
		 * Layout-Zusammenstellung
		 * -----------------------------------------------
		 */
		HBox header = new HBox(headerBild);
		HBox hboxCombo = new HBox(cbPflanze);
		hboxCombo.setPadding(new Insets(15, 0, 5, 0));

		VBox vb = new VBox(
				header,
				hboxCombo,
				new HBox(infoGrid),
				new HBox(aussaatGrid),
				new HBox(blueteGrid),
				new HBox(ernteGrid)
				);
		vb.setSpacing(5);

		this.setTitle("Botanik Kalender");
		this.getDialogPane().setContent(vb);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("kalender-dialog-layout");
	}

	/*
	 * Lädt die Pflanzen aus der Datenbank je nach Benutzerrolle (Admin oder Benutzer)
	 * und fügt sie der ObservableList für die ComboBox hinzu.
	 */
	private void readPflanzen() {
		try {
			ArrayList<Pflanze> alPflanze;
			Benutzer user = BotanikHub_Client.getBenutzer();
			alPflanze = (user.getTyp() == BenutzerTyp.ADMIN)
					? DB_Pflanze.readAllePflanzen()
							: DB_BotanikHub.readBotanikHubPflanzen();

			ol.clear();
			for (Pflanze p : alPflanze) {
				ol.add(new PflanzeFX(p));
			}
		} catch (SQLException e) {
			e.printStackTrace();
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

	/*
	 * Setzt alle drei Kalender-Grids (Aussaat, Blüte, Ernte) auf Anfangszustand zurück.
	 */
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
	 * Aktualisiert die komplette Kalenderansicht:
	 * - Setzt alles zurück
	 * - Markiert Monate entsprechend dem Kalendertyp der gewählten Pflanze
	 */
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
