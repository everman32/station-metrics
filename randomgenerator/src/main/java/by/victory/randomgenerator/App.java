package by.victory.randomgenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private volatile boolean running = true;

    public static void main(String[] args) {
        new App().run();
    }

    public void run() {
        int sendDelay = Integer.parseInt(PropertyReader.getProperty("sendDelayMs"));
        ChannelHttpClientFacade facade = new ChannelHttpClientFacade();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> running = false));

        while (running) {
            try {
                facade.getMeasurementAndSend();
                Thread.sleep(sendDelay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                logger.error("Error in main loop", e);
            }
        }
    }
}
