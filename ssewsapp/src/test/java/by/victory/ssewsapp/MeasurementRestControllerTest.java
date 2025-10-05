package by.victory.ssewsapp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;


@ExtendWith(MockitoExtension.class)
class MeasurementRestControllerTest {
    @InjectMocks
    private MeasurementRestController measurementRestController;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    public void getMeasurementAndPublishEvent() {
        Measurement measurement = new Measurement(10, 20);

        int responseStatus = measurementRestController.publishMeasurement(measurement);
        Assertions.assertEquals(200, responseStatus);
        Mockito.verify(applicationEventPublisher).publishEvent(measurement);
    }
}