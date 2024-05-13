package cannonball;

public class Main {
    public static void main(String[] args) throws Exception {
        String config = "0.5=1000,0.3=5000,0.15=10000,0.05=1000000";
        IntegerDistribution integerDistribution = new IntegerDistribution(config);
        Integer randomNumber = integerDistribution.getRandom();
        System.out.print(randomNumber);

    }
}