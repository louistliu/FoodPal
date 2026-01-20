package client.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Tests config service.
 */
public class ConfigServiceTest {

    private Path tempDir;
    private ConfigService configService;

    @AfterEach
    void cleanup() throws IOException {
        if (tempDir != null && Files.exists(tempDir)) {
            try (var stream = Files.walk(tempDir)) {
                stream.map(Path::toFile)
                      .forEach(f -> {
                          if (!f.delete()) {
                              f.deleteOnExit();
                          }
                      });
            }
        }
        configService = null;
    }

    @Test
    void loadDefaultsWhenFileMissing() throws IOException {
        tempDir = Files.createTempDirectory("cfgtest1");
        String cfgPath = tempDir.resolve("nonexistent-config.json").toString();
        configService = new ConfigService(new com.fasterxml.jackson.databind.ObjectMapper());
        Config cfg = configService.loadConfig(new String[] {"-cfg", cfgPath});
        assertEquals(new Config(), cfg);
    }

    @Test
    void persistAndReloadConfig() throws IOException {
        tempDir = Files.createTempDirectory("cfgtest2");
        String cfgPath = tempDir.resolve("app-config.json").toString();
        configService = new ConfigService(new com.fasterxml.jackson.databind.ObjectMapper());
        // initial load (file missing) sets internal target file
        Config modified = configService.loadConfig(new String[] {"-cfg", cfgPath});
        // modify and persist
        modified.setServerUrl("ws://example:1234");
        modified.setLanguage("Dutch");
        modified.setFavoriteRecipeIds(Arrays.asList(1L, 2L, 3L));
        configService.persistConfig();
        // reload from the same path
        ConfigService configServiceReload = new ConfigService(
              new com.fasterxml.jackson.databind.ObjectMapper());
        Config reloaded = configServiceReload.loadConfig(new String[] {"-cfg", cfgPath});
        assertEquals(modified, reloaded);
    }
}
