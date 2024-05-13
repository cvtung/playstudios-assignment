package cannonball;

import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class IntegerDistributionTest {

    @Test
    public void testConstructorWithNullConfig() {
        String config = null;

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> { new IntegerDistribution(config); }
        );

        assertEquals("Distribution config string cannot be null", exception.getMessage());
    }

    @Test
    public void testConstructorWithEmptyConfig() {
        String config = "   \t   ";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> { new IntegerDistribution(config); }
        );

        assertEquals("Distribution config string cannot be empty", exception.getMessage());
    }

    @Test
    public void testConstructorWithEmptyDistributionArrayConfig() {
        String config = ",";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> { new IntegerDistribution(config); }
        );

        assertEquals("Distribution array cannot be null or empty", exception.getMessage());
    }

    @Test
    public void testConstructorWithEmptyDistributionValueConfig1() {
        String config = "=";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> { new IntegerDistribution(config); }
        );

        assertEquals("Distribution values cannot be null or empty", exception.getMessage());
    }

    @Test
    public void testConstructorWithEmptyDistributionValueConfig2() {
        String config = "1=1000,=";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> { new IntegerDistribution(config); }
        );

        assertEquals("Distribution values cannot be null or empty", exception.getMessage());
    }

    @Test
    public void testConstructorWithUnparsableProbability1() {
        String config = "a=1000";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> { new IntegerDistribution(config); }
        );

        assertEquals("Probability cannot be parsed", exception.getMessage());
    }

    @Test
    public void testConstructorWithUnparsableProbability2() {
        String config = "=1000";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> { new IntegerDistribution(config); }
        );

        assertEquals("Probability cannot be parsed", exception.getMessage());
    }

    @Test
    public void testConstructorWithNegativeProbability() {
        String config = "-0.5=1000";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> { new IntegerDistribution(config); }
        );

        assertEquals("Probability cannot be negative", exception.getMessage());
    }

    @Test
    public void testConstructorWithUnparsablePoint1() {
        String config = "0.5=a";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> { new IntegerDistribution(config); }
        );

        assertEquals("Point cannot be parsed", exception.getMessage());
    }

    @Test
    public void testConstructorWithUnparsablePoint2() {
        String config = "0.5=";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> { new IntegerDistribution(config); }
        );

        assertEquals("Point cannot be parsed", exception.getMessage());
    }

    @Test
    public void testConstructorWithTotalProbabilityLessThan100() {
        String config = "0.5=1000";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> { new IntegerDistribution(config); }
        );

        assertEquals("Total probability cannot be less or greater than 100%", exception.getMessage());
    }

    @Test
    public void testConstructorWithTotalProbabilityGreaterThan100() {
        String config = "0.5=1000,0.4=2000,0.3=3000";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> { new IntegerDistribution(config); }
        );

        assertEquals("Total probability cannot be less or greater than 100%", exception.getMessage());
    }

    @Test
    public void ConstructorWithCorrectConfig() {

        String config = "0.5=1000,0.3=5000,0.15=10000,0.05=1000000";

        IntegerDistribution integerDistribution = new IntegerDistribution(config);
        ConcurrentHashMap<Float, Integer> distributionMap = integerDistribution.getDistributionMap();

        ConcurrentHashMap<Float, Integer> expectedDistributionMap = new ConcurrentHashMap<>();
        expectedDistributionMap.put(0.5f, 1000);
        expectedDistributionMap.put(0.3f, 5000);
        expectedDistributionMap.put(0.15f, 10000);
        expectedDistributionMap.put(0.05f, 1000000);

        //1. Test equal, ignore order
        assertThat(distributionMap, is(expectedDistributionMap));

        //2. Test size
        assertThat(distributionMap.size(), is(4));

        //3. Test map entry
        assertTrue(distributionMap.containsKey(0.5f) && distributionMap.get(0.5f) == 1000);
        assertTrue(distributionMap.containsKey(0.3f) && distributionMap.get(0.3f) == 5000);
        assertTrue(distributionMap.containsKey(0.15f) && distributionMap.get(0.15f) == 10000);
        assertTrue(distributionMap.containsKey(0.05f) && distributionMap.get(0.05f) == 1000000);
    }
}
