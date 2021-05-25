package grupo4.ss.itba.edu.ar.model;

import java.util.List;
import java.util.UUID;

public class Particle
{
    private UUID id;
    private Point position;
    private Vector velocity;
    private Target target;
    private double mass;
    private double radius;
    private double desiredSpeed;

    /**
     * constants from paper "Simulating dynamical features of escape panic" A = 2x10³N B = 0.08m accelerationTime =
     * 0.5s
     **/
    private static final double A = 2e3;
    private static final double B = 0.08;
    private static final double accelerationTime = 0.5;
    private static final double kn = 1.2e5;
    private static final double kt = 2.4e5;

    private Particle( ParticleBuilder builder ) {
        this.id = builder.id;
        this.position = new Point( builder.positionX, builder.positionY );
        this.velocity = new Vector( builder.velocityX, builder.velocityY );
        this.target = builder.target;
        this.mass = builder.mass;
        this.radius = builder.radius;
        this.desiredSpeed = builder.desiredSpeed;
    }

    private double getSpeed() {
        return this.velocity.getLength();
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

    private Vector getInteractionForce( Wall wall ) {
        Point closestPoint = wall.getClosestPoint( this.position );
        Vector normalVector = this.position.getNormalVector( closestPoint );
        Vector tangentUnitVector = this.position.getTangentialVector( closestPoint )
                                                .getUnitVector();
        double separation = this.radius - normalVector.getLength();
        double g = this.g( closestPoint );

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
        final double g = this.g( other );

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

    private double g( Point point ) {
        double separation = this.radius - this.position.getNormalVector( point )
                                                       .getLength();
        return separation >= 0 ? separation : 0;
    }

    private double g( Particle other ) {
        double separation = this.getSeparationLength( other );
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

    /* package */ Particle getCopy() {
        return Particle.builder()
                       .withId( this.id )
                       .withMass( this.mass )
                       .withPosition( this.position.getX(), this.position.getY() )
                       .withVelocity( this.velocity.getX(), this.velocity.getY() )
                       .withTarget( this.target )
                       .withDesiredSpeed( this.desiredSpeed )
                       .withRadius( this.radius )
                       .build();
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

        public Particle build() {
            return new Particle( this );
        }
    }
}
