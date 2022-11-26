package com.justin.reactor.advancedfeatures;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * Description: test case for parallelizing.
 *
 * @author Justin_Zhang
 * @date 11/25/2022 11:11
 */
public class ParallelizingTest {
  @Test
  public void parallel() {
    Flux.range(1, 10)
        .parallel(2)
        .subscribe(i -> System.out.println(Thread.currentThread().getName() + " -> " + i));
  }

  @Test
  public void parallel_run_on() {
    Flux.range(1, 10)
        .parallel(2)
        .runOn(Schedulers.parallel())
        // .sequential() can revert back to a normal Flux
        .subscribe(i -> System.out.println(Thread.currentThread().getName() + " -> " + i));
  }
}
