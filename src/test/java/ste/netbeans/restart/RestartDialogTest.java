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

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.BDDAssertions.then;

@ExtendWith(ApplicationExtension.class)
public class RestartDialogTest {

    private RestartDialog dialog;
    private AtomicBoolean restartTriggered;

    @Start
    public void start(Stage stage) {
        restartTriggered = new AtomicBoolean(false);
        // Use 1 second for faster, more reliable tests
        dialog = new RestartDialog(() -> restartTriggered.set(true), 1);

        Scene scene = new Scene(dialog, 400, 200);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void dialog_shows_message_and_countdown(FxRobot robot) {
        then((Object) robot.lookup("#dialog").query()).isNotNull();

        Label messageLabel = robot.lookup("#messageLabel").queryAs(Label.class);
        then(messageLabel).isNotNull();
        then(messageLabel.getText()).isEqualTo("NetBeans will restart in a few seconds:");

        Label countdownLabel = robot.lookup("#countdownLabel").queryAs(Label.class);
        then(countdownLabel).isNotNull();
        // It starts at 1
        then(countdownLabel.getText()).isEqualTo("1");

        then((Object) robot.lookup("#actions").query()).isNotNull();
        then((Object) robot.lookup("#cancelButton").query()).isNotNull();
    }

    @Test
    public void cancel_button_closes_dialog_and_no_restart(FxRobot robot) {
        robot.clickOn("#cancelButton");

        then(dialog.getScene().getWindow().isShowing()).isFalse();
        then(restartTriggered.get()).isFalse();
    }

    @Test
    public void restart_now_button_triggers_restart_immediately(FxRobot robot) {
        robot.clickOn("#actions");

        then(dialog.getScene().getWindow().isShowing()).isFalse();
        then(restartTriggered.get()).isTrue();
    }

    @Test
    public void restart_now_and_dont_ask_again_via_menu_triggers_restart_and_sets_preference(FxRobot robot) {
        // We need to show the menu first
        robot.clickOn("#actions .arrow-button");
        // Then click the menu item
        robot.clickOn("#dontAskAgainMenuItem");

        then(dialog.getScene().getWindow().isShowing()).isFalse();
        then(restartTriggered.get()).isTrue();
        
        // Verify preference
        then(org.openide.util.NbPreferences.forModule(RestartDialog.class)
            .get(RestartDialog.PREFERENCE_KEY, "")).isEqualTo(RestartDialog.PREFERENCE_DO_NOT_CONFIRM);
    }
    
    @Test
    public void countdown_ticks_down_and_restarts(FxRobot robot) throws InterruptedException {
        // Wait for the countdown to trigger restart. The dialog was started with 1 second,
        // so I need to wait for a bit more than 1 second.
        Thread.sleep(1500);

        then(dialog.getScene().getWindow().isShowing()).isFalse();
        then(restartTriggered.get()).isTrue();
    }

    @Test
    public void esc_key_closes_dialog_like_cancel(FxRobot robot) {
        robot.press(KeyCode.ESCAPE);
        robot.release(KeyCode.ESCAPE);

        then(dialog.getScene().getWindow().isShowing()).isFalse();
        then(restartTriggered.get()).isFalse();
    }

    @Test
    public void cancel_button_closes_dialog_and_waits_to_ensure_no_delayed_restart(FxRobot robot) throws InterruptedException {
        robot.clickOn("#cancelButton");

        // Wait to make sure no timer kicks in
        Thread.sleep(2000);

        then(dialog.getScene().getWindow().isShowing()).isFalse();
        then(restartTriggered.get()).isFalse();
    }
}
