package com.justin.reactor.debug;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.tools.agent.ReactorDebugAgent;

/**
 * Description: debugging test case.
 *
 * @author Justin_Zhang
 * @date 11/22/2022 17:30
 */
public class DebuggingTest {
  static {
    ReactorDebugAgent.init();
    ReactorDebugAgent.processExistingClasses();
  }

  /**
   * not easy to find root reason.
   */
  @Test
  public void typical_reactor_stack_trace() {
    Mono<Integer> mono = Flux.just(1, 2).single();
    Mono<Integer> monoMap = mono.map(num -> num + 1);
    monoMap.subscribe();
  }

  /**
   * traceback: assemble debug information.
   * capturing the stacktrace on every operator call
   */
  @Test
  public void traceback_debug_mode() {
    Hooks.onOperatorDebug();
    Mono<Integer> mono = Flux.just(1, 2).single();
    Mono<Integer> monoMap = mono.map(num -> num + 1);
    monoMap.subscribe();
  }

  /**
   * checkpoint: can identify problematic code.
   * checkpoint's second parameter can use with traceback.
   */
  @Test
  public void checkpoint_mode() {
    Mono<Integer> mono = Flux.just(1, 2).single().checkpoint("single error");
    Mono<Integer> monoMap = mono.map(num -> num + 1);
    monoMap.subscribe();
  }

  /**
   * without paying the cost of capturing the stacktrace on every operator call.
   */
  @Test
  public void production_ready_global_debug() {
    Mono<Integer> mono = Flux.just(1, 2).single();
    Mono<Integer> monoMap = mono.map(num -> num + 1);
    monoMap.subscribe();
  }
}
