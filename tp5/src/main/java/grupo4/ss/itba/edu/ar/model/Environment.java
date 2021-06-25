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
    private final List<EnvironmentState> states = new LinkedList<>();
    private List<Particle> particles;
    private final double dt;
    private final double dt2;
    private double area;
    private Random random;

    // ej_a
    private final List<Double> dischargeTimes = new LinkedList<>();
    private final List<Double> densityOverTime = new LinkedList<>();

    private Environment( Builder builder ) {
        this.walls = builder.walls;
        this.particles = builder.particles;
        this.dt = builder.dt;
        this.dt2 = builder.dt2;
        this.states.add( EnvironmentState.builder()
                                         .withParticles( this.particles )
                                         .withTime( 0 )
                                         .build() );
        this.area = this.particles.stream()
                                  .mapToDouble( Particle::getArea )
                                  .sum();
        this.random = builder.random;
    }

    public void run() {
        double timeAccumulator = 0;
        int i = 0;
        while ( i < 10_000 ) {
            if ( i % 1000 == 0 ) {
                System.out.printf( "{i: %d; q: %d }%n", i, this.particles.size() );
            }

            i++;
            List<Particle> particles = new LinkedList<>();
            final double currentTime = i * this.dt;

            double rightestX = 0;
            double leftestX = 20;
            double topmostY = 0;
            int count = 0;

            for ( Particle x : this.particles ) {
                x.setForceAndAcceleration( this.particles, this.walls );
                Particle aux = x.getCopy();
                Target auxTarget = aux.getTarget();
                aux.move( this.dt );
                if ( !auxTarget.reached(aux) ) {
                    Point position = aux.getPosition();
                    double radius = aux.getRadius();
                    count++;

                    if ( position.getX() + radius > rightestX ) {
                        rightestX = position.getX() + radius;
                    }

                    if ( position.getX() - radius < leftestX ) {
                        leftestX = position.getX() - radius;
                    }

                    if ( position.getY() + radius > topmostY ) {
                        topmostY = position.getY() + radius;
                    }
                } else {
                    aux.setTarget( this.getRandomTarget(aux.getRadius()) );
                    // this.dischargeTimes.add( currentTime );
                    // this.area -= aux.getArea();
                }
                particles.add( aux );
            }

            if ( timeAccumulator >= this.dt2 || i == 1 ) {
                this.states.add( EnvironmentState.builder()
                                                 .withParticles( this.particles )
                                                 .withTime( currentTime )
                                                 .build() );

                if ( count > 1 ) {
                    this.densityOverTime.add( this.area / ( topmostY * ( rightestX - leftestX ) ) );
                }

                if ( timeAccumulator >= this.dt2 ) {
                    timeAccumulator = 0;
                }
            }

            timeAccumulator += this.dt;
            this.particles = particles;
        }
    }

    public void printDensityOverTime() {
        String staticFilename = "density.csv";

        try ( BufferedWriter writer = new BufferedWriter( new FileWriter( staticFilename ) ) ) {
            StringBuilder builder = new StringBuilder();
            this.densityOverTime.forEach( x -> builder.append( x )
                                                      .append( System.lineSeparator() ) );
            writer.write( builder.toString() );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public void printToFile() {
        printStatic();
        printParticles();
    }

    public void printToFileParticlesOverTime( StringBuilder builder, String fileName ) {
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

    private void printParticles() {
        String particlesName = "particles.xyz";
        StringBuilder builder = new StringBuilder();
        try ( BufferedWriter writer = new BufferedWriter( new FileWriter( particlesName ) ) ) {
            for ( EnvironmentState state : this.states ) {
                builder.setLength( 0 );
                state.appendToStringBuilder( builder );
                writer.write( builder.toString() );
                writer.flush();
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

    public Target getRandomTarget(double radius) {
        double targetX = MathHelper.randBetween( this.random, 0 + radius, 20 - radius );
        double targetY = MathHelper.randBetween( this.random, 0 + radius, 20 - radius );
        return Target.builder()
                .withStart( targetX, targetY )
                .withEnd( targetX+0.01, targetY ) // TODO: remove +0.01 if possible
                .build();
    }

    public static class Builder
    {
        private final List<Wall> walls;
        private final List<Particle> particles;
        private Optional<Integer> seed;
        private double dt;
        private double dt2;
        private Random random;

        private Builder() {
            this.walls = new LinkedList<>();

            final Wall leftWall = Wall.builder()
                                      .withStart( 0, 0 )
                                      .withEnd( 0, 20 )
                                      .build();
            final Wall rightWall = Wall.builder()
                                       .withStart( 20, 0 )
                                       .withEnd( 20, 20 )
                                       .build();
            final Wall topWall = Wall.builder()
                                       .withStart( 0, 0 )
                                       .withEnd( 20, 0 )
                                       .build();
            final Wall bottomWall = Wall.builder()
                                       .withStart( 0, 20 )
                                       .withEnd( 20, 20 )
                                       .build();
            this.walls.add( leftWall );
            this.walls.add( rightWall );
            this.walls.add( topWall );
            this.walls.add( bottomWall );
            this.seed = Optional.empty();
            this.random = null;
            this.particles = new LinkedList<>();
        }

        public Builder withSeed( int seed ) {
            this.seed = Optional.of( seed );
            this.random = this.seed.map( Random::new )
                                .orElseGet( Random::new );
            return this;
        }

        public Builder withParticlesQuantity( int quantity ) {
            if (this.random != null) {
                this.random = seed.map( Random::new )
                                    .orElseGet( Random::new );
            }

            for ( int i = 0; i < quantity; i++ ) {
                double radius = MathHelper.randBetween( random, 0.5 / 2.0, 0.7 / 2.0 );
                double x = MathHelper.randBetween( random, 0 + radius, 20 - radius );
                double y = MathHelper.randBetween( random, 0 + radius, 20 - radius );
                
                double targetX = MathHelper.randBetween( this.random, 0 + radius, 20 - radius );
                double targetY = MathHelper.randBetween( this.random, 0 + radius, 20 - radius );
                Target target = Target.builder()
                        .withStart( targetX, targetY )
                        .withEnd( targetX+1, targetY ) // TODO: remove +1 if possible
                        .build();
                Particle particle = Particle.builder()
                                            .withId( UUID.randomUUID() )
                                            .withMass( 80 )
                                            .withRadius( radius )
                                            .withDesiredSpeed( 2 )
                                            .withTarget( target )
                                            .withPosition( x, y )
                                            .withVelocity( 0, 0 )
                                            .build();

                while ( !notOverlap( particle ) ) //TODO: Condicion de corte
                {
                    x = MathHelper.randBetween( random, 0 + radius, 20 - radius );
                    y = MathHelper.randBetween( random, 0 + radius, 20 - radius );
                    particle.setPosition( new Point( x, y ) );
                    particle.setTarget( target );
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

        public Builder withDt2( double dt2 ) {
            this.dt2 = dt2;
            return this;
        }

        public Environment build() {
            return new Environment( this );
        }
    }
}

