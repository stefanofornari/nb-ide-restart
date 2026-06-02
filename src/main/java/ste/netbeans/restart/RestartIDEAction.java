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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.swing.SwingUtilities;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbPreferences;
import static ste.netbeans.restart.RestartDialog.PREFERENCE_DO_NOT_CONFIRM;
import static ste.netbeans.restart.RestartDialog.PREFERENCE_KEY;

/**
 * Action that restarts the NetBeans IDE.
 *
 * <p>Registered under <b>File -> Restart IDE</b> (after a separator at the end
 * of the menu) and bound to the <b>Ctrl+Alt+Backspace</b> keyboard shortcut.</p>
 *
 * <p>The restart is delegated to
 * {@link ste.netbeans.restart.LifecycleManagerHelper#restartIDE()}, which
 * locates the platform's {@code LifecycleManager} via the global Lookup and
 * calls its {@code markForRestart()} / {@code exit()} pair.</p>
 */
@ActionID(
    category = "File",
    id = "ste.netbeans.restart.RestartIDEAction"
)
@ActionRegistration(
    displayName = "#CTL_RestartIDEAction",
    iconBase = "ste/netbeans/restart/restart-16x16.png",
    surviveFocusChange = true
)
@ActionReferences({
    // File menu
    // Put Restart just after Exit (position 2200)
    @ActionReference(
        path = "Menu/File",
        position = 3000
    ),
    // Global keyboard shortcut
    @ActionReference(
        path = "Shortcuts",
        name = "CS-BACK_SPACE"          // Ctrl+Shift+Backspace
    )
})

public final class RestartIDEAction implements ActionListener {

    /**
     * Invoked when the user triggers the action (menu click or key shortcut).
     *
     * @param e the action event (not used)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            String preference = NbPreferences.forModule(RestartDialog.class).get(PREFERENCE_KEY, "");
            if (preference.equals(PREFERENCE_DO_NOT_CONFIRM)) {
                LifecycleManagerHelper.restartIDE();
            } else {
                // 2. Switch to the JavaFX Application Thread
                Platform.runLater(() -> {
                    RestartDialog dialog = new RestartDialog(() -> {
                        LifecycleManagerHelper.restartIDE();
                    });

                    Scene scene = new Scene(dialog);

                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setTitle("Restart");
                    stage.setScene(scene);

                    // 3. Show the dialog
                    stage.show();
                });
            }
        });
    }
}