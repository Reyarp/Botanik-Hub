package GUI.HauptDialoge.PflanzenDialoge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;
import java.util.stream.Collectors;

import Enum.Kalendertyp;
import Enum.Month;
import Enum.Pflanzentyp;
import Enum.Vermehrungsarten;
import Enum.VerwendeteTeile;
import GUI.BotanikHub_Client;
import Modell.Botanikkalender;
import ModellFX.PflanzeFX;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class Pflanze_Ansehen_Dialog extends Dialog<ButtonType> {

	// Listen für Anzeige
	private ArrayList<Pflanzentyp> typ;
	private ArrayList<VerwendeteTeile> teile;
	private ArrayList<Vermehrungsarten> vermehrung;
	private TreeSet<Month> aussaat = new TreeSet<>();
	private TreeSet<Month> bluete = new TreeSet<>();
	private TreeSet<Month> ernte = new TreeSet<>();
	private Collection<Month> schnitt = new ArrayList<>();

	public Pflanze_Ansehen_Dialog(PflanzeFX p) {

		// Typen & Kalender vorbereiten
		typ = p.getAppPflanze().getPflanzenTyp();
		teile = p.getAppPflanze().getVerwendeteTeile();
		vermehrung = p.getAppPflanze().getVermehrung();
		schnitt = p.getAppPflanze().getKalender().stream()
				.filter(k -> k.getKalendertyp() == Kalendertyp.RUECKSCHNITT)
				.flatMap(k -> k.getMonat().stream())
				.distinct()
				.collect(Collectors.toCollection(TreeSet::new));

		for (Botanikkalender k : p.getAppPflanze().getKalender()) {
			switch (k.getKalendertyp()) {
			case AUSSAAT -> aussaat.addAll(k.getMonat());
			case BLUETE -> bluete.addAll(k.getMonat());
			case ERNTE -> ernte.addAll(k.getMonat());
			case RUECKSCHNITT -> schnitt.addAll(k.getMonat());
			}
		}

		// Dialog schließen-Button
		ButtonType cancel = new ButtonType("Schließen", ButtonData.CANCEL_CLOSE);
		this.getDialogPane().getButtonTypes().add(cancel);
		Button abbrechen = (Button) this.getDialogPane().lookupButton(cancel);
		abbrechen.getStyleClass().add("dialog-button-cancel");

		/*--------------------------------------------
		 * Pflanzenbild setzen
		 *-------------------------------------------- */
		String pfad = p.getAppPflanze().getBildPfad();
		ImageView pflanzeBild = new ImageView();

		if (pfad != null && !pfad.isEmpty()) {
			try {
				Image bild = new Image("file:" + pfad, true);
				pflanzeBild.setImage(bild);
			} catch (Exception e) {
				pflanzeBild.setImage(new Image(BotanikHub_Client.class.getResource("/keinBild.jpg").toString()));
			}
		} else {
			pflanzeBild.setImage(new Image(BotanikHub_Client.class.getResource("/keinBild.jpg").toString()));
		}

		pflanzeBild.setFitWidth(430);
		pflanzeBild.setFitHeight(350);
		pflanzeBild.setCache(true);
		pflanzeBild.setSmooth(true);

		/*--------------------------------------------
		 * Tabs vorbereiten
		 *-------------------------------------------- */
		TabPane tabs = new TabPane();
		tabs.getTabs().addAll(allgemeineinfo(p), eigenschafteninfo(p), pflegeinfo(p), kalender(p), notiz(p));

		// Layout
		HBox header = new HBox(pflanzeBild);
		header.setPrefSize(300, 350);
		header.setPadding(new Insets(5, 0, 5, 0));
		VBox gesamt = new VBox(header, tabs);

		// Dialog konfigurieren
		this.setTitle("Pflanze ansehen");
		this.getDialogPane().setContent(gesamt);
		this.getDialogPane().getStylesheets().add(BotanikHub_Client.class.getResource("/style.css").toString());
		this.getDialogPane().getStyleClass().add("dialog-layout");
	}

	public Tab allgemeineinfo(PflanzeFX p) {
		GridPane grid = new GridPane();
		grid.setHgap(35);
		grid.setVgap(10);

		Label nameLbl = new Label(p.getAppPflanze().getPflanzenName());
		Label botanLbl = new Label(p.getAppPflanze().getBotanikName());
		TextArea pflanzTypLabel = new TextArea(typ.stream().map(Pflanzentyp::getBeschreibung).collect(Collectors.joining(", ")));
		Label breiLbl = new Label(String.valueOf(p.getAppPflanze().getWuchsbreite()));
		Label hoehLbl = new Label(String.valueOf(p.getAppPflanze().getWuchshoehe()));

		// Format
		nameLbl.setPrefSize(260, 25);
		botanLbl.setPrefSize(260, 25);
		pflanzTypLabel.setPrefSize(260, 50);
		pflanzTypLabel.setEditable(false);
		breiLbl.setPrefSize(260, 25);
		hoehLbl.setPrefSize(260, 25);

		// Styling
		nameLbl.getStyleClass().add("dialog-label-info");
		botanLbl.getStyleClass().add("dialog-label-info");
		pflanzTypLabel.getStyleClass().add("dialog-label-info");
		breiLbl.getStyleClass().add("dialog-label-info");
		hoehLbl.getStyleClass().add("dialog-label-info");

		grid.add(new Label("Pflanzenname:"), 0, 0);
		grid.add(nameLbl, 1, 0);
		grid.add(new Label("Botanischer Name:"), 0, 1);
		grid.add(botanLbl, 1, 1);
		grid.add(new Label("Pflanzentyp:"), 0, 2);
		grid.add(pflanzTypLabel, 1, 2);
		grid.add(new Label("Wuchsbreite:"), 0, 3);
		grid.add(breiLbl, 1, 3);
		grid.add(new Label("Wuchshöhe:"), 0, 4);
		grid.add(hoehLbl, 1, 4);

		VBox vb = new VBox(grid);
		vb.setPadding(new Insets(15, 0, 0, 0));

		Tab tabAlg = new Tab("Allgemein");
		tabAlg.setClosable(false);
		tabAlg.setContent(vb);
		return tabAlg;
	}

	public Tab eigenschafteninfo(PflanzeFX p) {
		GridPane grid = new GridPane();
		grid.setHgap(35);
		grid.setVgap(10);

		Label lebenLbl = new Label(String.valueOf(p.getAppPflanze().getLebensdauer()));
		Label winterLbl = new Label(String.valueOf(p.getAppPflanze().getVertraeglichkeit()));
		String giftTxt = p.getAppPflanze().isGiftig() ? "Giftig" : "Nicht Giftig";
		Label giftLbl = new Label(giftTxt);
		TextArea teileLbl = new TextArea(teile.stream().map(VerwendeteTeile::getBeschreibung).collect(Collectors.joining(", ")));
		TextArea vermLbl = new TextArea(vermehrung.stream().map(Vermehrungsarten::getBeschreibung).collect(Collectors.joining(", ")));

		// Formatierung
		lebenLbl.setPrefSize(260, 25);
		winterLbl.setPrefSize(260, 25);
		giftLbl.setPrefSize(260, 25);
		teileLbl.setPrefSize(260, 50);
		vermLbl.setPrefSize(260, 50);
		teileLbl.setEditable(false);
		vermLbl.setEditable(false);

		// Styling
		lebenLbl.getStyleClass().add("dialog-label-info");
		winterLbl.getStyleClass().add("dialog-label-info");
		giftLbl.getStyleClass().add("dialog-label-info");
		teileLbl.getStyleClass().add("dialog-label-info");
		vermLbl.getStyleClass().add("dialog-label-info");

		grid.add(new Label("Lebensdauer:"), 0, 0);
		grid.add(lebenLbl, 1, 0);
		grid.add(new Label("Veträglichkeit:"), 0, 1);
		grid.add(winterLbl, 1, 1);
		grid.add(new Label("Verwendete Teile:"), 0, 2);
		grid.add(teileLbl, 1, 2);
		grid.add(new Label("Giftig:"), 0, 3);
		grid.add(giftLbl, 1, 3);
		grid.add(new Label("Vermehrung:"), 0, 4);
		grid.add(vermLbl, 1, 4);

		VBox vb = new VBox(grid);
		vb.setPadding(new Insets(15, 0, 0, 0));

		Tab tabEig = new Tab("Eigenschaften");
		tabEig.setClosable(false);
		tabEig.setContent(vb);
		return tabEig;
	}
	
	public Tab pflegeinfo(PflanzeFX p) {
		GridPane grid = new GridPane();
		grid.setHgap(35);
		grid.setVgap(10);

		Label duegnLbl = new Label(String.valueOf(p.getAppPflanze().getDuengung()));
		Label wasserLbk = new Label(String.valueOf(p.getAppPflanze().getWasserbedarf()));
		Label lichtLbl = new Label(String.valueOf(p.getAppPflanze().getLichtbedarf()));
		Label standortLbl = new Label(String.valueOf(p.getAppPflanze().getStandort()));
		TextArea schnittLbl = new TextArea(schnitt.stream().map(Month::getBeschreibung).collect(Collectors.joining(", ")));

		duegnLbl.setPrefSize(260, 25);
		wasserLbk.setPrefSize(260, 25);
		lichtLbl.setPrefSize(260, 25);
		standortLbl.setPrefSize(260, 25);
		schnittLbl.setPrefSize(260, 50);
		schnittLbl.setEditable(false);

		duegnLbl.getStyleClass().add("dialog-label-info");
		wasserLbk.getStyleClass().add("dialog-label-info");
		lichtLbl.getStyleClass().add("dialog-label-info");
		standortLbl.getStyleClass().add("dialog-label-info");
		schnittLbl.getStyleClass().add("dialog-label-info");

		grid.add(new Label("Düngung:"), 0, 0);
		grid.add(duegnLbl, 1, 0);
		grid.add(new Label("Wasserbedarf:"), 0, 1);
		grid.add(wasserLbk, 1, 1);
		grid.add(new Label("Lichtbedarf:"), 0, 2);
		grid.add(lichtLbl, 1, 2);
		grid.add(new Label("Standort:"), 0, 3);
		grid.add(standortLbl, 1, 3);
		grid.add(new Label("Rückschnittszeit:"), 0, 4);
		grid.add(schnittLbl, 1, 4);

		VBox vb = new VBox(grid);
		vb.setPadding(new Insets(15, 0, 0, 0));

		Tab tabPfle = new Tab("Pflege");
		tabPfle.setClosable(false);
		tabPfle.setContent(vb);
		return tabPfle;
	}

	public Tab kalender(PflanzeFX p) {
		GridPane grid = new GridPane();
		grid.setHgap(60);
		grid.setVgap(10);

		TextArea ausLbl = new TextArea(aussaat.stream().map(Month::getBeschreibung).collect(Collectors.joining(", ")));
		TextArea blueLbl = new TextArea(bluete.stream().map(Month::getBeschreibung).collect(Collectors.joining(", ")));
		TextArea ernLbl = new TextArea(ernte.stream().map(Month::getBeschreibung).collect(Collectors.joining(", ")));

		ausLbl.setPrefSize(260, 50);
		blueLbl.setPrefSize(260, 50);
		ernLbl.setPrefSize(260, 50);
		ausLbl.setEditable(false);
		blueLbl.setEditable(false);
		ernLbl.setEditable(false);

		ausLbl.getStyleClass().add("dialog-label-info");
		blueLbl.getStyleClass().add("dialog-label-info");
		ernLbl.getStyleClass().add("dialog-label-info");

		grid.add(new Label("Aussaatzeit:"), 0, 0);
		grid.add(ausLbl, 1, 0);
		grid.add(new Label("Blütezeit:"), 0, 1);
		grid.add(blueLbl, 1, 1);
		grid.add(new Label("Erntezeit:"), 0, 2);
		grid.add(ernLbl, 1, 2);

		VBox vb = new VBox(grid);
		vb.setPadding(new Insets(15, 0, 0, 0));

		Tab tabKal = new Tab("Kalender");
		tabKal.setClosable(false);
		tabKal.setContent(vb);
		return tabKal;
	}

	public Tab notiz(PflanzeFX p) {
		GridPane grid = new GridPane();
		grid.setHgap(35);
		grid.setVgap(15);

		TextArea notizLbl = new TextArea(p.getAppPflanze().getNotiz());
		notizLbl.setPrefWidth(340);
		notizLbl.setPrefHeight(200);
		notizLbl.setEditable(false);
		notizLbl.setOnMouseEntered(e -> notizLbl.setCursor(Cursor.WAIT));
		notizLbl.getStyleClass().add("dialog-label-info");

		grid.add(new Label("Notiz:"), 0, 0);
		grid.add(notizLbl, 1, 0);

		VBox vb = new VBox(grid);
		vb.setPadding(new Insets(15, 0, 0, 0));

		Tab notTab = new Tab("Notiz");
		notTab.setClosable(false);
		notTab.setContent(vb);
		return notTab;
	}
}
