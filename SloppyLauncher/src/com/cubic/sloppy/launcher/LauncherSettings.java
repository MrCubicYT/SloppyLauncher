package com.cubic.sloppy.launcher;

import animatefx.animation.*;
import com.jfoenix.controls.*;

import com.cubic.sloppy.*;
import fr.trxyy.alternative.alternative_api.*;
import fr.trxyy.alternative.alternative_api.utils.*;
import fr.trxyy.alternative.alternative_api.utils.config.*;
import fr.trxyy.alternative.alternative_api_ui.base.*;
import fr.trxyy.alternative.alternative_api_ui.components.*;
import javafx.application.*;
import javafx.beans.value.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import java.util.*;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LauncherSettings extends IScreen {

    private final LauncherLabel memorySliderLabel;
    private JFXSlider memorySlider;
    private final JFXCheckBox autoLogin;
    private final JFXCheckBox connect;
    private final JFXCheckBox useVMArguments;
    private final LauncherTextField vmArguments;
    private double xOffSet; // Position x Ã  l'instant du clic
    private double yOffSet; // Position y Ã  l'instant du clic
    Stage stage; // Le stage qu'on voudra faire bouger (ici notre menu des paramÃ¨tres)

    public LauncherSettings(final Pane root, final GameEngine engine, final LauncherPanel pane) {
        /* ===================== BOUGER LE MENU PARAMETRE ===================== */
        // Cet Ã©vent nous permet de rÃ©cupÃ©rer les valeurs en x et en y initiales.
        root.setOnMousePressed(event -> {
            xOffSet = event.getSceneX();
            yOffSet = event.getSceneY();
        });
        // Cet Ã©vent s'occupe de faire bouger le menu
        root.setOnMouseDragged(event -> {
            stage = (Stage) memorySlider.getScene().getWindow(); // On get le stage du menu des paramÃ¨tres
            stage.setX(event.getScreenX() - xOffSet); // On donne la nouvelle position en x
            stage.setY(event.getScreenY() - yOffSet); // On donne la nouvelle postion en y
        });

        this.drawBackgroundImage(engine, root, "setbg.jpg");
        pane.getConfig().loadConfiguration();

        /* ===================== RECTANGLE NOIR EN HAUT ===================== */
        LauncherRectangle topRectangle = new LauncherRectangle(root, 0, 0, 1500, 15);
        topRectangle.setOpacity(0.7);

        /* ===================== LABEL TITRE ===================== */
        LauncherLabel titleLabel = new LauncherLabel(root);
        titleLabel.setText("SETTINGS");
        titleLabel.setStyle("-fx-text-fill: white;");
        titleLabel.setFont(FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", 28F));
        titleLabel.setPosition(350, 20);
        titleLabel.setSize(230, 35);

        /* ===================== MC SIZE LABEL ===================== */
        LauncherLabel windowsSizeLabel = new LauncherLabel(root);
        windowsSizeLabel.setText("Window Size:");
        windowsSizeLabel.setOpacity(1.0);
        windowsSizeLabel.setFont(FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", 16F));
        windowsSizeLabel.setStyle("-fx-text-fill: white;");
        windowsSizeLabel.setSize(370, 30);
        windowsSizeLabel.setPosition(250, 110);

        /* ===================== SLIDER RAM LABEL ===================== */
        LauncherLabel sliderLabel = new LauncherLabel(root);
        sliderLabel.setText("Allocated RAM:");
        sliderLabel.setOpacity(1.0);
        sliderLabel.setFont(FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", 16F));
        sliderLabel.setStyle("-fx-text-fill: white;");
        sliderLabel.setSize(370, 30);
        sliderLabel.setPosition(250, 220);

        /* ===================== SLIDER RAM LABEL SELECTIONNED ===================== */
        this.memorySliderLabel = new LauncherLabel(root);
        this.memorySliderLabel.setOpacity(1.0);
        this.memorySliderLabel.setFont(FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", 16F));
        this.memorySliderLabel.setStyle("-fx-text-fill: white;");
        this.memorySliderLabel.setSize(370, 30);
        this.memorySliderLabel.setPosition(540, 220);

        /* ===================== SLIDER RAM ===================== */
        this.memorySlider = new JFXSlider();
        this.memorySlider.setStyle(
                "    -jfx-default-thumb: #FF0000;\r\n" + "    -jfx-default-track: #212121; -fx-pref-height: 10px;");
        this.memorySlider.setMin(1);
        this.memorySlider.setMax(10);
        if (pane.getConfig().getValue(EnumConfig.RAM) != null) {
            double d = Double.parseDouble((String) pane.getConfig().getValue(EnumConfig.RAM));
            this.memorySlider.setValue(d);
        }
        this.memorySlider.setLayoutX(250);
        this.memorySlider.setLayoutY(260);
        this.memorySlider.setPrefWidth(395);
        this.memorySlider.setBlockIncrement(1);
        memorySlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
                memorySlider.setValue(Math.round(new_val.doubleValue()));
            }
        });
        this.memorySlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                memorySliderLabel.setText(newValue + "GB");
            }
        });
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                root.getChildren().add(memorySlider);
            }
        });

        this.memorySliderLabel.setText(this.memorySlider.getValue() + "Gb");

        /* ===================== MC VERSION LABEL ===================== */
        LauncherLabel versionListLabel = new LauncherLabel(root);
        versionListLabel.setText("Choose Version:");
        versionListLabel.setOpacity(1.0);
        versionListLabel.setFont(FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", 16F));
        versionListLabel.setStyle("-fx-text-fill: white;");
        versionListLabel.setSize(370, 30);
        versionListLabel.setPosition(250, 160);

        /* ===================== VM ARGUMENTS TEXTFIELD ===================== */
        this.vmArguments = new LauncherTextField(root);
        this.vmArguments.setText((String) pane.getConfig().getValue(EnumConfig.VM_ARGUMENTS));
        this.vmArguments.setSize(390, 20);
        this.vmArguments.setPosition(250, 425);

        /* ===================== CHECKBOX USE VM ARGUMENTS ===================== */
        this.useVMArguments = new JFXCheckBox();
        this.useVMArguments.setText("Use JVM Arguments");
        this.useVMArguments.setSelected((Boolean) pane.getConfig().getValue(EnumConfig.USE_VM_ARGUMENTS));
        this.useVMArguments.setOpacity(1.0);
        this.useVMArguments.setFont(FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", 14F));
        this.useVMArguments.setStyle("-fx-text-fill: white; -jfx-checked-color: RED; -jfx-unchecked-color: BLACK");
        this.useVMArguments.setLayoutX(250);
        this.useVMArguments.setLayoutY(395);
        this.useVMArguments.setOnAction(event -> vmArguments.setDisable(!useVMArguments.isSelected()));
        root.getChildren().add(useVMArguments);
        this.vmArguments.setDisable(!this.useVMArguments.isSelected());

        /* ===================== AUTO LOGIN CHECK BOX ===================== */
        this.autoLogin = new JFXCheckBox();
        this.autoLogin.setText("Auto Connect");
        this.autoLogin.setSelected((Boolean) pane.getConfig().getValue(EnumConfig.AUTOLOGIN));
        this.autoLogin.setFont(FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", 14F));
        this.autoLogin.setStyle("-fx-text-fill: white; -jfx-checked-color: RED; -jfx-unchecked-color: BLACK");
        this.autoLogin.setLayoutX(250);
        this.autoLogin.setLayoutY(335);
        root.getChildren().add(autoLogin);

        /* ===================== CONNECT AUTO SERVER CHECK BOX ===================== */
        this.connect = new JFXCheckBox();
        this.connect.setText("Auto connect to the Sloppy SMP!");
        this.connect.setSelected((Boolean) pane.getConfig().getValue(EnumConfig.USE_CONNECT));
        this.connect.setFont(FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", 14F));
        this.connect.setStyle("-fx-text-fill: white; -jfx-checked-color: RED; -jfx-unchecked-color: BLACK");
        this.connect.setLayoutX(250);
        this.connect.setLayoutY(465);
        this.connect.setOnAction(event -> {
        	pane.getConfig().updateValue("useConnect", connect.isSelected());
            if (connect.isSelected()) {
                engine.reg(App.GAME_CONNECT);
            }
        });
        root.getChildren().add(this.connect);   
        
        /* ===================== BOUTON D'OUVERTURE DU REPERTOIRE DU JEU  ===================== */
        JFXButton openGameDirButton = new JFXButton("Open Game Directory");
        openGameDirButton.setStyle("-fx-background-color: rgba(53, 89, 119, 0.4); -fx-text-fill: white;");
        openGameDirButton.setFont(FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", 16F));
        openGameDirButton.setLayoutX(60); // Ajustez la position en X
        openGameDirButton.setLayoutY(550); // Ajustez la position en Y
        openGameDirButton.setOnAction(event -> openGameDirectory());
        root.getChildren().add(openGameDirButton);

        /* ===================== BOUTON DE VALIDATION ===================== */
        JFXButton saveButton = new JFXButton("Validate");
        saveButton.setStyle("-fx-background-color: rgba(53, 89, 119, 0.4); -fx-text-fill: white;");
        saveButton.setFont(FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", 16F));
        saveButton.setLayoutX(740);
        saveButton.setLayoutY(550);
        saveButton.setOnAction(event -> {
            HashMap<String, String> configMap = new HashMap<String, String>();
            configMap.put("allocatedram", String.valueOf(memorySlider.getValue()));
            configMap.put("autologin", "" + autoLogin.isSelected());
            configMap.put("usevmarguments", "" + useVMArguments.isSelected());
            configMap.put("vmarguments", "" + vmArguments.getText());
            configMap.put(EnumConfig.USE_CONNECT.getOption(), "" + connect.isSelected());
            pane.getConfig().updateValues(configMap);
            engine.reg(GameMemory.getMemory(Double.parseDouble((String) pane.getConfig().getValue(EnumConfig.RAM))));
            engine.reg(GameSize.getWindowSize(Integer.parseInt((String) pane.getConfig().getValue(EnumConfig.GAME_SIZE))));
            GameLinks links = new GameLinks("http://143.47.253.158/forge", "/1.20.1.json");
            engine.reg(links);
            Utils.regGameStyle(engine,pane.getConfig());
            final ZoomOutDown animation = new ZoomOutDown(root);
            animation.setOnFinished(actionEvent -> {
                Stage stage = (Stage) ((JFXButton) event.getSource()).getScene().getWindow();
                stage.close();
            });
            animation.setResetOnFinished(true);
            animation.play();
        });
        root.getChildren().add(saveButton);
    }
    
    private void openGameDirectory() {
        String os = System.getProperty("os.name").toLowerCase();
        Path gameDirectory;

        if (os.contains("win")) {
            gameDirectory = Paths.get(System.getenv("APPDATA"), ".sloppy", "bin", "game");
        } else if (os.contains("mac")) {
            gameDirectory = Paths.get(System.getProperty("user.home"), "Library", "Application Support", ".sloppy", "bin", "game");
        } else {
            gameDirectory = Paths.get(System.getProperty("user.home"), ".sloppy", "bin", "game");
        }

        try {
            Desktop.getDesktop().open(gameDirectory.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
