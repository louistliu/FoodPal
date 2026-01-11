package commons.sockets;

/**
 * A transport object used for serialization of two objects.
 *
 * @param first elem1
 * @param last elem2
 * @param <T> type of the first object
 * @param <K> type of the second object
 */
public record PairTransport<T, K>(T first, K last) {
}
