package Chatbot;

import java.util.List;

public class Chatbot {

	private List<Pflanze> pflanzenListe;
	private Pflanze aktuellePflanze;

	public ChatBot_Alisa(List<Pflanze> pflanzenListe, Pflanze aktuellePflanze) {
		super();
		this.pflanzenListe = pflanzenListe;
		this.aktuellePflanze = aktuellePflanze;
	}

	public List<Pflanze> getPflanzenListe() {
		return pflanzenListe;
	}

	public Pflanze getAktuellePflanze() {
		return aktuellePflanze;
	}

	public void setPflanzenListe(List<Pflanze> pflanzenListe) {
		this.pflanzenListe = pflanzenListe;
	}

	public void setAktuellePflanze(Pflanze aktuellePflanze) {
		this.aktuellePflanze = aktuellePflanze;
	}

	/*
	 * Idee: Mit der String eingabe im Dialog später in der Pflanzenliste suchen ob die Pflanze existiert
	 * Wenn ja = Pflanze gefunden
	 * Wenn nicht dann kommt ein String zurück mit einer Fehlermeldung bzw einem entsprechenden Text
	 */
	private Pflanze pflanzeFinden(String eingabe, List<Pflanze> Pflanzen) {
		for(Pflanze p : pflanzenListe) {
			if(eingabe.toLowerCase().contains(p.getPflanzenname().toLowerCase())) {
				return p; // pflanze gefunden
			}
		}
		return null;
	}

	public String antwortGeben(String eingabe, List<Pflanze> pflanzen) {
		Pflanze erkannt = pflanzeFinden(eingabe, pflanzen);

		if(erkannt != null) {
			this.aktuellePflanze = erkannt; // Setzt die gefundene Pflanze auf aktuelle Pflanze
		}

		if(aktuellePflanze == null) {
			System.out.println("Welche Pflanze meinst du?");
		}

		if(eingabe.contains("blüht".toLowerCase())) {
			return "Blütezeit von " + aktuellePflanze.getPflanzenname() + ": " + aktuellePflanze.getBluetezeitkalender().getMonths();
		}

		// Noch mehr fragen erstellen 

		return "Diese Frage kann ich leider nicht beantworten";

	}
}

