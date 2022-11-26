package com.justin.reactor.advancedfeatures;

import java.util.Arrays;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
 * Description: test case for batching.
 *
 * @author Justin_Zhang
 * @date 11/24/2022 17:12
 */
@Slf4j
public class BatchingTest {
  @Test
  public void grouping() {
    // TODO: how to use flatMap to consume groups?
    Flux<String> flux = Flux.just(1, 3, 5, 2, 4, 6, 11, 12, 13)
        .groupBy(i -> i % 2 == 0 ? "even" : "odd")
        .concatMap(g -> g.defaultIfEmpty(-1) //if empty groups, show them
            .map(String::valueOf) //map to string
            .startWith(g.key())); //start with the group's key
    StepVerifier.create(flux)
        .expectNext("odd", "1", "3", "5", "11", "13")
        .expectNext("even", "2", "4", "6", "12")
        .verifyComplete();
    flux.subscribe(log::info);
  }

  @Test
  public void windowing() {
    StepVerifier.create(
            Flux.range(1, 10)
                // if maxSize < skip, some elements are dropped.
                .window(5, 3) //overlapping windows
                .concatMap(g -> g.defaultIfEmpty(-1)) //show empty windows as -1
        )
        .expectNext(1, 2, 3, 4, 5)
        .expectNext(4, 5, 6, 7, 8)
        .expectNext(7, 8, 9, 10)
        .expectNext(10)
        .verifyComplete();

    StepVerifier.create(
            Flux.just(1, 3, 5, 2, 4, 6, 11, 12, 13)
                .windowWhile(i -> i % 2 == 0)
                .concatMap(g -> g.defaultIfEmpty(-1))
        )
        .expectNext(-1, -1, -1) //respectively triggered by odd 1 3 5
        .expectNext(2, 4, 6) // triggered by 11
        .expectNext(12) // triggered by 13
        // however, no empty completion window is emitted (would contain extra matching elements)
        .verifyComplete();
  }

  /**
   * the difference between window and buffer is the former emit the Flux,
   * the latter emit Collection.
   */
  @Test
  public void buffering() {
    StepVerifier.create(
            Flux.range(1, 10)
                .buffer(5, 3) //overlapping buffers
        )
        .expectNext(Arrays.asList(1, 2, 3, 4, 5))
        .expectNext(Arrays.asList(4, 5, 6, 7, 8))
        .expectNext(Arrays.asList(7, 8, 9, 10))
        .expectNext(Collections.singletonList(10))
        .verifyComplete();

    StepVerifier.create(
            Flux.just(1, 3, 5, 2, 4, 6, 11, 12, 13)
                .bufferWhile(i -> i % 2 == 0)
        )
        .expectNext(Arrays.asList(2, 4, 6)) // triggered by 11
        .expectNext(Collections.singletonList(12)) // triggered by 13
        .verifyComplete();
  }
}
