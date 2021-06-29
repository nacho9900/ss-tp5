package grupo4.ss.itba.edu.ar.model;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class Particle
{
    private final UUID id;
    private final double mass;
    @Getter
    private final double radius;
    private final double desiredSpeed;
    @Getter
    @Setter
    private Point position;
    @Getter
    @Setter
    private Vector velocity;
    private Vector force;
    @Getter
    private Vector acceleration;
    @Getter
    @Setter
    private Target target;
    @Getter
    @Setter
    private Particle previousState;
    @Getter
    @Setter
    private InfectedState infectedState;
    private Double infectedTime;

    /**
     * constants from paper "Simulating dynamical features of escape panic" A = 2x10³N B = 0.08m accelerationTime =
     * 0.5s
     **/
    private static final double A = 2e3;
    private static final double B = 0.08;
    private static final double accelerationTime = 0.5;
    private static final double kn = 1.2e5;
    private static final double kt = 2.4e5;
    private static final EulerMethodOperator eulerMethodOperator = new EulerMethodOperator();
    private static final VerletMovementOperator verletMovementOperator = new VerletMovementOperator();

    private Particle( ParticleBuilder builder ) {
        this.id = builder.id;
        this.position = new Point( builder.positionX, builder.positionY );
        this.velocity = new Vector( builder.velocityX, builder.velocityY );
        this.target = builder.target;
        this.mass = builder.mass;
        this.radius = builder.radius;
        this.desiredSpeed = builder.desiredSpeed;
        this.previousState = null;
        this.infectedState = builder.infectionState;
        this.infectedTime = builder.infectedTime;
    }

    public void move( double dt ) {
        if ( previousState == null ) {
            this.move( Particle.eulerMethodOperator, dt );
        }
        else {
            this.move( Particle.verletMovementOperator, dt );
        }
    }

    private void move( ParticleMovementOperator operator, double dt ) {
        operator.move( this, dt, desiredSpeed );
    }

    private Vector getForce( List<Particle> particles, List<Wall> walls ) {
        Vector interactionForce = new Vector( 0, 0 );

        for ( Particle particle : particles ) {
            if ( !this.equals( particle ) ) {
                interactionForce = Vector.sum( interactionForce, this.getInteractionForce( particle ) );
            }
        }

        for ( Wall wall : walls ) {
            interactionForce = Vector.sum( interactionForce, this.getInteractionForce( wall ) );
        }

        return Vector.sum( this.getDrivingForce(), interactionForce );
    }

    public void setForceAndAcceleration( List<Particle> particles, List<Wall> walls ) {
        this.force = this.getForce( particles, walls );
        this.acceleration = Vector.multiply( this.force, 1.0 / this.mass );
    }

    private Vector getInteractionForce( Wall wall ) {
        Point closestPoint = wall.getClosestPoint( this.position );
        Vector normalVector = this.position.getNormalVector( closestPoint );
        Vector tangentUnitVector = this.position.getTangentialVector( closestPoint )
                                                .getUnitVector();
        double separation = this.radius - normalVector.getLength();
        double g = this.g( separation );

        Vector normal = Vector.multiply( normalVector.getUnitVector(),
                                         ( Particle.A * Math.exp( separation / Particle.B ) ) + g * Particle.kn );

        if ( g == 0 ) {
            return normal;
        }

        double tangentSpeed = Vector.dot( this.velocity, tangentUnitVector );
        Vector tangent = Vector.multiply( tangentUnitVector, Particle.kt * g * tangentSpeed );

        return Vector.minus( normal, tangent );
    }

    private Vector getInteractionForce( Particle other ) {
        final double separation = this.getSeparationLength( other );
        final double g = this.g( separation );

        final Vector normal = Vector.multiply( this.getNormalUnitVector( other ),
                                               Particle.A * Math.exp( separation / Particle.B ) + g * Particle.kn );

        if ( g == 0 ) {
            return normal;
        }

        final Vector tangentialUnitVector = this.getTangentialUnitVector( other );
        final double tangentialVelocity = Vector.dot( Vector.minus( other.velocity, this.velocity ),
                                                      tangentialUnitVector );
        final Vector tangent = Vector.multiply( this.getTangentialUnitVector( other ),
                                                Particle.kt * g * tangentialVelocity );

        return Vector.sum( normal, tangent );
    }

    private double g( double separation ) {
        return separation >= 0 ? separation : 0;
    }

    private double getSeparationLength( Particle other ) {
        return ( this.radius + other.radius ) - this.getDistanceLength( other );
    }

    private Vector getTangentialUnitVector( Particle other ) {
        return this.position.getTangentialVector( other.position )
                            .getUnitVector();
    }

    private Vector getNormalUnitVector( Particle other ) {
        return this.position.getNormalVector( other.position )
                            .getUnitVector();
    }

    private double getDistanceLength( Particle other ) {
        return this.position.getNormalVector( other.position )
                            .getLength();
    }

    private Vector getDrivingForce() {
        return Vector.multiply( Vector.minus( this.getDesiredVelocity(), this.velocity ),
                                this.mass / Particle.accelerationTime );
    }

    private Vector getDesiredVelocity() {
        Point targetPoint = target.getTargetPoint( this.position );
        Vector desiredDirection = new Vector( targetPoint.getX() - this.position.getX(),
                                              targetPoint.getY() - this.position.getY() ).getUnitVector();
        return Vector.multiply( desiredDirection, this.desiredSpeed );
    }

    public boolean areOverlapped( Particle other ) {
        return this.getSeparationLength( other ) >= 0;
    }

    /* package */ Particle getCopy() {
        Particle particle = Particle.builder()
                                    .withId( this.id )
                                    .withMass( this.mass )
                                    .withPosition( this.position.getX(), this.position.getY() )
                                    .withVelocity( this.velocity.getX(), this.velocity.getY() )
                                    .withTarget( this.target )
                                    .withDesiredSpeed( this.desiredSpeed )
                                    .withRadius( this.radius )
                                    .withInfectedState( this.infectedState, this.infectedTime )
                                    .build();

        if ( this.acceleration != null ) {
            particle.acceleration = new Vector( this.acceleration.getX(), this.acceleration.getY() );
        }

        if ( this.force != null ) {
            particle.force = new Vector( this.force.getX(), this.force.getY() );
        }

        return particle;
    }

    public double getArea() {
        return Math.PI * Math.pow( this.radius, 2 );
    }

    public boolean tryInfect( Random random, Particle p ) {
        double distance = Math.sqrt( Math.pow( position.getY() - p.getPosition().getY(), 2 ) + Math.pow( position.getX() - p.getPosition().getX(), 2 ) );
        if (distance > Environment.infectRadius) return false;

        double probabilityOfGettingInfected = Environment.infectionProbability * 
                                                Environment.infectionProbabilityPerState.get(infectedState) * 
                                                Environment.defensesProbabilityPerState.get(p.getInfectedState());
        boolean infected = random.nextDouble() < probabilityOfGettingInfected;
        if ( infected ) {
            p.setInfectedState(InfectedState.SICK);
        }
        return infected;
    }

    public boolean isInfected() {
        return infectedState == InfectedState.SICK;
    }

    public void addInfectedTime(Double dt) {
        if (isInfected()) {
            this.infectedTime += dt;
            if (this.infectedTime > Environment.timeToCure) {
                this.infectedState = InfectedState.INMUNE;
            }
        }
    }

    public void appendToStringBuilder( StringBuilder stringBuilder ) {
        double R, G, B;
        boolean infected = this.isInfected();
        boolean inmune = this.infectedState == InfectedState.INMUNE;
        if ( isInfected() ) {
            R = 0;
            G = 1;
            B = 0;
        } else if ( this.infectedState == InfectedState.INMUNE ) {
            R = 0;
            G = 0;
            B = 1;
        } else {
            R = 1;
            G = 1;
            B = 1;
        }
        stringBuilder.append( this.position.getX() )
                     .append( " " )
                     .append( this.position.getY() )
                     .append( " " )
                     .append( this.radius )
                     .append( " " )
                     .append( R ) //R
                     .append( " " )
                     .append( G ) //G
                     .append( " " )
                     .append( B ) //B
                     .append( System.lineSeparator() );
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        Particle particle = (Particle) o;

        return Objects.equals( id, particle.id );
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public static ParticleBuilder builder() {
        return new ParticleBuilder();
    }

    public static class ParticleBuilder
    {
        private UUID id;
        private double positionX;
        private double positionY;
        private double velocityX;
        private double velocityY;
        private Target target;
        private double mass;
        private double radius;
        private double desiredSpeed;
        private InfectedState infectionState;
        private double infectedTime;

        public ParticleBuilder withId( UUID id ) {
            this.id = id;
            return this;
        }

        public ParticleBuilder withMass( double mass ) {
            if ( mass <= 0 ) {
                throw new IllegalArgumentException( "Mass of a Particle cannot be less or equals 0" );
            }
            this.mass = mass;
            return this;
        }

        public ParticleBuilder withRadius( double radius ) {
            if ( radius <= 0 ) {
                throw new IllegalArgumentException( "Radius of a Particle cannot be less or equals 0" );
            }
            this.radius = radius;
            return this;
        }

        public ParticleBuilder withDesiredSpeed( double desiredSpeed ) {
            if ( desiredSpeed <= 0 ) {
                throw new IllegalArgumentException( "Desired Speed of a Particle cannot be less or equals 0" );
            }
            this.desiredSpeed = desiredSpeed;
            return this;
        }

        public ParticleBuilder withPosition( double positionX, double positionY ) {
            this.positionX = positionX;
            this.positionY = positionY;
            return this;
        }

        public ParticleBuilder withVelocity( double velocityX, double velocityY ) {
            this.velocityX = velocityX;
            this.velocityY = velocityY;
            return this;
        }

        public ParticleBuilder withTarget( Target target ) {
            this.target = target;
            return this;
        }

        public ParticleBuilder withInfectedState( InfectedState state, Double infectedTime ) {
            this.infectionState = state;
            this.infectedTime = infectedTime;
            return this;
        }

        public Particle build() {
            return new Particle( this );
        }
    }
}
