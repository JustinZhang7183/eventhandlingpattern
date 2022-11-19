package com.justin.reactor.sample;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

/**
 * Description: the sample of backpressure and reshape requests.
 * <p>
 * When implementing backpressure in Reactor, the way consumer pressure is propagated
 * back to producer is by sending a request to the upstream operator.
 * </p>
 * <p>
 * The most direct ways of subscribing all immediately trigger an unbounded request of
 * Long.MAX_VALUE.
 * 1.subscribe() and most of its lambda-based variants
 * (except the one that has a custom Subscription).
 * 2.block(), blockFirst() and blockLast()
 * 3.iterating over a toIterable() or toStream()
 * </p>
 *
 * @author Justin_Zhang
 * @date 11/12/2022 15:05
 */
@Slf4j
public class BackpressureAndReshapeRequestsSample {
  /**
   * simple way to custom request to implement backpressure.
   */
  public void simpleWayToCustomRequest() {
    Flux.range(1, 10)
        .doOnRequest(num -> log.info("do on request: {}", num))
        .subscribe(new BaseSubscriber<Integer>() {
          @Override
          protected void hookOnSubscribe(Subscription subscription) {
            request(1);
          }

          @Override
          protected void hookOnNext(Integer value) {
            log.info("cancelling after having received {}", value);
            // cancel or don't cancel all can stop.
            cancel();
          }
        });
  }

  /**
   * change the demand from downstream.
   * TODO: how to test effect of backpressure using limitRate?
   */
  public void operatorsThatChangeTheDemandFromDownstream() {
    Flux.range(1, 20).limitRate(5)
        .subscribe(num -> log.info("limitRateWithPrefetch: {}", num.toString()));

    Flux.range(1, 20).limitRate(2, 5)
        .subscribe(num -> log.info("limitRateWithTide: {}", num.toString()));

    Flux.range(1, 20).limitRequest(5)
        .subscribe(num -> log.info("limitRequest: {}", num));
  }
}
