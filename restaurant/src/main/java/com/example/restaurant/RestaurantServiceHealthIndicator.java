package com.example.restaurant;

import reactor.core.publisher.Mono;

import org.springframework.boot.actuate.health.AbstractReactiveHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class RestaurantServiceHealthIndicator extends AbstractReactiveHealthIndicator {

	private final WebClient webClient;

	public RestaurantServiceHealthIndicator(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.baseUrl("http://localhost:8081").build();
	}

	@Override
	protected Mono<Health> doHealthCheck(Health.Builder builder) {
		return this.webClient.get().uri("/actuator/health")
				.exchange()
				.map(ClientResponse::statusCode)
				.map((status -> status.is2xxSuccessful() ? Health.up() : Health.down()))
				.map(Health.Builder::build);
	}

}
