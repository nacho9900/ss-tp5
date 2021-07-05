package grupo4.ss.itba.edu.ar.model;

import grupo4.ss.itba.edu.ar.utils.MathHelper;
import grupo4.ss.itba.edu.ar.utils.OutputColor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

    // New for TP Final
    private Random random;
    public static double infectionProbability;
    public static double infectRadius;
    public static double timeToCure;
    private double totalTime;
    
    /* 
     * Infection probabilities
     * For implementation see Particle.tryInfect method
     */
    // 0.0 means it doesn't infect, 1.0 means it infect with the global 'infectionProbability', higher values means its a super spreader
    public static Map<InfectedState, Double> infectionProbabilityPerState;
    // 0.0 means it is inmune to any infection, 1.0 means it has no defenses, higher values means it is a higher risk individual
    public static Map<InfectedState, Double> defensesProbabilityPerState;

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
        Environment.infectionProbability = builder.infectionProbability;
        Environment.infectRadius = builder.infectRadius;
        Environment.infectionProbabilityPerState = builder.infectionProbabilityPerState;
        Environment.defensesProbabilityPerState = builder.defensesProbabilityPerState;
        Environment.timeToCure = 22.0; // the equivalent in steps is steps = timeToCure * dt
    }

    public void run() {
        double timeAccumulator = 0;
        int i = 0;
        int amountInfected = (int) this.particles.stream().filter(particle -> particle.isInfected()).count();
        while ( i < 20_000 ) {
            if ( i % 1000 == 0 ) {
                System.out.printf( "{i: %d; q: %d, infected: %d}%n", i, this.particles.size(), amountInfected );
            }
            boolean diseaseCannotSpread = 
                // No more sick to spread disease
                particles.stream().filter(particle -> particle.isInfected()).count() == 0 ||
                // No more healthy to get infected
                particles.stream().filter(particle -> particle.isHealthy()).count() == 0;

            if ( diseaseCannotSpread ) {
                break;
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
                aux.addInfectedTime( this.dt );
                for (Particle aux2 : particles) {
                    boolean infected = false;
                    if (aux.isInfected() && !aux2.isInfected()) {
                        infected = aux.tryInfect(random, aux2);
                    } else if (!aux.isInfected() && aux2.isInfected()) {
                        infected = aux2.tryInfect(random, aux);
                    }
                    amountInfected += infected ? 1 : 0;
                }
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
        this.totalTime = i;
    }

    public double getTotalTime() {
        return totalTime;
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
        String particlesName = "particles_N-"+states.get(0).getParticles().size()+".xyz";
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
        private double infectionProbability;
        private double infectRadius;
        private Map<InfectedState, Double> infectionProbabilityPerState;
        private Map<InfectedState, Double> defensesProbabilityPerState;

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

        public Builder withParticlesQuantityAndInfectedDistribution( int quantity, Map<InfectedState, Double> infectedDistribution ) throws Exception {
            if (this.random != null) {
                this.random = seed.map( Random::new )
                                    .orElseGet( Random::new );
            }
            if (Math.abs(infectedDistribution.values().stream().reduce((x, y) -> x + y).get() - 1.0) > 0.0001) {
                throw new Exception("Infected distribution does not sum 1.0");
            }
            int quantityInmune = (int) (infectedDistribution.get(InfectedState.INMUNE) * quantity);
            int quantitySick = (int) (infectedDistribution.get(InfectedState.SICK) * quantity);
            // int quantityHealthy = quantity - quantityInmune - quantitySick;

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
                InfectedState infectedState = null;
                if (quantityInmune > 0) {
                    infectedState = InfectedState.INMUNE;
                    quantityInmune--;
                } else if (quantitySick > 0) {
                    infectedState = InfectedState.SICK;
                    quantitySick--;
                } else {
                    infectedState = InfectedState.HEALTHY;
                }
                Particle particle = Particle.builder()
                                            .withId( UUID.randomUUID() )
                                            .withMass( 80 )
                                            .withRadius( radius )
                                            .withDesiredSpeed( 2 )
                                            .withTarget( target )
                                            .withPosition( x, y )
                                            .withVelocity( 0, 0 )
                                            .withInfectedState( infectedState, 0.0 )
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

        public Builder withInfectionProfile( double probability, double radius, Double infectionProbability, Double defensesProbability ) {
            this.infectionProbability = probability;
            this.infectRadius = radius;
            this.infectionProbabilityPerState = new HashMap<>();
            infectionProbabilityPerState.put(InfectedState.HEALTHY, 0.0);
            infectionProbabilityPerState.put(InfectedState.SICK, infectionProbability);
            infectionProbabilityPerState.put(InfectedState.INMUNE, 0.0);

            this.defensesProbabilityPerState = new HashMap<>();
            defensesProbabilityPerState.put(InfectedState.HEALTHY, defensesProbability);
            defensesProbabilityPerState.put(InfectedState.SICK, 1.0);
            defensesProbabilityPerState.put(InfectedState.INMUNE, 0.0);
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

