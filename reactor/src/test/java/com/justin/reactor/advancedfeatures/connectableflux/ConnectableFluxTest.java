package com.justin.reactor.advancedfeatures.connectableflux;

import com.justin.reactor.util.ThreadUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

/**
 * Description: connectable flux test case.
 *
 * @author Justin_Zhang
 * @date 11/24/2022 16:21
 */
public class ConnectableFluxTest {
  @Test
  public void connect() {
    Flux<Integer> source = Flux.range(1, 3)
        .doOnSubscribe(s -> System.out.println("subscribed to source"));

    // TODO: the difference between publish() and replay()(this replay and sink's replay)
    ConnectableFlux<Integer> co = source.publish();

    co.subscribe(System.out::println, e -> {}, () -> {});
    co.subscribe(System.out::println, e -> {}, () -> {});

    System.out.println("done subscribing");
    ThreadUtil.sleepBySecond(1);
    System.out.println("will now connect");

    co.connect();
  }

  @Test
  public void auto_connect() {
    Flux<Integer> source = Flux.range(1, 3)
        .doOnSubscribe(s -> System.out.println("subscribed to source"));

    // refCount(n) can do this too. And it also detects when these subscriptions are cancelled.
    // if not enough subscribers are tracked, the source is "disconnected"
    Flux<Integer> autoCo = source.publish().autoConnect(2);

    autoCo.subscribe(System.out::println, e -> {}, () -> {});
    System.out.println("subscribed first");
    ThreadUtil.sleepBySecond(1);
    System.out.println("subscribing second");
    autoCo.subscribe(System.out::println, e -> {}, () -> {});
  }
}
