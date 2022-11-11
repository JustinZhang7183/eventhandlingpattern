package com.justin.reactor.pretaste;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Description: call back for customer after they order.
 *
 * @author Justin_Zhang
 * @date 11/11/2022 11:26
 */
@Slf4j
@Component
public class NoticeCustomerCallback {
  public void sendMessageToCustomerPhone(String orderNo) {
    log.info("order no: {}", orderNo);
  }
}
