package com.github.priyajitbera.carkg.service.data.jpa;

import java.util.Arrays;
import java.util.stream.Collectors;

public class IdGen {

  public String generate(String... parentIdChain) {
    return Arrays.stream(parentIdChain)
        .map(String::toLowerCase)
        .map(i -> i.replace(" ", "_"))
        .collect(Collectors.joining("-"));
  }
}
