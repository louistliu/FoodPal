package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Wrapper class of a list of recipes.
 */
public class RecipeList {

    private List<Recipe> recipeList;

    public RecipeList() {
        this.recipeList = new ArrayList<>();
    }

    public List<Recipe> getRecipeList() {
        return recipeList;
    }

    public void setRecipeList(List<Recipe> recipeList) {
        this.recipeList = recipeList;
    }

    /**
     * Adds a recipe without performing any checks.
     *
     * @param recipe recipe object to be added to recipes.
     */
    public void addRecipe(Recipe recipe) {
        this.recipeList.add(recipe);
    }

    /**
     * Removes a {@link Recipe} from a list if it is contained.
     *
     * @param recipe recipe to remove
     */
    public void deleteRecipe(Recipe recipe) {
        this.recipeList.remove(recipe);
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
