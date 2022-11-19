package com.justin.reactor.testing;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

/**
 * Description: test publisher test case.
 *
 * @author Justin_Zhang
 * @date 11/19/2022 14:51
 */
@Slf4j
public class TestPublisherTest {
  @Test
  public void test_publisher_create() {
    TestPublisher<Object> publisher = TestPublisher.create();
    StepVerifier.create(publisher.flux())
        .then(() -> publisher.next(1))
        .expectNextCount(1)
        .then(() -> publisher.emit(2, 3, 4))
        .expectNextCount(3)
        .verifyComplete();
  }

  @Test
  public void test_publisher_create_non_compliant() {
    TestPublisher<Object> publisher = TestPublisher
        .createNoncompliant(TestPublisher.Violation.ALLOW_NULL);
    StepVerifier.create(publisher.flux())
        .then(() -> publisher.emit(1, null))
        .expectNextCount(2)
        .verifyComplete();
  }
}
