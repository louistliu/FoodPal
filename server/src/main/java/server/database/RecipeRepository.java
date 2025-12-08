package server.database;

import commons.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link Recipe} entities.
 */
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
}
