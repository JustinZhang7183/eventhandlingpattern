package com.justin.reactor.sample;

import com.justin.reactor.util.ThreadUtil;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * Description: the sample of threading and scheduler.
 * <p>
 * 1.first of all, reactor can be considered to be concurrency-agnostic.
 * whether enable concurrency, it depends on you TODO: how?
 * </p>
 * <p>
 * 2.most operators continue working in the thread on which the previous operator executed.
 * </p>
 * <p>
 * 3.unless specified, the topmost operator itself runs on the thread in which
 * subscribe() call was made.
 * </p>
 *
 * @author Justin_Zhang
 * @date 11/14/2022 15:26
 */
@Slf4j
public class ThreadingAndSchedulerSample {
  /**
   * simple proving for above third point.
   */
  public void simpleProving() {
    Mono<String> mono = Mono.just("hello");
    Thread thread = new Thread(() -> mono
        .subscribe(log::info));
    thread.start();
    ThreadUtil.joinThread(thread);
  }

  public void immediateScheduler() {
    Flux.just("1", "2").subscribeOn(Schedulers.immediate()).subscribe(log::info);
  }

  public void singleScheduler() {
    Flux.just("1", "2").subscribeOn(Schedulers.single()).subscribe(log::info);
  }

  public void elasticScheduler() {
    Flux.just("1", "2").subscribeOn(Schedulers.boundedElastic()).subscribe(log::info);
  }

  /**
   * it has a cap on the number of backing threads(default is number of CPU cores multiply 10)
   * up to 100 000 tasks submitted after the cap has been reached are enqueued
   * and will be re-scheduled when a thread becomes available.
   */
  public void boundedElasticScheduler() {
    Flux.just("1", "2").subscribeOn(Schedulers.boundedElastic()).subscribe(log::info);
  }

  /**
   * a fixed pool of workers. It creates as many workers as CPU cores you have.
   */
  public void parallelScheduler() {
    List<String> list = new ArrayList<>();
    for (int i = 0; i < 1000; i++) {
      list.add(String.valueOf(i));
    }
    Flux.fromIterable(list).map(num -> {
      log.info(num);
      return num;
    }).subscribeOn(Schedulers.parallel()).subscribe(log::info);
    ThreadUtil.sleepBySecond(10);
  }

  /**
   * parallel interval sample.
   */
  public void parallelInterval() {
    Flux.interval(Duration.ofMillis(100), Schedulers.newParallel("parallel", 10))
        .subscribe(num -> log.info(num.toString()));
    ThreadUtil.sleepBySecond(10);
  }

  /**
   * publishOn() to switch execution context.
   */
  public void publishOn() {
    Scheduler s = Schedulers.newParallel("parallel-scheduler", 4);
    final Flux<String> flux = Flux
        .range(1, 2)
        .map(i -> {
          log.info("first map");
          return 10 + i;
        })
        .publishOn(s)
        .map(i -> {
          log.info("second map");
          return "value " + i;
        });
    Thread thread = new Thread(() -> flux.subscribe(log::info));
    thread.start();
    ThreadUtil.joinThread(thread);
  }

  /**
   * subscribeOn() to switch execution context.
   */
  public void subscribeOn() {
    Scheduler s = Schedulers.newParallel("parallel-scheduler", 4);
    final Flux<String> flux = Flux
        .range(1, 2)
        .map(i -> {
          log.info("first map");
          return 10 + i;
        })
        .subscribeOn(s)
        .map(i -> {
          log.info("second map");
          return "value " + i;
        });
    Thread thread = new Thread(() -> flux.subscribe(System.out::println));
    thread.start();
    ThreadUtil.joinThread(thread);
  }
}
