package client.utils;

import client.scenes.LanguageController;
import commons.Recipe;
import commons.RecipeIngredient;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * method for saving recipes as a markdown file locally.
 */
public class PrintRecipe {

    /**
     * exports the recipe to the local storage.
     *
     * @param owner  The window needed for the FileChooser class
     * @param recipe The recipe that needs to be exported
     */
    public static void exportRecipe(Window owner, Recipe recipe, LanguageController languageController) {
        StringBuilder content = new StringBuilder();
        content.append("# ").append(recipe.getName()).append("\n\n");
        content.append(recipe.getDescription()).append("\n\n");
        content.append("## ").append(languageController.get("label.ingredients1")).append("\n\n");

        for (RecipeIngredient ing : recipe.getIngredients()) {
            content.append("- ")
                    .append(ing.getAmount()).append(" ")
                    .append(ing.getUnit()).append(" ")
                    .append(ing.getIngredient().getName()).append("\n");
        }

        content.append("\n## ").append(languageController.get("label.instructions1")).append("\n\n");
        List<String> instructions = recipe.getInstructions();
        for (int i = 0; i < instructions.size(); i++) {
            content.append(i + 1).append(". ").append(instructions.get(i)).append("\n");
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(languageController.get("title.saveRecipeFile"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Markdown file", "*.md"));
        fileChooser.setInitialFileName(recipe.getName() + ".md");

        File file = fileChooser.showSaveDialog(owner);
        if (file == null) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method is for testing, and saves the file to a specified path.
     *
     * @param path   the path to where the recipe should be stored.
     * @param recipe the recipe that has to be stored.
     */
    public static void testExportToFile(String path, Recipe recipe) {
        StringBuilder content = new StringBuilder();

        content.append("# ").append(recipe.getName()).append("\n\n");

        content.append("## Ingredients\n");
        for (RecipeIngredient ri : recipe.getIngredients()) {
            content.append("- ")
                    .append(ri.getAmount()).append(" ")
                    .append(ri.getUnit()).append(" ")
                    .append(ri.getIngredient().getName())
                    .append("\n");
        }

        content.append("\n## Preparation Steps\n");
        for (int i = 0; i < recipe.getInstructions().size(); i++) {
            content.append(i + 1).append(". ")
                    .append(recipe.getInstructions().get(i))
                    .append("\n");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
