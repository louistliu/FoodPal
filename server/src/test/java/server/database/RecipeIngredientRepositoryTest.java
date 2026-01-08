package server.database;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import commons.Ingredient;
import commons.RecipeIngredient;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
class RecipeIngredientRepositoryTest {

    private List<Ingredient> testIngredients;

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private RecipeIngredientRepository recipeIngredientRepository;
    @Autowired
    private IngredientRepository ingredientRepository;

    @BeforeEach
    void setup() {
        testIngredients = new ArrayList<>();
        testIngredients.add(new Ingredient("Potato"));
        testIngredients.add(new Ingredient("Carrot"));
        testIngredients.add(new Ingredient("Chicken"));
        testIngredients.add(new Ingredient("Oat"));
        ingredientRepository.saveAllAndFlush(testIngredients);
    }

    @Test
    @Order(0)
    void injectedComponentsAreNotNull() {
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(recipeIngredientRepository).isNotNull();
        assertThat(ingredientRepository).isNotNull();
    }

    @Test
    @Order(2)
    void findByIngredientCorrectCount() {
        var ingredient = testIngredients.getFirst();
        RecipeIngredient recipeIngredient1 =
              new RecipeIngredient(ingredient, 1.5F, "g");
        recipeIngredientRepository.save(recipeIngredient1);
        recipeIngredientRepository.save(new RecipeIngredient(ingredient, 1F, "g"));
        recipeIngredientRepository.save(new RecipeIngredient(testIngredients.getLast(), 1F, "g"));
        assertThat(
              recipeIngredientRepository.findByIngredient(ingredient).size()
        ).isEqualTo(2);
    }

    @Test
    @Order(3)
    void findByIngredientSameObj() {
        var target = recipeIngredientRepository.save(
              new RecipeIngredient(testIngredients.get(0), 1F, "g"));
        recipeIngredientRepository.save(new RecipeIngredient(testIngredients.get(1), 1F, "g"));
        assertThat(recipeIngredientRepository.findByIngredient(testIngredients.get(0))
              .getLast()).isEqualTo(target);
        assertThat(recipeIngredientRepository.findByIngredient(testIngredients.get(0))
              .getFirst()).isEqualTo(target);
    }
}