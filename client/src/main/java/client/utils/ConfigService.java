package client.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 * Helper service for loading and saving the client configuration.
 */
@Singleton
public final class ConfigService {
    /**
     * Returns the search history from the config.
     *
     * @return list of search queries
     */
    public List<String> getSearchHistory() {
        return config != null ? config.getSearchHistory() : new ArrayList<>();
    }

    /**
     * Adds a search query to the search history and persists config.
     * Keeps only the last 10 entries, no duplicates in a row.
     *
     * @param query the search query to add
     */
    public void addSearchHistory(String query) {
        if (config == null || query == null || query.isBlank()) {
            return;
        }
        List<String> history = config.getSearchHistory();
        if (!history.isEmpty() && history.getLast().equalsIgnoreCase(query)) {
            return;
        }
        history.add(query);
        if (history.size() > 10) {
            history.removeFirst();
        }
        config.setSearchHistory(history);
        persistConfig();
    }

    /**
     * Clears the search history and persists config.
     */
    public void clearSearchHistory() {
        if (config == null) {
            return;
        }
        config.setSearchHistory(new ArrayList<>());
        persistConfig();
    }

    private final ObjectMapper mapper;
    private Config config;
    private File configFile;

    @Inject
    public ConfigService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Resolve the path to the configuration file. Supports -cfg <path>.
     * Also supports -Dcfg <path> for the maven javafx plugin.
     *
     * @param args command-line arguments
     * @return resolved config path or "config.json" when none provided
     */
    private static String resolveConfigPath(String[] args) {
        String configPath = "config.json";
        for (int i = 0; i < args.length; i++) {
            if ("-cfg".equals(args[i]) && i + 1 < args.length) {
                return args[i + 1];
            }
        }
        System.out.println("No config file path found, using root/config.json");
        return configPath;
    }

    /**
     * Get the loaded config.
     *
     * @return The loaded config
     */
    public Config getConfig() {
        return config;
    }

    /**
     * Set config, mainly for testing.
     *
     * @param config config to set
     */
    public void setConfig(Config config) {
        this.config = config;
    }

    /**
     * Load configuration from the given file using Jackson. If loading
     * fails the method returns a new Config with default values.
     *
     * @param args The arguments to parse
     * @return loaded Config or defaults
     */
    public Config loadConfig(String[] args) {
        System.out.println("Program arguments: " + Arrays.toString(args));
        String configPath = resolveConfigPath(args);
        configFile = new File(configPath);
        config = new Config();
        var mapper = new ObjectMapper();
        try {
            if (configFile.exists()) {
                config = mapper.readValue(configFile, Config.class);
                System.out.println("Config file found!");
                System.out.println(config);
                return config;
            }
        } catch (IOException e) {
            System.err.println("WARNING: Could not load config file, "
                  + "check the path and the permissions. "
                  + "Using default values.");
            System.out.println(e.getMessage());
        }
        System.out.println("Using default config values");
        return config;
    }

    /**
     * Save the given configuration to disk using Jackson and Commons IO.
     * Errors are logged to stderr.
     *
     * @param file   destination file
     * @param config configuration to persist
     */
    private void saveConfig(File file, Config config) {
        var mapper = new ObjectMapper();
        try {
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(config);
            FileUtils.writeStringToFile(file, json, "UTF-8");
        } catch (IOException e) {
            System.err.println("Could not save config:  " + e.getMessage());
        }
    }

    /**
     * Persist the given config to the configured config file. Public helper
     * for UI/controllers. If the configured file is not known this method
     * falls back to config.json in the working directory.
     */
    public void persistConfig() {
        if (configFile == null) {
            // fallback to working-directory config
            System.out.println("No config path provided, writing to project root!");
            saveConfig(new File("config.json"), config);
        } else {
            saveConfig(configFile, config);
        }
    }
}
