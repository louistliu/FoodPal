package server.controllers;

import commons.Ingredient;
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
import server.database.IngredientRepository;

/**
 * API endpoint responsible for handling ingredient manipulation.
 */
@Controller
public class IngredientController {

    private final IngredientRepository ingredientDB;

    @Autowired
    public IngredientController(IngredientRepository ingredientDB) {
        this.ingredientDB = ingredientDB;
    }

    /**
     * Creates a new ingredient in the database if it is not already present,
     * and is valid, then broadcasts it, otherwise returns an error.
     *
     * @param ingredient the basic ingredient object automatically validated
     * @return returns the new to all subscribed sockets or nothing,
     *       from client side {@code /topic/ingredients/create}
     * @throws Exception STOMP exception
     */
    @MessageMapping("/ingredients/create")
    @Valid
    public Ingredient create(@Payload Ingredient ingredient) throws Exception {
        var saved = ingredientDB.save(ingredient);
        // TODO: process ingredient here
        return saved;
    }


    /**
     * Get all ingredients when subscribing to {@code /user/queue/ingredients/create}.
     *
     * @return returns all ingredients in the database
     * @throws Exception STOMP exception
     */
    @SubscribeMapping("/ingredients/fetch")
    public List<Ingredient> fetchIngredients() throws Exception {
        System.out.println("SUBSCRIBED");
        return ingredientDB.findAll();
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
    @Valid
    public Ingredient delete(@Payload Ingredient ingredient) throws Exception {
        if (!ingredientDB.existsById(ingredient.getId())) {
            throw new EntityNotFoundException(
                  "No ingredient with id " + ingredient.getId() + " in the database");
        }
        ingredientDB.deleteById(ingredient.getId());
        return ingredient;
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
        return exception;
    }
}
