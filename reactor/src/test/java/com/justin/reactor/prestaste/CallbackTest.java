package com.justin.reactor.prestaste;

import com.justin.reactor.ReactorRunner;
import com.justin.reactor.service.OrderService;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

/**
 * Description: test for call back.
 *
 * @author Justin_Zhang
 * @date 11/11/2022 11:44
 */
@SpringBootTest(classes = ReactorRunner.class)
public class CallbackTest {
  @Autowired
  private OrderService orderService;
  
  @Test
  public void notice_customer_after_order_for_each() throws InterruptedException {
    Stream.of(1, 2).forEach(num -> orderService.createOrder());
  }

  @Test
  public void notice_customer_after_order_by_parallel() throws InterruptedException {
    Stream.of(1, 2).parallel().forEach(num -> orderService.createOrder());
  }

  @Test
  public void notice_customer_after_order_by_reactor() throws InterruptedException {
    // TODO: I thought it only takes 3 seconds, but not, why?
    Flux.just(1, 3).map(num -> orderService.createOrder()).subscribe();
  }
}
