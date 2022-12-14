package com.justin.reactor.util;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Description: the utils of Thread.
 *
 * @author Justin_Zhang
 * @date 11/12/2022 10:13
 */
public class ThreadUtil {
  /**
   * description: sleep for certain seconds.
   *
   * @param seconds the seconds of sleep
   */
  public static void sleepBySecond(Integer seconds) {
    try {
      TimeUnit.SECONDS.sleep(seconds);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * join threads.
   *
   * @param threads appoint threads
   */
  public static void joinThread(Thread... threads) {
    try {
      for (Thread thread : threads) {
        thread.join();
      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
