package server.database;

import commons.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Creates a repository of Ingredients with a primary key of type {@link Long}.
 */
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
}
