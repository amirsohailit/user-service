package com.mytestorg.user.application.port.ouput;

import reactor.core.publisher.Mono;

public interface MessageBrokerServicePort {
    /**
     * Publishes an event message to a message broker asynchronously.
     * <p>
     * Currently, only Kafka is implemented, but this method can be easily implemented to support other brokers in the future.
     * If all retries fail, logs the error and saves the failed event to the repository for further investigation or reprocessing.
     * </p>
     *
     * @param event the event message to publish
     * @return a {@link Mono} that completes when the event is published or the failure is recorded
     */
    Mono<Void> publish( String event);
}
