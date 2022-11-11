package com.justin.reactor.service;

import com.justin.reactor.pretaste.NoticeCustomerCallback;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

/**
 * Description: the controller of order service.
 *
 * @author Justin_Zhang
 * @date 11/11/2022 11:17
 */
@Service
public class OrderService {
  private final NoticeCustomerCallback noticeCustomerCallback;

  public OrderService(NoticeCustomerCallback noticeCustomerCallback) {
    this.noticeCustomerCallback = noticeCustomerCallback;
  }

  /**
   * description: service of create order.
   *
   * @return String
   * @throws InterruptedException interrupt sleep
   */
  public String createOrder() {
    try {
      TimeUnit.SECONDS.sleep(3);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    noticeCustomerCallback.sendMessageToCustomerPhone("no" + new Random().nextInt());
    return "create order successfully !";
  }
}
