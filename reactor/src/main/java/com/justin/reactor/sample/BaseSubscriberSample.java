package com.justin.reactor.sample;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;

/**
 * Description: an alternative subscribe way to lambdas.
 *
 * @author Justin_Zhang
 * @date 11/12/2022 14:10
 */
@Slf4j
public class BaseSubscriberSample {
  /**
   * use SampleSubscriber to subscribe.
   */
  public void useSampleSubscriberToSubscribe() {
    SampleSubscriber<Integer> sampleSubscriber = new SampleSubscriber<>();
    Flux<Integer> ints = Flux.range(1, 4);
    ints.subscribe(sampleSubscriber);
  }

  static class SampleSubscriber<T> extends BaseSubscriber<T> {
    @Override
    protected void hookOnSubscribe(Subscription subscription) {
      log.info("on subscribe");
      // default is unbounded request without backpressure.
      // super.hookOnSubscribe(subscription);
      // if you assign certain number of request, you must assign at hootOnNext to continue receive.
      request(1);
    }

    @Override
    protected void hookOnNext(T value) {
      super.hookOnNext(value);
      log.info("on next: {}", value.toString());
      request(1);
    }

    @Override
    protected void hookOnComplete() {
      super.hookOnComplete();
      log.info("on complete");
    }

    @Override
    protected void hookOnError(Throwable throwable) {
      super.hookOnError(throwable);
      log.info("on error");
    }

    @Override
    protected void hookOnCancel() {
      super.hookOnCancel();
      log.info("on cancel");
    }

    @Override
    protected void hookFinally(SignalType type) {
      super.hookFinally(type);
      log.info("hook finally");
    }
  }
}


