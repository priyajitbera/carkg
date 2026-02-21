package com.github.priyajitbera.carkg.service.common;

public interface DurationMeter {

  String start();

  long duration(String key);

  String durationLog(String key);
}
