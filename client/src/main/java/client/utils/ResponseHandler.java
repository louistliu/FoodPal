package client.utils;

import client.interfaces.IResponseHandler;
import java.lang.reflect.Type;
import java.util.function.Consumer;
import org.jspecify.annotations.NullMarked;
import org.springframework.messaging.simp.stomp.StompHeaders;

/**
 * Wrapper class used for handling webSocket responses.
 */
public record ResponseHandler<T>(Consumer<T> responseHandler, Type type)
      implements IResponseHandler<T> {

    @SuppressWarnings("unchecked")
    @Override
    public void handleResponse(Object obj) {
        // not the ideal way, but this is very convenient
        // currently does not support Lists of objects
        responseHandler.accept((T) obj);
    }

    @Override
    @NullMarked
    public Type getPayloadType(StompHeaders headers) {
        return type;
    }
}
