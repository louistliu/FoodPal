package server.controllers;

import commons.Recipe;
import commons.RecipeIngredient;
import commons.sockets.PairTransport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import server.services.RecipeIngredientService;

/**
 * API endpoint responsible for handling {@link RecipeIngredient} manipulation.
 */
@Controller
public class RecipeIngredientController {

    @Autowired
    private RecipeIngredientService riService;

    /**
     * Create a new recipe ingredient. Clients should send a {@link commons.RecipeIngredient}
     * payload to the application destination (for example
     * {@code /app/recipe_ingredient/create}). On success the saved recipe ingredient object is
     * returned so subscribers can broadcast or process the new recipe ingredient.
     *
     * @param wrapper recipe and recipe ingredient that should belong to the recipe.
     * @return returns the recipe ingredient created and saved in the DB.
     * @throws Exception in case {@link commons.Ingredient} is already in the recipe.
     */
    @MessageMapping("/recipe_ingredient/create")
    public PairTransport<Recipe, RecipeIngredient> create(
          PairTransport<Recipe, RecipeIngredient> wrapper)
          throws Exception {
        return riService.create(wrapper.first(), wrapper.last());
    }

    /**
     * Delete an existing recipe ingredient. The provided {@link commons.RecipeIngredient} object
     * must contain a valid id.
     *
     * @param recipeIngredient recipe ingredient to delete.
     * @return returns deleted recipe ingredient.
     * @throws Exception if {@link RecipeIngredient} with id does not exist in DB.
     */
    @MessageMapping("/recipe_ingredient/delete")
    public RecipeIngredient delete(RecipeIngredient recipeIngredient) throws Exception {
        return riService.delete(recipeIngredient);
    }

    /**
     * Delete an existing recipe ingredient. The provided {@link commons.RecipeIngredient} object
     * must contain a valid id.
     *
     * @param recipeIngredient recipe ingredient to update
     * @return new instance of recipe ingredient saved on the server.
     * @throws Exception if {@link RecipeIngredient} with id does not exist.
     */
    @MessageMapping("/recipe_ingredient/update")
    public RecipeIngredient update(RecipeIngredient recipeIngredient) throws Exception {
        return riService.update(recipeIngredient);
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
        return riService.handleException(exception);
    }
}
