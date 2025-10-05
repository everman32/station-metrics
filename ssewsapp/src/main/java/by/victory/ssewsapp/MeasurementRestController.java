package by.victory.ssewsapp;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeasurementRestController {
    private final ApplicationEventPublisher publisher;

    public MeasurementRestController(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @PostMapping("/measurement")
    public HttpStatus publishMeasurement(@RequestBody Measurement measurement) {
        publisher.publishEvent(measurement);
        return HttpStatus.OK;
    }
}
