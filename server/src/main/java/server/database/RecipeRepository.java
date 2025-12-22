package server.database;

import commons.Recipe;
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
}
