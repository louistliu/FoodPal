package client.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigTest {
    private ObjectMapper mapper;
    private Config sample;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        sample = new Config();
        sample.setServerUrl("ws://localhost:8080");
        sample.setLanguage("German");
        sample.getFavoriteRecipeIds().add(42L);
    }

    @Test
    void defaultServerUrl() {
        Config cfg = new Config();
        assertEquals("ws://localhost:8080", cfg.getServerUrl());
    }

    @Test
    void defaultLanguageEnglish() {
        Config cfg = new Config();
        assertEquals("English", cfg.getLanguage());
    }

    @Test
    void defaultFavoritesEmpty() {
        Config cfg = new Config();
        assertNotNull(cfg.getFavoriteRecipeIds());
        assertTrue(cfg.getFavoriteRecipeIds().isEmpty());
    }

    @Test
    void jacksonSerializationTest() throws Exception {
        String json = mapper.writeValueAsString(sample);
        Config b = mapper.readValue(json, Config.class);

        assertEquals(sample, b);
    }

    @Test
    void hashCodeTest() throws Exception {
        String json = mapper.writeValueAsString(sample);
        Config b = mapper.readValue(json, Config.class);

        assertEquals(sample.hashCode(), b.hashCode());
    }

    @Test
    void setServerUrlTest() {
        Config cfg = new Config();
        cfg.setServerUrl("ws://changed:1234");
        assertEquals("ws://changed:1234", cfg.getServerUrl());
    }

    @Test
    void setLanguageUpdatesTest() {
        Config cfg = new Config();
        cfg.setLanguage("German");
        assertEquals("German", cfg.getLanguage());
    }

    @Test
    void setFavoriteRecipeIdsTest() {
        Config cfg = new Config();
        List<Long> favs = new ArrayList<>();
        favs.add(7L);
        cfg.setFavoriteRecipeIds(favs);
        assertEquals(favs, cfg.getFavoriteRecipeIds());
    }

    @Test
    void equalsFalse() {
        Config other = new Config();
        other.setServerUrl("ws://different");
        assertNotEquals(sample, other);
    }

    @Test
    void equalsNull() {
        assertFalse(sample.equals(null));
    }
}