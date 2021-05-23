package grupo4.ss.itba.edu.ar.model;

import java.util.UUID;

public class Particle
{
    private UUID id;
    private double positionX;
    private double positionY;
    private Vector velocity;
    private double targetPositionX;
    private double targetPositionY;
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

    /**
     * constant from "TP5: Medios Granulares y Dinámica Peatonal" ejercicio 2 maxDesiredSpeed = 2m/s
     **/
    private static final double maxDesiredSpeed = 2;

    private Particle( ParticleBuilder builder ) {
        this.id = builder.id;
        this.positionX = builder.positionX;
        this.positionY = builder.positionY;
        this.velocity = new Vector( builder.velocityX, builder.velocityY );
        this.targetPositionX = builder.targetPositionX;
        this.targetPositionY = builder.targetPositionY;
        this.mass = builder.mass;
        this.radius = builder.radius;
    }

    private double getSpeed() {
        return this.velocity.getLength();
    }

    private Vector getDrivingForce() {
        return Vector.multiply( Vector.minus( this.getDesiredVelocity(), this.velocity ),
                                this.mass / Particle.accelerationTime );
    }

    private Vector getDesiredVelocity() {
        Vector desiredDirection = new Vector( this.targetPositionX - this.positionX,
                                              this.targetPositionY - this.positionY ).getUnitVector();
        return Vector.multiply( desiredDirection, this.desiredSpeed );
    }

    public static ParticleBuilder newBuilder() {
        return new ParticleBuilder();
    }

    public static class ParticleBuilder
    {
        private UUID id;
        private double positionX;
        private double positionY;
        private double velocityX;
        private double velocityY;
        private double targetPositionX;
        private double targetPositionY;
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

        public ParticleBuilder withTarget( double targetPositionX, double targetPositionY ) {
            this.targetPositionX = targetPositionX;
            this.targetPositionY = targetPositionY;
            return this;
        }

        public Particle build() {
            return new Particle( this );
        }
    }
}
