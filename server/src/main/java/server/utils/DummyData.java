package server.utils;

import commons.Recipe;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for creating initial data for demo, testing purposes.
 */
public class DummyData {

    /**
     * Get recipes to use as content.
     *
     * @return a list of recipes
     */
    public static List<Recipe> getDefaultRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(
              new Recipe("Test Recipe 1", "Indescribable", List.of(), List.of()));
        recipes.add(
              new Recipe("Test Recipe 3", "A not very so long of a description.",
                    List.of(), List.of()));
        return recipes;
    }
}
