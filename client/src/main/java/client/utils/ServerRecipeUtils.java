package client.utils;

import com.google.inject.Singleton;
import commons.Recipe;
import org.springframework.messaging.simp.stomp.StompHeaders;

/**
 * Utility class for interfacing with recipes explicitly.
 */
@Singleton
public class ServerRecipeUtils extends ServerSockets {

    /**
     * Adds a new recipe on the server if it does not already exist,
     * otherwise results in a error.
     *
     * @param recipe recipe to add
     */
    public void addRecipe(Recipe recipe) {
        StompHeaders headers = ServerSockets.setDestination("/app/recipes/create");
        super.send(headers, recipe);
    }

    /**
     * Fetches all recipes.
     */
    public void getRecipes() {
        super.get("/app/recipes/fetch");
    }
}
