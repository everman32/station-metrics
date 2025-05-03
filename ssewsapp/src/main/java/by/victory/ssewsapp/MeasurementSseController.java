package by.victory.ssewsapp;

import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@RestController
public class MeasurementSseController {
    private final Set<SseEmitter> clients = new CopyOnWriteArraySet<>();

    @CrossOrigin(origins = "*")
    @GetMapping("/open-measurement-stream")
    public SseEmitter openMeasurementStream() {
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        clients.add(sseEmitter);

        sseEmitter.onTimeout(() -> clients.remove(sseEmitter));
        sseEmitter.onError(throwable -> clients.remove(sseEmitter));

        return sseEmitter;
    }

    @Async
    @EventListener
    public void onMeasurementReceived(Measurement measurement) {
        List<SseEmitter> errorEmitters = new ArrayList<>();

        clients.forEach(emitter -> {
            try {
                emitter.send(measurement, MediaType.APPLICATION_JSON);
            } catch (Exception e) {
                errorEmitters.add(emitter);
            }
        });
        errorEmitters.forEach(clients::remove);
    }
}
