package server.service;

import commons.Ingredient;
import java.util.List;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * Implements ingredient business logic.
 */
public interface IIngredientService {

    /**
     * Create a new ingredient.
     *
     * @param ingredient the ingredient object to create
     * @return the saved {@link commons.Ingredient}
     * @throws Exception when processing fails
     */
    Ingredient create(@Payload Ingredient ingredient) throws Exception;

    /**
     * Delete an existing ingredient.
     *
     * @param ingredient ingredient to update
     * @return the updated {@link commons.Ingredient}
     * @throws Exception when ingredient is not deleted
     */
    Ingredient update(@Payload Ingredient ingredient) throws Exception;

    /**
     * Delete an ingredient from the database based on its ID.<br>
     * For consistency, an {@link Ingredient} is passed, not only the ID
     *
     * @param ingredient ingredient to be deleted
     * @return returns the deleted ingredient for clients to process
     * @throws Exception STOMP exception
     */
    Ingredient delete(@Payload Ingredient ingredient) throws Exception;

    /**
     * Get all ingredients in the database.
     *
     * @return returns all ingredients in the database
     * @throws Exception STOMP exception
     */
    List<Ingredient> fetchIngredients() throws Exception;

    /**
     * Handle errors that occur, when processing requests.
     *
     * @param exception an exception, which occurred during the processing of a request
     * @return returns the message of the exception
     */
    Throwable handleException(Throwable exception);
}
