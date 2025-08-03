package GUI.HauptDialoge.ErinnerungsDialoge;

import java.sql.SQLException;
import java.util.Optional;

import Client.BotanikHub_Client;
import GUI.HauptDialoge.PflanzenDialoge.SubDialoge.ErinnerungsTyp_Dialog;
import GUI.HauptDialoge.PflanzenDialoge.SubDialoge.Intervall_Dialog;
import GUI.Utilitys.Util_Animations;
import GUI.Utilitys.Util_Help;
import ModellFX.ErinnerungenFX;
import ServiceFunctions.Service_Erinnerung;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Erinnerung_Bearbeiten_Dialog extends Dialog<ButtonType> {

	public Erinnerung_Bearbeiten_Dialog(ErinnerungenFX erinnerung) {

		/*
		 * Dieser Dialog ist für vorhandene Erinnerungen die ein Benutzer gesetzt hat
		 * Hier kann er die erinnerung bearbeiten
		 */

		// Buttons & Co
		ButtonType cancel = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
		ButtonType save = new ButtonType("Speichern", ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(cancel, save);

		Button abbrechen = (Button) this.getDialogPane().lookupButton(cancel);
		Button speichern = (Button) this.getDialogPane().lookupButton(save);

		Button erTyp = new Button("Erinnerungstyp");
		TextArea erTypLbl = new TextArea(
				(erinnerung.getAppErinnerung().getTyp() != null)
				? erinnerung.getAppErinnerung().getTyp().getBeschreibung()
						: "Noch keinen Typ ausgewählt"
				);
		erTypLbl.setEditable(false);
		erTypLbl.setPrefSize(400, 50);

		Button intervall = new Button("Intervall");
		TextArea intervallLbl = new TextArea(
				(erinnerung.getAppErinnerung().getIntervall() != null)
				? erinnerung.getAppErinnerung().getIntervall().getBeschreibung()
						: "Noch kein Intervall ausgewählt"
				);
		intervallLbl.setEditable(false);
		intervallLbl.setPrefSize(400, 50);

		DatePicker date = new DatePicker(erinnerung.getAppErinnerung().getDatum());

		// CSS Styling
		erTyp.getStyleClass().add("dialog-button-ok");
		intervall.getStyleClass().add("dialog-button-ok");
		erTypLbl.getStyleClass().add("dialog-label");
		intervallLbl.getStyleClass().add("dialog-label");
		date.getStyleClass().add("date-picker");
		abbrechen.getStyleClass().add("dialog-button-cancel");
		speichern.getStyleClass().add("dialog-button-ok");

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

		// Eventhandler: erTyp, intervall, speichern
		erTyp.setOnAction(e -> {
			ErinnerungsTyp_Dialog dialog = new ErinnerungsTyp_Dialog(erinnerung);
			Optional<ButtonType> result = dialog.showAndWait();
			if (result.isPresent() && result.get().getButtonData() == ButtonData.OK_DONE) {
				if (erinnerung.getAppErinnerung().getTyp() != null) {
					erTypLbl.setText(erinnerung.getAppErinnerung().getTyp().toString());
				}
			}
		});

		intervall.setOnAction(e -> {
			Intervall_Dialog dialog = new Intervall_Dialog(erinnerung);
			Optional<ButtonType> o = dialog.showAndWait();
			if (o.isPresent() && o.get().getButtonData() == ButtonData.OK_DONE) {
				if (erinnerung.getAppErinnerung().getIntervall() != null) {
					intervallLbl.setText(erinnerung.getAppErinnerung().getIntervall().toString());
				}
			}
		});

		speichern.setOnAction(e -> {
			erinnerung.setDatum(date.getValue());
			erinnerung.setIntervall(erinnerung.getAppErinnerung().getIntervall());
			erinnerung.setTyp(erinnerung.getAppErinnerung().getTyp());

			try {
				Service_Erinnerung.putErinnerung(erinnerung.getAppErinnerung());
			} catch (SQLException e1) {
				Util_Help.alertWindow(AlertType.ERROR, "Fehler", e1.getMessage());
			}
		});

		// Eventfiler: speichern
		speichern.addEventFilter(ActionEvent.ACTION, e -> {
			if (date.getValue() == null) {
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
		VBox vb = new VBox(grid, speichern, abbrechen);
		vb.setPadding(new Insets(30, 10, 10, 10));

		this.setTitle("Erinnerung bearbeiten");
		this.getDialogPane().setContent(grid);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("dialog-layout");
		// Stage holen zum Icon setzen, da ich direkt im Dialog keins setzen kann
		Stage arg1 = (Stage) this.getDialogPane().getScene().getWindow();
		arg1.getIcons().add(new Image(BotanikHub_Client.class.getResource("/Window_Icon_Lebensbaum.jpg").toString()));
	}
}
