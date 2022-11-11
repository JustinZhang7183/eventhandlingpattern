package com.justin.reactor.sample;

import java.util.Arrays;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Description: the sample of creating mono and flux (publisher) by their factory methods.
 *
 * @author Justin_Zhang
 * @date 11/11/2022 16:03
 */
public class CreateSample {
  /**
   * create flux.
   */
  public void createFlux() {
    Flux<String> seq1 = Flux.just("foo", "bar", "foobar");
    List<String> iterable = Arrays.asList("foo", "bar", "foobar");
    Flux<String> seq2 = Flux.fromIterable(iterable);
    Flux<Integer> numbersFromFiveToSeven = Flux.range(5, 3);
  }

  /**
   * create mono.
   */
  public void createMono() {
    Mono<String> noData = Mono.empty();
    Mono<String> data = Mono.just("foo");
  }
}
