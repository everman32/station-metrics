package by.victory.randomgenerator;

import java.util.Random;

public class RandomChannel implements Channel {
    private static final int UPPERBOUND = 100;
    private final Random random;

    public RandomChannel() {
        this.random = new Random();
    }

    @Override
    public Measurement getMeasurement() {
        return new Measurement(random.nextInt(UPPERBOUND - 1) + 1,
                random.nextInt(UPPERBOUND - 1) + 1);
    }
}
