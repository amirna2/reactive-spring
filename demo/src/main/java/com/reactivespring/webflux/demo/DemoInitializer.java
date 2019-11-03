package com.reactivespring.webflux.demo;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

import com.reactivespring.webflux.demo.document.Item;
import com.reactivespring.webflux.demo.document.ItemCapped;
import com.reactivespring.webflux.demo.repository.ItemReactiveCappedRepository;
import com.reactivespring.webflux.demo.repository.ItemReactiveRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Component
@Profile("!test")
@Slf4j
public class DemoInitializer implements CommandLineRunner {

    List<Item> items = Arrays.asList(
            new Item(null,"Samsung TV", 400.00),
            new Item(null,"Apple iPad Pro", 1099.00),
            new Item(null,"Amazon Echo", 99.00),
            new Item(null,"Asus ChromeBox", 189.99),
            new Item(null,"Apple Airpods", 99.00));
    
    @Autowired
    ItemReactiveRepository itemReactiveRepository;
    
    @Autowired
    ItemReactiveCappedRepository itemReactiveCappedRepository;
    
    @Autowired
    MongoOperations mongoOperations;
    
    private void createDataSet() {
    	itemReactiveRepository.deleteAll()
        .thenMany(Flux.fromIterable(items))
        .flatMap(itemReactiveRepository::save)
        .thenMany(itemReactiveRepository.findAll())
        .subscribe((item -> {
            System.out.println("[Initializer] Item document created: "+ item);
        }));    
    }
    
    private void createCappedDataSet() {
    	mongoOperations.dropCollection(ItemCapped.class);
    	mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped());
    	
    	// non blocking periodic insertion of an new item in the database every one second
    	Flux<ItemCapped> item = Flux.interval(Duration.ofSeconds(1)).map(i -> 
    		new ItemCapped(null, "Random Item "+i, (100.00 + i)));
    	
    	// we subscribed to the flux created above so that each time a new item is created, it will get inserted in the
    	// database
    	itemReactiveCappedRepository
    		.insert(item)
    		.subscribe((itemCapped -> {
    			log.info("Inserted item: "+itemCapped);
    		}));
    }
    
    @Override
    public void run(String... args) throws Exception {
    	createDataSet();
    	createCappedDataSet();
    }

}
