package hr.fer.tel.rassus.server.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import hr.fer.tel.rassus.server.beans.Reading;
import hr.fer.tel.rassus.server.beans.Sensor;
import hr.fer.tel.rassus.server.services.ReadingRepository;
import hr.fer.tel.rassus.server.services.SensorRepository;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReadingController {

  private final ReadingRepository readingRepository;
  private final SensorRepository sensorRepository;

  public ReadingController(ReadingRepository readingRepository, SensorRepository sensorRepository) {
    this.readingRepository = readingRepository;
    this.sensorRepository = sensorRepository;
  }

  // 4.3 Spremanje ocitanja pojedinog senzora
  @PostMapping("/sensors/{id}/readings")
  public ResponseEntity<?> newReading(@PathVariable("id") Long id, @RequestBody Reading reading) {
    Sensor sensor = sensorRepository.findById(id).orElse(null);
    if (sensor == null) {
      return ResponseEntity.noContent().build();
    }
    reading.setSensor(sensor);
    Reading saved = readingRepository.save(reading);
    return ResponseEntity
        .created(linkTo(methodOn(this.getClass()).getReading(saved.getId())).toUri())
        .build();
  }

  // 4.5 Popis ocitanja pojedinog senzora
  @GetMapping("/sensors/{id}/readings")
  public ResponseEntity<List<Reading>> getReadings(@PathVariable("id") Long id) {
    if (!sensorRepository.existsById(id)) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(readingRepository.findBySensorId(id));
  }

  @GetMapping("/readings/{id}")
  public ResponseEntity<Reading> getReading(@PathVariable("id") Long id) {
    Reading reading = readingRepository.findById(id).orElse(null);
    if (reading == null) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(reading);
  }
}