package com.justin.reactor.sample;

import org.junit.jupiter.api.Test;

/**
 * Description: test case for create publisher.
 *
 * @author Justin_Zhang
 * @date 11/29/2022 14:28
 */
public class CreateSampleTest {
  private final CreateSample createSample = new CreateSample();

  @Test
  public void create_flux() {
    createSample.createFlux();
  }

  @Test
  public void create_mono() {
    createSample.createMono();
  }
}
