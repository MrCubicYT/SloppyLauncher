package com.cubic.sloppy;

import com.cubic.sloppy.launcher.*;
import fr.trxyy.alternative.alternative_api.*;
import fr.trxyy.alternative.alternative_api.utils.*;
import fr.trxyy.alternative.alternative_api_ui.*;
import fr.trxyy.alternative.alternative_api_ui.base.*;
import javafx.scene.*;
import javafx.scene.shape.*;
import javafx.stage.*;

import java.io.*;
import java.net.*;

public class App extends AlternativeBase {

    private static App instance;
    private Scene scene;
    private final GameFolder gameFolder = createGameFolder();
    private final LauncherPreferences launcherPreferences = createLauncherPreferences();
    private final GameLinks gameLinks = createGameLinks();
    private final GameEngine gameEngine = createGameEngine();
    private LauncherPanel panel;
    public static final GameConnect GAME_CONNECT = new GameConnect("starnetworks.mine.bz", "");

    public void launcher(){
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        setInstance(this);
        createContent();
        this.gameEngine.reg(primaryStage);
        LauncherBase launcherBase = new LauncherBase(primaryStage, scene, StageStyle.TRANSPARENT, this.gameEngine);
        launcherBase.setIconImage(primaryStage, "poop.png");
    }

    private GameFolder createGameFolder() {
        return new GameFolder("sloppy");
    }

    private LauncherPreferences createLauncherPreferences() {
        return new LauncherPreferences("Sloppy Launcher", 1050, 750, Mover.MOVE);
    }

    private GameLinks createGameLinks() {
        return new GameLinks("http://143.47.253.158/1.20.1/forge/", "1.20.1.json");
    }

    private GameEngine createGameEngine() {
        return new GameEngine(gameFolder, gameLinks, launcherPreferences, GameStyle.FORGE_1_19_HIGHER);
    }

    private void createContent() throws IOException {
        LauncherPane contentPane = new LauncherPane(this.gameEngine);
        scene = new Scene(contentPane);
        Rectangle rectangle = new Rectangle(this.gameEngine.getLauncherPreferences().getWidth(),
                this.gameEngine.getLauncherPreferences().getHeight());
        this.gameEngine.reg(gameLinks);
        rectangle.setArcWidth(20.0);
        rectangle.setArcHeight(20.0);
        contentPane.setClip(rectangle);
        contentPane.setStyle("-fx-background-color: transparent;");
        setPanel(new LauncherPanel(contentPane, this.gameEngine));
    }

    public static boolean netIsAvailable() {
        try {
            final HttpURLConnection urlConnection = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
            urlConnection.setRequestMethod("HEAD");
            urlConnection.connect();
            return (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (IOException e) {
            System.err.println("Error verifying internet connection!: " + e.getMessage());
            return false;
        }
    }

    public static App getInstance() {
        return instance;
    }

    private static void setInstance(App instance) {
        App.instance = instance;
    }

    public LauncherPanel getPanel() {
        return panel;
    }

    private void setPanel(LauncherPanel panel) {
        this.panel = panel;
    }

	public static GameConnect getGameConnect() {
		// TODO Auto-generated method stub
		return GAME_CONNECT;
	}
}