package com.justin.reactor.advancedfeatures.hotvscold;

import static reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Description: test case for hot and cold.
 *
 * @author Justin_Zhang
 * @date 11/24/2022 15:35
 */
public class HotVsColdTest {
  @Test
  public void cold_flux() {
    Flux<String> source = Flux.fromIterable(Arrays.asList("blue", "green", "orange", "purple"))
        .map(String::toUpperCase);
    source.subscribe(d -> System.out.println("Subscriber 1: " + d));
    source.subscribe(d -> System.out.println("Subscriber 2: " + d));
    // share() and replay() can turn a cold publisher into a hot one.
    // TODO: share() can't?
    Flux<String> replay = source.replay();
    replay.subscribe(d -> System.out.println("Subscriber 3: " + d));
    replay.subscribe(d -> System.out.println("Subscriber 4: " + d));
    // just() is a hot operator. defer() can transform just into a cold publisher.
    Flux<Integer> defer = Flux.defer(() -> Flux.just(1, 2));
    defer.subscribe(d -> System.out.println("Subscriber 5: " + d));
    defer.subscribe(d -> System.out.println("Subscriber 6: " + d));
    // TODO: just seems like a cold operator?
    Flux<Integer> just = Flux.just(1, 2);
    just.subscribe(d -> System.out.println("Subscriber 7: " + d));
    just.subscribe(d -> System.out.println("Subscriber 8: " + d));
  }

  @Test
  public void hot_flux() {
    Sinks.Many<String> hotSource = Sinks.unsafe().many().multicast().directBestEffort();

    Flux<String> hotFlux = hotSource.asFlux().map(String::toUpperCase);

    hotFlux.subscribe(d -> System.out.println("Subscriber 1 to Hot Source: " + d));

    hotSource.emitNext("blue", FAIL_FAST);
    hotSource.tryEmitNext("green").orThrow();

    hotFlux.subscribe(d -> System.out.println("Subscriber 2 to Hot Source: " + d));

    hotSource.emitNext("orange", FAIL_FAST);
    hotSource.emitNext("purple", FAIL_FAST);

    hotSource.emitComplete(FAIL_FAST);
  }
}
