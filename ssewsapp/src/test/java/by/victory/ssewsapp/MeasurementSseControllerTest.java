package by.victory.ssewsapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeasurementSseControllerTest {
    @Mock
    private SseEmitter emitter1;

    @Mock
    private SseEmitter emitter2;

    @Mock
    private Measurement measurement;

    private MeasurementSseController controller;
    private CopyOnWriteArraySet<SseEmitter> clients;

    @BeforeEach
    void setUp() {
        controller = new MeasurementSseController();
        clients = new CopyOnWriteArraySet<>();
        try {
            var field = MeasurementSseController.class.getDeclaredField("clients");
            field.setAccessible(true);
            clients = (CopyOnWriteArraySet<SseEmitter>) field.get(controller);
        } catch (Exception e) {
            fail("Failed to access clients field", e);
        }
    }

    @Test
    void openMeasurementStreamShouldCreateNewEmitterAndAddToClients() {
        SseEmitter result = controller.openMeasurementStream();

        assertNotNull(result);
        assertEquals(1, clients.size());
        assertTrue(clients.contains(result));
    }

    @Test
    void onMeasurementReceivedShouldSendToAllClients() throws IOException {
        clients.add(emitter1);
        clients.add(emitter2);

        controller.onMeasurementReceived(measurement);

        verify(emitter1).send(eq(measurement), eq(MediaType.APPLICATION_JSON));
        verify(emitter2).send(eq(measurement), eq(MediaType.APPLICATION_JSON));
    }

    @Test
    void onMeasurementReceivedShouldRemoveFailingClients() throws IOException {
        clients.add(emitter1);
        clients.add(emitter2);

        doThrow(IOException.class).when(emitter1).send(any(), any());

        controller.onMeasurementReceived(measurement);

        verify(emitter1).send(eq(measurement), eq(MediaType.APPLICATION_JSON));
        verify(emitter2).send(eq(measurement), eq(MediaType.APPLICATION_JSON));
        assertEquals(1, clients.size());
        assertFalse(clients.contains(emitter1));
        assertTrue(clients.contains(emitter2));
    }

    @Test
    void onMeasurementReceivedShouldHandleEmptyClients() {
        assertDoesNotThrow(() -> controller.onMeasurementReceived(measurement));
    }

    @Test
    void onMeasurementReceivedShouldHandleMultipleFailures() throws IOException {
        SseEmitter emitter3 = mock(SseEmitter.class);
        clients.add(emitter1);
        clients.add(emitter2);
        clients.add(emitter3);

        doThrow(IOException.class).when(emitter1).send(any(), any());
        doThrow(IllegalStateException.class).when(emitter2).send(any(), any());

        controller.onMeasurementReceived(measurement);

        verify(emitter1).send(eq(measurement), eq(MediaType.APPLICATION_JSON));
        verify(emitter2).send(eq(measurement), eq(MediaType.APPLICATION_JSON));
        verify(emitter3).send(eq(measurement), eq(MediaType.APPLICATION_JSON));
        assertEquals(1, clients.size());
        assertTrue(clients.contains(emitter3));
    }

    @Test
    void clientManagementShouldHandleConcurrentModification() throws IOException {
        clients.add(emitter1);
        clients.add(emitter2);

        doAnswer(invocation -> {
            clients.remove(emitter2);
            throw new IOException("Test exception");
        }).when(emitter1).send(any(), any());

        controller.onMeasurementReceived(measurement);

        verify(emitter1).send(eq(measurement), eq(MediaType.APPLICATION_JSON));
        assertEquals(0, clients.size());
    }
}