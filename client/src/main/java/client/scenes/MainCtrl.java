/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package client.scenes;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * Class handling inter-scene interaction.
 */
public class MainCtrl {

    private Stage primaryStage;

    // Main Screen Scene fields
    private MainScreenCtrl mainScrCtrl; // The new controller
    private Scene mainScreen; // The new scene
    private final LanguageController languageController = new LanguageController();

    /**
     * Creates central control of stages.
     *
     * @param primaryStage the base stage
     */
    public void initialize(Stage primaryStage, Pair<MainScreenCtrl, Parent> mainScreen) {
        this.primaryStage = primaryStage;

        this.mainScrCtrl = mainScreen.getKey();
        this.mainScreen = new Scene(mainScreen.getValue());

        showMainScreen();
        primaryStage.show();
    }

    public LanguageController getLanguageController() {
        return languageController;
    }

    /**
     * shows the main screen and sets window title.
     */
    public void showMainScreen() {
        if (primaryStage != null) {
            primaryStage.setTitle(languageController.get("app.title"));
        }
        primaryStage.setScene(mainScreen);
    }

}