package com.justin.reactor.sample;

import org.junit.jupiter.api.Test;

/**
 * Description: test case for threading and scheduler.
 *
 * @author Justin_Zhang
 * @date 11/14/2022 15:45
 */
public class ThreadingAndSchedulerSampleTest {
  private final ThreadingAndSchedulerSample threadingAndSchedulerSample =
      new ThreadingAndSchedulerSample();

  @Test
  public void sample_proving() {
    threadingAndSchedulerSample.simpleProving();
  }

  @Test
  public void immediate_scheduler() {
    threadingAndSchedulerSample.immediateScheduler();
  }

  @Test
  public void single_scheduler() {
    threadingAndSchedulerSample.singleScheduler();
  }

  @Test
  public void elastic_scheduler() {
    threadingAndSchedulerSample.elasticScheduler();
  }

  @Test
  public void bounded_elastic_scheduler() {
    threadingAndSchedulerSample.boundedElasticScheduler();
  }

  @Test
  public void parallel_scheduler() {
    threadingAndSchedulerSample.parallelScheduler();
  }

  @Test
  public void publish_on() {
    threadingAndSchedulerSample.publishOn();
  }

  @Test
  public void subscribe_on() {
    threadingAndSchedulerSample.subscribeOn();
  }
}
