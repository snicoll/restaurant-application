package com.example.restaurant.reservation

import java.net.URI

data class RestaurantAvailability(
		val name: String,
		val available: Boolean,
		val confirmationUri: URI? = null
)