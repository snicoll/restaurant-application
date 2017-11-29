package com.example.restaurant;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;

@RunWith(SpringRunner.class)
@WebFluxTest(RestaurantController.class)
public class RestaurantControllerTests {

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private RestaurantRepository repository;

	@MockBean
	private RestaurantAvailabilityService restaurantAvailabilityService;

	@Test
	public void findCheapSushiPlaces() {
		given(repository.findByCategoryAndPricePerPersonLessThan("sushi", 20.00))
				.willReturn(Flux.just(new Restaurant("Sushi2Go", 15.00, "sushi")));
		this.webTestClient.get()
				.uri("/restaurants?category={c}&maxPrice={s}", "sushi", 20.00)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$[0].name").isEqualTo("Sushi2Go")
				.jsonPath("$.length()", 1);
	}

	@Test
	public void findAvailableRestaurants() {
		given(repository.findAll()).willReturn(Flux.just(
				new Restaurant("Sushi1", 11.00, "sushi"),
				new Restaurant("Sushi2", 12.00, "sushi"),
				new Restaurant("Sushi3", 13.00, "sushi"),
				new Restaurant("Sushi4", 14.00, "sushi")));
		given(restaurantAvailabilityService.getRestaurantAvailability(any(String.class)))
				.willAnswer(availableRestaurants("Sushi1", "Sushi3"));
		FluxExchangeResult<RestaurantAvailability> result = this.webTestClient.get()
				.uri("/restaurants/available")
				.accept(APPLICATION_STREAM_JSON)
				.exchange()
				.expectStatus().isOk()
				.returnResult(RestaurantAvailability.class);

		StepVerifier.create(result.getResponseBody())
				.consumeNextWith(availability ->
						assertThat(availability.getName()).isEqualTo("Sushi1"))
				.consumeNextWith(availability ->
						assertThat(availability.getName()).isEqualTo("Sushi3"))
				.verifyComplete();
	}

	private Answer<Object> availableRestaurants(String... names) {
		List<String> matches = Arrays.asList(names);
		return invocation -> {
			String name = invocation.getArgument(0);
			boolean available = matches.contains(name);
			URI uri = UriComponentsBuilder.fromUriString("/restaurants/{name}/book").build(name);
			return Mono.just(new RestaurantAvailability(name, available, available ? uri : null));
		};
	}

}
