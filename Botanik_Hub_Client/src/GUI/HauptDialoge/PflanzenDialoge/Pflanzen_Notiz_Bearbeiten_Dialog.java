package GUI.HauptDialoge.PflanzenDialoge;

import java.sql.SQLException;

import GUI.BotanikHub_Client;
import ModellFX.PflanzeFX;
import TEST_DB.DB_BotanikHub;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.HBox;

public class Pflanzen_Notiz_Bearbeiten_Dialog extends Dialog<ButtonType> {

	public Pflanzen_Notiz_Bearbeiten_Dialog(PflanzeFX p) {

		/*--------------------------------------------
		 * GUI-Elemente: Buttons und Texteingabe
		 *-------------------------------------------- */
		ButtonType cancel = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
		ButtonType save = new ButtonType("Speichern", ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(cancel, save);

		Button abbrechen = (Button) this.getDialogPane().lookupButton(cancel);
		Button speichern = (Button) this.getDialogPane().lookupButton(save);

		TextArea notiz = new TextArea(p.getAppPflanze().getNotiz());
		notiz.setPrefWidth(400);
		notiz.setPrefHeight(200);

		abbrechen.getStyleClass().add("dialog-button-cancel");
		speichern.getStyleClass().add("dialog-button-ok");
		notiz.getStyleClass().add("text-area");

		/*--------------------------------------------
		 * Eventhandler: Speichert die Notiz in der DB
		 *-------------------------------------------- */
		speichern.setOnAction(e -> {
			p.getAppPflanze().setNotiz(notiz.getText());
			try {
				DB_BotanikHub.updateNotiz(p.getAppPflanze());
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});

		/*--------------------------------------------
		 * Layout: HBox mit Textfeld und Buttons
		 *-------------------------------------------- */
		HBox gesamt = new HBox(notiz, speichern, abbrechen);
		gesamt.setPadding(new Insets(5));

		/*--------------------------------------------
		 * Dialogeinstellungen
		 *-------------------------------------------- */
		this.setTitle("Notiz bearbeiten");
		this.getDialogPane().setContent(gesamt);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("dialog-layout");
	}
}
