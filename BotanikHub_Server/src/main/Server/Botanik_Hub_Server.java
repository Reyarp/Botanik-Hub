package Server;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.jsonb.JsonBindingFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.sun.net.httpserver.HttpServer;

import Database.DB_Benutzer;
import Database.DB_BotanikHub;
import Database.DB_BotanikKalender;
import Database.DB_Erinnerungen;
import Database.DB_Pflanze;
import Database.DB_PflanzenEntdecken;
import Database.DB_Verbindungstabellen;
import Database.DB_Wunschliste;
import Resource.Resource_Benutzer;
import Resource.Resource_BotanikHub;
import Resource.Resource_Botanikkalender;
import Resource.Resource_Erinnerung;
import Resource.Resource_Pflanze;
import Resource.Resource_Pflanze_Entdecken;
import Resource.Resource_Wunschliste;

public class Botanik_Hub_Server {

	public static void main(String[] args) {
			
		// Alle Tables erstellen
		// Reihenfolge sehr wichtig -> wegen Abhängikeiten und Foreign Keys
		try {
			DB_Benutzer.createBenutzer();
			DB_Pflanze.createPflanze();
			
			DB_BotanikHub.createBotanikHub();
			DB_PflanzenEntdecken.createPflanzenEntdecken();
			DB_Wunschliste.createWunschliste();
			
			DB_Verbindungstabellen.createVerwendeteTeile();
			DB_Verbindungstabellen.createVermehrung();
			DB_Verbindungstabellen.createPflanzenTyp();
			
			DB_BotanikKalender.createBotanikkalender();
			DB_Erinnerungen.createErinnerung();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// baseURI erstellen -> verbinung Server - Client
		URI baseURI = URI.create("http://localhost:4711/");
		ResourceConfig config = new ResourceConfig()
				// Alle resource registrieren
				.register(Resource_Benutzer.class)
				.register(Resource_Pflanze.class)
				.register(Resource_BotanikHub.class)
				.register(Resource_Botanikkalender.class)
				.register(Resource_Erinnerung.class)
				.register(Resource_Pflanze_Entdecken.class)
				.register(Resource_Wunschliste.class)
				// JsonBindungFeature für Datum
				.register(JsonBindingFeature.class)
				.register(JacksonFeature.class);
				

		HttpServer server = JdkHttpServerFactory.createHttpServer(baseURI, config);
		System.out.println("Server läuft - zum Beenden Eingabetaste drücken");
		try {
			System.in.read();
		} catch(IOException e) {
			e.printStackTrace();
		}
		System.out.println("Server wird gestoppt");
		server.stop(0);
		((ExecutorService) server.getExecutor()).shutdown();
	}
}
