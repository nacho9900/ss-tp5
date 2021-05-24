package grupo4.ss.itba.edu.ar.model;

public class LinearWall extends Line
{
    private LinearWall( Builder builder ) {
        super( builder );
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

        public LinearWall build() {
            return new LinearWall( this );
        }
    }
}
