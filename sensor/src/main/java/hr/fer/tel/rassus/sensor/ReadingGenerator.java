package hr.fer.tel.rassus.sensor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ReadingGenerator {

  private final List<Reading> readings = new ArrayList<>();
  private final long startMillis = System.currentTimeMillis();

  public ReadingGenerator(String file) throws IOException {
    List<String> lines = Files.readAllLines(Path.of(file));
    for (String line : lines.subList(1, lines.size())) {
      String[] v = line.split(",", -1);
      readings.add(new Reading(parse(v[0]), parse(v[1]), parse(v[2]),
          parse(v[3]), parse(v[4]), parse(v[5])));
    }
  }

  private static Double parse(String value) {
    return value.isBlank() ? null : Double.valueOf(value);
  }

  public Reading generate() {
    long activeSeconds = (System.currentTimeMillis() - startMillis) / 1000;
    int row = (int) (activeSeconds % 100) + 1;
    return readings.get(row - 1);
  }
}
