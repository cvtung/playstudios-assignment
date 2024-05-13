package cannonball;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * IntegerDistribution is a functional class that its main purpose is to generate a random value base on
 * a predefined distribution.
 *
 * @author Cao Van Tung
 * @version 1
 * @since 1.8
 */
public class IntegerDistribution {

    // Delimiter between distributions in the config
    private static final String DELIMITER_DISTRIBUTION_CONFIG = ",";

    // Delimiter between numbers in a distribution
    private static final String DELIMITER_DISTRIBUTION_VALUE = "=";

    private static final int PROBABILITY_TOTAL = 1;
    private static final int RANDOM_NUMBER_MIN = 1;
    private static final int RANDOM_NUMBER_MAX = 100;

    private ConcurrentHashMap<Float, Integer> distributionMap;

    public ConcurrentHashMap<Float, Integer> getDistributionMap() {
        return distributionMap;
    }

    /**
     * Validate and parse distribution a config string into a map of probabilities and corresponding points.
     * Config string should be in this format: "0.5=1000,0.3=5000,0.15=10000,0.05=1000000".
     * Total probability must be 100%.
     *
     * @param distribution Distribution config string
     * @throws IllegalArgumentException If any validation fails
     */
    public IntegerDistribution(String distribution) throws IllegalArgumentException {
        // Validate config
        if (distribution == null) {
            throw new IllegalArgumentException("Distribution config string cannot be null");
        }
        String config = distribution.replaceAll("\\s+", "");
        if (config.isEmpty() == true) {
            throw new IllegalArgumentException("Distribution config string cannot be empty");
        }

        // Split config into distribution strings
        String[] distributionArray = config.split(DELIMITER_DISTRIBUTION_CONFIG);
        if (distributionArray == null || distributionArray.length == 0) {
            throw new IllegalArgumentException("Distribution array cannot be null or empty");
        }

        // Init probability-point mapping
        distributionMap = new ConcurrentHashMap<>();

        // Init variable to check total probability from input config
        Float totalProbability = 0f;

        for (String distributionString : distributionArray) {
            if (distributionString == null || distributionString.isEmpty() == true) {
                throw new IllegalArgumentException("Distribution string cannot be null or empty");
            }

            // Split distribution string into values
            String[] values = distributionString.split(DELIMITER_DISTRIBUTION_VALUE);
            if (values == null || values.length == 0) {
                throw new IllegalArgumentException("Distribution values cannot be null or empty");
            }

            // Parse probability and validate probability
            Float probability = null;
            try {
                probability = Float.valueOf(values[0]);
            } catch (Exception e) {
                throw new IllegalArgumentException("Probability cannot be parsed");
            }
            if (probability == null) {
                throw new IllegalArgumentException("Probability cannot be null");
            }
            if (probability < 0) {
                throw new IllegalArgumentException("Probability cannot be negative");
            }

            // Parse point and validate point
            Integer point = null;
            try {
                point = Integer.valueOf(values[1]);
            } catch (Exception e) {
                throw new IllegalArgumentException("Point cannot be parsed");
            }
            if (point == null) {
                throw new IllegalArgumentException("Point cannot be null");
            }
            // point can be negative actually
            // if (point < 0) {
            //     throw new IllegalArgumentException("Point cannot be negative");
            // }

            // Store probability and point
            distributionMap.put(probability, point);

            // Accumulate total probability
            totalProbability = totalProbability + probability;
        }

        // Validate distribution map
        if (distributionMap == null || distributionMap.isEmpty() == true) {
            throw new IllegalArgumentException("Distribution map cannot be null or empty");
        }

        // Validate total probability
        if (totalProbability != PROBABILITY_TOTAL) {
            throw new IllegalArgumentException("Total probability cannot be less or greater than 100%");
        }
    }

    /**
     * Return a number based on its distribution.
     * This method generate a random number between 1 and 100
     * then check it with probability pools to find corresponding value of the chosen pool
     *
     * @return Integer a number based on its probability. Returns null if it could not find any value from distribution.
     */
    public Integer getRandom() {
        // Generate a random number between 1 and 100
        int randomNumber = ThreadLocalRandom.current().nextInt(RANDOM_NUMBER_MIN, RANDOM_NUMBER_MAX + 1);
        float randomFloat = randomNumber / 100f;

        // Variables to calculate max value of current probability pool
        // while min value of this pool is just greater than max value of previous probability pool
        float probabilityMax = 0f;
        for (Map.Entry<Float, Integer> distributionEntry : distributionMap.entrySet()) {
            probabilityMax = probabilityMax + distributionEntry.getKey();
            if (randomFloat <= probabilityMax) {
                return distributionEntry.getValue();
            }
        }

        return null;
    }

}