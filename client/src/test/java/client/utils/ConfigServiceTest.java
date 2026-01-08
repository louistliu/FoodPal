package client.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class ConfigServiceTest {

    private Path tempDir;

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
    }

    @Test
    void loadDefaultsWhenFileMissing() throws IOException {
        tempDir = Files.createTempDirectory("cfgtest1");
        String cfgPath = tempDir.resolve("nonexistent-config.json").toString();

        Config cfg = ConfigService.loadConfig(new String[]{"-cfg", cfgPath});

        assertEquals(new Config(), cfg);
    }

    @Test
    void persistAndReloadConfig() throws IOException {
        tempDir = Files.createTempDirectory("cfgtest2");
        String cfgPath = tempDir.resolve("app-config.json").toString();

        // initial load (file missing) sets internal target file
        Config modified = ConfigService.loadConfig(new String[]{"-cfg", cfgPath});

        // modify and persist
        modified.setServerUrl("ws://example:1234");
        modified.setLanguage("Dutch");
        modified.setFavoriteRecipeIds(Arrays.asList(1L, 2L, 3L));

        ConfigService.persistConfig();

        // reload from the same path
        Config reloaded = ConfigService.loadConfig(new String[]{"-cfg", cfgPath});

        assertEquals(modified, reloaded);
    }
}
