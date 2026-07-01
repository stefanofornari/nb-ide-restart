/*
 * Copyright 2025 Stefano Fornari
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ste.netbeans.restart;

import atlantafx.base.theme.NordLight;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 *
 */
public class RestartDialog extends VBox {

    private static final int DEFAULT_COUNTDOWN_SECONDS = 30;
    public static final String PREFERENCE_KEY = "confirm";
    public static final String PREFERENCE_DO_NOT_CONFIRM = "no";

    private final Runnable restartCallback;

    private int secondsLeft;
    private final Timeline countdownTimer;

    @FXML
    private Label messageLabel;

    @FXML
    private Label countdownLabel;

    @FXML
    private SplitMenuButton actions;

    @FXML
    private Button cancelButton;

    @FXML
    private javafx.scene.image.ImageView warningIcon;

    /**
     * Constructor with default 30 seconds countdown and modality.
     *
     * @param restartCallback called when restart should be triggered
     */
    public RestartDialog(final Runnable restartCallback) {
        this(restartCallback, DEFAULT_COUNTDOWN_SECONDS);
    }

    /**
     * Constructor with configurable countdown duration.
     *
     * @param restartCallback called when restart should be triggered
     * @param countdownSeconds initial countdown seconds
     */
    public RestartDialog(Runnable restartCallback, int countdownSeconds) {
        this.restartCallback = restartCallback;
        this.secondsLeft = countdownSeconds;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("RestartDialogFX.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (java.io.IOException exception) {
            throw new RuntimeException(exception);
        }

        this.setFocusTraversable(true);
        addEscKeyHandler();
        this.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                this.requestFocus();
            }
        });

        getStylesheets().addAll(
            new NordLight().getUserAgentStylesheet(),
            "ste/netbeans/restart/RestartDialogFX.css"
        );

        countdownLabel.setText(String.valueOf(secondsLeft));

        this.countdownTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> tick()));
        this.countdownTimer.setCycleCount(Timeline.INDEFINITE);
        this.countdownTimer.play();
    }

    private void tick() {
        secondsLeft--;
        if (secondsLeft > 0) {
            countdownLabel.setText(String.valueOf(secondsLeft));
        } else {
            handleRestart();
        }
    }

    @FXML
    private void handleRestart() {
        Platform.runLater(() -> {
            countdownTimer.stop();
            closeWindow();
        });
                
        if (restartCallback != null) {
            Platform.setImplicitExit(false);
            Platform.runLater(() -> restartCallback.run());
        }
    }

    @FXML
    private void handleRestartDoNotAskAgain() {
        org.openide.util.NbPreferences.forModule(RestartDialog.class).put(PREFERENCE_KEY, PREFERENCE_DO_NOT_CONFIRM);
        try {
            org.openide.util.NbPreferences.forModule(RestartDialog.class).flush();
        } catch (java.util.prefs.BackingStoreException e) {
            // ignore
        }
        handleRestart();
    }

    @FXML
    private void handleCancel() {
        countdownTimer.stop();
        closeWindow();
    }

    // Add ESC key handler after loading FXML
    private void addEscKeyHandler() {
        this.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                handleCancel();
            }
        });
    }

    private void closeWindow() {
        if (getScene() != null && getScene().getWindow() != null) {
            getScene().getWindow().hide();
        }
    }
}
