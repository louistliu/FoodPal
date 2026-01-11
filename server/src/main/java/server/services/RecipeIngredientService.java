package server.services;

import commons.Recipe;
import commons.RecipeIngredient;
import commons.sockets.PairTransport;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

/**
 * Defines required {@link commons.RecipeIngredient} endpoint methods.
 */
public interface RecipeIngredientService {

    /**
     * Creates a new {@link RecipeIngredient} if it is not already part of the recipe.
     * (The recipe does not contain an ingredient with the same name)
     *
     * @param recipeIngredient recipe ingredient to create
     * @param recipe           recipe the ingredient should belong to
     * @return recipe ingredient from the DB and the recipe it is part of.
     * @throws EntityExistsException if the recipe ingredient
     *                               with the same ingredient is already part of the recipe.
     */
    PairTransport<Recipe, RecipeIngredient> create(Recipe recipe,
                                                   RecipeIngredient recipeIngredient)
          throws Exception;

    /**
     * Deletes {@link RecipeIngredient} if it is in the DB and returns it.
     *
     * @param recipeIngredient object to delete.
     * @return the deleted {@link RecipeIngredient}
     * @throws EntityNotFoundException if recipe ingredient with the id does not exist in DB.
     */
    RecipeIngredient delete(RecipeIngredient recipeIngredient);

    /**
     * Updates {@link RecipeIngredient} if it is present in DB and returns it.
     *
     * @param recipeIngredient object to update properties of.
     * @return updated version of {@link RecipeIngredient};
     * @throws EntityNotFoundException if {@link RecipeIngredient} with id does not exist in DB.
     */
    RecipeIngredient update(RecipeIngredient recipeIngredient);

    /**
     * Parses exceptions.
     *
     * @param exception exception thrown by one of {@link RecipeIngredient} methods.
     * @return parsed exception.
     */
    Throwable handleException(Throwable exception);
}
