package com.reactivespring.webflux.demo.controller.v1;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import com.reactivespring.webflux.demo.document.ItemCapped;
import com.reactivespring.webflux.demo.repository.ItemReactiveCappedRepository;
import com.reactivespring.webflux.demo.repository.ItemReactiveRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ItemStreamController {

    public static final String V1_STREAM_ITEMS_ENDPOINT = "/v1/stream/items";

    @Autowired
    ItemReactiveCappedRepository repository;
        
    @GetMapping(value = V1_STREAM_ITEMS_ENDPOINT, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<ItemCapped> getItemStream() {
        return repository.findItemsBy();
    }
}
















