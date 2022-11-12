package com.justin.reactor.sample;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * Description: the samples of programmatically create sequence.
 *
 * @author Justin_Zhang
 * @date 11/12/2022 16:09
 */
@Slf4j
public class ProgrammaticallyCreateSequenceSample {
  /**
   * synchronous generate (one-by-one emissions).
   */
  public void synchronousGenerate() {
    Flux.generate(() -> 0,
        (state, sink) -> {
          sink.next("3 x " + state + " = " + 3 * state);
          if (state == 10) {
            sink.complete();
          }
          return state + 1;
        }).subscribe(str -> log.info(str.toString()));
  }

  /**
   * synchronous generate with clean up thing.
   */
  public void synchronousGenerateWithCleanup() {
    Flux.generate(AtomicInteger::new,
        (state, sink) -> {
          int num = state.getAndIncrement();
          sink.next("3 x " + num + " = " + 3 * num);
          if (num == 10) {
            sink.complete();
          }
          return state;
        }, (state) -> log.info("do some clean up work, state: {}", state))
        .subscribe(str -> log.info(str.toString()));
  }
}
