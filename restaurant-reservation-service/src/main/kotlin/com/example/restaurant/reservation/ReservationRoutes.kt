package com.example.restaurant.reservation

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class ReservationRoutes {

	@Bean
	fun routes(handler: ReservationHandler) = router {
		GET("/restaurants/{name}/availability", handler::checkAvailability)
	}

}
