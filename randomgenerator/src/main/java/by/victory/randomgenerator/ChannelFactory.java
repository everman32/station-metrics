package by.victory.randomgenerator;

public final class ChannelFactory {
    private ChannelFactory() {
    }

    public static Channel getChannel() {
        return PropertyReader.getProperty("channel")
                .equals("serial")
                ? new SerialPortChannel()
                : new RandomChannel();
    }
}
