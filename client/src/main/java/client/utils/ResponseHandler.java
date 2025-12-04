package client.utils;

import client.interfaces.IResponseHandler;
import com.google.common.base.Objects;
import java.lang.reflect.Type;
import java.util.function.Consumer;
import org.jspecify.annotations.NullMarked;
import org.springframework.messaging.simp.stomp.StompHeaders;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

/**
 * Wrapper class used for handling webSocket responses.
 * Always created as an anonymous class
 */
public abstract class ResponseHandler<T> extends TypeReference<T> implements IResponseHandler<T> {
    private final Consumer<T> responseHandler;

    /**
     * Initializes the private fields, sets the type of {@link TypeReference}.
     *
     * @param responseHandler function that handles the response object
     */
    public ResponseHandler(Consumer<T> responseHandler) {
        this.responseHandler = responseHandler;
    }

    @Override
    public void handleResponse(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        T parsedObj = (T) mapper.convertValue(obj, this);
        responseHandler.accept(parsedObj);
    }

    @Override
    @NullMarked
    public Type getPayloadType(StompHeaders headers) {
        return super.getType();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        ResponseHandler<?> that = (ResponseHandler<?>) object;
        return Objects.equal(responseHandler, that.responseHandler);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(responseHandler);
    }
}