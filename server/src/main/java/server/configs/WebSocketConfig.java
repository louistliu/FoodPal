package server.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Sets up websockets to use STOMP by target prefixes for socket data broker,
 * and application prefix for request endpoints. <br>
 * The broker automatically interchanges these, when handling requests based on default behavior.
 */
@Configuration
@EnableScheduling
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     *  Configures broker prefixes {@code /topic} for channels that broadcast new changes,
     *  {@code /queue} for client specific messages.<br>
     *  All should be sent and handled at {@code /api} prefixed endpoints.
     *  All methods should return Objects, that are Jackson parsable
     *
     * @param registry Web Socket server configuration object.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
    }

    /**
     *  Sets the initial connection endpoint to be {@code /food-pal}.
     *
     * @param registry Web Socket server configuration object.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/food-pal");
    }
}
