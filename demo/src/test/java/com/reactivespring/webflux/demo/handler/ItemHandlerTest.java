package com.reactivespring.webflux.demo.handler;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.reactivespring.webflux.demo.document.Item;
import com.reactivespring.webflux.demo.repository.ItemReactiveRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext
@ActiveProfiles("test")
public class ItemHandlerTest {

    @Autowired
    WebTestClient webTestClient;
    @Autowired
    ItemReactiveRepository itemReactiveRepository;
    
    List<Item> items = Arrays.asList(
            new Item(null,"Samsung TV", 400.00),
            new Item(null,"Apple iPad Pro", 1099.00),
            new Item(null,"Amazon Echo", 99.00),
            new Item(null,"Asus ChromeBox", 189.99),
            new Item("abc","Apple Airpods", 99.00));
    
    @Before
    public void setup() {
        itemReactiveRepository.deleteAll()
        .thenMany(Flux.fromIterable(items))
        .flatMap(itemReactiveRepository::save)
        .doOnNext((item -> {
            System.out.println("Inserted Item: "+ item);
        }))
        // blocking only for testing purposes. 
        //This ensures the save operation is completed before any unit test is started
        .blockLast();       
    }
    
    @Test
    public void getAllItems() {
        webTestClient.get()
            .uri(ItemsHandler.V1_ITEMS_FUNCTIONAL_ENDPOINT)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
            .expectBodyList(Item.class)
            .hasSize(5);
    }
    
    @Test
    public void getAllItems_approach2() {
        webTestClient.get()
            .uri(ItemsHandler.V1_ITEMS_FUNCTIONAL_ENDPOINT)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
            .expectBodyList(Item.class)
            .consumeWith((response)-> {
                List<Item> items = response.getResponseBody();
                items.forEach(item -> assertTrue(item.getId() != null));
            });
    }
    
    @Test
    public void getAllItems_approach3() {
        Flux<Item> itemFlux = webTestClient.get()
            .uri(ItemsHandler.V1_ITEMS_FUNCTIONAL_ENDPOINT)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
            .returnResult(Item.class)
            .getResponseBody();

        StepVerifier.create(itemFlux.log())
        .expectSubscription()
        .expectNextCount(5)
        .verifyComplete();
    }
    
    @Test
    public void getOneItem() {
        webTestClient.get()
            .uri(ItemsHandler.V1_ITEMS_FUNCTIONAL_ENDPOINT.concat("/{id}"), "abc")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.price", 99.00);
    }
    
    @Test
    public void getOneItem_NotFound() {
        webTestClient.get()
            .uri(ItemsHandler.V1_ITEMS_FUNCTIONAL_ENDPOINT.concat("/{id}"), "xyz")
            .exchange()
            .expectStatus().isNotFound();
    }
    
    @Test
    public void createItem() {
        Item item = new Item(null, "Samsung Galaxy Tab 9", 349.99);
        webTestClient.post()
            .uri(ItemsHandler.V1_ITEMS_FUNCTIONAL_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .body(Mono.just(item), Item.class)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.id").isNotEmpty()
            .jsonPath("$.price").isEqualTo(349.99)
            .jsonPath("$.description").isEqualTo("Samsung Galaxy Tab 9");
    }
    
    @Test
    public void deleteItem() {
        webTestClient.delete()
        .uri(ItemsHandler.V1_ITEMS_FUNCTIONAL_ENDPOINT.concat("/{id}"), "abc")
        .accept(MediaType.APPLICATION_JSON_UTF8)
        .exchange()
        .expectStatus().isOk();
    }
    
    @Test
    public void deleteItem_NotAllowed() {
        webTestClient.delete()
        .uri(ItemsHandler.V1_ITEMS_FUNCTIONAL_ENDPOINT.concat("/{id}"), "def")
        .accept(MediaType.APPLICATION_JSON_UTF8)
        .exchange()
        .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }
    
    @Test
    public void updateItem() {
        double newPrice = 89.00;
        Item newItem = new Item(null,"Apple Airpods", newPrice);
        
        webTestClient.put()
            .uri(ItemsHandler.V1_ITEMS_FUNCTIONAL_ENDPOINT.concat("/{id}"), "abc")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .body(Mono.just(newItem), Item.class)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.price", newPrice);       
    }
    
    @Test
    public void updateItem_NotFound() {
        
        double newPrice = 89.00;
        Item newItem = new Item(null,"Apple Airpods", newPrice);
        
        webTestClient.put()
            .uri(ItemsHandler.V1_ITEMS_FUNCTIONAL_ENDPOINT.concat("/{id}"), "def")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .body(Mono.just(newItem), Item.class)
            .exchange()
            .expectStatus().isNotFound();       
    }
}
