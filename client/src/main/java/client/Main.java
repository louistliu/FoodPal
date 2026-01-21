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

package client;

import client.scenes.MainCtrl;
import client.scenes.MainScreenCtrl;
import client.utils.Config;
import client.utils.ConfigService;
import client.utils.communication.ServerSockets;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.IOException;
import java.net.URISyntaxException;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Application entry point. Sets up configuration and dependency injection
 * before JavaFX starts, and provides access to the injector-backed FXML helper.
 */
public class Main extends Application {

    private static Injector INJECTOR;
    private static MyFXML FXML;
    private static Config config;

    /**
     * Application entry. Loads configuration, initializes Guice injector
     * and starts the JavaFX runtime.
     *
     * @param args command-line arguments (supports -cfg <path>)
     * @throws URISyntaxException when config path resolution fails
     * @throws IOException        if config file cannot be read
     */
    public static void main(String[] args) throws URISyntaxException, IOException {
        config = ConfigService.loadConfig(args);
        INJECTOR = Guice.createInjector(new MyModule(config));
        launch(args);
    }

    /**
     * JavaFX application start hook. Initializes the FXML helper, verifies
     * the server is available, wires controllers and sets up config saving
     * on close.
     *
     * @param primaryStage primary JavaFX stage
     * @throws Exception on FXML loading or initialization problems
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize FXML helper with the configured injector
        FXML = new MyFXML(INJECTOR);

        // Ensure config is saved on exit
        primaryStage.setOnCloseRequest(e -> ConfigService.persistConfig());

        var serverSockets = INJECTOR.getInstance(ServerSockets.class);
        if (!serverSockets.isServerAvailable()) {
            var msg = "Server needs to be started before the client,"
                    + " but it does not seem to be available. Shutting down.";
            System.err.println(msg);
            System.exit(1);
            return;
        }

        var mainScreen = FXML.load(MainScreenCtrl.class, "client", "scenes", "MainScreen.fxml");

        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);

        // Pass the mainScreen Pair to initialize
        mainCtrl.initialize(primaryStage, mainScreen);
    }

    /**
     * Returns the shared MyFXML helper initialized during startup.
     *
     * @return MyFXML instance or null if called before startup
     */
    public static MyFXML getFxml() {
        return FXML;
    }

}
