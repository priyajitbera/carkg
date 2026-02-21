package com.github.priyajitbera.carkg.service.common;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NoArgsConstructor;
import org.springframework.util.StopWatch;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class SimpleDurationMeter implements DurationMeter {

  public static final DurationMeter DURATION_METER = new SimpleDurationMeter();

  private Map<String, StopWatch> stopWatches = new ConcurrentHashMap<>();

  @Override
  public String start() {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    String key = UUID.randomUUID().toString();
    stopWatches.put(key, stopWatch);
    return key;
  }

  @Override
  public long duration(String key) {
    StopWatch stopWatch = stopWatches.get(key);
    if (stopWatch == null) throw new IllegalArgumentException("No stopwatch found for key: " + key);
    stopWatch.stop();
    long duration = stopWatch.getTotalTimeMillis();
    stopWatches.remove(key);
    return duration;
  }

  @Override
  public String durationLog(String key) {
    return "duration: " + duration(key) + " ms";
  }
}
