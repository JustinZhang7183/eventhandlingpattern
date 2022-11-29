package com.justin.reactor.sample;

import com.justin.reactor.util.ThreadUtil;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * Description: the sample of cancel subscribe.
 * cancellation is a signal that the resource should stop producing elements.
 * it is not guaranteed to be immediate. maybe producer complete fast before receive signal.
 *
 * @author Justin_Zhang
 * @date 11/12/2022 10:04
 */
@Slf4j
public class CancelSubscribeSample {
  private final ExecutorService executor = Executors
      .newFixedThreadPool(Runtime.getRuntime().availableProcessors());

  /**
   * description: cancel a fixed flux.
   * TODO: I can't cancel a fixed flux. Why?
   */
  public void cancelFixedFlux() {
    List<Integer> list = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      list.add(i);
    }
    Disposable disposable = Flux.fromIterable(list).subscribe(num -> log.info(num.toString()));
    disposable.dispose();
  }

  /**
   * cancel a interval flux.
   */
  public void cancelIntervalFlux() {
    Disposable disposable = Flux.interval(Duration.ofMillis(300),
            Schedulers.newParallel("parallel-1"))
        .subscribe(num -> log.info(num.toString()));
    ThreadUtil.sleepBySecond(1);
    disposable.dispose();
  }

  /**
   * description: cancel a flux which subscribe in another thread by main thread. (counter-example)
   */
  public void tryToCancelFluxByThread() {
    AtomicReference<Disposable> disposableAtomic = new AtomicReference<>();
    // executor.execute(() -> disposableAtomic.set(subscribeOneFlux()));
    executor.execute(() -> disposableAtomic.set(subscribeFlux()));
    // after flux has produced all element, disposable had a result otherwise it's a null
    // because flux.just().map is synchronous, though execute by another thread,
    // disposable still need to wait flux producing done to receive result
    ThreadUtil.sleepBySecond(2);
    disposableAtomic.get().dispose();
  }

  private Disposable subscribeFlux() {
    return Flux.just(1, 2, 3).subscribe(num -> {
      ThreadUtil.sleepBySecond(num);
      log.info(num.toString());
    });
  }

  /**
   * swap flux by wrapper class Swap.
   */
  public void swapDisposable() {
    Disposable disposable = Flux.interval(Duration.ofSeconds(1),
            Schedulers.newParallel("parallel-2"))
        .subscribe(num -> log.info(num.toString()));
    ThreadUtil.sleepBySecond(2);
    Disposable.Swap swap = Disposables.swap();
    // need to update current disposable first.
    swap.update(disposable);
    // then replace, it doesn't stop old one.
    swap.replace(Flux.interval(Duration.ofSeconds(2), Schedulers.newParallel("parallel-3"))
        .subscribe(num -> log.info(num.toString())));
    // whether to stop old one depends on requirement.
    disposable.dispose();
    ThreadUtil.sleepBySecond(5);
  }

  /**
   * composite multiple disposable, then you can add and remove one or more.
   * you can dispose all disposable you add.
   */
  public void compositeDisposable() {
    Disposable disposable = Flux.interval(Duration.ofSeconds(1),
            Schedulers.newParallel("parallel-4"))
        .subscribe(num -> log.info(num.toString()));
    Disposable disposable2 = Flux.interval(Duration.ofSeconds(1),
            Schedulers.newParallel("parallel-5"))
        .subscribe(num -> log.info(num.toString()));
    Disposable disposable3 = Flux.interval(Duration.ofSeconds(1),
            Schedulers.newParallel("parallel-6"))
        .subscribe(num -> log.info(num.toString()));
    ThreadUtil.sleepBySecond(3);
    Disposable.Composite composite = Disposables.composite(disposable);
    composite.add(disposable2);
    composite.add(disposable3);
    composite.remove(disposable2);
    composite.dispose();
    ThreadUtil.sleepBySecond(5);
    disposable2.dispose();
  }
}
