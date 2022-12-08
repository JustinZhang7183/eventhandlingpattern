package com.justin.reactor.sample;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * Description: sample of subscribe a publisher.
 *
 * @author Justin_Zhang
 * @date 11/11/2022 16:20
 */
@Slf4j
public class SubscribeSample {
  private final Flux<Integer> flux = Flux.just(1, 2, 3, 0, 4);

  public void subscribeDoNothing() {
    flux.subscribe();
  }

  public void subscribeDoSomething() {
    flux.subscribe(num ->  log.info(num.toString()));
  }

  /**
   * subscribe do something and react error.
   */
  public void subscribeDoSomethingAndReactError() {
    flux.map(num -> num / num)
        .subscribe(num -> log.info(num.toString()),
          err -> log.info(err.getMessage()));
  }

  /**
   * subscribe do something and react error and run some code when complete.
   */
  public void subscribeDoSomethingAndReactErrorAndRunSomeCodeWhenComplete() {
    flux.subscribe(num -> log.info(num.toString()),
        err -> log.info(err.getMessage()),
        () -> log.info("complete"));
  }

  /**
   * subscribe do something and react error and run some code when complete
   * and so something with subscription.
   * this is a deprecated method. Why does it only do something with subscription?
   * GOTCHA: because I implement subscription custom,
   * GOTCHA: and I don't assign the number of request before. (line56)
   */
  public void
      subscribeDoSomethingReactErrorRunSomeCodeCompleteDoSomethingWithSubscription() {
    flux.subscribe(num -> log.info(num.toString()),
        err -> log.info(err.getMessage()),
        () -> log.info("complete"),
        subscription -> {
          log.info(subscription.toString());
          subscription.request(2);
        });
  }
}
