package com.justin.reactor.sample;

import org.junit.jupiter.api.Test;

/**
 * Description: test case for BaseSubscriber.
 *
 * @author Justin_Zhang
 * @date 11/12/2022 14:34
 */
public class BaseSubscriberSampleTest {
  private final BaseSubscriberSample baseSubscriberSample = new BaseSubscriberSample();

  @Test
  public void use_base_subscriber_to_subscribe() {
    baseSubscriberSample.useSampleSubscriberToSubscribe();
  }
}
