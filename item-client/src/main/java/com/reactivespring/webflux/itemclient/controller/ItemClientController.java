package com.reactivespring.webflux.itemclient.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.reactivespring.webflux.itemclient.domain.Item;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ItemClientController {

    WebClient webClient = WebClient.create("http://localhost:8080");
    
    @GetMapping("/client/retrieve")
    public Flux<Item> getAllItemsUsingRetrieve() {
        return webClient.get().uri("/v1/items")
        .accept(MediaType.APPLICATION_JSON_UTF8)
        .retrieve()
        .bodyToFlux(Item.class)
        .log("[ItemClientController] all items retrieve ");
    }
    
    @GetMapping("/client/exchange")
    public Flux<Item> getAllItemsUsingExchange(){

        return webClient.get().uri("/v1/items")
                .exchange()
                .flatMapMany(clientResponse -> clientResponse.bodyToFlux(Item.class))
                .log("[ItemClientController] all items exchange ");
    }
    
    @GetMapping("/client/retrieve/singleitem/{id}")
    public Mono<Item> getSingleItemUsingRetrieve(@PathVariable String id) {
        return webClient.get().uri("/v1/items/{id}",id)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .retrieve()
                .bodyToMono(Item.class)
                .log("[ItemClientController] get single item ");
    }
    
    @GetMapping("/client/exchange/singleitem/{id}")
    public Mono<Item> getSingleItemsUsingExchange(@PathVariable String id){

        return webClient.get().uri("/v1/items/{id}",id)
                .exchange()
                .flatMap(clientResponse -> clientResponse.bodyToMono(Item.class))
                .log("[ItemClientController] get all items exchange ");
    }
    
    
    @PostMapping("/client/createitem")
    public Mono<Item> createItem(@RequestBody Item item) {
        
        Mono<Item> newItem = Mono.just(item);
        
        return webClient.post().uri("/v1/items")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(newItem, Item.class)
                .retrieve()
                .bodyToMono(Item.class)
                .log("[ItemClientController] create single item ");
                
    }
    
    @PutMapping("/client/updateitem/{id}")
    public Mono<Item> updateItem(@PathVariable String id, @RequestBody Item item) {
        
        Mono<Item> updatedItem = Mono.just(item);
        
        return webClient.put().uri("/v1/items/{id}",id)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(updatedItem, Item.class)
                .retrieve()
                .bodyToMono(Item.class)
                .log("[ItemClientController] updated single item ");
    }
    
    @DeleteMapping("/client/deleteitem/{id}")
    public Mono<Void> deleteItem(@PathVariable String id) {
        
        return webClient.delete().uri("/v1/items/{id}",id)
                .exchange()
                .flatMap(clientResponse -> clientResponse.bodyToMono(Void.class)
                .log("[ItemClientController] delete item "));

    }
}
