package com.reactivespring.webflux.demo.controller;

import java.time.Duration;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class FluxAndMonoController {

    @GetMapping(value = "/flux")
    public Flux<Integer> returnFlux() {
        // this end point returns a flux where the onNext element is delayed by a second, until the transmission is complete
        // the result when hitting that end point from the browser is the response will be delayed, until the browser has
        // received all the elements and then will display the result as a JSON payload
        return Flux.just(1,2,3,4)
                //.delayElements(Duration.ofSeconds(1))
                .log();
    }
    
    @GetMapping(value = "/fluxstream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    // this emits a flux of values (seconds) every 1 second indefinitely 
    public Flux<Long> returnFluxStream() {
        return Flux.interval(Duration.ofSeconds(1))
                .log();
    }
    
    @GetMapping(value = "/mono")
    public Mono<Integer> returnMono() {
        //a stream that contains 0 or 1 value
        return Mono.just(1)
                .log();
    }
}
