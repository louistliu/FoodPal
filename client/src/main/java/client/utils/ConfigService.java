package client.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.io.FileUtils;

/**
 * Helper service for loading and saving the client configuration.
 */
public final class ConfigService {

    private static Config config;
    private static File configFile;

    private ConfigService() {
    }

    /** Get the loaded config.
     *
     * @return The loaded config
     */
    public static Config getConfig() {
        return config;
    }

    /**
     * Load configuration from the given file using Jackson. If loading
     * fails the method returns a new Config with default values.
     *
     * @param args The arguments to parse
     * @return loaded Config or defaults
     */
    public static Config loadConfig(String[] args) {
        System.out.println("Program arguments: " + Arrays.toString(args));
        String configPath = resolveConfigPath(args);
        configFile = new File(configPath);
        config = new Config();
        var mapper = new ObjectMapper();
        try {
            if (configFile.exists()) {
                config =  mapper.readValue(configFile, Config.class);
                System.out.println("Config file found!");
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
            System.err.println("Could not save config:  " + e.getMessage());
        }
    }

    /**
     * Persist the given config to the configured config file. Public helper
     * for UI/controllers. If the configured file is not known this method
     * falls back to config.json in the working directory.
     */
    public static void persistConfig() {
        if (configFile == null) {
            // fallback to working-directory config
            System.out.println("No config path provided, writing to project root!");
            saveConfig(new File("config.json"), config);
        } else {
            saveConfig(configFile, config);
        }
    }
}
