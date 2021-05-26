package grupo4.ss.itba.edu.ar;

import grupo4.ss.itba.edu.ar.model.Environment;

/**
 * Hello world!
 */
public class App
{
    public static void main( String[] args ) {
        Environment environment = Environment.builder()
                                             .withSeed( 94 )
                                             .withOpening( 3 )
                                             .withParticlesQuantity( 380 )
                                             .withDt( 1e-2 )
                                             .build();
        environment.run();
        environment.printToFile(1.0 / 60.0); // 60FPS
    }
}
