package com.reactivespring.webflux.demo;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.reactivespring.webflux.demo.document.Item;
import com.reactivespring.webflux.demo.repository.ItemReactiveRepository;

import reactor.core.publisher.Flux;

@Component
@Profile("!test")
public class DemoInitializer implements CommandLineRunner {

    List<Item> items = Arrays.asList(
            new Item(null,"Samsung TV", 400.00),
            new Item(null,"Apple iPad Pro", 1099.00),
            new Item(null,"Amazon Echo", 99.00),
            new Item(null,"Asus ChromeBox", 189.99),
            new Item(null,"Apple Airpods", 99.00));
    
    @Autowired
    ItemReactiveRepository itemReactiveRepository;
    
    @Override
    public void run(String... args) throws Exception {
        itemReactiveRepository.deleteAll()
        .thenMany(Flux.fromIterable(items))
        .flatMap(itemReactiveRepository::save)
        .thenMany(itemReactiveRepository.findAll())
        .subscribe((item -> {
            System.out.println("[Initializer] Item document created: "+ item);
        }));    
    }

}
