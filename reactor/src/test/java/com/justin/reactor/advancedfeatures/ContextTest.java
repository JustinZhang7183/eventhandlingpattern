package com.justin.reactor.advancedfeatures;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Description: test case for context.
 *
 * @author Justin_Zhang
 * @date 11/25/2022 16:03
 */
public class ContextTest {
  @Test
  public void write_and_read_context() {
    String key = "message";
    Mono<String> r = Mono.just("Hello")
        .flatMap(s -> Mono.deferContextual(ctx ->
            Mono.just(s + " " + ctx.get(key))))
        .contextWrite(ctx -> ctx.put(key, "World"));
    // subscription signal flows from bottom to top, so write first, then read it.
    StepVerifier.create(r)
        .expectNext("Hello World")
        .verifyComplete();
  }

  @Test
  public void write_and_read_context_order() {
    String key = "message";
    Mono<String> r = Mono.just("Hello")
        .contextWrite(ctx -> ctx.put(key, "World"))
        .flatMap(s -> Mono.deferContextual(ctx ->
            Mono.just(s + " " + ctx.getOrDefault(key, "Stranger"))));

    StepVerifier.create(r)
        .expectNext("Hello Stranger")
        .verifyComplete();
  }

  @Test
  public void write_and_read_context_overwrite() {
    String key = "message";
    Mono<String> r = Mono
        .deferContextual(ctx -> Mono.just("Hello " + ctx.get(key)))
        .contextWrite(ctx -> ctx.put(key, "Reactor"))
        .contextWrite(ctx -> ctx.put(key, "World"));

    StepVerifier.create(r)
        .expectNext("Hello Reactor")
        .verifyComplete();
  }

  @Test
  public void context_is_not_propagated_along_with_data_signal() {
    String key = "message";
    Mono<String> r = Mono
        .deferContextual(ctx -> Mono.just("Hello " + ctx.get(key)))
        .contextWrite(ctx -> ctx.put(key, "Reactor"))
        .flatMap(s -> Mono.deferContextual(ctx ->
            Mono.just(s + " " + ctx.get(key))))
        .contextWrite(ctx -> ctx.put(key, "World"));

    StepVerifier.create(r)
        .expectNext("Hello Reactor World")
        .verifyComplete();
  }

  @Test
  public void write_context_in_flat_map() {
    String key = "message";
    Mono<String> r = Mono.just("Hello")
        .flatMap(s -> Mono
            .deferContextual(ctxView -> Mono.just(s + " " + ctxView.get(key)))
        )
        .flatMap(s -> Mono
            .deferContextual(ctxView -> Mono.just(s + " " + ctxView.get(key)))
            .contextWrite(ctx -> ctx.put(key, "Reactor"))
        )
        .contextWrite(ctx -> ctx.put(key, "World"));

    StepVerifier.create(r)
        .expectNext("Hello World Reactor")
        .verifyComplete();
  }
}
