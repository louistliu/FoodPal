package server.controllers;

import commons.Ingredient;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
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
     * @return returns the new to all subscribed sockets or nothing
     * @throws Exception STOMP exception
     */
    @MessageMapping("ingredients/create")
    @SendTo("/topic/ingredients/create")
    @Valid
    public Ingredient create(@Payload Ingredient ingredient) throws Exception {
        var saved = ingredientDB.save(ingredient);
        System.out.println(saved);
        return ingredient;
    }
}
