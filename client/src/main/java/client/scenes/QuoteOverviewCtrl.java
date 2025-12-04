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

import client.utils.ResponseHandler;
import client.utils.ServerSockets;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Ingredient;
import commons.Quote;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * Scene handler form for the overview of all quotes.
 */
public class QuoteOverviewCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final ServerSockets socketUtils;

    private ObservableList<Quote> data;

    @FXML
    private TableView<Quote> table;
    @FXML
    private TableColumn<Quote, String> colFirstName;
    @FXML
    private TableColumn<Quote, String> colLastName;
    @FXML
    private TableColumn<Quote, String> colQuote;

    /**
     * Creates a new class instance and injects dependencies to the constructor and fields.
     *
     * @param server   server client communication utility
     * @param mainCtrl main scene controller responsible for stage switching
     */
    @Inject
    public QuoteOverviewCtrl(ServerUtils server, MainCtrl mainCtrl, ServerSockets socketUtils) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.socketUtils = socketUtils;
        socketDemo();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colFirstName.setCellValueFactory(
              q -> new SimpleStringProperty(q.getValue().person.firstName));
        colLastName.setCellValueFactory(
              q -> new SimpleStringProperty(q.getValue().person.lastName));
        colQuote.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().quote));
    }

    /**
     * Switches the scene to the {@link AddQuoteCtrl} via the main scene controller.
     */
    public void addQuote() {
        mainCtrl.showAdd();
    }

    /**
     * Fetches all quotes from the server and refreshes the table with them.
     */
    public void refresh() {
        var quotes = server.getQuotes();
        data = FXCollections.observableList(quotes);
        table.setItems(data);
    }

    /**
     * Show a demo, how current socket implementation works.
     */
    private void socketDemo() {
        ResponseHandler<Ingredient> ingredientResponseHandler = new ResponseHandler<>(
              System.out::println) {
        };
        ResponseHandler<Throwable> exceptionHandler = new ResponseHandler<>(
              System.out::println) {};
        ResponseHandler<List<Ingredient>> ingredientProcess = new ResponseHandler<>(
              x -> x.forEach(System.out::println)) {
        };
        socketUtils.subscribe(
              ServerSockets.setDestination("/topic/ingredients/create"),
              ingredientResponseHandler);
        socketUtils.subscribe(
              ServerSockets.setDestination("/user/queue/errors"), exceptionHandler);
        socketUtils.subscribe(
              ServerSockets.setDestination("/app/ingredients/fetch"), ingredientProcess);

        socketUtils.addIngredient(new Ingredient("test"));
    }
}