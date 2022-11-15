package com.justin.reactor.sample;

import com.justin.reactor.util.ThreadUtil;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

/**
 * Description: the sample of sink.
 * safely produce from multiple threads by using Sinks.One and Sinks.Many.
 *
 * @author Justin_Zhang
 * @date 11/15/2022 14:29
 */
@Slf4j
public class SinkSample {
  /**
   * using multiple thread to emit elements (reply()) .
   */
  @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
  public void multipleThreadEmitElementsReply() {
    Sinks.Many<Integer> replaySink = Sinks.many().replay().all();
    Thread thread1 = new Thread(() -> {
      replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
      replaySink.asFlux().log().subscribe();
    });
    Thread thread2 = new Thread(() -> replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST));
    Thread thread3 = new Thread(() -> {
      replaySink.emitNext(3, Sinks.EmitFailureHandler.busyLooping(Duration.ofSeconds(2)));
      Sinks.EmitResult emitResult = replaySink.tryEmitNext(4);
      replaySink.asFlux()
          .takeWhile(num -> num < 4)
          .log()
          .blockLast(); // TODO: how does this work?
    });
    thread1.start();
    ThreadUtil.sleepBySecond(1);
    thread2.start();
    thread3.start();
    ThreadUtil.joinThread(thread1, thread2, thread3);
  }

  /**
   * sink.many.multicast.
   */
  public void sinkMulticast() {
    Sinks.Many<Integer> multicastSink = Sinks.many().multicast().onBackpressureBuffer();
    multicastSink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
    multicastSink.asFlux().log().subscribe();
    multicastSink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);
    multicastSink.asFlux().log().subscribe();
    multicastSink.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);
  }

  /**
   * sink.many.unicast.
   */
  public void sinkUnicast() {
    Sinks.Many<Integer> unicastSink = Sinks.many().unicast().onBackpressureBuffer();
    unicastSink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
    unicastSink.asFlux().log().subscribe();
    unicastSink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);
    unicastSink.asFlux().log().subscribe();
    unicastSink.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);
  }

  /**
   * sink.many.unicast.
   */
  public void sinkReplay() {
    Sinks.Many<Integer> replaySink = Sinks.many().replay().all();
    replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
    replaySink.asFlux().log().subscribe();
    replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);
    replaySink.asFlux().log().subscribe();
    replaySink.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);
  }

  /**
   * unicast backpressure buffer.
   */
  public void unicastBackpressureBuffer() {
    Sinks.Many<Object> unicastSink = Sinks.many().unicast()
        .onBackpressureBuffer(Queues.one().get());
    unicastSink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
    unicastSink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);
    unicastSink.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);
    unicastSink.asFlux().log().subscribe();
  }

  /**
   * multicast backpressure buffer.
   */
  public void multicastBackpressureBuffer() {
    // By default, if all of its subscribers are cancelled
    // it clears its internal buffer and stops accepting new subscribers
    Sinks.Many<Integer> multicastSink = Sinks.many().multicast().onBackpressureBuffer(2, true);
    multicastSink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
    multicastSink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);
    multicastSink.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);
    Disposable disposable = multicastSink.asFlux().log().subscribe();
    disposable.dispose();
    multicastSink.emitNext(4, Sinks.EmitFailureHandler.FAIL_FAST);
    multicastSink.asFlux().log().subscribe();
    multicastSink.emitNext(5, Sinks.EmitFailureHandler.FAIL_FAST);
  }

  /**
   * multicast direct all or nothing.
   */
  public void multicastDirectAllOrNothing() {
    // if any of the subscribers is too slow (has zero demand),
    // the onNext is dropped for all subscribers.
    Sinks.Many<Integer> multicastSink = Sinks.many().multicast().directAllOrNothing();
    multicastSink.asFlux().subscribe(new BaseSubscriber<Integer>() {
      @Override
      protected void hookOnSubscribe(Subscription subscription) {
        request(2);
      }

      @Override
      protected void hookOnNext(Integer value) {
        log.info(value.toString());
      }
    });
    multicastSink.asFlux().subscribe(num -> log.info(num.toString()));
    Thread thread = new Thread(() -> {
      for (int i = 0; i < 5; i++) {
        multicastSink.emitNext(i, Sinks.EmitFailureHandler.FAIL_FAST);
      }
    });
    thread.start();
    ThreadUtil.joinThread(thread);
  }

  /**
   * multicast direct best effort.
   */
  public void multicastDirectBestEffort() {
    // if a subscriber is too slow (has zero demand),
    // the onNext is dropped for this slow subscriber only.
    Sinks.Many<Integer> multicastSink = Sinks.many().multicast().directBestEffort();
    multicastSink.asFlux().subscribe(new BaseSubscriber<Integer>() {
      @Override
      protected void hookOnSubscribe(Subscription subscription) {
        request(2);
      }

      @Override
      protected void hookOnNext(Integer value) {
        log.info(value.toString());
      }
    });
    multicastSink.asFlux().subscribe(num -> log.info(num.toString()));
    Thread thread = new Thread(() -> {
      for (int i = 0; i < 5; i++) {
        multicastSink.emitNext(i, Sinks.EmitFailureHandler.FAIL_FAST);
      }
    });
    thread.start();
    ThreadUtil.joinThread(thread);
  }

  /**
   * replay limit history size.
   */
  public void replayLimitHistorySize() {
    Sinks.Many<Integer> replaySink = Sinks.many().replay().limit(2);
    replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
    replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);
    replaySink.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);
    replaySink.asFlux().log().subscribe();
  }

  /**
   * replay limit time-based window.
   */
  public void replayLimitTimeBasedWindow() {
    Sinks.Many<Integer> replaySink = Sinks.many().replay().limit(Duration.ofSeconds(2));
    replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
    ThreadUtil.sleepBySecond(1);
    replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);
    ThreadUtil.sleepBySecond(1);
    replaySink.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);
    replaySink.emitNext(4, Sinks.EmitFailureHandler.FAIL_FAST);
    ThreadUtil.sleepBySecond(1);
    replaySink.asFlux().log().subscribe();
  }

  /**
   * replay limit time-based window and history size.
   */
  public void replayLimitTimeBasedWindowAndHistorySize() {
    Sinks.Many<Integer> replaySink = Sinks.many().replay().limit(1, Duration.ofSeconds(2));
    replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
    ThreadUtil.sleepBySecond(1);
    replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);
    ThreadUtil.sleepBySecond(1);
    replaySink.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);
    replaySink.emitNext(4, Sinks.EmitFailureHandler.FAIL_FAST);
    ThreadUtil.sleepBySecond(1);
    replaySink.asFlux().log().subscribe();
  }

  /**
   * unsafe many.
   */
  @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
  public void unsafe_many() {
    Sinks.Many<Integer> unsafeReplay = Sinks.unsafe().many().replay().all();
    Thread thread = new Thread(() -> unsafeReplay.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST));
    Thread thread1 = new Thread(() -> unsafeReplay.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST));
    Thread thread2 = new Thread(() -> unsafeReplay.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST));
    unsafeReplay.asFlux().log().subscribe();
    thread.start();
    thread1.start();
    thread2.start();
    ThreadUtil.joinThread(thread, thread1, thread2);
  }

  /**
   * sinks one().
   * <p>
   * emitValue generates an onNext() signal and onComplete()
   * </p>
   * <p>
   * emitEmpty generates an onComplete() signal
   * </p>
   * <p>
   * emitError generates an onError() signal
   * </p>
   */
  public void sinksOne() {
    Sinks.One<Integer> sink = Sinks.one();
    sink.emitValue(1, Sinks.EmitFailureHandler.FAIL_FAST);
    sink.asMono().log().subscribe();
  }

  /**
   * sinks empty.
   */
  public void sinksEmpty() {
    Sinks.Empty<Object> sink = Sinks.empty();
    sink.emitEmpty(Sinks.EmitFailureHandler.FAIL_FAST);
    sink.asMono().log().subscribe();
  }
}
