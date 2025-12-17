package server.controllers;

import commons.Recipe;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import server.database.RecipeRepository;

/**
 * STOMP API endpoints responsible for creating, fetching and deleting
 * {@link commons.Recipe} entities. Methods are mapped to STOMP application
 * destinations; clients should send requests to the configured application
 * prefix (usually {@code /app}) and subscribe to broadcast topics (usually
 * {@code /topic}) or receive user-specific replies via {@code /user/queue}.
 */
@Controller
public class RecipeController {

    private final RecipeRepository recipeDB;

    @Autowired
    public RecipeController(RecipeRepository recipeDB) {
        this.recipeDB = recipeDB;
        recipeDB.save(new Recipe("Test Recipe 1", "Indescribable", List.of(), List.of()));
        recipeDB.save(new Recipe("Test Recipe 3", "A not very so long of a description.", List.of(),
              List.of()));
    }

    /**
     * Create a new recipe. Clients should send a {@link commons.Recipe}
     * payload to the application destination (for example
     * {@code /app/recipes/create}). On success the saved recipe object is
     * returned so subscribers can broadcast or process the new recipe.
     *
     * @param recipe the recipe object (validated) to create
     * @return the saved {@link commons.Recipe}
     * @throws Exception when processing fails
     */
    @MessageMapping("/recipes/create")
    @Valid
    public Recipe create(@Payload Recipe recipe) throws Exception {
        if (!recipeDB.findBy(recipe.getName()).isEmpty()) {
            throw new EntityExistsException(
                  "Recipe with name: " + recipe.getName() + " is already in the database");
        }
        return recipeDB.save(recipe);
    }

    /**
     * Return all recipes for a new subscription. Clients can subscribe to
     * the corresponding destination to receive the full list of recipes
     * (for example using a user-specific queue).
     *
     * @return list of all {@link commons.Recipe} in the database
     * @throws Exception when retrieval fails
     */
    @SubscribeMapping("/recipes/fetch")
    public List<Recipe> fetchIngredients() throws Exception {
        return recipeDB.findAll();
    }

    /**
     * Delete an existing recipe. The provided {@link commons.Recipe} object
     * must contain a valid id; if the recipe does not exist an
     * {@link jakarta.persistence.EntityNotFoundException} is thrown.
     *
     * @param recipe recipe (with id) to delete
     * @return the deleted {@link commons.Recipe}
     * @throws Exception when deletion fails or the recipe is not found
     */
    @MessageMapping("/recipes/delete")
    @Valid
    public Recipe delete(@Payload Recipe recipe) throws Exception {
        if (!recipeDB.existsById(recipe.getId())) {
            throw new EntityNotFoundException(
                  "No recipe with id " + recipe.getId() + " in the database");
        }
        recipeDB.deleteById(recipe.getId());
        return recipe;
    }

    /**
     * Delete an existing recipe. The provided {@link commons.Recipe} object
     * must contain a valid id; if the recipe does not exist an
     * {@link jakarta.persistence.EntityNotFoundException} is thrown.
     *
     * @param recipe recipe (with id) to update
     * @return the updated {@link commons.Recipe}
     * @throws Exception when update fails or the recipe is not found
     */
    @MessageMapping("/recipes/update")
    @Valid
    public Recipe update(@Payload Recipe recipe) throws Exception {
        if (!recipeDB.existsById(recipe.getId())) {
            throw new EntityNotFoundException(
                  "No recipe with id " + recipe.getId() + " in the database");
        }

        return recipeDB.save(recipe);
    }

    /**
     * Handle errors that occur, when processing requests.<br>
     * Subscribable on {@code /user/queue/errors}
     *
     * @param exception an exception, which occurred during the processing of a
     *                  request
     * @return returns the message of the exception to the individual client
     */
    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public Throwable handleException(Throwable exception) {
        return exception;
    }
}
