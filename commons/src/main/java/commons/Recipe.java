package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A recipe entity, containing all the necessary details.
 */
@Entity
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private String name;
    @NotNull
    private String description;

    private boolean isFavorite = false;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    // One recipe can contain many different RecipeIngredients.
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = "instruction_sequence") // Persists the List order in the database.
    private List<String> instructions = new ArrayList<>();

    /**
     * A constructor for Jackson serializing of Recipe.
     */
    @SuppressWarnings("unused")
    protected Recipe() {
    }

    /**
     * Creates a new Recipe with all necessary details.
     *
     * @param name         The name of the recipe
     * @param description  The description of the recipe
     * @param ingredients  The list of the ingredients used
     * @param instructions The list of the instruction steps
     */
    public Recipe(@NotNull String name, @NotNull String description,
            List<RecipeIngredient> ingredients, List<String> instructions) {
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public Recipe(String name) {
        this.name = name;
        this.description = "";
        this.ingredients = new ArrayList<>();
        this.instructions = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(@NotNull String description) {
        this.description = description;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

    /**
     * Adds an ingredient to the recipe.
     *
     * @param ingredient the ingredient to be added
     */
    public void addIngredient(@NotNull RecipeIngredient ingredient) {
        this.ingredients.add(ingredient);
    }

    /**
     * Removes the first occurrence of an ingredient from the recipe.
     *
     * @param ingredient the ingredient to be removed
     */
    public void removeIngredient(@NotNull RecipeIngredient ingredient) {
        this.ingredients.remove(ingredient);
    }

    /**
     * Add an instruction to the end of the list.
     *
     * @param instruction The instruction step to be added
     */
    public void addInstruction(@NotNull String instruction) {
        this.instructions.add(instruction);
    }

    /**
     * Insert an instruction at a specific step number.
     *
     * @param index       The step number (counting from 0)
     * @param instruction The instruction step to be added
     */
    public void addInstruction(int index, @NotNull String instruction) {
        this.instructions.add(index, instruction);
    }

    /**
     * Removes the first occurrence of a given instruction.
     *
     * @param instruction The instruction to be removed
     */
    public void removeInstruction(@NotNull String instruction) {
        this.instructions.remove(instruction);
    }

    /**
     * Checks if two recipes are the same excluding ids.
     *
     * @param recipe recipe to check
     * @return if two recipes are equal
     */
    public boolean equalsNoId(Recipe recipe) {
        return isFavorite == recipe.isFavorite
                && Objects.equals(name, recipe.name)
                && Objects.equals(description, recipe.description)
                && Objects.deepEquals(ingredients, recipe.ingredients)
                && Objects.deepEquals(instructions, recipe.instructions);

    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
