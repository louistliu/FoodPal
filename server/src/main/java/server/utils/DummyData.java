package server.utils;

import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for creating initial data for demo, testing purposes.
 */
public class DummyData {

    private static final Ingredient flour = new Ingredient("Flour");
    private static final Ingredient sugar = new Ingredient("Table Sugar");
    private static final Ingredient salt = new Ingredient("Salt");
    private static final Ingredient butter = new Ingredient("Butter");
    private static final Ingredient egg = new Ingredient("Egg");
    private static final Ingredient milk = new Ingredient("Milk");
    private static final Ingredient yeast = new Ingredient("Yeast");
    private static final Ingredient cocoa = new Ingredient("Cocoa Powder");
    private static final Ingredient water = new Ingredient("Water");
    private static final Ingredient oil = new Ingredient("Olive Oil");
    private static final Ingredient pasta = new Ingredient("Pasta");
    private static final Ingredient garlic = new Ingredient("Garlic");

    /**
     * Returns the global master list of ingredients.
     *
     * @return a list of ingredients
     */
    public static List<Ingredient> getDefaultIngredients() {
        List<Ingredient> ingredients = List.of(flour, sugar, salt, butter, egg,
                milk, yeast, cocoa, water, oil, pasta, garlic);
        return ingredients;
    }

    /**
     * Returns a list of recipes built using the shared constants.
     *
     * @return a list of recipes
     */
    public static List<Recipe> getDefaultRecipes() {

        Recipe bread = new Recipe("Basic White Bread", "A simple and crusty homemade loaf.",
                new ArrayList<>(), new ArrayList<>());
        bread.addIngredient(new RecipeIngredient(flour, 500.0f, "g"));
        bread.addIngredient(new RecipeIngredient(water, 350.0f, "ml"));
        bread.addIngredient(new RecipeIngredient(yeast, 7.0f, "g"));
        bread.addIngredient(new RecipeIngredient(salt, 10.0f, "g"));
        bread.addInstruction("Mix the flour, yeast, and salt in a large bowl.");
        bread.addInstruction("Add warm water and knead for 10 minutes.");
        bread.addInstruction("Let rise for 1 hour, then bake at 200°C.");

        Recipe cake = new Recipe("Chocolate Mug Cake",
                "A quick and rich chocolate dessert in a mug.",
                new ArrayList<>(), new ArrayList<>());
        cake.addIngredient(new RecipeIngredient(flour, 4.0f, "tbsp"));
        cake.addIngredient(new RecipeIngredient(sugar, 2.0f, "tbsp"));
        cake.addIngredient(new RecipeIngredient(cocoa, 2.0f, "tbsp"));
        cake.addIngredient(new RecipeIngredient(milk, 3.0f, "tbsp"));
        cake.addInstruction("Whisk dry ingredients together in a microwave-safe mug.");
        cake.addInstruction("Stir in milk and melted butter.");
        cake.addInstruction("Microwave on high for 90 seconds.");

        Recipe spaghetti = new Recipe("Spaghetti", "Classic Italian pasta with garlic and oil.",
                new ArrayList<>(), new ArrayList<>());
        spaghetti.addIngredient(new RecipeIngredient(pasta, 200.0f, "g"));
        spaghetti.addIngredient(new RecipeIngredient(garlic, 20.0f, "g"));
        spaghetti.addIngredient(new RecipeIngredient(oil, 50.0f, "ml"));
        spaghetti.addInstruction("Boil pasta in salted water until al dente.");
        spaghetti.addInstruction("Sauté sliced garlic in olive oil until golden.");
        spaghetti.addInstruction("Toss pasta with the garlic oil and serve.");

        Recipe pancakes = new Recipe("Simple Pancakes", "Fluffy breakfast pancakes.",
                new ArrayList<>(), new ArrayList<>());
        pancakes.addIngredient(new RecipeIngredient(flour, 1.5f, "cup(s)"));
        pancakes.addIngredient(new RecipeIngredient(milk, 1.0f, "cup(s)"));
        pancakes.addIngredient(new RecipeIngredient(egg, 1.0f, "pcs"));
        pancakes.addIngredient(new RecipeIngredient(sugar, 1.0f, "tbsp"));
        pancakes.addInstruction("Whisk together milk, egg, and melted butter.");
        pancakes.addInstruction("Stir in flour and sugar until just combined.");
        pancakes.addInstruction("Cook spoonfuls on a hot griddle until bubbly.");


        Recipe eggs = new Recipe("Scrambled Eggs", "Perfectly soft and buttery eggs.",
                new ArrayList<>(), new ArrayList<>());
        eggs.addIngredient(new RecipeIngredient(egg, 3.0f, "pcs"));
        eggs.addIngredient(new RecipeIngredient(butter, 1.0f, "tbsp"));
        eggs.addIngredient(new RecipeIngredient(milk, 1.0f, "tbsp"));
        eggs.addIngredient(new RecipeIngredient(salt, 1.0f, "g"));
        eggs.addInstruction("Whisk eggs, milk, and salt until combined.");
        eggs.addInstruction("Melt butter in a non-stick pan over medium-low heat.");
        eggs.addInstruction("Pour in eggs and stir gently until soft curds form.");

        Recipe pizza = new Recipe("Homemade Pizza Dough", "Classic thin-crust pizza base.",
                new ArrayList<>(), new ArrayList<>());
        pizza.addIngredient(new RecipeIngredient(flour, 2.5f, "cup(s)"));
        pizza.addIngredient(new RecipeIngredient(water, 250.0f, "ml"));
        pizza.addIngredient(new RecipeIngredient(yeast, 2.0f, "tsp"));
        pizza.addIngredient(new RecipeIngredient(sugar, 1.0f, "tsp"));
        pizza.addIngredient(new RecipeIngredient(salt, 1.0f, "tsp"));
        pizza.addIngredient(new RecipeIngredient(oil, 2.0f, "tbsp"));
        pizza.addInstruction("Dissolve sugar and yeast in warm water.");
        pizza.addInstruction("Let sit for 5 minutes until foamy.");
        pizza.addInstruction("Mix in flour, salt, and olive oil.");
        pizza.addInstruction("Knead on a floured surface for 8 minutes.");
        pizza.addInstruction("Place in an oiled bowl.");
        pizza.addInstruction("Cover with a damp cloth.");
        pizza.addInstruction("Let rise in a warm place for 90 minutes.");
        pizza.addInstruction("Punch down and roll into circles.");

        Recipe biscuits = new Recipe("Butter Biscuits", "Flaky and salty tea biscuits.",
                new ArrayList<>(), new ArrayList<>());
        biscuits.addIngredient(new RecipeIngredient(flour, 250.0f, "g"));
        biscuits.addIngredient(new RecipeIngredient(butter, 100.0f, "g"));
        biscuits.addIngredient(new RecipeIngredient(milk, 150.0f, "ml"));
        biscuits.addIngredient(new RecipeIngredient(salt, 0.5f, "tsp"));
        biscuits.addInstruction("Cut cold butter into flour and salt.");
        biscuits.addInstruction("Stir in milk until a dough forms.");
        biscuits.addInstruction("Bake at 220°C for 12 minutes.");

        Recipe brownies = new Recipe("Brownies", "Chewy and very sweet.",
                new ArrayList<>(), new ArrayList<>());
        brownies.addIngredient(new RecipeIngredient(butter, 0.5f, "cup(s)"));
        brownies.addIngredient(new RecipeIngredient(sugar, 1.0f, "cup(s)"));
        brownies.addIngredient(new RecipeIngredient(egg, 2.0f, "pcs"));
        brownies.addIngredient(new RecipeIngredient(cocoa, 0.5f, "cup(s)"));
        brownies.addIngredient(new RecipeIngredient(flour, 0.5f, "cup(s)"));
        brownies.addInstruction("Melt butter and whisk in sugar and cocoa.");
        brownies.addInstruction("Add eggs one at a time.");
        brownies.addInstruction("Fold in flour gently.");
        brownies.addInstruction("Bake in a square pan for 25 minutes.");

        Recipe garlicBread = new Recipe("Garlic Butter Spread", "Perfect for making garlic bread.",
                new ArrayList<>(), new ArrayList<>());
        garlicBread.addIngredient(new RecipeIngredient(butter, 4.0f, "tbsp"));
        garlicBread.addIngredient(new RecipeIngredient(garlic, 3.0f, "pcs"));
        garlicBread.addIngredient(new RecipeIngredient(salt, 0.25f, "tsp"));
        garlicBread.addInstruction("Mince the garlic finely.");
        garlicBread.addInstruction("Mix into softened butter with salt.");
        garlicBread.addInstruction("Spread on bread and toast until golden.");

        List<Recipe> recipes = new ArrayList<>();

        recipes.add(bread);
        recipes.add(cake);
        recipes.add(spaghetti);
        recipes.add(pancakes);
        recipes.add(eggs);
        recipes.add(pizza);
        recipes.add(biscuits);
        recipes.add(brownies);
        recipes.add(garlicBread);

        return recipes;
    }
}
