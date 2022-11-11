package com.justin.reactor.sample;

import org.junit.jupiter.api.Test;

/**
 * Description: test case for subscribe.
 *
 * @author Justin_Zhang
 * @date 11/11/2022 16:30
 */
public class SubscribeSampleTest {
  private final SubscribeSample subscribeSample = new SubscribeSample();

  @Test
  public void subscribe_do_nothing() {
    subscribeSample.subscribeDoNothing();
  }

  @Test
  public void subscribe_do_something() {
    subscribeSample.subscribeDoSomething();
  }

  @Test
  public void subscribe_do_something_and_react_error() {
    subscribeSample.subscribeDoSomethingAndReactError();
  }

  @Test
  public void subscribe_do_something_and_react_error_and_run_some_code_when_complete() {
    subscribeSample.subscribeDoSomethingAndReactErrorAndRunSomeCodeWhenComplete();
  }

  @Test
  public void
      subscribe_do_something_react_error_run_some_code_complete_do_something_with_subscription() {
    subscribeSample.subscribeDoSomethingReactErrorRunSomeCodeCompleteDoSomethingWithSubscription();
  }
}
