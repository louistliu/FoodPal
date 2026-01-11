package server.services;

import commons.Recipe;
import commons.RecipeIngredient;
import commons.sockets.PairTransport;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.RecipeIngredientRepository;
import server.database.RecipeRepository;

/**
 * Service handling data validation for {@link RecipeIngredient}.
 */
@Service
public class RecipeIngredientServiceImpl implements RecipeIngredientService {
    @Autowired
    private RecipeIngredientRepository repository;
    @Autowired
    private RecipeRepository recipeDB;

    @Override
    public PairTransport<Recipe, RecipeIngredient> create(Recipe recipe,
                                                          RecipeIngredient recipeIngredient)
          throws Exception {
        if (recipeDB.findById(recipe.getId()).isPresent()) {
            if (!recipeDB.findByIngredient(recipe, recipeIngredient.getIngredient()).isEmpty()) {
                throw new EntityExistsException("Recipe already has an ingredient with name: "
                      + recipeIngredient.getIngredient().getName());
            }
        }
        try {
            return new PairTransport<>(recipe, repository.save(recipeIngredient));
        } catch (Exception ex) {
            throw new Exception("Ingredient not saved on the server");
        }
    }

    @Override
    public RecipeIngredient delete(RecipeIngredient recipeIngredient)
          throws EntityNotFoundException {
        if (repository.existsById(recipeIngredient.getId())) {
            throw new EntityNotFoundException(
                  "No recipe ingredient with id " + recipeIngredient.getId()
                        + " in the database");
        }
        repository.delete(recipeIngredient);
        return recipeIngredient;
    }

    @Override
    public RecipeIngredient update(RecipeIngredient recipeIngredient)
          throws EntityNotFoundException {
        if (repository.existsById(recipeIngredient.getId())) {
            throw new EntityNotFoundException(
                  "No recipe ingredient with id " + recipeIngredient.getId()
                        + " in the database");
        }
        return repository.save(recipeIngredient);
    }

    @Override
    public Throwable handleException(Throwable error) {
        return error;
    }
}
