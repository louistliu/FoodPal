package client.utils.communication;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

/**
 * Provides an instance of {@link WebSocketClient} with
 * {@link WebSocketClient}, {@link TaskScheduler}, {@link MessageConverter}.
 */
@Singleton
public class StompClientProvider implements Provider<WebSocketStompClient> {

    @Inject
    private WebSocketClient socketClient;
    @Inject
    private TaskScheduler taskScheduler;
    @Inject
    private MessageConverter messageConverter;


    /**
     * Returns an instance of the provider.
     *
     * @return An instance of the provider.
     */
    @Override
    public WebSocketStompClient get() {
        var client = new WebSocketStompClient(socketClient);
        client.setMessageConverter(messageConverter);
        client.setTaskScheduler(taskScheduler);
        return client;
    }
}
