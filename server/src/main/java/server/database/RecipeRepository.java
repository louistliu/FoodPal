package server.database;

import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository for {@link Recipe} entities.
 *
 */
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    /**
     * SQL query for recipes by name.
     *
     * @param name name of recipe
     * @return return all recipes with name
     */
    @Query("select r from #{#entityName} r where r.name = ?1")
    List<Recipe> findBy(String name);

    /**
     * Find all recipe ingredients in a recipe that contain the specified ingredient.
     *
     * @param recipe     recipe to check
     * @param ingredient ingredient that the recipe ingredient contains
     * @return all {@link RecipeIngredient} which are part of the recipe and have the ingredient.
     */
    default List<RecipeIngredient> findByIngredient(Recipe recipe, Ingredient ingredient) {
        return findByIngredient(recipe.getId(), ingredient.getName());
    }

    /**
     * Find all recipe ingredients in a recipe that contain the specified ingredient.
     *
     * @param recipeId       recipe to check
     * @param ingredientName ingredient that the recipe ingredient contains
     * @return all {@link RecipeIngredient} which are part of the recipe and have the ingredient.
     */
    @Query("select ri from Recipe r join r.ingredients ri"
          + " where r.id = ?1 and ri.ingredient.name = ?2")
    List<RecipeIngredient> findByIngredient(Long recipeId, String ingredientName);
}
