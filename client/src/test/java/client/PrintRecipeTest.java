package client;

import client.utils.PrintRecipe;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Files;

import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;

import java.util.List;

class PrintRecipeTest {

    @Test
    void testExportRecipeToFile() throws Exception {

        Ingredient flour = new Ingredient("Flour");
        RecipeIngredient ri1 = new RecipeIngredient(flour, 200, "g");

        Ingredient eggs = new Ingredient("Egg");
        RecipeIngredient ri2 = new RecipeIngredient(eggs, 3, "eggs");

        Recipe recipe = new Recipe(
                "Test Recipe",
                "Test Description",
                List.of(ri1, ri2),
                List.of("Mix", "Cook")
        );

        File tempFile = File.createTempFile("recipeTest", ".md");
        PrintRecipe.testExportToFile(tempFile.getAbsolutePath(), recipe);

        if (!tempFile.exists()){
            throw new AssertionError();
        }

        System.out.println("saved to: " + tempFile.getAbsolutePath());
        System.out.println(Files.readString(tempFile.toPath()));
    }


}