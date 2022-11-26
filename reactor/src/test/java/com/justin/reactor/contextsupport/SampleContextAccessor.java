package com.justin.reactor.contextsupport;

import io.micrometer.context.ThreadLocalAccessor;

/**
 * Description:
 *
 * @author Justin_Zhang
 * @date 11/26/2022 10:06
 */
public class SampleContextAccessor implements ThreadLocalAccessor {
  private Object value;

  @Override
  public Object key() {
    return "TLKEY";
  }

  @Override
  public Object getValue() {
    return value;
  }

  @Override
  public void setValue(Object value) {
    this.value = value;
  }

  @Override
  public void reset() {
    this.value = null;
  }
}
