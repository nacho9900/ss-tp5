package grupo4.ss.itba.edu.ar;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import grupo4.ss.itba.edu.ar.model.Environment;
import grupo4.ss.itba.edu.ar.model.InfectedState;

/**
 * Hello world!
 */
public class App
{
    public static void main( String[] args ) throws Exception {
        // defaultRun();
        // timeUntilZeroHealthy();
        // nonHealthyAmountPerTime();
        nonHealthyPercentagePerTime();
    }

    public static void defaultRun() throws Exception {
        Map<InfectedState, Double> infectionDistribution = new HashMap<>();
        infectionDistribution.put(InfectedState.HEALTHY, 0.8);
        infectionDistribution.put(InfectedState.SICK, 0.1);
        infectionDistribution.put(InfectedState.INMUNE, 0.1);
        Double infectionProbability = 1.0;
        Double defensesProbability = 1.0;

        Environment environment = Environment.builder()
                                             .withSeed( 28 )
                                             .withParticlesQuantityAndInfectedDistribution( 80, infectionDistribution, 0.0 )
                                             .withInfectionProfile( 0.001, 1.0, infectionProbability, defensesProbability )
                                             .withDt( 1e-2 )
                                             .withDt2( 1e-2 )
                                             .build();
        environment.run();
        environment.printToFile();
    }

    public static Environment run(double healthyPercentage, double sickPercentage, double inmunePercentage,
        double generalProbability, double infectProbability, double defendProbability, int seed, int N,
        double infectionRadius, double dt, double dt2, double stillPercentage) throws Exception {
        Map<InfectedState, Double> infectionDistribution = new HashMap<>();
        infectionDistribution.put(InfectedState.HEALTHY, healthyPercentage);
        infectionDistribution.put(InfectedState.SICK, sickPercentage);
        infectionDistribution.put(InfectedState.INMUNE, inmunePercentage);
        
        if (Math.abs(healthyPercentage+sickPercentage+inmunePercentage - 1.0) > 0.00001) {
            throw new IllegalArgumentException("Distribution percentages must add up to 1");
        }

        Environment environment = Environment.builder()
                                             .withSeed( seed )
                                             .withParticlesQuantityAndInfectedDistribution( N, infectionDistribution, stillPercentage )
                                             .withInfectionProfile( generalProbability, infectionRadius, infectProbability, defendProbability )
                                             .withDt( dt )
                                             .withDt2( dt2 )
                                             .build();
        environment.run();
        return environment;
    }

    // ej 1 (falta decidir que variable/s variamos y que valores le ponemos a las variables fijas,
    //  puse N por ahora como variable)
    public static void timeUntilZeroHealthy() throws Exception {
        // time it takes for a simulation to have no healthy people, only sick and inmune

        // varying the N
        Double radius = 1.0;
        for ( int n = 50; n <= 190; n+=15) {
            Environment e = run(.9, .1, .0, .002, 1.0, 1.0, 53845, n, radius, 1e-2, 1e-2, 0.0);
            System.out.println(e.getTotalTime());
            e.printToFile("r", radius);
        }
    }

    // ej 2
    public static void nonHealthyAmountPerTime() throws Exception {
        // nonHealthy = sick + inmune (people who once got sick)

        for ( double r = 0.5; r <= 2.1; r+=0.2) {
            for (int seed = 0; seed < 10; seed++) {
                Environment e = run(.9, .1, .0, .002, 1.0, 1.0, seed, 90, r, 1e-2, 1e-2, 0.0);
                e.printNonHealthyOverTime(r);
                e.printToFile("r", r);
            }
        }
    }

    // ej 3
    public static void nonHealthyPercentagePerTime() throws Exception {
        // nonHealthy = sick + inmune (people who once got sick)

        int N = 90;
        for ( double staticPercentage = 0.0; staticPercentage <= 1.0; staticPercentage+=0.1) {
            for (int seed = 0; seed < 10; seed++) {
                Environment e = run(.9, .1, .0, .002, 1.0, 1.0, seed, N, 1.0, 1e-2, 1e-2, staticPercentage);
                e.printDistributionOverTime(staticPercentage, N);
                e.printToFile("static", staticPercentage);
            }
        }
    }
}
