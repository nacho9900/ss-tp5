package grupo4.ss.itba.edu.ar;

import grupo4.ss.itba.edu.ar.model.Environment;

/**
 * Hello world!
 */
public class App
{
    public static void main( String[] args ) {
        // defaultRun();
        a();
    }

    public static void defaultRun() {
        Environment environment = Environment.builder()
                                             .withSeed( 94 )
                                             .withOpening( 3 )
                                             .withParticlesQuantity( 380 )
                                             .withDt( 1e-2 )
                                             .build();
        environment.run();
        environment.printToFile(1.0 / 60.0); // 60FPS
    }

    public static void a() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            Environment environment = Environment.builder()
                                                 .withSeed( i )
                                                 .withOpening( 1.2 )
                                                 .withParticlesQuantity( 200 )
                                                 .withDt( 1e-2 )
                                                 .build();
            environment.run();
            environment.printToFileParticlesOverTime(builder, "ej_a/particlesOverTime_N_200_opening_1_2_dt_1e-2_seed_"+ i +".csv");
        }
    }
}
