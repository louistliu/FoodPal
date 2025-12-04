package client.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Config {
    // Fields with default values
    public String serverURL = "ws://localhost:8080";
    public String language = "English";

    // Favourites as ID's
    public List<Long> favouriteRecipes = new ArrayList<>();

    // Empty constructor for Jackson
    public Config() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Config config = (Config) o;
        return Objects.equals(serverURL, config.serverURL) && Objects.equals(language, config.language) && Objects.equals(favouriteRecipes, config.favouriteRecipes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverURL, language, favouriteRecipes);
    }
}