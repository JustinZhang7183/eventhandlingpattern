package com.justin.reactor.exposingmetrics;

import com.justin.reactor.util.ThreadUtil;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.observability.micrometer.Micrometer;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * Description: test case for metrics.
 *
 * @author Justin_Zhang
 * @date 11/23/2022 16:01
 */
@Slf4j
public class MetricsTest {
  /**
   * test for scheduler metrics.
   * need to integrate some monitoring system like prometheus.
   */
  @Test
  public void scheduler_metrics() {
    Scheduler scheduler = Schedulers.newParallel("test", 4);
    Scheduler timedScheduler = Micrometer.timedScheduler(scheduler, new SimpleMeterRegistry(),
        "schedulerMetrics", Tags.of(Tag.of("addtionalTag", "yes")));
    timedScheduler.schedule(() -> Flux.just(1, 2).subscribe(num -> log.info(num.toString())));
    timedScheduler.start();
    ThreadUtil.sleepBySecond(2);
  }
}
