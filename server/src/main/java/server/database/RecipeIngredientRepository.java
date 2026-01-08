package server.database;

import commons.Ingredient;
import commons.RecipeIngredient;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *  {@link RecipeIngredient} database interface.
 */
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {
    /**
     * Lists all {@link RecipeIngredient} that have the specified ingredient.
     *
     * @param ingredient {@link Ingredient} contained by a {@link RecipeIngredient}.
     * @return a list of {@link RecipeIngredient} which contain the ingredient.
     */
    List<RecipeIngredient> findByIngredient(Ingredient ingredient);
}
