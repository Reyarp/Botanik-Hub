package GUI.HauptDialoge.PflanzenDialoge;

import java.sql.SQLException;

import Client.BotanikHub_Client;
import GUI.Utilitys.Util_Help;
import Modell.Benutzer;
import Modell.BotanikHub;
import ModellFX.PflanzeFX;
import ServiceFunctions.Service_BotanikHub;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


public class Pflanzen_Notiz_Bearbeiten_Dialog extends Dialog<ButtonType> {

	public Pflanzen_Notiz_Bearbeiten_Dialog(PflanzeFX p, Benutzer benutzer) {

		/*
		 * Kleiner Dialog für Notizbearbeiten Button
		 */

		// Buttons & Co
		ButtonType cancel = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
		ButtonType save = new ButtonType("Speichern", ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(cancel, save);

		Button abbrechen = (Button) this.getDialogPane().lookupButton(cancel);
		Button speichern = (Button) this.getDialogPane().lookupButton(save);

		TextArea notiz = new TextArea(p.getAppPflanze().getNotiz());
		notiz.setPrefWidth(400);
		notiz.setPrefHeight(200);

		// CSS Styling
		abbrechen.getStyleClass().add("dialog-button-cancel");
		speichern.getStyleClass().add("dialog-button-ok");
		notiz.getStyleClass().add("text-area");

		// Eventhandler: speichern
		speichern.setOnAction(e -> {
			p.getAppPflanze().setNotiz(notiz.getText());
			try {
				BotanikHub hub = new BotanikHub(benutzer, p.getAppPflanze());
				Service_BotanikHub.putNotiz(hub);
			} catch (SQLException e1) {
				Util_Help.alertWindow(AlertType.ERROR, "Fehler: Notiz speichern", e1.getMessage());
			}
		});

		// Zusammenbau & Dialogeinstellungen
		HBox gesamt = new HBox(notiz, speichern, abbrechen);
		gesamt.setPadding(new Insets(5));

		this.setTitle("Notiz bearbeiten");
		this.getDialogPane().setContent(gesamt);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("dialog-layout");
		// Stage holen zum Icon setzen, da ich direkt im Dialog keins setzen kann
		Stage arg1 = (Stage) this.getDialogPane().getScene().getWindow();
		arg1.getIcons().add(new Image(BotanikHub_Client.class.getResource("/Window_Icon_Lebensbaum.jpg").toString()));
	}
}
