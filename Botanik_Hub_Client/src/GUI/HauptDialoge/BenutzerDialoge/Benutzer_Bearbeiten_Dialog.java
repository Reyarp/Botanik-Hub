package GUI.HauptDialoge.BenutzerDialoge;

import java.sql.SQLException;

import GUI.BotanikHub_Client;
import GUI.Utilitys.Util;
import ModellFX.BenutzerFX;
import TEST_DB.DB_Benutzer;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class Benutzer_Bearbeiten_Dialog extends Dialog<ButtonType> {

	public Benutzer_Bearbeiten_Dialog(BenutzerFX b) {	

		/* -----------------------------------------------
		 * GUI-Elemente: Buttons, Textfelder, Labels
		 * ----------------------------------------------- */
		ButtonType cancel = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
		ButtonType save = new ButtonType("Speichern", ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(cancel, save);

		Button abbrechen = (Button) this.getDialogPane().lookupButton(cancel);
		abbrechen.setPrefWidth(80);
		Button speichern = (Button) this.getDialogPane().lookupButton(save);

		TextField nameTxt = new TextField(b.getBenutzerName());
		Label idLbl = new Label(String.valueOf(b.getAppBenutzer().getBenutzerId()));

		// CSS
		abbrechen.getStyleClass().add("benutzer-dialog-button-cancel");
		speichern.getStyleClass().add("benutzer-dialog-button-ok");

		/* -----------------------------------------------
		 * Layout: Grid mit Benutzerfeldern
		 * ----------------------------------------------- */
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);

		grid.add(new Label("Benutzer ID:"), 0, 0);
		grid.add(idLbl, 1, 0);

		grid.add(new Label("Benutzername:"), 0, 1);
		grid.add(nameTxt, 1, 1);

		/* -----------------------------------------------
		 * Pflichtfeldprüfung & Validierung
		 * ----------------------------------------------- */
		speichern.addEventFilter(ActionEvent.ACTION, e -> {
			try {
				if (DB_Benutzer.updateNameExistiert(nameTxt.getText(), b.getAppBenutzer().getBenutzerId())) {
					Util.alertWindow(AlertType.ERROR, "Fehler", "Benutzername existiert bereits").showAndWait();
					e.consume();
				}
			} catch (SQLException e1) {
				Util.alertWindow(AlertType.ERROR, "Fehler", e1.toString()).showAndWait();
			}
		});

		/* -----------------------------------------------
		 * Speichern: Benutzername in DB übernehmen
		 * ----------------------------------------------- */
		speichern.setOnAction(e -> {
			try {
				b.getAppBenutzer().setBenutzerName(nameTxt.getText());
				DB_Benutzer.updateBenutzer(b.getAppBenutzer());
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});

		/* -----------------------------------------------
		 * Zusammenbau & Dialogeinstellungen
		 * ----------------------------------------------- */
		VBox gesamt = new VBox(grid);
		gesamt.setPadding(new Insets(5));

		this.setTitle("Benutzer bearbeiten");
		this.getDialogPane().setContent(gesamt);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("benutzer-dialog-layout");
	}
}
