package com.justin.reactor.nonblocking;

import com.justin.reactor.util.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

/**
 * Description: test case for non-blocking.
 *
 * @author Justin_Zhang
 * @date 11/30/2022 10:40
 */
@Slf4j
public class NonBlockingTest {
  @Test
  public void non_blocking_test() {
    Sinks.Many<Object> sink = Sinks.many().replay().all();
    sink.asFlux().publishOn(Schedulers.single())
        .subscribe(num -> log.info(num.toString()));
    Sinks.Many<Object> sink2 = Sinks.many().replay().all();
    sink2.asFlux().publishOn(Schedulers.single())
        .subscribe(num -> log.info(num.toString()));
    int index = 10;
    Thread thread = new Thread(() -> {
      for (int i = 0; i < index; i++) {
        Sinks.EmitResult emitResult = sink.tryEmitNext("thread 1 emit " + i);
        ThreadUtil.sleepBySecond(1);
      }
    });
    Thread thread2 = new Thread(() -> {
      for (int i = 0; i < index; i++) {
        Sinks.EmitResult emitResult = sink2.tryEmitNext("thread 2 emit " + i);
        ThreadUtil.sleepBySecond(1);
      }
    });
    thread.start();
    thread2.start();
    ThreadUtil.joinThread(thread, thread2);
  }
}
