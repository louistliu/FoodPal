package commons.sockets;

/**
 * Types of payload that can be sent between client and server.
 */
public enum PayloadType {
    Ingredient,
    IngredientCollection,
    RecipeIngredient,
    RecipeIngredientCollection,
    Recipe,
    RecipeCollection,
}