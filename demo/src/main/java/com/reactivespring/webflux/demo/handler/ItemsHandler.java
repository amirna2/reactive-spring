package com.reactivespring.webflux.demo.handler;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.reactivespring.webflux.demo.document.Item;
import com.reactivespring.webflux.demo.document.ItemCapped;
import com.reactivespring.webflux.demo.repository.ItemReactiveCappedRepository;
import com.reactivespring.webflux.demo.repository.ItemReactiveRepository;

import reactor.core.publisher.Mono;

@Component
public class ItemsHandler {

    public static final String V1_ITEMS_FUNCTIONAL_ENDPOINT = "/v1/functional/items";
    public static final String V1_ERRORS_FUNCTIONAL_ENDPOINT = "/v1/functional/errors";
    public static final String V1_STREAM_ITEMS_FUNCTIONAL_ENDPOINT = "/v1/functional/stream/items";
    

    static final Mono<ServerResponse> notFound = ServerResponse.notFound().build();

    @Autowired
    ItemReactiveRepository repository;
    
    @Autowired
    ItemReactiveRepository itemReactiveRepository;
    
    @Autowired
    ItemReactiveCappedRepository itemReactiveCappedRepository;
    
    public Mono<ServerResponse> getAllItems(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(repository.findAll(), Item.class);
    }

    public Mono<ServerResponse> getOneItem(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Item> found = repository.findById(id);
        
        return found.flatMap(item -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .body(fromObject(item)))
                    .switchIfEmpty(notFound);
    }
    
    public Mono<ServerResponse> createItem(ServerRequest request) {
        Mono<Item> itemToCreate = request.bodyToMono(Item.class);
        return itemToCreate.flatMap(item -> 
            ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(repository.save(item),Item.class));
    }
   
    public Mono<ServerResponse> deleteItem(ServerRequest request) {
        String id = request.pathVariable("id");        
        Mono<Void> deleted = repository.deleteById(id);
                
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(deleted,Void.class);
    }
    
    public Mono<ServerResponse> updateItem(ServerRequest serverRequest) {
        
        String id = serverRequest.pathVariable("id");

        Mono<Item> updatedItem = serverRequest.bodyToMono(Item.class)
                .flatMap((item) -> {
                    Mono<Item> itemMono = repository.findById(id)
                            .flatMap(currentItem -> {
                                currentItem.setDescription(item.getDescription());
                                currentItem.setPrice(item.getPrice());
                                return repository.save(currentItem);

                            });
                    return itemMono;
                });

        return updatedItem.flatMap(item ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromObject(item)))
                .switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> runtimeException(ServerRequest request) {
    	throw new RuntimeException("RuntimeException test");
    }
    
    public Mono<ServerResponse> itemsStream(ServerRequest reuest) {
    	return ServerResponse.ok()
    			.contentType(MediaType.APPLICATION_STREAM_JSON)
    			.body(itemReactiveCappedRepository.findItemsBy(), ItemCapped.class);
    }
}
