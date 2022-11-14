package com.justin.reactor.sample;

import com.justin.reactor.util.ThreadUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

/**
 * Description: the samples of programmatically create sequence.
 *
 * @author Justin_Zhang
 * @date 11/12/2022 16:09
 */
@Slf4j
public class ProgrammaticallyCreateSequenceSample {
  /**
   * <p>
   * synchronous generate (one-by-one emissions).
   * meaning that the sink is a SynchronousSink and that its next() method
   * can only be called at most once per callback invocation
   * </p>
   * <p>
   * The most useful variant is probably the one that also lets you keep a state that
   * you can refer to in your sink usage to decide what to emit next.
   * </p>
   */
  public void synchronousGenerate() {
    Flux.generate(() -> 0,
        (state, sink) -> {
          sink.next("3 x " + state + " = " + 3 * state);
          if (state == 10) {
            sink.complete();
          }
          return state + 1;
        }).subscribe(str -> log.info(str.toString()));
  }

  /**
   * synchronous generate with clean up thing.
   */
  public void synchronousGenerateWithCleanup() {
    Flux.generate(AtomicInteger::new,
            (state, sink) -> {
              int num = state.getAndIncrement();
              sink.next("3 x " + num + " = " + 3 * num);
              if (num == 10) {
                sink.complete();
              }
              return state;
            }, (state) -> log.info("do some clean up work, state: {}", state))
        .subscribe(str -> log.info(str.toString()));
  }

  /**
   * asynchronous multi thread create.
   * which is suitable for multiple emissions per round, even from multiple threads
   * <p>
   * OverflowStrategy:
   * 1.IGNORE to Completely ignore downstream backpressure requests.
   * This may yield IllegalStateException when queues get full downstream.
   * 2.ERROR to signal an IllegalStateException when the downstream can’t keep up.
   * 3.DROP to drop the incoming signal if the downstream is not ready to receive it.
   * 4.LATEST to let downstream only get the latest signals from upstream.
   * 5.BUFFER (the default) to buffer all signals if the downstream can’t keep up.
   * (this does unbounded buffering and may lead to OutOfMemoryError).
   * </p>
   */
  public void asynchronousMultiThreadCreate() {
    MyEventProcessor<String> myEventProcessor = new MyEventProcessor<>();
    Flux<String> bride = Flux.create(sink -> registerListener(myEventProcessor, sink),
        FluxSink.OverflowStrategy.DROP);
    // request on separate thread to backpressure.
    bride.subscribeOn(Schedulers.boundedElastic(), true)
        .subscribe(str -> log.info("log: {}", str));
    // enough time for register of processor.
    ThreadUtil.sleepBySecond(1);
    MyEvent<String> event = new MyEvent<>();
    event.register(myEventProcessor);
    Thread thread = produceByAnotherThread(event);
    Thread thread2 = produceByAnotherThread(event);
    ThreadUtil.joinThread(thread);
    ThreadUtil.joinThread(thread2);
    event.done();
  }

  /**
   * asynchronous single thread push.
   * using push: only one producing thread may invoke next, complete or error
   * is not safe for multiple thread
   */
  public void asynchronousSingleThreadPush() {
    MyEventProcessor<String> myEventProcessor = new MyEventProcessor<>();
    Flux<String> bride = Flux.push(sink -> registerListener(myEventProcessor, sink));
    bride.subscribe(str -> log.info("log: {}", str));
    // enough time for register of processor.
    ThreadUtil.sleepBySecond(1);
    MyEvent<String> event = new MyEvent<>();
    event.register(myEventProcessor);
    Thread thread = produceByAnotherThread(event);
    Thread thread2 = produceByAnotherThread(event);
    ThreadUtil.joinThread(thread);
    ThreadUtil.joinThread(thread2);
    event.done();
  }

  /**
   * hybrid push and pull model.
   */
  public void hybridPushPullMode() {
    MyEventProcessor<String> myEventProcessor = new MyEventProcessor<>();
    Flux<String> bride = Flux.create(sink -> {
      registerListener(myEventProcessor, sink);
      sink.onRequest(num -> {
        log.info("request number: {}", num);
        for (int i = 1; i <= 5; i++) {
          sink.next("on request: " + i);
        }
      });
    });
    bride.subscribe(new BaseSubscriber<>() {
      @Override
      protected void hookOnSubscribe(Subscription subscription) {
        request(10);
      }

      @Override
      protected void hookOnNext(String value) {
        log.info(value);
        // request(1);
      }
    });
    MyEvent<String> event = new MyEvent<>();
    event.register(myEventProcessor);
    event.generateChunkData();
    event.done();
  }

  /**
   * cleaning up after push or create.
   */
  public void cleaningUpAfterPushOrCreate() {
    MyEventProcessor<String> processor = new MyEventProcessor<>();
    Flux<String> flux = Flux.create(sink -> {
      registerListener(processor, sink);
      sink.onCancel(() -> log.info("cancel..."))
          .onDispose(() -> log.info("dispose..."));
    });
    Disposable subscribe = flux.subscribe(log::info);
    MyEvent<String> event = new MyEvent<>();
    event.register(processor);
    event.generateChunkData();
    subscribe.dispose();
    event.done();
  }

  /**
   * the sample of handle.
   * combination of map and filter.
   */
  public void handle() {
    Flux<String> flux = Flux.just(-1, 30, 13, 9, 20)
        .handle((i, sink) -> {
          String letter = alphabet(i);
          if (letter != null) {
            sink.next(letter);
          }
        });
    flux.subscribe(log::info);
  }

  private String alphabet(int letterNumber) {
    if (letterNumber < 1 || letterNumber > 26) {
      return null;
    }
    int letterIndexAscii = 'A' + letterNumber - 1;
    return "" + (char) letterIndexAscii;
  }

  private static void registerListener(MyEventProcessor<String> myEventProcessor,
                                       FluxSink<String> sink) {
    myEventProcessor.register(
        new MyEventListener<>() {
          @Override
          public void onDataChunk(List<String> chunk) {
            for (String s : chunk) {
              sink.next(s);
            }
          }

          @Override
          public void processComplete() {
            log.info("complete");
            sink.complete();
          }
        });
  }

  private static void produceByCurrentThread(MyEvent<String> event) {
    event.generateChunkData();
  }

  private static Thread produceByAnotherThread(MyEvent<String> event) {
    Thread thread = new Thread(event::generateChunkData);
    thread.start();
    return thread;
  }

  interface MyEventListener<T> {
    void onDataChunk(List<T> chunk);

    void processComplete();
  }

  static class MyEventProcessor<T> {
    private MyEventListener<T> listener;

    public void register(MyEventListener<T> listener) {
      this.listener = listener;
    }

    public void process(List<T> list) {
      listener.onDataChunk(list);
    }

    public void done() {
      listener.processComplete();
    }
  }

  static class MyEvent<T> {
    private MyEventProcessor<T> processor;

    public void register(MyEventProcessor<T> processor) {
      this.processor = processor;
    }

    public void generateChunkData() {
      List<String> list = new ArrayList<>();
      list.add("value 1");
      list.add("value 2");
      processor.process((List<T>) list);
    }

    public void done() {
      processor.done();
    }
  }
}
