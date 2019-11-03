package com.reactivespring.webflux.demo.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;

import com.reactivespring.webflux.demo.document.Item;
import com.reactivespring.webflux.demo.document.ItemCapped;

import reactor.core.publisher.Flux;

public interface ItemReactiveCappedRepository extends ReactiveMongoRepository<ItemCapped, String> {
    
	@Tailable
	Flux<ItemCapped> findItemsBy();
}
