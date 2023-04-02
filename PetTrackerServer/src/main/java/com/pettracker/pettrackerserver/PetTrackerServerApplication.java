package com.pettracker.pettrackerserver;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@SpringBootApplication(scanBasePackages = "com.pettracker.pettrackerserver")
@EnableAutoConfiguration
public class PetTrackerServerApplication {

	public static void main(String[] args) {
//		SpringApplication.run(PetTrackerServerApplication.class, args);
		FirebaseOptions options;
		try {
			String value_name = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
			System.out.println(value_name);
			options = FirebaseOptions.builder().setCredentials(GoogleCredentials.getApplicationDefault()).build();
			FirebaseApp.initializeApp(options);
		} catch(RuntimeException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
		ConfigurableApplicationContext run = SpringApplication.run(PetTrackerServerApplication.class, args);
		PetTrackerServerApplication app = run.getBean(PetTrackerServerApplication.class);
//        app.run();
		
	}
	
	private void run() {
        Runnable r1 = () -> {
            for(int i = 0; i < 30; ++i) {
                System.out.println("task 1");
                try {
        			String registrationToken = "cnbYxhIdTYyCDL4bzuDQmV:APA91bEri00liWE__rKnAffnm8dHTut3kGFxQNjVjg4YS1gTNBXGGSPiPklpqvKl4A43e5yO-Kav8DL55PJ8sJKjx7LPbZCMAkbUnMquEeAL-agj685AtZaKns8OyZ00xl9mAtgxxuCi";
        		    Notification.Builder builder = Notification.builder();
        		    Message message = Message.builder()
        		            .setNotification(builder.build())
        		            .putData("title", "abc")
        		            .putData("body", "abc")
        		            .setToken(registrationToken)
        		            .build();
        			String response = FirebaseMessaging.getInstance().send(message);
        			System.out.println("Successfully sent message: " + response);
                    Thread.sleep(5000);
        		} catch (FirebaseMessagingException e) {
        			System.out.println("issue");
        			e.printStackTrace();
        		}catch (Exception e) {
        			System.out.println("issue");
        			e.printStackTrace();
        		}
            }
        };

        Runnable r2 = () -> {
            for(int i = 0; i < 30; ++i) {
                System.out.println("task 2");
                try {Thread.sleep(5000);} catch(Exception ignored) {}
            }
        };

        //Create an executor service with 2 threads (it can be like 50
        //if you need it to be).  Submit our two tasks to it and they'll
        //both run to completion (or forever if they don't end).
        ExecutorService service = Executors.newFixedThreadPool(2);
        service.submit(r1);
        service.submit(r2);

        //Wait or completion of tasks (or forever).
        service.shutdown();
        try { service.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS); }
        catch (InterruptedException e) { e.printStackTrace(); }
    }

}
