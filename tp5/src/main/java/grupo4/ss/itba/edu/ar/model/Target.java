package grupo4.ss.itba.edu.ar.model;

public class Target extends Line
{
    private Target( Builder builder ) {
        super( builder );

        /* This is because of the implementation for the Target Point, if the target is not Horizontal the Target
        Point method must be changed */
        if ( this.start.getY() != this.end.getY() ) {
            throw new IllegalArgumentException( "Target must be Horizontal" );
        }
    }

    public Point getTargetPoint( Point point ) {
        if ( point.getX() <= this.start.getX() ) {
            return new Point( this.start.getX(), this.start.getY() );
        }

        if ( point.getX() >= this.end.getX() ) {
            return new Point( this.end.getX(), this.end.getY() );
        }

        return new Point( point.getX(), this.start.getY() );
    }

    public boolean reached( Particle particle ) {
        return particle.getPosition().getY() <= this.end.getY();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends Line.Builder
    {
        @Override
        public Builder withStart( double startPositionX, double startPositionY ) {
            return (Builder) super.withStart( startPositionX, startPositionY );
        }

        @Override
        public Builder withEnd( double endPositionX, double endPositionY ) {
            return (Builder) super.withEnd( endPositionX, endPositionY );
        }

        public Target build() {
            return new Target( this );
        }
    }
}
