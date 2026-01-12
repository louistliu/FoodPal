package client.utils;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import commons.Recipe;
import org.springframework.messaging.simp.stomp.StompHeaders;

/**
 * Utility class for interfacing with recipes explicitly.
 */
@Singleton
public class ServerRecipeUtils {

    @Inject
    private ServerSockets sockets;

    /**
     * Adds a new recipe on the server if it does not already exist,
     * otherwise results in an error.
     *
     * @param recipe recipe to add
     */
    public void addRecipe(Recipe recipe) {
        StompHeaders headers = ServerSockets.setDestination("/app/recipes/create");
        sockets.send(headers, recipe);
    }

    /**
     * Adds a new recipe on the server if it does not already exist,
     * otherwise results in an error.
     *
     * @param recipe recipe to add
     */
    public void updateRecipe(Recipe recipe) {
        StompHeaders headers = ServerSockets.setDestination("/app/recipes/update");
        sockets.send(headers, recipe);
    }

    /**
     * Fetches all recipes.
     */
    public void getRecipes() {
        sockets.get("/app/recipes/fetch");
    }

}
