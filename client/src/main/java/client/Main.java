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

import static com.google.inject.Guice.createInjector;

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

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    public static void main(String[] args) throws URISyntaxException, IOException {
        launch(args); // Pass args so we can read them in start()
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        var params = getParameters().getNamed();
        String configPath = params.getOrDefault("cfg", "config.json");
        File configFile = new File(configPath);
        // Load configuration (or create defaults)
        Config config = loadConfig(configFile);
        // Save configuration when the application closes
        primaryStage.setOnCloseRequest(e -> saveConfig(configFile, config));

        // Create Injector with configuration
        Injector injector = Guice.createInjector(new MyModule(config));
        // Create MyFXML instance locally
        var fxml = new MyFXML(injector);
        var serverUtils = injector.getInstance(ServerUtils.class);
        if (!serverUtils.isServerAvailable()) {
            var msg = "Server needs to be started before the client,"
                    + " but it does not seem to be available. Shutting down.";
            System.err.println(msg);
            return;
        }
        // var serverSockets = INJECTOR.getInstance(ServerSockets.class);

        var overview = FXML.load(QuoteOverviewCtrl.class, "client", "scenes", "QuoteOverview.fxml");
        var add = FXML.load(AddQuoteCtrl.class, "client", "scenes", "AddQuote.fxml");
        var mainScreen = FXML.load(MainScreenCtrl.class, "client", "scenes", "MainScreen.fxml");

        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);

        // Pass the mainScreen Pair to initialize
        mainCtrl.initialize(primaryStage, overview, add, mainScreen);
    }

    public static MyFXML getFxml() {
        return FXML;
    }

    // Load config using Jackson
    private Config loadConfig(File file) {
        var mapper = new ObjectMapper();
        try {
            if (file.exists()) {
                return mapper.readValue(file, Config.class);
            }
        } catch (IOException e) {
            System.err.println("Could not load config, using defaults.");
            e.printStackTrace();
        }
        return new Config();
    }

    // Save config using Jackson + Commons IO
    private void saveConfig(File file, Config config) {
        var mapper = new ObjectMapper();
        try {
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(config);
            FileUtils.writeStringToFile(file, json, "UTF-8");
        } catch (IOException e) {
            System.err.println("Could not save config.");
            e.printStackTrace();
        }
    }
}
