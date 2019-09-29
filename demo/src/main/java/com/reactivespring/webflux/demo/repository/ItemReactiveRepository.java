package com.reactivespring.webflux.demo.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.reactivespring.webflux.demo.document.Item;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, String> {
    
    // Custom find method is named as findBy<Attribute> where attribute start with a capital letter
    // and matches the attribute we want to find a document by.
    // Here we want to find an Item by its "description" attribute
    Mono<Item> findByDescription(String description);
    
    
    Flux<Item> findByPrice(double priceInDollar);
    

}
