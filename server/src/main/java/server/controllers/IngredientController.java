package server.controllers;

import commons.Ingredient;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import server.service.IIngredientService;

/**
 * API endpoint responsible for handling ingredient manipulation.
 */
@Controller
public class IngredientController {

    @Autowired
    private IIngredientService ingredientService;

    /**
     * Create a new ingredient. Clients should send a {@link commons.Ingredient}
     * payload to the application destination (for example:
     * {@code /app/ingredient/create}). On success the saved ingredient object is
     * returned so subscribers can broadcast or process the new ingredient.
     *
     * @param ingredient the ingredient object (validated) to create
     * @return the saved {@link commons.Ingredient}
     * @throws Exception when processing fails
     */
    @MessageMapping("/ingredients/create")
    @SendTo("/topic/ingredients/create")
    @SendToUser("/queue/ingredients/create")
    @Valid
    public Ingredient create(@Payload Ingredient ingredient) throws Exception {
        return ingredientService.create(ingredient);
    }

    /**
     * Delete an existing ingredient. The provided {@link commons.Ingredient} object
     * must contain a valid id; if the ingredient does not exist an
     * {@link jakarta.persistence.EntityNotFoundException} is thrown.
     *
     * @param ingredient ingredient (with id) to update
     * @return the updated {@link commons.Ingredient}
     * @throws Exception when update fails or the ingredient is not found
     */
    @MessageMapping("/ingredients/update")
    @SendTo("/topic/ingredients/update")
    @Valid
    public Ingredient update(@Payload Ingredient ingredient) throws Exception {
        return ingredientService.update(ingredient);
    }

    /**
     * Get all ingredients when subscribing to {@code /user/queue/ingredients/create}.
     *
     * @return returns all ingredients in the database
     * @throws Exception STOMP exception
     */
    @SubscribeMapping("/ingredients/fetch")
    public List<Ingredient> fetchIngredients() throws Exception {
        return ingredientService.fetchIngredients();
    }

    /**
     * Delete an ingredient from the database based on its ID.<br>
     * For consistency, an {@link Ingredient} is passed, not only the ID
     *
     * @param ingredient ingredient to be deleted
     * @return returns the deleted ingredient for clients to process
     * @throws Exception STOMP exception
     */
    @MessageMapping("/ingredients/delete")
    @SendTo("/topic/ingredients/delete")
    @Valid
    public Ingredient delete(@Payload Ingredient ingredient) throws Exception {
        return ingredientService.delete(ingredient);
    }


    /**
     * Handle errors that occur, when processing requests.<br>
     * Subscribable on {@code /user/queue/errors}
     *
     * @param exception an exception, which occurred during the processing of a request
     * @return returns the message of the exception to the individual client
     */
    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public Throwable handleException(Throwable exception) {
        return ingredientService.handleException(exception);
    }
}
