package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * An RecipeIngredient that can be used in a recipe.
 */
@Entity
public class RecipeIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(optional = false)
    // One Ingredient can be included in multiple
    // RecipeIngredients
    private Ingredient ingredient;
    @NotNull
    private float amount;
    @NotNull
    private String unit;

    /**
     * A constructor for Jackson serializing of RecipeIngredient.
     */
    @SuppressWarnings("unused")
    protected RecipeIngredient() {
    }

    /**
     * Create a RecipeIngredient to be used in a recipe.
     *
     * @param ingredient The ingredient to be used in the recipe
     * @param amount     The amount of the ingredient that should be used for the
     *                   recipe
     * @param unit       The unit of the amount (e.g. grams)
     */
    public RecipeIngredient(Ingredient ingredient, float amount, @NotNull String unit) {
        this.ingredient = ingredient;
        this.unit = unit;
        this.amount = amount;
    }

    /**
     * Copy {@link RecipeIngredient}.
     *
     * @return a deep copy of {@link RecipeIngredient}
     */
    public RecipeIngredient copy() {
        return new RecipeIngredient(ingredient, amount, unit);
    }

    public long getId() {
        return id;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public @NotNull String getUnit() {
        return unit;
    }

    public void setUnit(@NotNull String unit) {
        this.unit = unit;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
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
