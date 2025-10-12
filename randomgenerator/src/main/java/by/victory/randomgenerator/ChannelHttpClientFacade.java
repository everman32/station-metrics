package by.victory.randomgenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelHttpClientFacade {
    private static final Logger logger = LoggerFactory.getLogger(ChannelHttpClientFacade.class);
    private final Channel channel;
    private final HttpClient httpClient;

    public ChannelHttpClientFacade() {
        this.channel = ChannelFactory.getChannel();
        this.httpClient = new HttpClient();
    }

    public void getMeasurementAndSend() {
        Measurement measurement = channel.getMeasurement();
        if (measurement != null) {
            httpClient.send(measurement);
            logger.debug("Sent measurement: {}", measurement);
        } else {
            logger.warn("Received null measurement from channel");
        }
    }
}
