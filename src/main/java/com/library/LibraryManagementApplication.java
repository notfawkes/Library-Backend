package com.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class LibraryManagementApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(LibraryManagementApplication.class);

        // Bind server port and address for Render
        Map<String, Object> props = new HashMap<>();
        props.put("server.port", System.getenv().getOrDefault("PORT", "8080"));
        props.put("server.address", "0.0.0.0"); // Bind to all network interfaces
        app.setDefaultProperties(props);

        app.run(args);
    }

}
