package com.reactivespring.webflux.demo.handler;


import java.time.Duration;

import javax.swing.plaf.basic.BasicComboBoxUI.ItemHandler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.reactivespring.webflux.demo.controller.v1.ItemStreamController;
import com.reactivespring.webflux.demo.document.ItemCapped;
import com.reactivespring.webflux.demo.repository.ItemReactiveCappedRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext
@ActiveProfiles("test")
@Slf4j
public class ItemStreamsHandlerTest {

    @Autowired
    WebTestClient webTestClient;
    
    @Autowired
    ItemReactiveCappedRepository itemReactiveCappedRepository;
    
    @Autowired
    MongoOperations mongoOperations;
    
    private void createCappedDataSet() {
    	mongoOperations.dropCollection(ItemCapped.class);
    	mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped());
    	
    	// non blocking periodic insertion of an new item in the database every one second
    	Flux<ItemCapped> item = Flux.interval(Duration.ofSeconds(1))
    			.map(i -> new ItemCapped(null, "Random Item "+i, (100.00 + i)))
    			.take(5);
    	
    	// we subscribed to the flux created above so that each time a new item is created, it will get inserted in the
    	// database
    	itemReactiveCappedRepository
    		.insert(item)
    		.doOnNext((itemCapped -> {
    			log.info("Inserted item: "+itemCapped);
    		}))
    		.blockLast();
    	
    }
    @Before
    public void setup() {
    	createCappedDataSet();
    }
    
    @Test
    public void testStreamAllItems() {
    	Flux<ItemCapped> items = webTestClient.get().uri(ItemsHandler.V1_STREAM_ITEMS_FUNCTIONAL_ENDPOINT)
    			.exchange()
    			.expectStatus().isOk()
    			.returnResult(ItemCapped.class)
    			.getResponseBody()
    			.take(5);
    	
    	StepVerifier.create(items)
    		.expectNextCount(5)
    		.thenCancel()
    		.verify();
    			
    }
}