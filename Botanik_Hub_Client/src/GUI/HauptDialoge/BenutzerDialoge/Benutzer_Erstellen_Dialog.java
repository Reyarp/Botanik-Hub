package GUI.HauptDialoge.BenutzerDialoge;

import java.sql.SQLException;
import java.time.LocalDate;

import Enum.BenutzerTyp;
import GUI.BotanikHub_Client;
import GUI.Utilitys.Util;
import Modell.Benutzer;
import ModellFX.BenutzerFX;
import TEST_DB.DB_Benutzer;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Benutzer_Erstellen_Dialog extends Dialog<ButtonType> {

	public Benutzer_Erstellen_Dialog(BenutzerFX b, StackPane stack, Stage stage) {


		/*--------------------------------------------
		 * GUI-Elemente: Buttons & Eingabefelder
		 *-------------------------------------------- */
		ButtonType save = new ButtonType("Speichern", ButtonData.OK_DONE);
		ButtonType cancel = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
		this.getDialogPane().getButtonTypes().addAll(save, cancel);

		Button speichern = (Button) this.getDialogPane().lookupButton(save);
		speichern.setDisable(true);
		Button abbrechen = (Button) this.getDialogPane().lookupButton(cancel);

		TextField nameTxt = new TextField();
		nameTxt.setPromptText("Benutzername eingeben");

		PasswordField pw = new PasswordField();
		pw.setPromptText("Passwort eingeben");

		PasswordField pwRep = new PasswordField();
		pwRep.setPromptText("Passwort eingeben");

		abbrechen.getStyleClass().add("dialog-button-cancel");
		speichern.getStyleClass().add("dialog-button-ok");

		/*--------------------------------------------
		 * ChangeListener: Speichern nur aktiv bei Eingabe
		 *-------------------------------------------- */
		ChangeListener<String> loginListner = (arg0, oldVal, newVal) -> {
			boolean name = nameTxt.getText().isEmpty();
			boolean pass = pw.getText().isEmpty();
			boolean pass2 = pwRep.getText().isEmpty();
			speichern.setDisable(name || pass || pass2);
		};

		nameTxt.textProperty().addListener(loginListner);
		pw.textProperty().addListener(loginListner);
		pwRep.textProperty().addListener(loginListner);

		/*--------------------------------------------
		 * Eventfilter: Passwortabgleich und Namensprüfung
		 *-------------------------------------------- */
		speichern.addEventFilter(ActionEvent.ACTION, e -> {
			if (!pw.getText().equals(pwRep.getText())) {
				Util.alertWindow(AlertType.ERROR, "Fehler", "Passwörter stimmen nicht überein").showAndWait();
				pw.setText("");
				pwRep.setText("");
				e.consume();
			}

			try {
				if (DB_Benutzer.insertNameExistiert(nameTxt.getText())) {
					Util.alertWindow(AlertType.ERROR, "Fehler", "Benutzer existiert bereits").showAndWait();
					pw.setText("");
					pwRep.setText("");
					e.consume();
				}
			} catch (SQLException e1) {
				Util.alertWindow(AlertType.ERROR, "Fehler", e1.toString()).showAndWait();
			}
		});

		/*--------------------------------------------
		 * ResultConverter: Benutzerobjekt anlegen und speichern
		 *-------------------------------------------- */
		this.setResultConverter(new Callback<ButtonType, ButtonType>() {
			@Override
			public ButtonType call(ButtonType button) {
				if (button == save && pw.getText().equals(pwRep.getText())) {
					Benutzer benutzer = new Benutzer(
							nameTxt.getText(),
							pw.getText(),
							0,  // ID wird von DB generiert
							BenutzerTyp.BENUTZER,
							LocalDate.now()
					);
					try {
						DB_Benutzer.insertBenutzer(benutzer);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				return button;
			}
		});

		/*--------------------------------------------
		 * Layout für Benutzeranlage (GridPane)
		 *-------------------------------------------- */
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);

		grid.add(new Label("Benutzername eingeben"), 0, 0);
		grid.add(nameTxt, 1, 0);

		grid.add(new Label("Passwort"), 0, 1);
		grid.add(pw, 1, 1);

		grid.add(new Label("Passwort wiederholen"), 0, 2);
		grid.add(pwRep, 1, 2);

		VBox vb = new VBox(grid);
		vb.setPadding(new Insets(5));

		/*--------------------------------------------
		 * Dialogeinstellungen
		 *-------------------------------------------- */
		this.setTitle("Neuen Benutzer erstellen");
		this.getDialogPane().setContent(vb);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("dialog-layout");
	}
}
