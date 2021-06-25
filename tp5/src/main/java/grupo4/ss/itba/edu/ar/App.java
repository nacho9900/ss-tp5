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
        defaultRun();
    }

    public static void defaultRun() throws Exception {
        Map<InfectedState, Double> infectionDistribution = new HashMap<>();
        infectionDistribution.put(InfectedState.HEALTHY, 0.8);
        infectionDistribution.put(InfectedState.INFECTED_DONT_SNEEZE, 0.0);
        infectionDistribution.put(InfectedState.INFECTED_SNEEZES, 0.1);
        infectionDistribution.put(InfectedState.INMUNE, 0.1);
        Environment environment = Environment.builder()
                                             .withSeed( 28 )
                                             .withParticlesQuantityAndInfectedDistribution( 80, infectionDistribution )
                                             .withInfectionProfile( 0.5, 1.0, Arrays.asList(0.0, 0.0, 0.8, 0.0), Arrays.asList(1.0, 1.0, 1.0, 0.0) )
                                             .withDt( 1e-2 )
                                             .withDt2( 1e-2 )
                                             .build();
        environment.run();
        environment.printToFile();
        // environment.printDensityOverTime();
    }
}
