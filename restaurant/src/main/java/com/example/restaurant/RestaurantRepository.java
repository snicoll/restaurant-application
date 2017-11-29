package com.example.restaurant;

import reactor.core.publisher.Flux;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface RestaurantRepository extends ReactiveCrudRepository<Restaurant, String> {

	Flux<Restaurant> findByCategoryAndPricePerPersonLessThan(String category, Double maxPrice);

}
