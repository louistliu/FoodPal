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

import client.scenes.AddQuoteCtrl;
import client.scenes.MainCtrl;
import client.scenes.MainScreenCtrl;
import client.scenes.QuoteOverviewCtrl;
import client.utils.Config;
import client.utils.ServerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

/**
 * Application entry point. Sets up configuration and dependency injection
 * before JavaFX starts, and provides access to the injector-backed FXML helper.
 */
public class Main extends Application {

    private static Injector INJECTOR;
    private static MyFXML FXML;
    private static File CONFIG_FILE;

    /**
     * Application entry. Loads configuration, initializes Guice injector
     * and starts the JavaFX runtime.
     *
     * @param args command-line arguments (supports -cfg <path>)
     * @throws URISyntaxException when config path resolution fails
     * @throws IOException if config file cannot be read
     */
    public static void main(String[] args) throws URISyntaxException, IOException {
        String configPath = resolveConfigPath(args);
        CONFIG_FILE = new File(configPath);
        Config config = loadConfig(CONFIG_FILE);
        INJECTOR = Guice.createInjector(new MyModule(config));
        launch(args);
    }

    /**
     * Resolve the path to the configuration file. Supports -cfg <path>.
     *
     * @param args command-line arguments
     * @return resolved config path or "config.json" when none provided
     */
    private static String resolveConfigPath(String[] args) {
        String configPath = "config.json";
        if (args == null) {
            return configPath;
        }
        for (int i = 0; i < args.length; i++) {
            if ("-cfg".equals(args[i]) && i + 1 < args.length) {
                return args[i + 1];
            }
        }
        return configPath;
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
        primaryStage.setOnCloseRequest(e -> saveConfig(CONFIG_FILE, INJECTOR.getInstance(Config.class)));

        var serverUtils = INJECTOR.getInstance(ServerUtils.class);
        if (!serverUtils.isServerAvailable()) {
            var msg = "Server needs to be started before the client," + " but it does not seem to be available. Shutting down.";
            System.err.println(msg);
            return;
        }

        var overview = FXML.load(QuoteOverviewCtrl.class, "client", "scenes", "QuoteOverview.fxml");
        var add = FXML.load(AddQuoteCtrl.class, "client", "scenes", "AddQuote.fxml");
        var mainScreen = FXML.load(MainScreenCtrl.class, "client", "scenes", "MainScreen.fxml");

        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);

        // Pass the mainScreen Pair to initialize
        mainCtrl.initialize(primaryStage, overview, add, mainScreen);
    }

    /**
     * Returns the shared MyFXML helper initialized during startup.
     *
     * @return MyFXML instance or null if called before startup
     */
    public static MyFXML getFxml() {
        return FXML;
    }

    /**
     * Load configuration from the given file using Jackson. If loading
     * fails the method returns a new Config with default values.
     *
     * @param file file to read from
     * @return loaded Config or defaults
     */
    private static Config loadConfig(File file) {
        var mapper = new ObjectMapper();
        try {
            if (file.exists()) {
                return mapper.readValue(file, Config.class);
            }
        } catch (IOException e) {
            System.err.println("WARNING: Could not load config file, check the path and the permissions. "
                  + "Using default values.");
        }
        return new Config();
    }

    /**
     * Save the given configuration to disk using Jackson and Commons IO.
     * Errors are logged to stderr.
     *
     * @param file destination file
     * @param config configuration to persist
     */
    private static void saveConfig(File file, Config config) {
        var mapper = new ObjectMapper();
        try {
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(config);
            FileUtils.writeStringToFile(file, json, "UTF-8");
        } catch (IOException e) {
            System.err.println("Could not save config.");
        }
    }

    /**
     * Persist the given config to the configured config file. Public helper
     * for UI/controllers. If the configured file is not known this method
     * falls back to config.json in the working directory.
     *
     * @param config configuration to persist
     */
    public static void persistConfig(Config config) {
        if (CONFIG_FILE == null) {
            // fallback to working-directory config
            saveConfig(new File("config.json"), config);
        } else {
            saveConfig(CONFIG_FILE, config);
        }
    }
}
