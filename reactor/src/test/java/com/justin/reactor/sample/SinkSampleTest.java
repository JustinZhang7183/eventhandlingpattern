package com.justin.reactor.sample;

import org.junit.jupiter.api.Test;

/**
 * Description: test case for sink.
 *
 * @author Justin_Zhang
 * @date 11/15/2022 14:37
 */
public class SinkSampleTest {
  private final SinkSample sinkSample = new SinkSample();

  @Test
  public void multiple_thread_emit_elements_reply() {
    sinkSample.multipleThreadEmitElementsReply();
  }

  @Test
  public void sink_multicast() {
    sinkSample.sinkMulticast();
  }

  @Test
  public void sink_unicast() {
    sinkSample.sinkUnicast();
  }

  @Test
  public void sink_replay() {
    sinkSample.sinkReplay();
  }

  @Test
  public void unicast_backpressure_buffer() {
    sinkSample.unicastBackpressureBuffer();
  }

  @Test
  public void multicast_backpressure_buffer() {
    sinkSample.multicastBackpressureBuffer();
  }

  @Test
  public void multicast_direct_all_or_nothing() {
    sinkSample.multicastDirectAllOrNothing();
  }

  @Test
  public void multicast_direct_best_effort() {
    sinkSample.multicastDirectBestEffort();
  }

  @Test
  public void replay_limit_history_size() {
    sinkSample.replayLimitHistorySize();
  }

  @Test
  public void replay_limit_time_based_window() {
    sinkSample.replayLimitTimeBasedWindow();
  }

  @Test
  public void replay_limit_time_based_window_and_history_size() {
    sinkSample.replayLimitTimeBasedWindowAndHistorySize();
  }

  @Test
  public void unsafe_many() {
    sinkSample.unsafe_many();
  }

  @Test
  public void sinks_one() {
    sinkSample.sinksOne();
  }

  @Test
  public void sinks_empty() {
    sinkSample.sinksEmpty();
  }
}
