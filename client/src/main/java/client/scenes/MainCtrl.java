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

    private QuoteOverviewCtrl overviewCtrl;
    private Scene overview;

    private AddQuoteCtrl addCtrl;
    private Scene add;

    // Main Screen Scene fields
    private MainScreenCtrl mainScrCtrl; // The new controller
    private Scene mainScreen; // The new scene

    /**
     * Creates central control of stages.
     *
     * @param primaryStage the base stage
     * @param overview     stage showing all the quotes
     * @param add          stage showing the UI for adding a new quote
     */
    public void initialize(Stage primaryStage, Pair<QuoteOverviewCtrl, Parent> overview,
                           Pair<AddQuoteCtrl, Parent> add,
                           Pair<MainScreenCtrl, Parent> mainScreen) {
        this.primaryStage = primaryStage;
        this.overviewCtrl = overview.getKey();
        this.overview = new Scene(overview.getValue());

        this.addCtrl = add.getKey();
        this.add = new Scene(add.getValue());

        this.mainScrCtrl = mainScreen.getKey();
        this.mainScreen = new Scene(mainScreen.getValue());

        showMainScreen();
        primaryStage.show();
    }

    /**
     * refreshes and shows the overview stage, sets window title.
     */
    public void showOverview() {
        primaryStage.setTitle("Quotes: Overview");
        primaryStage.setScene(overview);
        overviewCtrl.refresh();
    }

    /**
     * shows add stage, sets window title and enables user input.
     */
    public void showAdd() {
        primaryStage.setTitle("Quotes: Adding Quote");
        primaryStage.setScene(add);
        add.setOnKeyPressed(e -> addCtrl.keyPressed(e));
    }

    /**
     * shows the main screen and sets window title.
     */
    public void showMainScreen() {
        primaryStage.setTitle("FoodPal: Recipe Organizer");
        primaryStage.setScene(mainScreen);
    }
}