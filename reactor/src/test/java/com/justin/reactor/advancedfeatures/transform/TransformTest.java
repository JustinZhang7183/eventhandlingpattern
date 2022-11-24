package com.justin.reactor.advancedfeatures.transform;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

/**
 * Description: test case for transform test.
 *
 * @author Justin_Zhang
 * @date 11/24/2022 14:15
 */
public class TransformTest {
  @Test
  public void transform() {
    Function<Flux<String>, Flux<String>> filterAndMap =
        f -> f.filter(color -> !color.equals("orange"))
            .map(String::toUpperCase);
    Flux.fromIterable(Arrays.asList("blue", "green", "orange", "purple"))
        .doOnNext(System.out::println)
        .transform(filterAndMap)
        .subscribe(d -> System.out.println("Subscriber to Transformed MapAndFilter: " + d));
  }
  
  @Test
  public void transform_deferred() {
    AtomicInteger ai = new AtomicInteger();
    Function<Flux<String>, Flux<String>> filterAndMap = f -> {
      if (ai.incrementAndGet() == 1) {
        return f.filter(color -> !color.equals("orange"))
            .map(String::toUpperCase);
      }
      return f.filter(color -> !color.equals("purple"))
          .map(String::toUpperCase);
    };

    Flux<String> composedFlux =
        Flux.fromIterable(Arrays.asList("blue", "green", "orange", "purple"))
            .doOnNext(System.out::println)
            .transformDeferred(filterAndMap);

    composedFlux.subscribe(d -> System.out.println("Subscriber 1 to Composed MapAndFilter :" + d));
    composedFlux.subscribe(d -> System.out.println("Subscriber 2 to Composed MapAndFilter: " + d));
  }
}
