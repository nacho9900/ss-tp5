package grupo4.ss.itba.edu.ar.model;

public abstract class Line
{
    protected final Point start;
    protected final Point end;

    protected Line( Builder builder ) {
        this.start = new Point( builder.startPositionX, builder.startPositionY );
        this.end = new Point( builder.endPositionX, builder.endPositionY );
    }

    public double getLength() {
        return Math.sqrt( Math.pow( end.getX() - start.getX(), 2 ) + Math.pow( end.getY() - start.getY(), 2 ) );
    }

    protected static class Builder
    {
        private double startPositionX;
        private double startPositionY;
        private double endPositionX;
        private double endPositionY;

        public Builder withStart( double startPositionX, double startPositionY ) {
            this.startPositionX = startPositionX;
            this.startPositionY = startPositionY;
            return this;
        }

        public Builder withEnd( double endPositionX, double endPositionY ) {
            this.endPositionX = endPositionX;
            this.endPositionY = endPositionY;
            return this;
        }
    }
}
