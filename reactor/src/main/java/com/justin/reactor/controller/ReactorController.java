package com.justin.reactor.controller;

import java.time.Duration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Description: controller for reactor.
 *
 * @author Justin_Zhang
 * @date 11/26/2022 15:35
 */
@RestController
public class ReactorController {
  @GetMapping("/flux")
  public Flux<String> getFluxStr() {
    return Flux.interval(Duration.ofSeconds(1)).map(num -> String.valueOf(num).concat("-flux "));
  }
}
