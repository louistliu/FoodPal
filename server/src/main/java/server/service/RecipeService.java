package server.service;

import commons.Recipe;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.RecipeRepository;
import server.utils.DummyData;

/**
 * Service for handling all business logic of Recipes.
 */
@Service
public class RecipeService implements IRecipeService {

    private final RecipeRepository repository;

    @Autowired
    public RecipeService(RecipeRepository repository) {
        this.repository = repository;

        List<Recipe> recipes = DummyData.getDefaultRecipes();
        repository.saveAllAndFlush(recipes);
    }

    /**
     * Create a new recipe. On success the saved recipe object is
     * returned.
     *
     * @param recipe the recipe object (validated) to create.
     * @return the saved {@link Recipe}.
     * @throws Exception when processing fails.
     */
    @Override
    public Recipe create(Recipe recipe) throws Exception {
        if (!repository.findBy(recipe.getName()).isEmpty()) {
            throw new EntityExistsException(
                  "Recipe with name: " + recipe.getName() + " is already in the database");
        }
        return repository.save(recipe);
    }

    /**
     * Update an existing recipe. If the recipe does not exist an
     * {@link EntityNotFoundException} is thrown.
     *
     * @param recipe recipe to update
     * @return the updated {@link Recipe}
     * @throws Exception when update fails or the recipe is not found
     */
    @Override
    public Recipe update(Recipe recipe) throws Exception {
        if (!repository.existsById(recipe.getId())) {
            throw new EntityNotFoundException(
                  "No recipe with id " + recipe.getId() + " in the database");
        }
        Optional<Recipe> serverRecipe = repository.findById(recipe.getId());
        if (serverRecipe.isEmpty()) {
            throw new EntityNotFoundException("Server has not saved this recipe");
        }
        if (!repository.findBy(recipe.getName()).isEmpty()
              && !recipe.getName().equals(serverRecipe.get().getName())) {
            throw new EntityNotFoundException(
                  "Recipe with name " + recipe.getName() + " already exists");
        }
        return repository.save(recipe);
    }

    /**
     * Deletes an existing recipe from the db. If the recipe does not exist an
     * {@link EntityNotFoundException} is thrown.
     *
     * @param recipe recipe to delete
     * @return the deleted {@link Recipe}
     * @throws Exception when deletion fails or the recipe is not found
     */
    @Override
    public Recipe delete(Recipe recipe) throws Exception {
        if (!repository.existsById(recipe.getId())) {
            throw new EntityNotFoundException(
                  "No recipe with id " + recipe.getId() + " in the database");
        }
        repository.deleteById(recipe.getId());
        return recipe;
    }

    /**
     * Get all recipes in the database.
     *
     * @return list of all {@link Recipe} in the database
     * @throws Exception when retrieval fails
     */
    @Override
    public List<Recipe> fetchIngredients() throws Exception {
        return repository.findAll();
    }

    /**
     * Handle errors that occur, when processing requests.
     *
     * @param exception an exception, which occurred during the processing of a
     *                  request
     * @return returns the message of the exception to the individual client
     */
    @Override
    public Throwable handleException(Throwable exception) {
        return exception;
    }
}
