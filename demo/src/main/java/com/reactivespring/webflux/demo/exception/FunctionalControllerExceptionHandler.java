package com.reactivespring.webflux.demo.exception;

import java.util.Map;

import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.reactivespring.webflux.demo.handler.ItemsHandler;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class FunctionalControllerExceptionHandler extends AbstractErrorWebExceptionHandler {
	
		public FunctionalControllerExceptionHandler(
				ErrorAttributes errorAttributes,
				ApplicationContext applicationContext,
				ServerCodecConfigurer serverCodecConfigurer) {
			super(errorAttributes, new ResourceProperties(), applicationContext);
			super.setMessageWriters(serverCodecConfigurer.getWriters());
			super.setMessageReaders(serverCodecConfigurer.getReaders());

		}

		
		@Override
		protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
			return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
		}	
		
		private Mono<ServerResponse> renderErrorResponse(ServerRequest serverRequest) {
			
			Map<String, Object> attributes = this.getErrorAttributes(serverRequest, false);
			log.info("Error Atrributes" + attributes);
			return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.body(BodyInserters.fromObject(attributes.get("message")));
		}

}
