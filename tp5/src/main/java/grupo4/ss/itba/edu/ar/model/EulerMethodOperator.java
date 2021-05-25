package grupo4.ss.itba.edu.ar.model;

public class EulerMethodOperator implements ParticleMovementOperator
{
    @Override
    public void move( Particle particle, double dt ) {
        particle.getStates()
                .add( ParticleState.builder()
                                   .withParticle( particle )
                                   .build() );
        Vector velocity = Vector.sum( particle.getVelocity(), Vector.multiply( particle.getAcceleration(), dt ) );
        particle.setVelocity( velocity );
        Vector displacedDistance = Vector.sum( Vector.multiply( velocity, dt ),
                                               Vector.multiply( particle.getAcceleration(), Math.pow( dt, 2 ) / 2.0 ) );
        Vector positionVector = Vector.sum( particle.getPosition()
                                                    .asVector(), displacedDistance );
        particle.setPosition( new Point( positionVector.getX(), positionVector.getY() ) );
    }
}
