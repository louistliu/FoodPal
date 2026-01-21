package server.service;

import commons.Ingredient;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.IngredientRepository;
import server.utils.DummyData;

/**
 * Handles business logic of {@link Ingredient},
 * does all the interaction with {@link IngredientRepository}.
 */
@Service
public class IngredientService implements IIngredientService {

    private final IngredientRepository repository;

    @Autowired
    public IngredientService(IngredientRepository repository) {
        this.repository = repository;

        repository.saveAllAndFlush(DummyData.getDefaultIngredients());
    }

    /**
     * Create a new ingredient.
     *
     * @param ingredient the ingredient object to create
     * @return the saved {@link Ingredient}
     * @throws Exception when processing fails
     */
    @Override
    public Ingredient create(Ingredient ingredient) throws Exception {
        if (ingredient == null || ingredient.getName() == null
              || ingredient.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Ingredient name cannot be empty");
        }
        if (!repository.findByName(ingredient.getName()).isEmpty()) {
            throw new EntityExistsException(
                  "Ingredient with name: " + ingredient.getName() + " is already in the database");
        }
        return repository.save(ingredient);
    }

    /**
     * Delete an existing ingredient.
     *
     * @param ingredient ingredient to update
     * @return the updated {@link Ingredient}
     * @throws Exception when ingredient is not deleted
     */
    @Override
    public Ingredient update(Ingredient ingredient) throws Exception {
        if (!repository.existsById(ingredient.getId())) {
            throw new EntityNotFoundException(
                  "No ingredient with id " + ingredient.getId() + " in the database");
        }

        return repository.save(ingredient);
    }

    /**
     * Delete an ingredient from the database based on its ID.<br>
     * For consistency, an {@link Ingredient} is passed, not only the ID
     *
     * @param ingredient ingredient to be deleted
     * @return returns the deleted ingredient for clients to process
     * @throws Exception STOMP exception
     */
    @Override
    public Ingredient delete(Ingredient ingredient) throws Exception {
        if (!repository.existsById(ingredient.getId())) {
            throw new EntityNotFoundException(
                  "No ingredient with id " + ingredient.getId() + " in the database");
        }
        repository.deleteById(ingredient.getId());
        return ingredient;
    }

    /**
     * Get all ingredients in the database.
     *
     * @return returns all ingredients in the database
     * @throws Exception STOMP exception
     */
    @Override
    public List<Ingredient> fetchIngredients() throws Exception {
        return repository.findAll().stream()
              .filter(i -> i.getName() != null && !i.getName().trim().isEmpty())
              .toList();
    }

    /**
     * Handle errors that occur, when processing requests.
     *
     * @param exception an exception, which occurred during the processing of a request
     * @return returns the message of the exception
     */
    @Override
    public Throwable handleException(Throwable exception) {
        return exception;
    }
}
