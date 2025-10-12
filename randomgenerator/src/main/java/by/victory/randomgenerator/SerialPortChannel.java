package by.victory.randomgenerator;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

public class SerialPortChannel implements Channel {
    private static final Logger logger = LoggerFactory.getLogger(SerialPortChannel.class);
    private final AtomicReference<Measurement> currentMeasurement =
            new AtomicReference<>(new Measurement(0, 0));

    public SerialPortChannel() {

        String serialPortName = PropertyReader.getProperty("serialPort");
        SerialPort serialPort = new SerialPort(serialPortName);
        try {
            serialPort.openPort();
            serialPort.setParams(
                    SerialPort.BAUDRATE_115200,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            serialPort.addEventListener(new SerialPortListener(serialPort));
        } catch (SerialPortException e) {
            logger.error("Serial port open error", e);
        }
    }

    @Override
    public Measurement getMeasurement() {
        return currentMeasurement.get();
    }

    public final class SerialPortListener implements SerialPortEventListener, AutoCloseable {
        private static final Logger logger = LoggerFactory.getLogger(SerialPortListener.class);
        private final SerialPort serialPort;

        public SerialPortListener(SerialPort serialPort) {
            this.serialPort = serialPort;
        }

        @Override
        public void close() {
            if (serialPort != null && serialPort.isOpened()) {
                try {
                    serialPort.closePort();
                } catch (SerialPortException e) {
                    logger.error("Error closing serial port", e);
                }
            }
        }

        @Override
        public void serialEvent(SerialPortEvent serialPortEvent) {
            if (serialPortEvent.isRXCHAR() && serialPortEvent.getEventValue() > 2) {
                try {
                    byte[] buffer = serialPort.readBytes(serialPortEvent.getEventValue());
                    currentMeasurement.set(new Measurement(buffer[2], buffer[4]));

                } catch (SerialPortException e) {
                    logger.error("Error receiving serial event");
                }
            }
        }
    }
}
