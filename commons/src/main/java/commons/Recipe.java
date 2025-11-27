package commons;

import java.util.*;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.validation.constraints.NotNull;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private String name;
    @NotNull
    private String description;

    @OneToMany(cascade = CascadeType.ALL) // One recipe can contain many different RecipeIngredients.
    private List<RecipeIngredient> ingredients = new ArrayList<RecipeIngredient>();
    @ElementCollection
    @OrderColumn(name = "instruction_sequence") // Persists the List order in the database.
    private List<String> instructions = new ArrayList<String>();

    /**
     * A constructor for Jackson serializing of Recipe.
     */
    @SuppressWarnings("unused")
    protected Recipe() {
    }

    /**
     * Creates a new Recipe with all necessary details.
     *
     * @param name The name of the recipe
     * @param description The description of the recipe
     * @param ingredients The list of the ingredients used
     * @param instructions The list of the instruction steps
     */
    public Recipe(@NotNull String name, @NotNull String description, List<RecipeIngredient> ingredients, List<String> instructions) {
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;
        this.instructions = instructions;
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
     * @param ingredient the ingredient to be added
     */
    public void addIngredient(@NotNull RecipeIngredient ingredient) {
        this.ingredients.add(ingredient);
    }

    /**
     * Removes an ingredient from the recipe.
     * @param ingredient the ingredient to be removed
     */
    public void removeIngredient(@NotNull RecipeIngredient ingredient) {
        this.ingredients.remove(ingredient);
    }

    /**
     * Add an instruction to the end of the list.
     * @param instruction The instruction step to be added
     */
    public void addInstruction(@NotNull String instruction) {
        this.instructions.add(instruction);
    }

    /**
     * Insert an instruction at a specific step number.
     * @param index The step number (counting from 0)
     * @param instruction The instruction step to be added
     */
    public void addInstruction(int index, String instruction) {
        this.instructions.add(index, instruction);
    }

    /**
     * Removes a given instruction.
     * @param instruction The instruction to be removed
     */
    public void removeInstruction(@NotNull String instruction) {
        this.instructions.remove(instruction);
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
