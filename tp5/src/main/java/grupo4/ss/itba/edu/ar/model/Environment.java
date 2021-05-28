package grupo4.ss.itba.edu.ar.model;

import grupo4.ss.itba.edu.ar.utils.MathHelper;
import grupo4.ss.itba.edu.ar.utils.OutputColor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class Environment
{
    private final List<Wall> walls;
    private final Target target1;
    private final Target target2;
    private final List<EnvironmentState> states = new LinkedList<>();
    private List<Particle> particles;
    private final double dt;

    // ej_a
    private List<Double> dischargeTimes = new LinkedList<>();

    private Environment( Builder builder ) {
        this.walls = builder.walls;
        this.target1 = builder.target1;
        this.target2 = builder.target2;
        this.particles = builder.particles;
        this.dt = builder.dt;
        this.states.add( EnvironmentState.builder()
                                         .withParticles( this.particles )
                                         .withTime( 0 )
                                         .build() );
    }

    public void run() {
        int i = 0;
        while ( !this.particles.isEmpty() && i < 60000 ) {
            System.out.printf( "{i: %d; q: %d }%n", i, this.particles.size() );
            i++;
            this.particles.forEach( x -> x.setForceAndAcceleration( this.particles, this.walls ) );
            List<Particle> particles = new LinkedList<>();

            for ( Particle particle : this.particles ) {
                particle.move( this.dt );
                if ( !target2.reached( particle ) ) {
                    if ( target1.reached( particle ) ) {
                        particle.setTarget( this.target2 );
                    }
                    particles.add( particle );
                } else {
                    // ej_a
                    this.dischargeTimes.add(i * this.dt);
                }
            }

            this.states.add( EnvironmentState.builder()
                                             .withParticles( this.particles )
                                             .withTime( i * dt )
                                             .build() );
            this.particles = particles;
        }
    }

    public void printToFile( double dt ) {
        printStatic();
        printParticles( dt );
    }

    public void printToFileParticlesOverTime(StringBuilder builder, String fileName) {
        try ( BufferedWriter writer = new BufferedWriter( new FileWriter( fileName ) ) ) {
            for ( Double dischargeTime : this.dischargeTimes ) {
                builder.setLength( 0 );
                builder.append( dischargeTime )
                        .append( System.lineSeparator() );
                writer.write( builder.toString() );
                writer.flush();
            }
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    private void printParticles( double dt ) {
        String particlesName = "particles.xyz";
        StringBuilder builder = new StringBuilder();
        double accumulator = 0;
        boolean first = true;
        try ( BufferedWriter writer = new BufferedWriter( new FileWriter( particlesName ) ) ) {
            for ( EnvironmentState state : this.states ) {
                if ( first || accumulator > dt ) {
                    builder.setLength( 0 );
                    state.appendToStringBuilder( builder );
                    writer.write( builder.toString() );
                    writer.flush();
                    first = false;
                }
                else {
                    accumulator += this.dt;
                }
            }
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    private void printStatic() {
        String staticFilename = "static.xyz";

        try ( BufferedWriter writer = new BufferedWriter( new FileWriter( staticFilename ) ) ) {
            StringBuilder builder = new StringBuilder();

            int quantity = 0;
            for ( Line wall : walls ) {
                quantity += wall.appendToStringBuilder( builder, OutputColor.Orange );
            }

            quantity += target1.appendToStringBuilder( builder, OutputColor.Red );
            quantity += target2.appendToStringBuilder( builder, OutputColor.Red );

            writer.write( quantity + System.lineSeparator() + System.lineSeparator() + builder );
            writer.flush();
        }
        catch ( IOException ioException ) {
            ioException.printStackTrace();
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder
    {
        private final List<Wall> walls;
        private final List<Particle> particles;
        private Optional<Integer> seed;
        private Target target1;
        private Target target2;
        private double dt;

        private Builder() {
            this.walls = new LinkedList<>();

            final Wall leftWall = Wall.builder()
                                      .withStart( 0, 0 )
                                      .withEnd( 0, 20 )
                                      .build();
            final Wall upperWall = Wall.builder()
                                       .withStart( 0, 20 )
                                       .withEnd( 20, 20 )
                                       .build();
            final Wall rightWall = Wall.builder()
                                       .withStart( 20, 0 )
                                       .withEnd( 20, 20 )
                                       .build();
            this.walls.add( leftWall );
            this.walls.add( upperWall );
            this.walls.add( rightWall );
            this.seed = Optional.empty();
            this.particles = new LinkedList<>();
        }

        public Builder withOpening( double opening ) {
            this.buildBottomWall( opening );
            this.buildTargets( opening );

            return this;
        }

        private void buildBottomWall( double opening ) {
            final Wall bottomLeftWall = Wall.builder()
                                            .withStart( 0, 0 )
                                            .withEnd( 10 - ( opening / 2.0 ), 0 )
                                            .build();
            final Wall bottomRightWall = Wall.builder()
                                             .withStart( 10 + ( opening / 2.0 ), 0 )
                                             .withEnd( 20, 0 )
                                             .build();
            this.walls.add( bottomLeftWall );
            this.walls.add( bottomRightWall );
        }

        private void buildTargets( double opening ) {
            this.target1 = Target.builder()
                                 .withStart( 10 - ( opening / 2.0 ) + 0.1, 0 )
                                 .withEnd( 10 + ( opening / 2.0 ) - 0.1, 0 )
                                 .build();
            this.target2 = Target.builder()
                                 .withStart( 8.5, -10 )
                                 .withEnd( 11.5, -10 )
                                 .build();
        }

        public Builder withSeed( int seed ) {
            this.seed = Optional.of( seed );
            return this;
        }

        public Builder withParticlesQuantity( int quantity ) {
            Random random = seed.map( Random::new )
                                .orElseGet( Random::new );

            for ( int i = 0; i < quantity; i++ ) {
                double radius = MathHelper.randBetween( random, 0.5 / 2.0, 0.7 / 2.0 );
                double x = MathHelper.randBetween( random, 0 + radius, 20 - radius );
                double y = MathHelper.randBetween( random, 0 + radius, 20 - radius );

                Particle particle = Particle.builder()
                                            .withId( UUID.randomUUID() )
                                            .withMass( 80 )
                                            .withRadius( radius )
                                            .withDesiredSpeed( 2 )
                                            .withTarget( this.target1 )
                                            .withPosition( x, y )
                                            .withVelocity( 0, 0 )
                                            .build();

                while ( !notOverlap( particle ) ) //TODO: Condicion de corte
                {
                    x = MathHelper.randBetween( random, 0 + radius, 20 - radius );
                    y = MathHelper.randBetween( random, 0 + radius, 20 - radius );
                    particle.setPosition( new Point( x, y ) );
                }

                this.particles.add( particle );
            }

            return this;
        }

        public boolean notOverlap( Particle particle ) {
            return this.particles.stream()
                                 .noneMatch( x -> x.areOverlapped( particle ) );
        }

        public Builder withDt( double dt ) {
            this.dt = dt;
            return this;
        }

        public Environment build() {
            return new Environment( this );
        }
    }
}

