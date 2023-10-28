package com.cubic.sloppy.launcher;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import com.cubic.sloppy.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;

import fr.trxyy.alternative.alternative_api_ui.LauncherAlert;
import fr.trxyy.alternative.alternative_api_ui.LauncherPane;
import fr.trxyy.alternative.alternative_api_ui.base.IScreen;
import fr.trxyy.alternative.alternative_api.*;
import fr.trxyy.alternative.alternative_api.updater.GameUpdater;
import fr.trxyy.alternative.alternative_api.utils.*;
import fr.trxyy.alternative.alternative_api.utils.config.*;
import fr.trxyy.alternative.alternative_api_ui.components.*;
import fr.trxyy.alternative.alternative_auth.account.AccountType;
import fr.trxyy.alternative.alternative_auth.base.GameAuth;
import animatefx.animation.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.*;

public class LauncherPanel extends IScreen {
	
	private final GameEngine engine;  //launcher panel thing
	private final LauncherConfig config; //launcher config thing?
	private final GameUpdater gameUpdater = new GameUpdater();
	private final DecimalFormat decimalFormat = new DecimalFormat(".#");
	
	private GameAuth auth;
	
	private LauncherImage titleImage; //main img
	private LauncherImage updateAvatar;
	private LauncherImage avatar;
	
	private LauncherRectangle connectionRectangle;
	private LauncherRectangle autoLoginRectangle;
	private LauncherRectangle updateRectangle;
	
	private LauncherLabel crackTitle;
	private LauncherLabel autoLoginLabel;
	private LauncherLabel updateLabel;
	private LauncherLabel currentStep;
	private LauncherLabel currentFileLabel;
	private LauncherLabel percentageLabel;
	
	private LauncherButton autoLoginButton;
	private LauncherButton autoLoginButton2;
	private LauncherButton settingsButton;
	private LauncherButton microsoftButton;
	private LauncherButton packsButton;
	
	private Timer autoLoginTimer;
	
	private MediaPlayer mediaPlayer;
	
	private JFXTextField usernameField;
	
	private JFXPasswordField passwordField;
	
	private JFXToggleButton rememberMe;
	
	private JFXButton loginButton;
	
	public JFXProgressBar bar;
	
	
	
	public LauncherPanel(Pane root, GameEngine engine) { 
		
	    this.engine = engine; //sets variable to engine given to this method
	    this.drawAnimatedBackground(engine, root, "bg.mp4"); //draw animated background
	    Platform.runLater(root::requestFocus); //deselect default text field
	    
	    this.config = new LauncherConfig(engine); //create laucher config file?
	    this.config.loadConfiguration(); //load laucher config file?
	    
	    setupBackGround(root);
	    setupButtons(root);
	    setupConnectionsGUI(root);
        setupUpdateGUI(root);
        initConfig(root);
	    
	}
	
    private void checkAutoLogin(Pane root) {
        if (!isAutoLoginEnabled()) {
            return;
        }
    }
    
    private boolean isAutoLoginEnabled() {
        return this.config.getValue(EnumConfig.AUTOLOGIN).equals(true);
    }
	
    private void setupBackGround(Pane root) {
        LauncherRectangle topRectangle = new LauncherRectangle(root, 0, 0, 70, engine.getHeight()); //launcher side black bar
        topRectangle.setFill(Color.rgb(0, 0, 0, 0.70)); //fill colour
        
        this.drawImage(engine, getResourceLocation().loadImage(engine, "sloppysmp.png"), //main img
        engine.getWidth() / 2 - 270, 20, 580, 150, root, Mover.DONT_MOVE); //main img location

        root.getScene().getStylesheets().add("css/design.css"); //some css design

        //main img and style
        this.titleImage = new LauncherImage(root);
        this.titleImage.setImage(getResourceLocation().loadImage(engine, "poop.png"));
        this.titleImage.setSize(50, 50);
        this.titleImage.setBounds(10, 5, 50, 50);

        //close button style
        LauncherButton closeButton = new LauncherButton(root);
        // this.closeButton.setInvisible();
        LauncherImage closeImg = new LauncherImage(root, getResourceLocation().loadImage(engine, "close.png"));
        closeImg.setSize(15, 15);
        closeButton.setGraphic(closeImg);
        closeButton.setBackground(null);
        closeButton.setPosition(engine.getWidth() - 35, 2);
        closeButton.setSize(15, 15);
        closeButton.setOnAction(event -> {
            final FadeOutDown animation = new FadeOutDown(root);
            animation.setOnFinished(actionEvent -> System.exit(0));
            animation.play();
        });

        //reduce button style
        LauncherButton reduceButton = new LauncherButton(root);
        // this.reduceButton.setInvisible();
        LauncherImage reduceImg = new LauncherImage(root, getResourceLocation().loadImage(engine, "reduce.png"));
        reduceImg.setSize(15, 15);
        reduceButton.setGraphic(reduceImg);
        reduceButton.setBackground(null);
        reduceButton.setPosition(engine.getWidth() - 65, 2);
        reduceButton.setSize(15, 15);
        reduceButton.setOnAction(event -> {
            final ZoomOutDown animation2 = new ZoomOutDown(root);
            animation2.setOnFinished(actionEvent -> {
                Stage stage = (Stage) ((LauncherButton) event.getSource()).getScene().getWindow();
                stage.setIconified(true);
            });
            animation2.setResetOnFinished(true);
            animation2.play();
        });
        final FadeInDown animation3 = new FadeInDown(root);
        animation3.setResetOnFinished(true);
        animation3.play();
    }
    
    private void initConfig(Pane root) {
  	  //boolean useMusic = (boolean) config.getValue(EnumConfig.USE_MUSIC);
  	  boolean useConnect = (boolean) config.getValue(EnumConfig.USE_CONNECT);
  	  boolean rememberMe = (boolean) config.getValue(EnumConfig.REMEMBER_ME);
  	  boolean useMicrosoft = (boolean) config.getValue(EnumConfig.USE_MICROSOFT);
  	  boolean usePremium = (boolean) config.getValue(EnumConfig.USE_PREMIUM);
  	  String password = (String) config.getValue(EnumConfig.PASSWORD);
  	  String username = (String) config.getValue(EnumConfig.USERNAME);
  	  String version = (String) config.getValue(EnumConfig.VERSION);
  	  String email = usernameField.getText();

  	  //mediaPlayer.setMute(!useMusic);

  	  if (useConnect) {
  		System.out.println("useconnect");
  	    engine.reg(App.getGameConnect());
  	  }

  	  if (rememberMe) {
  	    passwordField.setText(password);
  	  } else {
  	    passwordField.setText("");
  	  }

  	  if (useMicrosoft) {
  		connectAccountPremium(username, root);
  	    connectAccountPremiumCO(username, root);
  	  } else if (email.length() > 3 && email.contains("@")) {
  	    if (!passwordField.getText().isEmpty()) {
  	      GameAuth auth = new GameAuth(email, password, AccountType.MOJANG);
  	      if (auth.isLogged()) {
  	        connectAccountPremium(auth.getSession().getUsername(), root);
  	        connectAccountPremiumCO(auth.getSession().getUsername(), root);
  	      } else {
  	        connectAccountPremiumOFF(root);
  	        connectAccountCrackCO(root);
  	      }
  	    } else {
  	      connectAccountPremiumOFF(root);
  	      connectAccountCrackCO(root);
  	    }
  	  } else if (usePremium) {
  	    connectAccountPremiumOFF(root);
  	    connectAccountCrackCO(root);
  	  } else {
  	    this.rememberMe.setSelected(false);
  	    connectAccountCrack(root);
  	    connectAccountCrackCO(root);
  	  }

  	  GameLinks links = new GameLinks("https://majestycraft.com/minecraft" + urlModifier(version), version + ".json");
  	  engine.reg(links);
  	  Utils.regGameStyle(engine, config);
  	}
    
    private void setupButtons(Pane root) {
        this.microsoftButton = new LauncherButton(root);
        this.microsoftButton.setStyle("-fx-background-color: rgba(0 ,0 ,0 , 0); -fx-text-fill: orange");
        LauncherImage microsoftImg = new LauncherImage(root, getResourceLocation().loadImage(engine, "microsoft.png"));
        microsoftImg.setSize(27, 27);
        this.microsoftButton.setGraphic(microsoftImg);
        this.microsoftButton.setPosition(engine.getWidth() / 2 - 522, engine.getHeight() / 2 - 100);
        this.microsoftButton.setSize(60, 46);
        microsoftButton.setOnAction(event -> {
        	  if (!App.netIsAvailable()) {
        	    showConnectionErrorAlert();
        	    return;
        	  }
        	  
        	  auth = new GameAuth(AccountType.MICROSOFT);
        	  showMicrosoftAuth();
        	  if (auth.isLogged()) {
        	    connectAccountPremiumCO(auth.getSession().getUsername(), root);
        	    config.updateValue("useMicrosoft", true);
        	    update();
        	  } else {
        	    showAuthErrorAlert();
        	  }
        });

        this.settingsButton = new LauncherButton(root);
        this.settingsButton.setStyle("-fx-background-color: rgba(0 ,0 ,0 , 0); -fx-text-fill: orange");
        LauncherImage settingsImg = new LauncherImage(root, getResourceLocation().loadImage(engine, "settings.png"));
        settingsImg.setSize(27, 27);
        this.settingsButton.setGraphic(settingsImg);
        this.settingsButton.setPosition(engine.getWidth() / 2 - 522, engine.getHeight() / 2);
        this.settingsButton.setSize(60, 46);
        this.settingsButton.setOnAction(event -> {
            Scene scene = new Scene(createSettingsPanel(root));
            Stage stage = new Stage();
            scene.setFill(Color.TRANSPARENT);
            stage.setResizable(false);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setTitle("Parametres Launcher");
            stage.setWidth(900);
            stage.setHeight(600);
            stage.setScene(scene);
            stage.showAndWait();
        });

        JFXRippler rippler4 = new JFXRippler(this.settingsButton);
        rippler4.setLayoutX((float) engine.getWidth() / 2 - 515);
        rippler4.setLayoutY((float) engine.getHeight() / 2);
        rippler4.getStyleClass().add("rippler2");
        root.getChildren().add(rippler4);
        
        
        this.packsButton = new LauncherButton(root);
        this.packsButton.setStyle("-fx-background-color: rgba(0 ,0 ,0 , 0); -fx-text-fill: orange");
        settingsImg = new LauncherImage(root, getResourceLocation().loadImage(engine, "pack.png"));
        settingsImg.setSize(27, 27);
        this.packsButton.setGraphic(settingsImg);
        this.packsButton.setPosition(engine.getWidth() / 2 - 522, engine.getHeight() / 2+100);
        this.packsButton.setSize(60, 46);
        this.packsButton.setOnAction(event -> {
            Scene scene = new Scene(createPacksPanel(root));
            Stage stage = new Stage();
            scene.setFill(Color.TRANSPARENT);
            stage.setResizable(false);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setTitle("Parametres Launcher");
            stage.setWidth(900);
            stage.setHeight(600);
            stage.setScene(scene);
            stage.showAndWait();
        });

        JFXRippler rippler5 = new JFXRippler(this.packsButton);
        rippler5.setLayoutX((float) engine.getWidth() / 2 - 515);
        rippler5.setLayoutY((float) engine.getHeight() / 2 + 50 );
        rippler5.getStyleClass().add("rippler2");
        root.getChildren().add(rippler5);
    }
    
    private void setupConnectionsGUI(Pane root) {
        this.connectionRectangle = new LauncherRectangle(root, engine.getWidth() / 2 - 188, engine.getHeight() / 2 - 150,
                380, 320);
        this.connectionRectangle.setArcWidth(50.0);
        this.connectionRectangle.setArcHeight(50.0);
        this.connectionRectangle.setFill(Color.rgb(0, 0, 0, 0.30));
        this.connectionRectangle.setVisible(true);


        this.crackTitle = new LauncherLabel(root);
        this.crackTitle.setText("Connection");
        this.crackTitle.setFont(Font.font("Roboto-Light.ttf", FontWeight.BOLD, 27d));
        this.crackTitle.setStyle("-fx-background-color: transparent; -fx-text-fill: white");
        this.crackTitle.setPosition(engine.getWidth() / 2 - 116, engine.getHeight() / 2 - 130);
        this.crackTitle.setSize(500, 40);

        JFXRippler rippler2 = new JFXRippler(this.crackTitle);
        rippler2.setLayoutX((float) engine.getWidth() / 2 - 72);
        rippler2.setLayoutY((float) engine.getHeight() / 2 - 130);
        rippler2.getStyleClass().add("rippler");
        root.getChildren().add(rippler2);

        this.usernameField = new JFXTextField();

        this.usernameField.getStyleClass().add("input");
        this.usernameField.setLayoutX((float) engine.getWidth() / 2 - 126);
        this.usernameField.setLayoutY((float) engine.getHeight() / 2 - 52);
        this.usernameField.setFont(FontLoader.loadFont("leadcoat.ttf", "Lead Coat", 14F));
        this.usernameField.setStyle("-fx-background-color: rgba(0 ,0 ,0 , 0.2); -fx-text-fill: orange; -fx-font-family: leadcoat");
        this.usernameField.setPromptText("Username or Email");
        if (!(boolean) config.getValue(EnumConfig.USE_MICROSOFT)) {
            this.usernameField.setText((String) this.config.getValue(EnumConfig.USERNAME));
        }
        root.getChildren().add(this.usernameField);

        this.passwordField = new JFXPasswordField();
        this.passwordField.setLayoutX((float) engine.getWidth() / 2 - 126);
        this.passwordField.setLayoutY((float) engine.getHeight() / 2 + 15);
        this.passwordField.getStyleClass().add("input");
        this.passwordField.setFont(FontLoader.loadFont("Roboto-Light.ttf", "Roboto Light", 14F));
        this.passwordField.setStyle("-fx-background-color: rgba(0 ,0 ,0 , 0.4); -fx-text-fill: orange");
        this.passwordField.setPromptText("Password (leave blank for crack)");
        root.getChildren().add(this.passwordField);

        this.rememberMe = new JFXToggleButton();
        this.rememberMe.setText("Remember me");
        this.rememberMe.setSelected((boolean) config.getValue(EnumConfig.REMEMBER_ME));
        this.rememberMe.getStyleClass().add("jfx-toggle-button");
        this.rememberMe.setLayoutX(385);
        this.rememberMe.setLayoutY(427);
        this.rememberMe.setOnAction(event -> config.updateValue("rememberme", rememberMe.isSelected()));

        root.getChildren().add(this.rememberMe);

        this.loginButton = new JFXButton("Login");
        this.loginButton.getStyleClass().add("button-raised");
        this.loginButton.setLayoutX(400);
        this.loginButton.setLayoutY(480);
        this.loginButton.setFont(FontLoader.loadFont("../resources/leadcoat.ttf", "leadcoat", 22F));
        this.loginButton.setOnAction(event -> {
            if (!App.netIsAvailable()) {
            	Platform.runLater(() -> new LauncherAlert("Authentication Failed!", "Cannot connect, you are offline. Please use crack mode to connect."));
                return;
            }

            config.updateValue("useMicrosoft", false);

            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.length() <= 3) {
            	new LauncherAlert("Authentication Failed!", "Cannot connect, seems to be an 'offline' authentication. \nThere is an issue during connection attempt. \n\n-Make sure the username has at least 3 characters.");
                return;
            }

            if (password.isEmpty()) {
                auth = new GameAuth(username, password, AccountType.OFFLINE);
                connectAccountCrackCO(root);
            } else {
                auth = new GameAuth(username, password, AccountType.MOJANG);
                connectAccountPremiumCO(username, root);
                if ((boolean) config.getValue(EnumConfig.REMEMBER_ME)) {
                    config.updateValue("password", password);
                } else {
                    config.updateValue("password", "");
                }
            }

            if (auth.isLogged()) {
                config.updateValue("username", username);
                update();
            } else {
            	new LauncherAlert("Authentication Failed!", "Cannot connect, seems to be an 'online' authentication. \\nThere's an issue during the connection attempt. \\n\\n-Ensure the username is at least 3 characters long (unmigrated account). \\n-Please mind the case sensitivity. \\nMake sure you are using a Mojang account. \\nEnsure you are using your email if connecting with a Mojang account!");
            }
        });
        root.getChildren().add(this.loginButton);


        this.autoLoginRectangle = new LauncherRectangle(root, 0, engine.getHeight() - 32, 2000,
                engine.getHeight());
        this.autoLoginRectangle.setFill(Color.rgb(0, 0, 0, 0.70));
        this.autoLoginRectangle.setOpacity(1.0);
        this.autoLoginRectangle.setVisible(false);

        /* ===================== MESSAGE AUTOLOGIN ===================== */
        this.autoLoginLabel = new LauncherLabel(root);
        this.autoLoginLabel.setText("Auto-connecting in 3 seconds. Press ESC to cancel.");
        this.autoLoginLabel.setFont(FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", 18F));
        this.autoLoginLabel.setStyle("-fx-background-color: transparent; -fx-text-fill: red;");
        this.autoLoginLabel.setPosition(engine.getWidth() / 2 - 280, engine.getHeight() - 34);
        this.autoLoginLabel.setOpacity(0.7);
        this.autoLoginLabel.setSize(700, 40);
        this.autoLoginLabel.setVisible(false);

        /* ===================== ANNULER AUTOLOGIN ===================== */
        this.autoLoginButton = new LauncherButton(root);
        this.autoLoginButton.setText("Cancel");
        this.autoLoginButton.setFont(FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", 14F));
        this.autoLoginButton.setPosition(engine.getWidth() / 2 + 60, engine.getHeight() - 30);
        this.autoLoginButton.setSize(100, 20);
        this.autoLoginButton.setStyle("-fx-background-color: rgba(255, 0, 0, 0.4); -fx-text-fill: black;");
        this.autoLoginButton.setVisible(false);
        this.autoLoginButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                autoLoginTimer.cancel();
                autoLoginLabel.setVisible(false);
                autoLoginButton.setVisible(false);
                autoLoginRectangle.setVisible(false);
                autoLoginButton2.setVisible(false);
            }
        });
        
        /* ===================== ANNULER AUTOLOGIN ===================== */
        this.autoLoginButton2 = new LauncherButton(root);
        this.autoLoginButton2.setText("Start");
        this.autoLoginButton2.setFont(FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", 14F));
        this.autoLoginButton2.setPosition(engine.getWidth() / 2 + 170, engine.getHeight() - 30);
        this.autoLoginButton2.setSize(100, 20);
        this.autoLoginButton2.setStyle("-fx-background-color: rgba(15, 209, 70, 0.4); -fx-text-fill: black;");
        this.autoLoginButton2.setVisible(false);
        this.autoLoginButton2.setOnAction(event -> {
        	if (!engine.getGameMaintenance().isAccessBlocked()) {
                autoLoginTimer.cancel();
                autoLoginLabel.setVisible(false);
                autoLoginButton.setVisible(false);
                autoLoginRectangle.setVisible(false);
                autoLoginButton2.setVisible(false);
                if ((boolean) config.getValue(EnumConfig.USE_CONNECT)) {
                    engine.reg(App.getGameConnect());
                }
                checkAutoLogin(root);
        	}
        });
        
        if (this.config.getValue(EnumConfig.AUTOLOGIN).equals(true)) {
            Platform.runLater(() -> {
                autoLoginTimer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    final int waitTime = 7;
                    int elapsed = 0;

                    @Override

                    public void run() {
                    	elapsed++;
                    	if (elapsed % waitTime == 0) {
                    		if (!engine.getGameMaintenance().isAccessBlocked()) {
                                autoLoginTimer.cancel();
                                autoLoginLabel.setVisible(false);
                                autoLoginButton.setVisible(false);
                                autoLoginRectangle.setVisible(false);
                                autoLoginButton2.setVisible(false);
                                checkAutoLogin(root);
                    		}
                    	}
                        else {
                            final int time = (waitTime - (elapsed % waitTime));
                            Platform.runLater(() -> autoLoginLabel.setText(String.format("Auto-login in %d seconds.", time)));
                        }
                    }
                    

                };
                autoLoginTimer.schedule(timerTask, 0, 1000);
                autoLoginLabel.setVisible(true);
                autoLoginRectangle.setVisible(true);
                autoLoginButton.setVisible(true);
                autoLoginButton2.setVisible(true);
               });

        }
    }
    
    private void setupUpdateGUI(Pane root) {
        /* ===================== RECTANGLE DE MISE A JOURS ===================== */
        this.updateRectangle = new LauncherRectangle(root, engine.getWidth() / 2 - 175, engine.getHeight() / 2 - 80,
                350, 180);
        this.updateRectangle.setArcWidth(50.0);
        this.updateRectangle.setArcHeight(50.0);
        this.updateRectangle.setFill(Color.rgb(0, 0, 0, 0.60));
        this.updateRectangle.setVisible(false);

        /* =============== LABEL TITRE MISE A JOUR =============== **/
        this.updateLabel = new LauncherLabel(root);
        this.updateLabel.setText("- UPDATE -");
        this.updateLabel.setAlignment(Pos.CENTER);
        this.updateLabel.setFont(FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", 22F));
        this.updateLabel.setStyle("-fx-background-color: transparent; -fx-text-fill: orange;");
        this.updateLabel.setPosition(engine.getWidth() / 2 - 95, engine.getHeight() / 2 - 75);
        this.updateLabel.setOpacity(1);
        this.updateLabel.setSize(190, 40);
        this.updateLabel.setVisible(false);

        /* =============== ETAPE DE MISE A JOUR =============== **/
        this.currentStep = new LauncherLabel(root);
        this.currentStep.setText("- UPDATE -");
        this.currentStep.setFont(Font.font("Verdana", FontPosture.ITALIC, 18F)); // FontPosture.ITALIC
        this.currentStep.setStyle("-fx-background-color: transparent; -fx-text-fill: orange;");
        this.currentStep.setAlignment(Pos.CENTER);
        this.currentStep.setPosition(engine.getWidth() / 2 - 160, engine.getHeight() / 2 + 63);
        this.currentStep.setOpacity(0.4);
        this.currentStep.setSize(320, 40);
        this.currentStep.setVisible(false);

        /* =============== FICHIER ACTUEL EN TELECHARGEMENT =============== **/
        this.currentFileLabel = new LauncherLabel(root);
        this.currentFileLabel.setText("launchwrapper-12.0.jar");
        this.currentFileLabel.setFont(FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", 18F));
        this.currentFileLabel.setStyle("-fx-background-color: transparent; -fx-text-fill: orange;");
        this.currentFileLabel.setAlignment(Pos.CENTER);
        this.currentFileLabel.setPosition(engine.getWidth() / 2 - 160, engine.getHeight() / 2 + 5);
        this.currentFileLabel.setOpacity(0.8);
        this.currentFileLabel.setSize(320, 40);
        this.currentFileLabel.setVisible(false);

        /* =============== POURCENTAGE =============== **/
        this.percentageLabel = new LauncherLabel(root);
        this.percentageLabel.setText("0%");
        this.percentageLabel.setFont(FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", 30F));
        this.percentageLabel.setStyle("-fx-background-color: transparent; -fx-text-fill: orange;");
        this.percentageLabel.setAlignment(Pos.CENTER);
        this.percentageLabel.setPosition(engine.getWidth() / 2 - 50, engine.getHeight() / 2 - 25);
        this.percentageLabel.setOpacity(0.8);
        this.percentageLabel.setSize(100, 40);
        this.percentageLabel.setVisible(false);

        /* ===================== BARRE DE CHARGEMENT ===================== */
        this.bar = new JFXProgressBar();
        this.bar.setLayoutX((float) engine.getWidth() / 2 - 125);
        this.bar.setLayoutY((float) engine.getHeight() / 2 + 40);
        this.bar.getStyleClass().add("jfx-progress-bar");
        // this.bar.setSize(250, 20);
        this.bar.setVisible(false);
        root.getChildren().add(this.bar);
    }
    
    public void update() {
        new ZoomOutDown(this.microsoftButton).setResetOnFinished(false).play();
        new ZoomOutDown(this.settingsButton).setResetOnFinished(false).play();
        new ZoomOutDown(this.crackTitle).setResetOnFinished(false).play();
        new ZoomOutDown(this.usernameField).setResetOnFinished(false).play();
        new ZoomOutDown(this.passwordField).setResetOnFinished(false).play();
        new ZoomOutDown(this.avatar).setResetOnFinished(false).play();
        new ZoomOutDown(this.rememberMe).setResetOnFinished(false).play();
        new ZoomOutDown(this.loginButton).setResetOnFinished(false).play();
        new ZoomOutDown(this.connectionRectangle).setResetOnFinished(false).play();

        this.usernameField.setDisable(true);
        this.connectionRectangle.setDisable(true);
        this.rememberMe.setDisable(true);
        this.passwordField.setDisable(true);
        this.loginButton.setDisable(true);
        this.settingsButton.setDisable(true);

        this.updateRectangle.setVisible(true);
        this.updateLabel.setVisible(true);
        this.currentStep.setVisible(true);
        this.currentFileLabel.setVisible(true);
        this.percentageLabel.setVisible(true);
        this.bar.setVisible(true);
        avatar.setVisible(false);
        new ZoomInDown(this.updateRectangle).play();
        new ZoomInDown(this.updateLabel).play();
        new ZoomInDown(this.currentStep).play();
        new ZoomInDown(this.currentFileLabel).play();
        new ZoomInDown(this.percentageLabel).play();
        new ZoomInDown(this.bar).play();
        new ZoomInDown(updateAvatar).play();
        updateAvatar.setVisible(true);
        engine.getGameLinks().JSON_URL = engine.getGameLinks().BASE_URL
                + this.config.getValue(EnumConfig.VERSION) + ".json";
        this.gameUpdater.reg(engine);
        this.gameUpdater.reg(auth.getSession());

        /*
         * Change settings in GameEngine from launcher_config.json
         */
        engine.reg(GameMemory.getMemory(Double.parseDouble((String) this.config.getValue(EnumConfig.RAM))));
        engine.reg(GameSize.getWindowSize(Integer.parseInt((String) this.config.getValue(EnumConfig.GAME_SIZE))));
        
        if ((boolean) config.getValue(EnumConfig.USE_CONNECT)) {
            engine.reg(App.getGameConnect());
        }

        boolean useVmArgs = (Boolean) config.getValue(EnumConfig.USE_VM_ARGUMENTS);
        String vmArgs = (String) config.getValue(EnumConfig.VM_ARGUMENTS);
        String[] s = null;
        if (useVmArgs) {
            if (vmArgs.length() > 3) {
                s = vmArgs.split(" ");
            }
            assert s != null;
            JVMArguments arguments = new JVMArguments(s);
            engine.reg(arguments);
        }
        /* END */

        engine.reg(this.gameUpdater);

        Thread updateThread = new Thread(() -> engine.getGameUpdater().start());
        updateThread.start();

        /*
         * ===================== REFAICHIR LE NOM DU FICHIER, PROGRESSBAR, POURCENTAGE
         * =====================
         **/
        Timeline timeline = new Timeline(
                new KeyFrame(javafx.util.Duration.seconds(0.0D), event -> timelineUpdate(engine)),
                new KeyFrame(javafx.util.Duration.seconds(0.1D)));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

    }
    
    private double percent;
    
    public void timelineUpdate(GameEngine engine) {
        if (engine.getGameUpdater().downloadedFiles > 0) {
            this.percent = engine.getGameUpdater().downloadedFiles * 100.0D / engine.getGameUpdater().filesToDownload;
            this.percentageLabel.setText(decimalFormat.format(percent) + "%");
        }
        this.currentFileLabel.setText(engine.getGameUpdater().getCurrentFile());
        this.currentStep.setText(engine.getGameUpdater().getCurrentInfo());
        this.bar.setProgress(percent / 100.0D);
    }
    
    private Parent createSettingsPanel(Pane root) {
        root = new LauncherPane(engine);
        Rectangle rect = new Rectangle(1000, 750);
        rect.setArcHeight(15.0);
        rect.setArcWidth(15.0);
        root.setClip(rect);
        root.setStyle("-fx-background-color: transparent;");
        new LauncherSettings(root, engine, this);
        new ZoomInLeft(rect).play();
        return root;
    }
    
    private Parent createPacksPanel(Pane root) {
        root = new LauncherPane(engine);
        Rectangle rect = new Rectangle(1000, 750);
        rect.setArcHeight(15.0);
        rect.setArcWidth(15.0);
        root.setClip(rect);
        root.setStyle("-fx-background-color: transparent;");
        new LauncherPacks(root, engine, this);
        new ZoomInLeft(rect).play();
        return root;
    }
    
    public void connectAccountCrack(Pane root) {
        avatar = new LauncherImage(root, new Image("https://minotar.net/cube/MHF_Steve.png"));
        avatar.setBounds(engine.getWidth() / 2 - 182, engine.getHeight() / 2 - 42, 50, 60);
    }

    public void connectAccountPremium(String username, Pane root) {
        avatar = new LauncherImage(root, new Image("https://minotar.net/cube/" + username + ".png"));
        avatar.setBounds(engine.getWidth() / 2 - 182, engine.getHeight() / 2 - 42, 50, 60);
    }

    public void connectAccountPremiumOFF(Pane root) {
        avatar = new LauncherImage(root, new Image("https://minotar.net/cube/MHF_Steve.png"));
        avatar.setBounds(engine.getWidth() / 2 - 182, engine.getHeight() / 2 - 42, 50, 60);
    }

    public void connectAccountCrackCO(Pane root) {
        updateAvatar = new LauncherImage(root, new Image("https://minotar.net/body/MHF_Steve.png"));
        updateAvatar.setSize(100, 200);
        updateAvatar.setBounds(engine.getWidth() / 2 - 280, engine.getHeight() / 2 - 90, 100, 200);
        updateAvatar.setVisible(false);
    }

    public void connectAccountPremiumCO(String username, Pane root) {
        updateAvatar = new LauncherImage(root, new Image("https://minotar.net/body/" + username + ".png"));
        updateAvatar.setBounds(engine.getWidth() / 2 - 280, engine.getHeight() / 2 - 90, 100, 200);
        updateAvatar.setVisible(false);
    }
    
    private void showMicrosoftAuth() {
        Scene scene = new Scene(createMicrosoftPanel());
        Stage stage = new Stage();
        scene.setFill(Color.TRANSPARENT);
        stage.setResizable(false);
        stage.setTitle("Microsoft Authentication");
        stage.setWidth(500);
        stage.setHeight(600);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private Parent createMicrosoftPanel() {
        LauncherPane contentPane = new LauncherPane(engine);
        auth.connectMicrosoft(engine, contentPane);
        return contentPane;
    }
    
    private String urlModifier(String version) {
        if ((boolean)(config.getValue(EnumConfig.USE_FORGE))) {
            return "/" + version + "/forge/";
        } else if ((boolean)(config.getValue(EnumConfig.USE_OPTIFINE))) {
            return "/" + version + "/";
        } else {
            return "/";
        }
    }
    
    public LauncherConfig getConfig() {
        return config;
    }
    
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
    
    private void showConnectionErrorAlert() {
        Platform.runLater(() -> new LauncherAlert("Authentication Error", "Unable to connect, you are not connected to the internet."));
    }

    private void showAuthErrorAlert() {
        Platform.runLater(() -> new LauncherAlert("Authentication Error", "Unable to connect, username or password is incorrect."));
    }
}
