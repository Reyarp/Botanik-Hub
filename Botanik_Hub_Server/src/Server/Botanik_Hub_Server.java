package Server;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutorService;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.sun.net.httpserver.HttpServer;

import Resource.Benutzer.BenutzerResource;

public class Botanik_Hub_Server {

	public static void main(String[] args) {
		URI baseURI = URI.create("http://localhost:4711/");
		ResourceConfig config = new ResourceConfig().register(BenutzerResource.class);
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
