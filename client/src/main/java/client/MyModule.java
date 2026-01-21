/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package client;

import client.scenes.AddIngredientScreenCtrl;
import client.scenes.AddInstructionScreenCtrl;
import client.scenes.MainCtrl;
import client.scenes.MainScreenCtrl;
import client.utils.Config;
import client.utils.communication.ServerSockets;
import client.utils.communication.StompClientProvider;
import client.utils.communication.StompSessionHandler;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

/**
 * Binds javafx scenes in a relation for dependency injection.
 */
public class MyModule implements Module {

    private final Config config;

    /**
     * Create a new Guice module instance bound with the provided configuration.
     *
     * @param config application configuration to install into the injector
     */
    public MyModule(Config config) {
        this.config = config;
    }

    /**
     * Bind application-wide singleton controllers and utilities. The
     * provided config instance is bound so it can be injected
     * into scene controllers and helpers.
     */
    @Override
    public void configure(Binder binder) {
        binder.bind(Config.class).toInstance(config);
        binder.bind(MainCtrl.class).in(Scopes.SINGLETON);

        // Setup Providers
        binder.bind(WebSocketStompClient.class).toProvider(StompClientProvider.class);
        binder.bind(StompSessionHandler.class);

        // Bind the FoodPal Main Screen controller
        binder.bind(MainScreenCtrl.class).in(Scopes.SINGLETON);
        binder.bind(AddIngredientScreenCtrl.class).in(Scopes.SINGLETON);
        binder.bind(AddInstructionScreenCtrl.class).in(Scopes.SINGLETON);
        binder.bind(ServerSockets.class);
    }

    /**
     * Provides a {@link WebSocketClient}.
     *
     * @return {@link StandardWebSocketClient}
     */
    @Provides
    public WebSocketClient provideStandardWebSocketClient() {
        return new StandardWebSocketClient();
    }

    /**
     * Provides {@link TaskScheduler}.
     *
     * @return {@link DefaultManagedTaskScheduler}
     */
    @Provides
    public TaskScheduler provideTaskScheduler() {
        return new DefaultManagedTaskScheduler();
    }

    /**
     * Provides {@link MessageConverter}.
     *
     * @return {@link JacksonJsonMessageConverter}
     */
    @Provides
    public MessageConverter provideMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
