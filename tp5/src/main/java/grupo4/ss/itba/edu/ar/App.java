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
        infectionDistribution.put(InfectedState.SICK, 0.1);
        infectionDistribution.put(InfectedState.INMUNE, 0.1);
        Double infectionProbability = 1.0;
        Double defensesProbability = 1.0;

        Environment environment = Environment.builder()
                                             .withSeed( 28 )
                                             .withParticlesQuantityAndInfectedDistribution( 80, infectionDistribution )
                                             .withInfectionProfile( 0.001, 1.0, infectionProbability, defensesProbability )
                                             .withDt( 1e-2 )
                                             .withDt2( 1e-2 )
                                             .build();
        environment.run();
        environment.printToFile();
        // environment.printDensityOverTime();
    }
}
