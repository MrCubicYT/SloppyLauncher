package com.cubic.sloppy;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.LogManager;

import fr.trxyy.alternative.alternative_api.GameEngine;
import fr.trxyy.alternative.alternative_api.utils.config.LauncherConfig;

public class Main {
	
	public static ResourceBundle bundle;
    public static LauncherConfig config;
    public static String language;


    public static void loadConfiguration(GameEngine engine) {
        config = new LauncherConfig(engine);
        config.loadConfiguration();
    }

    public static void main(String[] args) {
        configureLogging();
        loadConfiguration(new GameEngine(null, null, null, null));
        //try {
            App app = new App();
            app.launcher();
       // } catch (Exception e) {
        //    System.err.println("Error Launching Application! " + e.getMessage());
       // }
    }

    private static void configureLogging() {
        try {
            LogManager.getLogManager().readConfiguration(
                    App.class.getResourceAsStream("/logging.properties"));
        } catch (IOException e) {
            System.err.println("Error configuring login manager! " + e.getMessage());
        }
    }
}
