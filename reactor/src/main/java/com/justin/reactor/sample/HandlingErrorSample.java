package com.justin.reactor.sample;

import com.justin.reactor.util.ThreadUtil;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;
import reactor.util.retry.RetrySpec;

/**
 * Description: the sample of handling errors.
 *
 * @author Justin_Zhang
 * @date 11/14/2022 16:23
 */
@Slf4j
public class HandlingErrorSample {
  /**
   * static fallback value.
   */
  public void onErrorReturn() {
    Flux<String> flux = Flux.just(1, 2, 0, 4)
        .map(HandlingErrorSample::divide100ByNum) //this triggers an error with 0
        .onErrorReturn(e -> e instanceof ArithmeticException, "Divided by zero :(");
    flux.subscribe(log::info);
  }

  /**
   * catch and swallow the error.
   */
  public void onErrorComplete() {
    Flux<String> flux = Flux.just(1, 2, 0, 4)
        .map(HandlingErrorSample::divide100ByNum)
        .onErrorComplete();
    flux.subscribe(log::info);
  }

  /**
   * fallback method.
   */
  public void onErrorResume() {
    Flux<String> flux = Flux.just(1, 2, 0, 4)
        .flatMap(num -> Flux.just(divide100ByNum(num)))
        .onErrorResume(error -> {
          if (error instanceof ArithmeticException) {
            return Flux.just("100 / 0");
          } else {
            // you can also abstract factory method to handle it.
            return Flux.just("100 / null");
          }
        });
    flux.subscribe(log::info);
  }

  /**
   * catch and rethrow.
   */
  public void onErrorResumeRethrow() {
    Flux<String> flux = Flux.just(1, 2, 0, 4)
        .flatMap(num -> Flux.just(divide100ByNum(num)))
        .onErrorResume(error -> Flux.error(new BusinessException("business error", error)));
    flux.subscribe(log::info);
  }

  /**
   * on error map to rethrow.
   */
  public void onErrorMap() {
    Flux<String> flux = Flux.just(1, 2, 0, 4)
        .flatMap(num -> Flux.just(divide100ByNum(num)))
        .onErrorMap(error -> new BusinessException("business error", error));
    flux.subscribe(log::info);
  }

  /**
   * do on error.
   */
  public void doOnError() {
    Flux<String> flux = Flux.just(1, 2, 0, 4)
        .flatMap(num -> Flux.just(divide100ByNum(num)))
        .doOnError(error -> log.info(error.getMessage()));
    flux.subscribe(log::info);
  }

  /**
   * do finally.
   */
  public void doFinally() {
    Flux<String> flux = Flux.just(1, 2, 0, 4)
        .flatMap(num -> Flux.just(divide100ByNum(num)))
        .doFinally(type -> log.info("do finally: {}", type.toString()));
    flux.subscribe(log::info);
  }

  /**
   * like try-with-resource.
   */
  public void using() {
    AtomicBoolean isDisposed = new AtomicBoolean();
    Disposable disposableInstance = new Disposable() {
      @Override
      public String toString() {
        return "DISPOSABLE";
      }

      @Override
      public void dispose() {
        isDisposed.set(true);
      }
    };
    Flux<String> flux = Flux.using(() -> disposableInstance,
        disposable -> Flux.just(disposable.toString()),
        Disposable::dispose);
    flux.subscribe(log::info);
  }

  /**
   * demonstrate terminal when on error.
   */
  public void demonstrateTerminal() {
    Flux<String> flux = Flux.interval(Duration.ofMillis(200)).map(input -> {
      if (input < 3) {
        return "tick " + input;
      } else {
        throw new RuntimeException("error");
      }
    }).onErrorReturn("oh no");
    flux.subscribe(log::info);
    ThreadUtil.sleepBySecond(2);
  }

  /**
   * retry.
   * This is really a different sequence, and the original one is still terminated.
   */
  public void retry() {
    Flux.interval(Duration.ofMillis(200)).map(input -> {
      if (input < 3) {
        return "tick " + input;
      } else {
        throw new RuntimeException("error");
      }
    }).retry(1)
        .elapsed()
        .subscribe(str -> log.info(str.toString()), err -> log.info(err.toString()));
    ThreadUtil.sleepBySecond(2);
  }

  /**
   * retryWhen.
   */
  public void  retryWhen() {
    Flux<String> flux = Flux.interval(Duration.ofMillis(200)).map(input -> {
      if (input < 3) {
        return "tick " + input;
      } else {
        throw new RuntimeException("error");
      }
      // }).retryWhen(Retry.from(companion -> companion.take(3)));
      // TODO: RetrySpec and RetryBackoffSpec
    }).retryWhen(Retry.max(3));
    flux.subscribe(log::info);
    ThreadUtil.sleepBySecond(5);
  }

  /**
   * retryWhen and throw.
   */
  public void retryWhenThrow() {
    Flux<String> flux = Flux.interval(Duration.ofMillis(200)).map(input -> {
      if (input < 3) {
        return "tick " + input;
      } else {
        throw new RuntimeException("error");
      }
    }).retryWhen(Retry.from(companion -> companion.map(rs -> {
      if (rs.totalRetries() < 3) {
        return rs.totalRetries();
      } else {
        throw Exceptions.propagate(rs.failure());
      }
    })));
    flux.subscribe(log::info);
    ThreadUtil.sleepBySecond(5);
  }

  /**
   * retryWhen with transient errors.
   */
  public void retryWhenWithTransientErrors() {
    AtomicInteger atomicInteger = new AtomicInteger();
    Flux<String> flux = Flux.interval(Duration.ofMillis(200)).map(input -> {
      atomicInteger.incrementAndGet();
      if (atomicInteger.get() % 3 != 0) {
        log.info("{} % 3 == {}", atomicInteger.get(), atomicInteger.get() % 3);
        return "tick " + atomicInteger.get();
      } else {
        throw new RuntimeException("error");
      }
    }).retryWhen(Retry.max(3).transientErrors(true));
    flux.subscribe(log::info);
    ThreadUtil.sleepBySecond(5);
  }

  /**
   * propagate and unwrap error.
   */
  public void propagateAndUnwrapError() {
    Flux<String> flux = Flux.range(1, 10).map(num -> {
      try {
        return convert(num);
      } catch (IOException e) {
        throw Exceptions.propagate(e);
      }
    });
    flux.subscribe(log::info, error -> {
      if (Exceptions.unwrap(error) instanceof IOException) {
        log.error("I/O error");
      } else {
        log.error("error");
      }
    });
  }

  private String convert(Integer num) throws IOException {
    if (num > 3) {
      throw new IOException("error " + num);
    }
    return "success " + num;
  }

  private static String divide100ByNum(Integer i) {
    return "100 / " + i + " = " + (100 / i);
  }

  static class BusinessException extends Exception {
    public BusinessException() {
      super();
    }

    public BusinessException(String message) {
      super(message);
    }

    public BusinessException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
