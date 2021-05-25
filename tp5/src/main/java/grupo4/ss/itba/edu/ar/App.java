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
                                             .withOpening( 2 )
                                             .build();

        environment.printToFile();
    }
}
