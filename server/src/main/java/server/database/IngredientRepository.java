package server.database;

import commons.Ingredient;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Creates a repository of Ingredients with a primary key of type {@link Long}.
 */
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    /**
     * SQL query for finding all ingredients with a name.
     *
     * @param name - String of the name.
     * @return - All ingredients with a certain name.
     */
    List<Ingredient> findByName(String name);
}
