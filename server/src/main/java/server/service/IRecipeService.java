package server.service;

import commons.Recipe;
import java.util.List;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * Service for handling all business logic of Recipes.
 */
public interface IRecipeService {

    /**
     * Create a new recipe. On success the saved recipe object is
     * returned.
     *
     * @param recipe the recipe object (validated) to create.
     * @return the saved {@link commons.Recipe}.
     * @throws Exception when processing fails.
     */
    Recipe create(@Payload Recipe recipe) throws Exception;

    /**
     * Update an existing recipe. If the recipe does not exist an
     * {@link jakarta.persistence.EntityNotFoundException} is thrown.
     *
     * @param recipe recipe to update
     * @return the updated {@link commons.Recipe}
     * @throws Exception when update fails or the recipe is not found
     */
    Recipe update(@Payload Recipe recipe) throws Exception;

    /**
     * Deletes an existing recipe from the db. If the recipe does not exist an
     * {@link jakarta.persistence.EntityNotFoundException} is thrown.
     *
     * @param recipe recipe to delete
     * @return the deleted {@link commons.Recipe}
     * @throws Exception when deletion fails or the recipe is not found
     */
    Recipe delete(@Payload Recipe recipe) throws Exception;

    /**
     * Get all recipes in the database.
     *
     * @return list of all {@link commons.Recipe} in the database
     * @throws Exception when retrieval fails
     */
    List<Recipe> fetchIngredients() throws Exception;

    /**
     * Handle errors that occur, when processing requests.
     *
     * @param exception an exception, which occurred during the processing of a
     *                  request
     * @return returns the message of the exception to the individual client
     */
    Throwable handleException(Throwable exception);
}
