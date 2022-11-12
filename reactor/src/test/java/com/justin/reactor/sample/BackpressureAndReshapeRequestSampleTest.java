package com.justin.reactor.sample;

import org.junit.jupiter.api.Test;

/**
 * Description: test case for backpressure and reshape request.
 *
 * @author Justin_Zhang
 * @date 11/12/2022 15:20
 */
public class BackpressureAndReshapeRequestSampleTest {
  private final BackpressureAndReshapeRequestsSample backpressureAndReshapeRequestsSample =
      new BackpressureAndReshapeRequestsSample();

  @Test
  public void simple_way_to_custom_request_to_implement_backpressure() {
    backpressureAndReshapeRequestsSample.simpleWayToCustomRequest();
  }

  @Test
  public void change_the_demand_from_downstream() {
    backpressureAndReshapeRequestsSample.operatorsThatChangeTheDemandFromDownstream();
  }
}
