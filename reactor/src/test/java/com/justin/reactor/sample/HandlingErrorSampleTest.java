package com.justin.reactor.sample;

import org.junit.jupiter.api.Test;

/**
 * Description: test case or handling errors.
 *
 * @author Justin_Zhang
 * @date 11/14/2022 16:28
 */
public class HandlingErrorSampleTest {
  private final HandlingErrorSample handlingErrorSample = new HandlingErrorSample();

  @Test
  public void on_error_return() {
    handlingErrorSample.onErrorReturn();
  }

  @Test
  public void on_error_complete() {
    handlingErrorSample.onErrorComplete();
  }

  @Test
  public void on_error_resume() {
    handlingErrorSample.onErrorResume();
  }

  @Test
  public void on_error_resume_rethrow() {
    handlingErrorSample.onErrorResumeRethrow();
  }

  @Test
  public void on_error_map() {
    handlingErrorSample.onErrorMap();
  }

  @Test
  public void on_error_error() {
    handlingErrorSample.doOnError();
  }

  @Test
  public void do_finally() {
    handlingErrorSample.doFinally();
  }

  @Test
  public void using() {
    handlingErrorSample.using();
  }

  @Test
  public void demonstrate_terminal() {
    handlingErrorSample.demonstrateTerminal();
  }

  @Test
  public void retry() {
    handlingErrorSample.retry();
  }
}
