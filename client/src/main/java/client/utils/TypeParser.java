package client.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import commons.Ingredient;
import commons.RecipeIngredient;
import commons.sockets.PayloadType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.messaging.simp.stomp.StompConversionException;
import org.springframework.messaging.simp.stomp.StompHeaders;

/**
 * Parses data into {@link Type}.
 */
public class TypeParser {

    /**
     * Parse a PayloadType value to a Type value.
     *
     * @param type enum of types
     * @return returns {@link Type} from the enum.
     */
    public static Type getPayloadType(PayloadType type) {
        return switch (type) {
            case Ingredient -> Ingredient.class;
            case IngredientCollection -> new TypeReference<List<Ingredient>>() {
            }.getType();
            case RecipeIngredient -> RecipeIngredient.class;
            case RecipeIngredientCollection -> new TypeReference<List<RecipeIngredient>>() {
            }.getType();
            case Recipe, RecipeCollection -> throw new NotImplementedException("Type " + type);
            default ->
                  throw new IllegalArgumentException("Payload of type" + type + " does not exist");
        };
    }

    /**
     * Get the type of payload from STOMP headers.
     *
     * @param headers headers to be parsed into a type
     * @return returns the {@link Type} of payload
     */
    public static Type getPayloadType(StompHeaders headers) {
        System.out.println(headers);
        String typeStr = headers.getFirst(ServerSockets.OBJECT_TYPE_KEY);
        if (typeStr == null) {
            throw new StompConversionException("Null type in header: " + headers.toString());
        }
        PayloadType type = PayloadType.valueOf(typeStr);
        return TypeParser.getPayloadType(type);
    }
}
