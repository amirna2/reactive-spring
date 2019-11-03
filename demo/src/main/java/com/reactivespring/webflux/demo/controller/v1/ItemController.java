package com.reactivespring.webflux.demo.controller.v1;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.reactivespring.webflux.demo.document.Item;
import com.reactivespring.webflux.demo.repository.ItemReactiveRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ItemController {

    public static final String V1_ITEMS_ENDPOINT = "/v1/items";

    @Autowired
    ItemReactiveRepository repository;
        
    @GetMapping(V1_ITEMS_ENDPOINT)
    public Flux<Item> getAllItems() {
        return repository.findAll();
    }
    
    @GetMapping(V1_ITEMS_ENDPOINT+"/{id}")
    public Mono<ResponseEntity<Item>> getOneItem(@PathVariable String id) {
        return repository.findById(id)
                .map((item) -> new ResponseEntity<>(item, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(V1_ITEMS_ENDPOINT)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createItem(@RequestBody Item item ) {
        return repository.save(item);
    }

    
    @DeleteMapping(V1_ITEMS_ENDPOINT+"/{id}")
    public Mono<Void> deleteItem(@PathVariable String id) {
        return repository.deleteById(id);
    }
    
/*    
    @DeleteMapping(V1_ITEMS_ENDPOINT+"/{id}")
    public Mono<ResponseEntity<Void>> deleteItem(@PathVariable String id) {
        if (id == null) {
            return Mono.just(new ResponseEntity<Void>(HttpStatus.METHOD_NOT_ALLOWED));
        }
       
        return repository.findById(id).log("Delete:")
                .flatMap(currentItem -> { 
                    repository.delete(currentItem).log("repository:delete");
                    return Mono.just(new ResponseEntity<Void>(HttpStatus.OK));
                })
                .defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));          
    }
*/    
    @PutMapping(V1_ITEMS_ENDPOINT+"/{id}")
    public Mono<ResponseEntity<Item>> updateItem(@PathVariable String id, @RequestBody Item item) {
        return repository.findById(id)
            .flatMap(currentItem -> { 
                currentItem.setDescription(item.getDescription());
                currentItem.setPrice(item.getPrice());
                return repository.save(currentItem);
            })
            .map(updatedItem -> new ResponseEntity<>(item, HttpStatus.OK))
            .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping(V1_ITEMS_ENDPOINT + "/runtimeException")
    public Flux<Item> runtimeException() {
    	return repository.findAll()
    			.concatWith(Mono.error(new RuntimeException("RuntimeException Test")));
    	
    }
    
    @GetMapping(V1_ITEMS_ENDPOINT + "/ioException")
    public Flux<Item> ioException() {
    	return repository.findAll()
    			.concatWith(Mono.error(new IOException("IOException Test")));
    	
    }
}
















