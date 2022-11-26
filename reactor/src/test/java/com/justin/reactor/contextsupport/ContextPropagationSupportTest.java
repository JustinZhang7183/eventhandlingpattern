package com.justin.reactor.contextsupport;

import io.micrometer.context.ContextRegistry;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import reactor.core.publisher.Mono;

/**
 * Description: test case for context propagation support.
 *
 * @author Justin_Zhang
 * @date 11/26/2022 09:33
 */
@Slf4j
public class ContextPropagationSupportTest {
  //assuming TL is known to Context-Propagation as key TLKEY.
  static final ThreadLocal<String> TL = new ThreadLocal<>();

  static final String TLKEY = "TLKEY";

  @Test
  public void context_capture() {
    // TODO: Wrong way, How do implement capture ThreadLocal values?
    ContextRegistry contextRegistry = new ContextRegistry();
    contextRegistry.registerThreadLocalAccessor(TLKEY, TL);
    //in the main thread, TL is set to "HELLO"
    TL.set("HELLO");
    Mono.deferContextual(ctx ->
            Mono.delay(Duration.ofSeconds(1))
                //we're now in another thread, TL is not set
                .map(v -> "delayed ctx[" + TLKEY + "]="
                    + ctx.getOrDefault(TLKEY, "not found") + ", TL=" + TL.get()))
        .contextCapture()
        .log()
        .block(); // returns "delayed ctx[TLKEY]=HELLO, TL=null"
  }

  @Test
  public void restore_snapshot_using_handle() {
    //in the main thread, TL is set to "HELLO"
    TL.set("HELLO");

    Mono.delay(Duration.ofSeconds(1))
        //we're now in another thread, TL is not set yet
        .doOnNext(v -> System.out.println(TL.get()))
        //inside the handler however, TL _is_ restored
        .handle((v, sink) -> sink.next("handled delayed TL=" + TL.get()))
        .contextCapture()
        .log()
        .block(); // prints "null" and returns "handled delayed TL=HELLO"
  }

  @Test
  public void test() {
    Mono.just(1).zipWith(Mono.just(2), Integer::sum).log().subscribe();
    Mono.just(1).zipWhen(num -> Mono.just(num + 1)).log().subscribe();
    // each source need to emit at least one element.
    Mono<Void> mono = Mono.empty();
    Mono.just(1).zipWhen(num -> mono).log().subscribe();
  }
}
