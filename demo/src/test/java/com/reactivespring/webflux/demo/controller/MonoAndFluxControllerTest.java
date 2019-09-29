package com.reactivespring.webflux.demo.controller;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@WebFluxTest
@DirtiesContext
public class MonoAndFluxControllerTest {

    @Autowired
    WebTestClient webTestClient;
    
    @Test
    public void flux_approach1() {
        // The first testing approach: we verify how the web application would get the response and validate it 
        Flux<Integer> response = webTestClient
            .get().uri("/flux")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .exchange()
            .expectStatus().isOk()
            .returnResult(Integer.class)
            .getResponseBody();
            
        StepVerifier
            .create(response)
            .expectSubscription()
            .expectNext(1,2,3,4)
            .verifyComplete();
    }
    
    @Test
    public void flux_approach2() {
        
        // The second testing approach: we don't evaluate the response, but only check the response request is valid
        webTestClient
            .get().uri("/flux")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
            .expectBodyList(Integer.class)
            .hasSize(4);
    }
    
    @Test
    public void flux_approach3() {
                
        List<Integer> expected = Arrays.asList(1,2,3,4);
        
        // The third testing approach: we get result in the response body and compare with expected result
        EntityExchangeResult<List<Integer>>  entityExchangeResult = webTestClient
            .get().uri("/flux")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Integer.class)
            .returnResult();
        
        assertEquals(entityExchangeResult.getResponseBody(), expected);
    }
    
    @Test
    public void flux_approach4() {
                
        List<Integer> expected = Arrays.asList(1,2,3,4);
        
        // The 4th testing approach: we consume the result, which gives us access to the response body and
        // then compare with expected values.
        webTestClient
            .get().uri("/flux")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Integer.class)
            .consumeWith((response)-> {
                assertEquals(response.getResponseBody(), expected);
            });
        
    }
    
    @Test
    public void fluxStream_approach1() {
        // Testing of infinite stream of Long values emitted from the /fluxstream end point
        Flux<Long> response = webTestClient
            .get().uri("/fluxstream")
            .accept(MediaType.APPLICATION_STREAM_JSON)
            .exchange()
            .expectStatus().isOk()
            .returnResult(Long.class)
            .getResponseBody();
            
        StepVerifier
            .create(response)
            .expectSubscription()
            .expectNext(0l,1l,2l,3l,4l)
            .thenCancel()
            .verify();
    }
    
    
    @Test
    public void mono_approach1() {
        Integer expected = 1;
        webTestClient
            .get().uri("/mono")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Integer.class)
            .consumeWith((response)-> {
                assertEquals(response.getResponseBody(), expected);
            });
    }
}
