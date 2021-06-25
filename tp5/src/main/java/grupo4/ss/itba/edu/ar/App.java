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

        Map<InfectedState, Double> infectionProbabilityPerState = new HashMap<>();
        infectionProbabilityPerState.put(InfectedState.HEALTHY, 0.0);
        infectionProbabilityPerState.put(InfectedState.INFECTED_DONT_SNEEZE, 0.0);
        infectionProbabilityPerState.put(InfectedState.INFECTED_SNEEZES, 0.8);
        infectionProbabilityPerState.put(InfectedState.INMUNE, 0.0);

        Map<InfectedState, Double> defensesProbabilityPerState = new HashMap<>();
        defensesProbabilityPerState.put(InfectedState.HEALTHY, 1.0);
        defensesProbabilityPerState.put(InfectedState.INFECTED_DONT_SNEEZE, 1.0);
        defensesProbabilityPerState.put(InfectedState.INFECTED_SNEEZES, 1.0);
        defensesProbabilityPerState.put(InfectedState.INMUNE, 0.0);
        Environment environment = Environment.builder()
                                             .withSeed( 28 )
                                             .withParticlesQuantityAndInfectedDistribution( 80, infectionDistribution )
                                             .withInfectionProfile( 0.5, 1.0, infectionProbabilityPerState, defensesProbabilityPerState )
                                             .withDt( 1e-2 )
                                             .withDt2( 1e-2 )
                                             .build();
        environment.run();
        environment.printToFile();
        // environment.printDensityOverTime();
    }
}
