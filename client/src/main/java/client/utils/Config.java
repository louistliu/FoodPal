package client.utils;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Application configuration persisted to disk using jackson.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Singleton
public class Config {
    // Default values
    private String serverUrl = "ws://localhost:8080/";
    private String language = "English";
    private List<Long> favoriteRecipeIds = new ArrayList<>();

    // empty constructor for jackson & default/empty configs
    public Config() {
    }

    /**
     * Returns the configured WebSocket server URL.
     *
     * @return server URL string
     */
    public String getServerUrl() {
        return serverUrl;
    }

    /**
     * Set the WebSocket server URL.
     *
     * @param serverUrl server URL string
     */
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    /**
     * Returns the configured language used by the UI.
     *
     * @return language name
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Set the UI language name.
     *
     * @param language language name
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Returns the list of favorite recipe identifiers.
     *
     * @return mutable list of favorite recipe ids
     */
    public List<Long> getFavoriteRecipeIds() {
        return favoriteRecipeIds;
    }

    /**
     * Replace the favorite recipe id list.
     *
     * @param favoriteRecipeIds new list of favorite ids
     */
    public void setFavoriteRecipeIds(List<Long> favoriteRecipeIds) {
        this.favoriteRecipeIds = favoriteRecipeIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Config config = (Config) o;
        return Objects.equals(serverUrl, config.serverUrl)
                && Objects.equals(language, config.language)
                && Objects.equals(favoriteRecipeIds, config.favoriteRecipeIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverUrl, language, favoriteRecipeIds);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

}