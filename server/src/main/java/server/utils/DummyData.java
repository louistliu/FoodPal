package server.utils;

import commons.Ingredient;
import commons.Recipe;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for creating initial data for demo, testing purposes.
 */
public class DummyData {

    /**
     * Get ingredients to use as content.
     *
     * @return a list of ingredients
     */
    public static List<Ingredient> getDefaultIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Otto"));
        ingredients.add(new Ingredient("Andy"));
        return ingredients;
    }

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
