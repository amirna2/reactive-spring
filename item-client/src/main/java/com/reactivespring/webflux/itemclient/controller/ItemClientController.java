package com.reactivespring.webflux.itemclient.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.reactivespring.webflux.itemclient.domain.Item;

import reactor.core.publisher.Flux;

@RestController
public class ItemClientController {

    WebClient webClient = WebClient.create("http://localhost.8080");
    
    @GetMapping("/client/retrieve")
    public Flux<Item> getAllItemsUsingRetrieve() {
        return webClient.get().uri("/v1/items")
        .accept(MediaType.APPLICATION_JSON_UTF8)
        .retrieve()
        .bodyToFlux(Item.class)
        .log("[ItemClientController] getAllItemsUsingRetrieve: ");
    }
    
    @GetMapping("/client/exchange")
    public Flux<Item> getAllItemsUsingExchange(){

        return webClient.get().uri("/v1/items")
                .exchange()
                .flatMapMany(clientResponse -> clientResponse.bodyToFlux(Item.class))
                .log("Items in Client Project exchange : ");
    }
    
}