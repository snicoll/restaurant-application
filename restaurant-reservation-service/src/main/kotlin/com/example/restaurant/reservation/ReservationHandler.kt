package com.example.restaurant.reservation

import java.time.Duration
import java.util.Random

import reactor.core.publisher.Mono

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.util.UriComponentsBuilder

@Component
class ReservationHandler {

	private val random = Random()

	fun checkAvailability(request: ServerRequest) =
			ServerResponse.ok().body(getAvailability(request))

	private fun getAvailability(request: ServerRequest): Mono<RestaurantAvailability> {
		val name = request.pathVariable("name")
		return Mono.delay(randomThinkTime()).then(randomAvailability())
				.map { RestaurantAvailability(name, it, if (it) confirmationUrl(name) else null) }
	}

	/**
	 * Simulate restaurant figuring out if they have a table left.
	 */
	private fun randomThinkTime() =
			Duration.ofMillis(1000L + random.nextInt(7000))

	private fun randomAvailability(): Mono<Boolean> =
			Mono.defer { Mono.just(random.nextBoolean()) }

	private fun confirmationUrl(name: String) = UriComponentsBuilder
			.fromUriString("/restaurants/{name}/book")
			.buildAndExpand(name)
			.toUri()

}
