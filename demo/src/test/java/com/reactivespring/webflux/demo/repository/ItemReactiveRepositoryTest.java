package com.reactivespring.webflux.demo.repository;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.reactivespring.webflux.demo.document.Item;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
@RunWith(SpringRunner.class)
@DirtiesContext
public class ItemReactiveRepositoryTest {

    List<Item> items = Arrays.asList(
            new Item(null,"Samsung TV", 400.00),
            new Item(null,"Apple iPad Pro", 1099.00),
            new Item(null,"Amazon Echo", 99.00),
            new Item(null,"Asus ChromeBox", 189.99),
            new Item("abc","Apple Airpods", 99.00));
    
    @Autowired
    ItemReactiveRepository itemReactiveRepository;
    
    @After 
    public void cleanup() {
        itemReactiveRepository.deleteAll();
    }
    
    @Before
    public void setupTest() {
        itemReactiveRepository.deleteAll()
            .thenMany(Flux.fromIterable(items))
            .flatMap(itemReactiveRepository::save)
            .doOnNext((item -> {
                System.out.println("Inserted Item: "+ item);
            }))
            .blockLast();  // blocking only for testing purposes
    }
    
    @Test
    public void getAllItems() {
        Flux<Item> itemFlux = itemReactiveRepository.findAll();
        
        StepVerifier.create(itemFlux)
        .expectSubscription()
        .expectNextCount(5)
        .expectComplete();
    }
    
    
    @Test
    public void getItemById() {
        Mono<Item> itemFlux = itemReactiveRepository.findById("abc");
        
        StepVerifier.create(itemFlux)
        .expectSubscription()
        .expectNextMatches((item) -> item.getDescription().equals("Apple Airpods"))
        .expectComplete();
    }
    
    
    @Test
    public void findByDescription() {
        Mono<Item> itemFlux = itemReactiveRepository.findByDescription("Apple Airpods").log("findByDescription: ");
        StepVerifier.create(itemFlux)
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }   
    
    @Test
    public void findByPriceInDollar() {
        Flux<Item> itemFlux = itemReactiveRepository.findByPrice(400.00).log("findByPrice: ");
        
        StepVerifier.create(itemFlux)
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }   
    
    @Test
    public void saveItem() {
        Item item = new Item("12345","Google Pixel 4", 699.00);
        Mono<Item> saved = itemReactiveRepository.save(item).log("saved Item: ");
        
        StepVerifier.create(saved)
        .expectSubscription()
        .expectNextMatches( (savedItem) -> 
            savedItem.getId().equals(item.getId()) && savedItem.getDescription().equals(item.getDescription()) )
        .verifyComplete();
    }
    
    @Test
    public void updateItem() {
        double newPrice = 299.00;
        Mono<Item> updated = itemReactiveRepository.findByDescription("Samsung TV")
        // map transforms the Flux Item into an Item object which can be manipulated
        .map(item -> {
            item.setPrice(newPrice); // update the item with the new price and return it
            return item;
        })
        .flatMap( item -> {
            return itemReactiveRepository.save(item); // saves the returned item back into the database
        });
        
        StepVerifier.create(updated)
        .expectSubscription()
        .expectNextMatches( (item) -> 
            item.getPrice() == newPrice)
        .verifyComplete();
    }
    
    @Test
    public void deleteItemById() {
        Mono<Void> deleted = itemReactiveRepository.findById("abc")
        // map transforms the Flux Item into an Item object which can be manipulated
        .map(Item::getId)
        .flatMap( id -> {
            return itemReactiveRepository.deleteById(id); // deletes the item from the database document
        });
        
        StepVerifier.create(deleted.log("deleteItem: "))
        .expectSubscription()
        .verifyComplete();
        
        Mono<Item> item = itemReactiveRepository.findById("abc").log("verify deleteById: ");
        StepVerifier.create(item)
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();
        
    }
    
    @Test
    public void deleteItemByDescription() {
        Mono<Object> deleted = itemReactiveRepository.findByDescription("Apple Airpods")
        // we can delete the object directly
        .flatMap( item -> {
            return itemReactiveRepository.delete(item); // deletes the item from the database document
        });
        
        StepVerifier.create(deleted.log("deleteItem: "))
        .expectSubscription()
        .verifyComplete();
        
        Mono<Item> item = itemReactiveRepository.findByDescription("Apple Airpods").log("findByDescription: ");
        StepVerifier.create(item)
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();
        
        itemReactiveRepository.deleteAll();
    }
}
