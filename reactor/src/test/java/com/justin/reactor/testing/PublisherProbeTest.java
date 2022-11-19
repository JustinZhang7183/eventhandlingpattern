package com.justin.reactor.testing;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;

/**
 * Description: publisher probe test case.
 *
 * @author Justin_Zhang
 * @date 11/19/2022 15:22
 */
public class PublisherProbeTest {
  /**
   * verify execution situation of complex chain with data.
   */
  private Flux<String> processOrFallback(Mono<String> source, Publisher<String> fallback) {
    return source.flatMapMany(phrase -> Flux.fromArray(phrase.split("\\s+")))
        .switchIfEmpty(fallback);
  }

  /**
   * verify execution situation of complex chain without data.
   */
  private Mono<Void> processOrFallback(Mono<String> commandSource, Mono<Void> doWhenEmpty) {
    return commandSource
        .flatMap(command -> executeCommand(command).then())
        .switchIfEmpty(doWhenEmpty);
  }

  private Mono<String> executeCommand(String command) {
    return Mono.just(command + " DONE");
  }

  @Test
  public void test_split_path_is_used() {
    StepVerifier.create(processOrFallback(Mono.just("just  a  phrase with   tabs"),
        Mono.just("EMPTY_PHRASE")))
        .expectNext("just", "a", "phrase", "with", "tabs")
        .verifyComplete();
  }

  @Test
  public void test_empty_path_is_used() {
    StepVerifier.create(processOrFallback(Mono.empty(), Mono.just("EMPTY_PHRASE")))
        .expectNext("EMPTY_PHRASE")
        .verifyComplete();
  }

  @Test
  public void test_command_empty_path_is_used() {
    PublisherProbe<Void> probe = PublisherProbe.empty();
    StepVerifier.create(processOrFallback(Mono.empty(), probe.mono()))
            .verifyComplete();
    probe.assertWasSubscribed();
    probe.assertWasRequested();
    probe.assertWasNotCancelled();
  }
}
