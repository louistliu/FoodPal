package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IngredientTest {

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
        assertNotEquals(ingredient1, null);
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


}