package grupo4.ss.itba.edu.ar;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import grupo4.ss.itba.edu.ar.model.Environment;

/**
 * Hello world!
 */
public class App
{
    public static void main( String[] args ) {
        defaultRun();
        // a();
        // c();
    }

    public static void defaultRun() {
        Environment environment = Environment.builder()
                                             .withSeed( 94 )
                                             .withOpening( 1.6 )
                                             .withParticlesQuantity( 200 )
                                             .withDt( 1e-2 )
                                             .withDt2( 1e-2 )
                                             .build();
        environment.run();
        environment.printToFile();
        environment.printDensityOverTime();
    }

    public static void a() {
        StringBuilder builder = new StringBuilder();
        for ( int i = 0; i < 10; i++ ) {
            Environment environment = Environment.builder()
                                                 .withSeed( i )
                                                 .withOpening( 1.2 )
                                                 .withParticlesQuantity( 200 )
                                                 .withDt( 1e-4 )
                                                 .withDt2( 1e-2 )
                                                 .build();
            environment.run();
            environment.printToFileParticlesOverTime( builder,
                                                      "ej_a/particlesOverTime_N_200_opening_1_2_dt_1e-4_seed_" + i +
                                                      ".csv" );
        }
    }

    public static void c() {
        StringBuilder builder = new StringBuilder();
        List<Integer> Ns = new LinkedList<>();
        List<Double> ds = new LinkedList<>();
        Ns.add( 200 );
        Ns.add( 260 );
        Ns.add( 320 );
        Ns.add( 380 );
        ds.add( 1.2 );
        ds.add( 1.8 );
        ds.add( 2.4 );
        ds.add( 3.0 );

        for ( int j = 0; j < Ns.size(); j++ ) {
            Integer N = Ns.get( j );
            Double d = ds.get( j );
            for ( int i = 0; i < 10; i++ ) {
                Environment environment = Environment.builder()
                                                     .withSeed( i )
                                                     .withOpening( d )
                                                     .withParticlesQuantity( N )
                                                     .withDt( 1e-4 )
                                                     .withDt2( 1e-2 )
                                                     .build();
                environment.run();
                environment.printToFileParticlesOverTime( builder, "ej_c/flowPerOpening_N_" + N + "_opening_" +
                                                                   d.toString()
                                                                    .replace( '.', '_' ) + "_dt_1e-4_seed_" + i +
                                                                   ".csv" );
            }
        }
    }
}
