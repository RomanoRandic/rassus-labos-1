package hr.fer.tel.rassus.server.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import hr.fer.tel.rassus.server.beans.Neighbour;
import hr.fer.tel.rassus.server.beans.Sensor;
import hr.fer.tel.rassus.server.services.SensorRepository;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sensors")
public class SensorsController {

  private final SensorRepository sensorRepository;

  public SensorsController(SensorRepository sensorRepository) {
    this.sensorRepository = sensorRepository;
  }

  // 4.1 Registracija
  @PostMapping
  public ResponseEntity<?> register(@RequestBody Sensor sensor) {
    Sensor saved = sensorRepository.save(sensor);
    return ResponseEntity
        .created(linkTo(methodOn(this.getClass()).getSensor(saved.getId())).toUri())
        .build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Sensor> getSensor(@PathVariable("id") Long id) {
    Sensor sensor = sensorRepository.findById(id).orElse(null);
    if (sensor == null) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(sensor);
  }

  // 4.4 Popis senzora
  @GetMapping
  public List<Sensor> getSensors() {
    return sensorRepository.findAll();
  }

  // 4.2 Najblizi susjed
  @GetMapping("/{id}/neighbour")
  public ResponseEntity<Neighbour> getNeighbour(@PathVariable("id") Long id) {
    Sensor me = sensorRepository.findById(id).orElse(null);
    if (me == null) {
      return ResponseEntity.noContent().build();
    }
    Sensor closest = null;
    double min = Double.MAX_VALUE;
    for (Sensor other : sensorRepository.findAll()) {
      if (other.getId().equals(me.getId())) {
        continue;
      }
      double d = haversine(me, other);
      if (d < min) {
        min = d;
        closest = other;
      }
    }
    if (closest == null) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(new Neighbour(closest.getLatitude(), closest.getLongitude(),
        closest.getIp(), closest.getPort()));
  }

  private static double haversine(Sensor s1, Sensor s2) {
    double R = 6371;
    double lat1 = Math.toRadians(s1.getLatitude());
    double lat2 = Math.toRadians(s2.getLatitude());
    double lon1 = Math.toRadians(s1.getLongitude());
    double lon2 = Math.toRadians(s2.getLongitude());
    double dlon = lon2 - lon1;
    double dlat = lat2 - lat1;
    double a = Math.pow(Math.sin(dlat / 2), 2)
        + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
  }
}