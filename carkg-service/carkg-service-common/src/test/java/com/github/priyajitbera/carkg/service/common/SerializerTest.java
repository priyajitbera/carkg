package com.github.priyajitbera.carkg.service.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class SerializerTest {

  @Test
  public void testOutputExamples() {
    Map<String, Object> car1 = new HashMap<>();
    car1.put("id", 1);
    car1.put("model", "Model S");
    car1.put("brand", "Tesla");
    car1.put("features", Arrays.asList("Autopilot", "Ludicrous Mode"));

    Map<String, Object> car2 = new HashMap<>();
    car2.put("id", 2);
    car2.put("model", "Mustang Mach-E");
    car2.put("brand", "Ford");
    car2.put("features", Arrays.asList("Co-Pilot360"));

    Map<String, Object> garage = new HashMap<>();
    garage.put("owner", "Alice");
    garage.put("location", "San Francisco");
    garage.put("cars", Arrays.asList(car1, car2));
    garage.put("capacity", 2);

    try (java.io.PrintWriter out = new java.io.PrintWriter("examples.txt")) {
      out.println("=== toTokenOriented Output ===");
      out.println(Serializer.toTokenOriented(garage));
      out.println("\n==============================");

      out.println("\n=== toTokenOriented1 Output ===");
      out.println(Serializer.toTokenOriented1(garage));
      out.println("===============================");
    } catch (java.io.FileNotFoundException e) {
      e.printStackTrace();
    }
  }
}
