package com.cpd.proiect;

import com.cpd.proiect.control.CountdownStarter;
import com.cpd.proiect.gui.ProjectMainFrame;
import com.cpd.proiect.gui.PubMainPanel;
import com.cpd.proiect.gui.SubMainPanel;
import com.cpd.proiect.socket.AppClient;
import com.cpd.proiect.socket.AppServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class ProiectApplication {

	public static int listenPort = 5050;
	public static int senderPort = 6060;
	public static String ip = "127.0.0.1";


	public static void main(String[] args) {
		SpringApplicationBuilder builder = new SpringApplicationBuilder(ProiectApplication.class);
		builder.headless(false);
		ConfigurableApplicationContext context = builder.run(args);

//		SpringApplication.run(ProiectApplication.class, args);
		WebClient.Builder internalApiCallsBuilder = WebClient.builder();

		PubMainPanel pubMainPanel = new PubMainPanel(internalApiCallsBuilder);
		SubMainPanel subMainPanel = new SubMainPanel();
		ProjectMainFrame mainFrame = new ProjectMainFrame("Window 0", subMainPanel, pubMainPanel);

		// Initi token ring participants
		CountdownStarter countdownStarter = new CountdownStarter();
		AppServer appServer = new AppServer(listenPort, pubMainPanel);
		AppClient appClient = new AppClient(ip, senderPort, pubMainPanel);

		// Start token ring
		countdownStarter.beginCountdown();
		appClient.start();
		appServer.start();


		System.out.println("Token ring started");



	}

}
