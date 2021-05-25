package grupo4.ss.itba.edu.ar.model;

import grupo4.ss.itba.edu.ar.utils.OutputColor;
import jdk.jshell.spi.ExecutionControl;

import java.util.Optional;

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

    public Point getClosestPoint( Point point ) {
        Optional<Double> maybeSlope = this.getSlope();
        double perpendicularSlope;
        if ( maybeSlope.isEmpty() ) {
            return new Point( this.start.getX(), point.getY() );
        }
        else {
            if ( maybeSlope.get() == 0 ) {
                return new Point( point.getX(), this.start.getY() );
            }

            perpendicularSlope = -( 1.0 / maybeSlope.get() );
        }

        double perpendicularB = point.getY() - perpendicularSlope * point.getX();

        /* look at this for a complete implementation https://math.stackexchange.com/a/717749*/
        throw new RuntimeException( "Method not implemented for not horizontal nor vertical lines" );
    }

    public int appendToStringBuilder( StringBuilder stringBuilder, OutputColor color ) {
        double length = this.getLength();
        double radius = 0.05;
        int quantity = (int) Math.floor( length / radius );
        Optional<Double> maybeSlope = this.getSlope();
        for ( int i = 0; i < quantity; i++ ) {
            double deltaX, deltaY;

            if ( maybeSlope.isPresent() ) {
                deltaY = i * radius * maybeSlope.get();
                deltaX = maybeSlope.get() == 0 ? i * radius : ( i * radius ) / maybeSlope.get();
            }
            else {
                deltaX = 0;
                deltaY = i * radius;
            }

            stringBuilder.append( this.start.getX() + deltaX )
                         .append( " " )
                         .append( this.start.getY() + deltaY )
                         .append( " " )
                         .append( radius )
                         .append( color )
                         .append( System.lineSeparator() );
        }

        return quantity;
    }

    public Optional<Double> getSlope() {
        if ( this.start.getX() == this.end.getX() ) {
            return Optional.empty();
        }

        return Optional.of( ( this.end.getY() - this.start.getY() ) / ( this.end.getX() - this.start.getX() ) );
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
