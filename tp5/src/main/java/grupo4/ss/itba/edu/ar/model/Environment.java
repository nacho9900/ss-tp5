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
    private final Target target3;
    private final Target target4;
    private final List<EnvironmentState> states = new LinkedList<>();
    private List<Particle> particles;
    private final double dt;
    private final double dt2;
    private double area;

    // ej_a
    private final List<Double> dischargeTimes = new LinkedList<>();
    private final List<Double> densityOverTime = new LinkedList<>();

    private Environment( Builder builder ) {
        this.walls = builder.walls;
        this.target1 = builder.target1;
        this.target2 = builder.target2;
        this.target3 = builder.target3;
        this.target4 = builder.target4;
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
    }

    public void run() {
        double timeAccumulator = 0;
        int i = 0;
        while ( !this.particles.isEmpty() ) {
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
                aux.move( this.dt );
                if ( !target2.reachedBelow( aux ) && !target4.reachedUpper( aux ) ) {
                    if ( aux.getTarget()
                            .equals( target1 ) ) {
                        if ( target1.reachedBelow( aux ) ) {
                            aux.setTarget( this.target2 );
                            // ej_a
                            this.dischargeTimes.add( currentTime );
                            this.area -= aux.getArea();
                        }
                        else {
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
                        }
                    }

                    if ( aux.getTarget()
                            .equals( target3 ) ) {
                        if ( target3.reachedBelow( aux ) ) {
                            aux.setTarget( this.target4 );
                            // ej_a
                            this.dischargeTimes.add( currentTime );
                            this.area -= aux.getArea();
                        }
                        else {
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
                        }
                    }

                    particles.add( aux );
                }
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

            quantity += target1.appendToStringBuilder( builder, OutputColor.Red );
            quantity += target2.appendToStringBuilder( builder, OutputColor.Red );
            quantity += target3.appendToStringBuilder( builder, OutputColor.Red );
            quantity += target4.appendToStringBuilder( builder, OutputColor.Red );

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
        private Target target3;
        private Target target4;
        private double dt;
        private double dt2;

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
            this.walls.add( leftWall );
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

            final Wall topLeftWall = Wall.builder()
                                         .withStart( 0, 20 )
                                         .withEnd( 10 - ( opening / 2.0 ), 20 )
                                         .build();
            final Wall topRightWall = Wall.builder()
                                          .withStart( 10 + ( opening / 2.0 ), 20 )
                                          .withEnd( 20, 20 )
                                          .build();
            this.walls.add( topLeftWall );
            this.walls.add( topRightWall );
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

            this.target3 = Target.builder()
                                 .withStart( 10 - ( opening / 2.0 ) + 0.1, 20 )
                                 .withEnd( 10 + ( opening / 2.0 ) - 0.1, 20 )
                                 .build();
            this.target4 = Target.builder()
                                 .withStart( 8.5, 30 )
                                 .withEnd( 11.5, 30 )
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
                                            .withTarget( y > 10 ? this.target1 : this.target3 )
                                            .withPosition( x, y )
                                            .withVelocity( 0, 0 )
                                            .build();

                while ( !notOverlap( particle ) ) //TODO: Condicion de corte
                {
                    x = MathHelper.randBetween( random, 0 + radius, 20 - radius );
                    y = MathHelper.randBetween( random, 0 + radius, 20 - radius );
                    particle.setPosition( new Point( x, y ) );
                    particle.setTarget( y > 10 ? this.target1 : this.target3 );
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

