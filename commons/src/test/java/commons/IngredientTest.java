package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

class IngredientTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private Ingredient ingredient1;
    private Ingredient ingredient2;

    @BeforeEach
    public void setUp() {
        ingredient1 = new Ingredient("Milk");
        ingredient2 = new Ingredient("Cheese");

    }

    @Test
    public void defaultConstructorTest() {
        Ingredient ing = new Ingredient("Test");
        assertNotNull(ing);
    }

    @Test
    public void constructorTest() {
        assertEquals("Milk", ingredient1.getName());
        assertEquals("Cheese", ingredient2.getName());
    }

    @Test
    public void equalsTrueTest() {
        assertEquals(ingredient1, ingredient1);
    }

    @Test
    public void equalsDifferentTest() {
        assertNotEquals(ingredient1, ingredient2);
    }

    @Test
    public void equalsNullTest() {
        assertNotNull(ingredient1);
    }

    @Test
    public void equalsHashCodeTest() {
        Ingredient ing = new Ingredient("Milk");
        assertEquals(ingredient1.hashCode(), ing.hashCode());
    }

    @Test
    public void notEqualsHashCodeTest() {
        assertNotEquals(ingredient1.hashCode(), ingredient2.hashCode());
    }

    @Test
    public void toStringTest() {
        var string = ingredient1.toString();
        assertTrue(string.contains("name"));
    }

    @Test
    public void jacksonIngredientSerializationEquals() {
        String json = mapper.writeValueAsString(ingredient1);
        Ingredient parsed = mapper.readValue(json, Ingredient.class);
        assertEquals(ingredient1, parsed);
    }

}