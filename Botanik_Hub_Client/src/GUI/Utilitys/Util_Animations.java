package GUI.Utilitys;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class Util_Animations {

	/*--------------------------------------------
	 * Fade-In-Effekt (z. B. Einblenden von Labels)
	 *-------------------------------------------- */
	public static void fadeInAnimation(Node node, Duration d, Duration delay, double toValue) {
		FadeTransition fadeIn = new FadeTransition(d, node); 		// Animation erzeugen
		fadeIn.setCycleCount(1);									// nur einmal ausführen
		fadeIn.setFromValue(0);										// Startwert: unsichtbar
		fadeIn.setDelay(delay);										// Startverzögerung
		fadeIn.setToValue(toValue);									// Endwert (z. B. 1 = sichtbar)
		fadeIn.play();												// Starten
	}

	/*--------------------------------------------
	 * Zoom-In-Effekt (z. B. bei Hover-Effekt)
	 *-------------------------------------------- */
	public static void zoomInAnimation(Node node, double scale, Duration d) {
		ScaleTransition zoomIn = new ScaleTransition(d, node);		// Zoom-In erstellen
		zoomIn.setToX(scale);										// neue Breite
		zoomIn.setToY(scale);										// neue Höhe
		zoomIn.play();												// Animation starten
	}

	/*--------------------------------------------
	 * Zoom-Out-Effekt (z. B. beim Verlassen von Hover)
	 *-------------------------------------------- */
	public static void zoomOutAnimation(Node node, Duration d) {
		ScaleTransition zoomOut = new ScaleTransition(d, node);		// Zoom-Out erstellen
		zoomOut.setFromX(1);										// Ausgangsgröße X
		zoomOut.setFromY(1);										// Ausgangsgröße Y
		zoomOut.play();												// Animation starten
	}

	/*--------------------------------------------
	 * Pause-Effekt mit rotem Rahmen (z. B. für Fehler)
	 *-------------------------------------------- */
	public static void pauseAnimation(Node node, Duration d) {
		PauseTransition pause = new PauseTransition(d);				// Pause erzeugen
		node.setStyle("-fx-border-color: red;");					// roten Rahmen setzen
		pause.setOnFinished(e -> node.setStyle(null));				// Rahmen wieder entfernen
		pause.play();												// starten
	}
}
