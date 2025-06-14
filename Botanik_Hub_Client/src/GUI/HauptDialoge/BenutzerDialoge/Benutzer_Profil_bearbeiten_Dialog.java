package GUI.HauptDialoge.BenutzerDialoge;

import java.sql.SQLException;

import GUI.BotanikHub_Client;
import GUI.Utilitys.Util;
import GUI.Utilitys.Util_Animations;
import ModellFX.BenutzerFX;
import TEST_DB.DB_Benutzer;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class Benutzer_Profil_bearbeiten_Dialog extends Dialog<ButtonType> {

	public Benutzer_Profil_bearbeiten_Dialog(BenutzerFX b) {


		/*--------------------------------------------
		 * GUI-Elemente: Buttons, Felder, Layout
		 *-------------------------------------------- */
		ButtonType cancel = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
		ButtonType save = new ButtonType("Speichern", ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(cancel, save);

		Button abbrechen = (Button) this.getDialogPane().lookupButton(cancel);
		abbrechen.setPrefWidth(80);
		Button speichern = (Button) this.getDialogPane().lookupButton(save);

		TextField nameTxt = new TextField(b.getBenutzerName());
		nameTxt.setPromptText("Benutzername");

		PasswordField pw = new PasswordField();
		pw.setPromptText("Passwort eingeben");

		PasswordField pwRep = new PasswordField();
		pwRep.setPromptText("Passwort eingeben");

		abbrechen.getStyleClass().add("benutzer-dialog-button-cancel");
		speichern.getStyleClass().add("benutzer-dialog-button-ok");

		/*--------------------------------------------
		 * Layout für Profilbearbeitung (GridPane)
		 *-------------------------------------------- */
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);

		grid.add(new Label("Name:"), 0, 0);
		grid.add(nameTxt, 1, 0);

		grid.add(new Label("Passwort ändern"), 0, 1);

		grid.add(new Label("Passwort:"), 0, 2);
		grid.add(pw, 1, 2);

		grid.add(new Label("Passwort wiederholen:"), 0, 3);
		grid.add(pwRep, 1, 3);

		/*--------------------------------------------
		 * Eventfilter: Eingabeprüfung vor dem Speichern
		 *-------------------------------------------- */
		speichern.addEventFilter(ActionEvent.ACTION, e -> {
			try {
				if (DB_Benutzer.updateNameExistiert(nameTxt.getText(), b.getAppBenutzer().getBenutzerId())) {
					Util.alertWindow(AlertType.ERROR, "Fehler", "Benutzername existiert bereits").showAndWait();
					e.consume();
				}
			} catch (SQLException e1) {
				Util.alertWindow(AlertType.ERROR, "Fehler", e1.toString()).showAndWait();
			}

			if (nameTxt.getText() == null || nameTxt.getText().trim().isEmpty()) {
				Util_Animations.pauseAnimation(nameTxt, Duration.seconds(5));
				Util.alertWindow(AlertType.ERROR, "Fehler", "Bitte einen Namen eingeben").showAndWait();
				e.consume();
			}

			if (!pw.getText().equals(pwRep.getText())) {
				Util_Animations.pauseAnimation(pw, Duration.seconds(5));
				Util_Animations.pauseAnimation(pwRep, Duration.seconds(5));
				Util.alertWindow(AlertType.ERROR, "Fehler", "Passwörter stimmen nicht überein").showAndWait();
				pw.setText("");
				pwRep.setText("");
				e.consume();
			}
		});

		/*--------------------------------------------
		 * Eventhandler: Profil speichern (bei Erfolg)
		 *-------------------------------------------- */
		speichern.setOnAction(e -> {
			if (pw.getText().equals(pwRep.getText())
					&& pw.getText() != null
					&& !pw.getText().isEmpty()
					&& pw.getText().length() <= 50) {
				b.getAppBenutzer().setPasswort(pw.getText());
				Util.alertWindow(AlertType.INFORMATION, "Info", "Passwort erfolgreich geändert").showAndWait();
			}

			b.getAppBenutzer().setBenutzerName(nameTxt.getText());

			try {
				DB_Benutzer.updateBenutzer(b.getAppBenutzer());
			} catch (SQLException e1) {
				Util.alertWindow(AlertType.ERROR, "Fehler", e1.toString()).showAndWait();
			}
		});

		/*--------------------------------------------
		 * Dialogaufbau (VBox)
		 *-------------------------------------------- */
		VBox gesamt = new VBox(grid);
		gesamt.setPadding(new Insets(5));

		this.setTitle("Profil bearbeiten");
		this.getDialogPane().setContent(gesamt);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("benutzer-dialog-layout");
	}
}
