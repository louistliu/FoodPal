package commons;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecipeTest {
    private static final ObjectMapper mapper = new ObjectMapper();
    private Recipe recipe;
    private Recipe otherRecipe;
    private Ingredient flour;
    private RecipeIngredient flourIngredient;

    @BeforeEach
    void setup() {
        flour = new Ingredient("Flour");
        flourIngredient = new RecipeIngredient(flour, 200, "g");
        recipe = new Recipe("Cake", "Yummy",
              new ArrayList<>(List.of(flourIngredient)), new ArrayList<>(List.of("Mix", "Cook")));
        Ingredient sugar = new Ingredient("Sugar");
        RecipeIngredient sugarIngredient = new RecipeIngredient(sugar, 50, "g");
        otherRecipe = new Recipe("Cookie", "Tasty",
              new ArrayList<>(List.of(sugarIngredient)), new ArrayList<>(List.of("Mix", "Bake")));
    }

    @Test
    void recipeSerializeDeserializeEquals() {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        ingredients.add(flourIngredient);
        List<String> instructions = List.of("Mix", "Cook");
        Recipe local = new Recipe("Cake", "Yummy", ingredients, instructions);

        String json = mapper.writeValueAsString(local);
        Recipe parsed = mapper.readValue(json, Recipe.class);

        assertEquals(local, parsed);
    }

    @Test
    void recipeListSerializationEquals() {
        Recipe local = new Recipe("Cookie", "Tasty",
              List.of(new RecipeIngredient(new Ingredient("Sugar"), 50, "g")), List.of("Mix", "Bake"));

        String json = mapper.writeValueAsString(List.of(local));
        List<Recipe> parsed = mapper.readValue(json, new TypeReference<>() {
        });

        assertEquals(List.of(local), parsed);
    }

    @Test
    void getNameTest() {
        assertEquals("Cake", recipe.getName());
    }

    @Test
    void setNameTest() {
        recipe.setName("Pie");
        assertEquals("Pie", recipe.getName());
    }

    @Test
    void getDescriptionTest() {
        assertEquals("Yummy", recipe.getDescription());
    }

    @Test
    void setDescriptionTest() {
        recipe.setDescription("Delicious");
        assertEquals("Delicious", recipe.getDescription());
    }

    @Test
    void getIngredientsTest() {
        assertEquals(List.of(flourIngredient), recipe.getIngredients());
    }

    @Test
    void setIngredientsTest() {
        recipe.setIngredients(List.of());
        assertEquals(List.of(), recipe.getIngredients());
    }

    @Test
    void getInstructionsTest() {
        assertEquals(List.of("Mix", "Cook"), recipe.getInstructions());
    }

    @Test
    void setInstructionsTest() {
        recipe.setInstructions(List.of("Step1"));
        assertEquals(List.of("Step1"), recipe.getInstructions());
    }

    @Test
    void addIngredientTest() {
        RecipeIngredient newIng = new RecipeIngredient(new Ingredient("Salt"), 5, "g");
        recipe.addIngredient(newIng);
        assertEquals(List.of(flourIngredient, newIng), recipe.getIngredients());
    }

    @Test
    void removeIngredientTest() {
        RecipeIngredient toRemove = new RecipeIngredient(new Ingredient("Pepper"), 1, "tsp");
        recipe.addIngredient(toRemove);
        recipe.removeIngredient(toRemove);
        assertEquals(List.of(flourIngredient), recipe.getIngredients());
    }

    @Test
    void addInstructionTest() {
        recipe.addInstruction("Serve");
        assertEquals(List.of("Mix", "Cook", "Serve"), recipe.getInstructions());
    }

    @Test
    void addInstructionAtIndexTest() {
        recipe.addInstruction(1, "Stir");
        assertEquals(List.of("Mix", "Stir", "Cook"), recipe.getInstructions());
    }

    @Test
    void removeInstructionTest() {
        recipe.removeInstruction("Cook");
        assertEquals(List.of("Mix"), recipe.getInstructions());
    }
    
    @Test
    void equalsSameTest() {
        Recipe recipeEqual = new Recipe("Cake", "Yummy",
              new ArrayList<>(List.of(flourIngredient)), new ArrayList<>(List.of("Mix", "Cook")));
        assertEquals(recipe, recipeEqual);
    }

    @Test
    void equalsDifferentTest() {
        assertNotEquals(recipe, otherRecipe);
    }

    @Test
    void equalsNullTest() {
        assertNotEquals(null, recipe);
    }

    @Test
    void equalsObjectTest() {
        assertNotEquals(new Object(), recipe);
    }

    @Test
    void hashCodeTest() {
        Recipe equalRecipe = new Recipe("Cake", "Yummy", List.of(flourIngredient), List.of("Mix", "Cook"));
        assertEquals(recipe.hashCode(), equalRecipe.hashCode());
    }

    @Test
    void toStringTest() {
        assertTrue(recipe.toString().contains("Cake"));
    }
}
