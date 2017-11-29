package com.example.restaurant;

import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class RestaurantAvailabilityService {

	private final WebClient webClient;

	public RestaurantAvailabilityService(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.baseUrl("http://localhost:8081").build();
	}

	public Mono<RestaurantAvailability> getRestaurantAvailability(String name) {
		return this.webClient.get()
				.uri("/restaurants/{name}/availability", name)
				.retrieve().bodyToMono(RestaurantAvailability.class);
	}

}
