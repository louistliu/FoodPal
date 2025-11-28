package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecipeIngredientTest {

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
    public void constructorTest() {
        assertEquals(ingredient1, ing1.getIngredient());
    }

    @Test
    public void constructorTest1() {
        assertEquals(750f, ing1.getAmount());
    }


    @Test
    public void constructorTest2() {
        assertEquals("g", ing1.getUnit());
    }


    @Test
    public void testEquals() {
        assertEquals(ingredient1, ingredient1);
    }

    @Test
    public void testNotEquals1() {
        var ing3 = new RecipeIngredient(ingredient1, 745, "g");
        assertNotEquals(ing1, ing3);
    }

    @Test
    public void testNotEquals2() {
        var ing3 = new RecipeIngredient(ingredient1, 750, "L");
        assertNotEquals(ing1, ing3);
    }

    @Test
    public void testNotEquals3() {
        var ing3 = new RecipeIngredient(ingredient2, 750, "g");
        assertNotEquals(ing1, ing3);
    }

    @Test
    public void equalsNullTest() {
        assertNotEquals(ing1, null);
    }


    @Test
    void testHashCodeFalse() {
        assertNotEquals(ing1.hashCode(), ing2.hashCode());
    }

    @Test
    void testHashCodeTrue() {
        var ing3 = new RecipeIngredient(ingredient1, 750, "g");
        assertEquals(ing1.hashCode(), ing3.hashCode());
    }

    @Test
    void testToString() {
        var string = ing1.toString();
        assertTrue(string.contains("ingredient"));
    }

    @Test
    void testToString1() {
        var string = ing1.toString();
        assertTrue(string.contains("amount"));
    }

    @Test
    void testToString2() {
        var string = ing1.toString();
        assertTrue(string.contains("unit"));
    }

    @Test
    void testToString3() {
        var string = ing1.toString();
        assertTrue(string.contains("id"));
    }
}