package grupo4.ss.itba.edu.ar.model;

public class VerletMovementOperator implements ParticleMovementOperator
{
    @Override
    public void move( Particle particle, double dt, double maxSpeed ) {
        Vector prevPositionVector = particle.getPreviousState()
                                            .getPosition()
                                            .asVector();
        particle.setPreviousState( particle.getCopy() );
        Vector positionVector = Vector.sum( Vector.minus( Vector.multiply( particle.getPosition()
                                                                                   .asVector(), 2 ),
                                                          prevPositionVector ),
                                            Vector.multiply( particle.getAcceleration(), Math.pow( dt, 2 ) ) );
        particle.setPosition( new Point( positionVector.getX(), positionVector.getY() ) );
        Vector velocity = Vector.multiply( Vector.minus( positionVector, prevPositionVector ), 1 / ( 2 * dt ) );

        if ( velocity.getLength() > maxSpeed ) {
            velocity = Vector.multiply( velocity.getUnitVector(), maxSpeed );
        }

        particle.setVelocity( velocity );
    }
}
