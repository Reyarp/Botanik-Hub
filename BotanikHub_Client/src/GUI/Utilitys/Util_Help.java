package GUI.Utilitys;

import java.util.function.UnaryOperator;

import Client.BotanikHub_Client;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class Util_Help {

	/*
	 * Klasse mit einigen Hilfsmethoden
	 */

	// Hintergrundsetzen für die StackPane
	public static void hintergrundSetzen(StackPane stack) {
		// Bild aus Resource laden
		Image backgroundImage = new Image(BotanikHub_Client.class.getResource("/haupt2.jpeg").toString());

		// ImageView anlegen und anpassen
		ImageView background = new ImageView(backgroundImage);
		background.setFitWidth(1400);
		background.setFitHeight(1000);
		background.setPreserveRatio(true);

		// Bild in die StackPane einfügen
		stack.getChildren().add(background);
	}


	// GUI-Elemente im Stack positionieren
	public static void guiPlatzieren(Node node, double translateX, double translateY, double width, double height, double opacity, boolean preservation) {
		// Position und Sichtbarkeit setzen
		node.setTranslateX(translateX);
		node.setTranslateY(translateY);
		node.setOpacity(opacity);

		// Falls ImageView → Breite, Höhe, Seitenverhältnis setzen
		if (node instanceof ImageView) {
			((ImageView) node).setFitWidth(width);
			((ImageView) node).setFitHeight(height);
			((ImageView) node).setPreserveRatio(preservation);
		}
	}

	// Alert-Window
	public static Alert alertWindow(AlertType type, String titel, String inhalt) {
		Alert alert;

		// Wenn CONFIRMATION -> mit YES/NO Buttons
		if (type == AlertType.CONFIRMATION) {
			alert = new Alert(type, inhalt, ButtonType.YES, ButtonType.NO);
		} else {
			alert = new Alert(type);
			alert.setContentText(inhalt);
		}

		// Titel setzen & ohne Header
		alert.setTitle(titel);
		alert.setHeaderText(null);

		// Buttons stylen
		if (type == AlertType.CONFIRMATION) {
			alert.setGraphic(null); // kein Icon
			Button yesBtn = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
			Button noBtn  = (Button) alert.getDialogPane().lookupButton(ButtonType.NO);
			yesBtn.getStyleClass().add("alert-button-ok");
			noBtn.getStyleClass().add("alert-button-cancel");
		} else {
			Button okBtn = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
			okBtn.getStyleClass().add("alert-button-ok");
		}

		// CSS-Datei einbinden
		DialogPane pane = alert.getDialogPane();
		pane.getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		pane.getStyleClass().add("dialog-layout");

		return alert;
	}


	// ToolTip mit CSS Styling
	public static Tooltip tip(Node node, String text, Duration delay, Duration duration) {
		Tooltip tool = new Tooltip();
		tool.setText(text);
		tool.setShowDelay(delay);			// Verzögerung vor Anzeige
		tool.setShowDuration(duration);		// Dauer der Anzeige

		// Tooltip-Styling direkt im Code
		tool.setStyle("-fx-background-color: #417154;" +
				"-fx-text-fill: white;" +
				"-fx-padding: 2px;" +
				"-fx-background-radius: 3;" +
				"-fx-border-color: transparent;");

		// Tooltip dem Node zuweisen
		Tooltip.install(node, tool);

		return tool;
	}

	// AnchorPane positionierung Methode
	public static void anchorpane(Node node, Double oben, Double unten, Double links, Double rechts) {
		if (oben != null)   AnchorPane.setTopAnchor(node, oben);
		if (unten != null)  AnchorPane.setBottomAnchor(node, unten);
		if (links != null)  AnchorPane.setLeftAnchor(node, links);
		if (rechts != null) AnchorPane.setRightAnchor(node, rechts);
	}

	// Eingabefilterung mit UnaryOperator
	public static UnaryOperator<TextFormatter.Change> gibNurZiffernFilter() {
		return change -> {
			// getControlNewText gibt den neuen Gesamttext im Feld zurück
			String neuerText = change.getControlNewText();

			if(neuerText.isEmpty()) {
				return change;
			}

			/* Nur erlauben, wenn Zahl oder Kommazahl mit max. 2 Nachkommastellen
			 * {0.2} steht für 2 maximal 2 Nachkommastellen
			 * ? steht für Optional -> man muss keine Nachkommastellen setzen */
			if (neuerText.matches("^\\d{0,3}(\\.\\d{0,2})?$")) {

				return change;
			}
			return null; // Eingabe blockieren
		};
	}
}
