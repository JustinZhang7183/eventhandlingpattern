package com.justin.reactor.sample;

import org.junit.jupiter.api.Test;

/**
 * Description: test case for programmatically create sequence.
 *
 * @author Justin_Zhang
 * @date 11/12/2022 16:19
 */
public class ProgrammaticallyCreateSequenceSampleTest {
  private final ProgrammaticallyCreateSequenceSample programmaticallyCreateSequenceSample =
      new ProgrammaticallyCreateSequenceSample();

  @Test
  public void synchronous_generate() {
    programmaticallyCreateSequenceSample.synchronousGenerate();
  }

  @Test
  public void synchronous_generate_with_clean_up_thing() {
    programmaticallyCreateSequenceSample.synchronousGenerateWithCleanup();
  }

  @Test
  public void asynchronous_multi_thread_create() {
    programmaticallyCreateSequenceSample.asynchronousMultiThreadCreate();
  }

  @Test
  public void asynchronous_single_thread_push() {
    programmaticallyCreateSequenceSample.asynchronousSingleThreadPush();
  }

  @Test
  public void hybrid_push_pull_model() {
    programmaticallyCreateSequenceSample.hybridPushPullMode();
  }

  @Test
  public void cleaning_up_after_push_or_create() {
    programmaticallyCreateSequenceSample.cleaningUpAfterPushOrCreate();
  }

  @Test
  public void handle_test() {
    programmaticallyCreateSequenceSample.handle();
  }
}
