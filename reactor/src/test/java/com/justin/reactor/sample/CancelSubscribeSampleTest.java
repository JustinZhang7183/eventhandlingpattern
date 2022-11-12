package com.justin.reactor.sample;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Description: test case for cancellation of subscription.
 *
 * @author Justin_Zhang
 * @date 11/12/2022 10:28
 */
public class CancelSubscribeSampleTest {
  private final CancelSubscribeSample cancelSubscribeSample = new CancelSubscribeSample();

  @Test
  public void cancel_fixed_flux() {
    cancelSubscribeSample.cancelFixedFlux();
  }

  @Test
  public void cancel_interval_flux() {
    cancelSubscribeSample.cancelIntervalFlux();
  }

  @Test
  public void try_to_cancel_flux_by_thread() {
    Assertions.assertThrows(NullPointerException.class,
        cancelSubscribeSample::tryToCancelFluxByThread);
  }

  @Test
  public void swap_disposable() {
    cancelSubscribeSample.swapDisposable();
  }

  @Test
  public void composite_disposable() {
    cancelSubscribeSample.compositeDisposable();
  }
}
