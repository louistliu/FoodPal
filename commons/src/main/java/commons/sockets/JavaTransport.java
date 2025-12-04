package commons.sockets;

/**
 * A wrapper object to send {@code java.lang} Objects between client and server.
 * Does not support Lists.
 *
 * @param contents object to wrap
 * @param <T> type of object
 */
public record JavaTransport<T>(T contents) {
}
