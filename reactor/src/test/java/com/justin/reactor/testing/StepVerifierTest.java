package com.justin.reactor.testing;

import com.justin.reactor.util.ThreadUtil;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;
import reactor.util.context.Context;

/**
 * Description: step verifier test sample.
 *
 * @author Justin_Zhang
 * @date 11/19/2022 11:25
 */
@Slf4j
public class StepVerifierTest {
  @Test
  public void simple_sample_of_verifier() {
    Flux<String> source = Flux.just("one", "two");
    Flux<String> flux = source.concatWith(Mono.error(new IllegalArgumentException("error")));
    StepVerifier.create(flux).expectNext("one")
        .expectNext("two")
        .expectErrorMessage("error")
        .verify();

    StepVerifierOptions stepVerifierOptions = StepVerifierOptions.create()
        .scenarioName("test step verifier options");
    StepVerifier.create(source, stepVerifierOptions).expectNextCount(2).as("expect 2")
        .verifyComplete();

    StepVerifier.create(flux).expectNextCount(2).as("expect 2").verifyError();

    StepVerifier.create(flux).consumeNextWith(log::info).expectNext("two").expectError().verify();

    // typical usage is to capture elements that have been dropped by some operator and  assert them
    // need to use hooks.
    StepVerifier.create(flux).consumeNextWith(log::info).expectNext("two").expectError()
        .verifyThenAssertThat().hasNotDroppedErrors();
  }

  @Test
  public void manipulate_time() {
    Duration duration = StepVerifier.withVirtualTime(() -> Mono.delay(Duration.ofSeconds(2)))
        .thenAwait(Duration.ofSeconds(2))
        .expectNext(0L)
        .verifyComplete();
    log.info(String.valueOf(duration.getSeconds()));
    Duration duration2 = StepVerifier.withVirtualTime(() -> Mono.delay(Duration.ofDays(1)))
        .expectSubscription()
        .expectNoEvent(Duration.ofDays(1))
        .expectNext(0L)
        .verifyComplete();
    log.info(String.valueOf(duration2.getSeconds()));
  }

  @Test
  public void testing_context() {
    // need to know what things can context do?
    StepVerifier.create(Mono.just(1), StepVerifierOptions.create()
            .withInitialContext(Context.of("context1", "context2")))
        .expectAccessibleContext()
        .contains("context1", "context2")
        .then()
        .expectNext(1)
        .verifyComplete();
  }
}
