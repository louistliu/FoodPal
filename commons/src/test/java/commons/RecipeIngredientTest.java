package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

class RecipeIngredientTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private RecipeIngredient ing1;
    private RecipeIngredient ing2;
    private Ingredient ingredient1;
    private Ingredient ingredient2;

    @BeforeEach
    public void setUp() {
        ingredient1 = new Ingredient("rice");
        ingredient2 = new Ingredient("flour");
        ing1 = new RecipeIngredient(ingredient1, 750, "g");
        ing2 = new RecipeIngredient(ingredient2, 750, "g");
    }

    @Test
    public void constructorSetsIngredientTest() {
        assertEquals(ingredient1, ing1.getIngredient());
    }

    @Test
    public void constructorSetsAmountTest() {
        assertEquals(750f, ing1.getAmount());
    }

    @Test
    public void constructorSetsUnitTest() {
        assertEquals("g", ing1.getUnit());
    }

    @Test
    public void equalsSelfTest() {
        assertEquals(ingredient1, ingredient1);
    }

    @Test
    public void notEqualsDifferentAmountTest() {
        var ing3 = new RecipeIngredient(ingredient1, 745, "g");
        assertNotEquals(ing1, ing3);
    }

    @Test
    public void notEqualsDifferentUnitTest() {
        var ing3 = new RecipeIngredient(ingredient1, 750, "L");
        assertNotEquals(ing1, ing3);
    }

    @Test
    public void notEqualsDifferentIngredientTest() {
        var ing3 = new RecipeIngredient(ingredient2, 750, "g");
        assertNotEquals(ing1, ing3);
    }

    @Test
    public void equalsNullTest() {
        assertNotEquals(null, ing1);
    }

    @Test
    void hashCodeDifferentTest() {
        assertNotEquals(ing1.hashCode(), ing2.hashCode());
    }

    @Test
    void hashCodeEqualTest() {
        var ing3 = new RecipeIngredient(ingredient1, 750, "g");
        assertEquals(ing1.hashCode(), ing3.hashCode());
    }

    @Test
    void toStringContainsIngredientTest() {
        var string = ing1.toString();
        assertTrue(string.contains("ingredient"));
    }

    @Test
    void toStringContainsAmountTest() {
        var string = ing1.toString();
        assertTrue(string.contains("amount"));
    }

    @Test
    void toStringContainsUnitTest() {
        var string = ing1.toString();
        assertTrue(string.contains("unit"));
    }

    @Test
    void toStringContainsIdTest() {
        var string = ing1.toString();
        assertTrue(string.contains("id"));
    }

    @Test
    void serializationEqualsTest() throws Exception {
        RecipeIngredient original = new RecipeIngredient(new Ingredient("Salt"), 5, "g");
        String json = mapper.writeValueAsString(original);
        RecipeIngredient parsed = mapper.readValue(json, RecipeIngredient.class);
        assertEquals(original, parsed);
    }
}