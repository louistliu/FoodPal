package server.database;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class RecipeRepositoryTest {

    private List<Ingredient> testIngredients;
    private List<RecipeIngredient> testRecipeIngredients;

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
    @Autowired
    private RecipeRepository recipeRepository;

    @BeforeEach
    void setup() {
        testIngredients = new ArrayList<>();
        testIngredients.add(new Ingredient("Potato"));
        testIngredients.add(new Ingredient("Carrot"));
        testIngredients.add(new Ingredient("Chicken"));
        testIngredients.add(new Ingredient("Oat"));
        ingredientRepository.saveAllAndFlush(testIngredients);
        testRecipeIngredients = new ArrayList<>();
        testRecipeIngredients.add(new RecipeIngredient(testIngredients.get(0), 1F, "g"));
        testRecipeIngredients.add(new RecipeIngredient(testIngredients.get(1), 1F, "g"));
        recipeIngredientRepository.saveAll(testRecipeIngredients);
    }

    @Test
    @Order(0)
    void injectedComponentsAreNotNull() {
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(recipeIngredientRepository).isNotNull();
        assertThat(ingredientRepository).isNotNull();
        assertThat(recipeRepository).isNotNull();
    }

    @Test
    @Order(1)
    void findByRecipeIngredient() {
        var recipeIngredients = List.of(testRecipeIngredients.get(0));
        var recipe = new Recipe("stew", "a stew", recipeIngredients, List.of());
        var recipe1 =
              new Recipe("chicken", "obviously chicken", List.of(testRecipeIngredients.get(1)),
                    List.of());

        recipeRepository.save(recipe);
        recipeRepository.save(recipe1);
        assertThat(
              recipeRepository.findByIngredient(recipe, testIngredients.get(0)).size()).isEqualTo(
              1);
        assertThat(
              recipeRepository.findByIngredient(recipe, testIngredients.get(1)).size()).isEqualTo(
              0);
    }

    @Test
    @Order(2)
    void findByName() {
        var recipe = new Recipe("unique-name", "a unique recipe", List.of(), List.of());
        recipeRepository.saveAndFlush(recipe);
        var found = recipeRepository.findBy("unique-name");
        assertThat(found.get(0).getName()).isEqualTo("unique-name");
    }
}