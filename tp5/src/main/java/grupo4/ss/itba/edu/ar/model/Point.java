package grupo4.ss.itba.edu.ar.model;

import lombok.Getter;

public class Point
{
    @Getter
    private final double x;
    @Getter
    private final double y;

    public Point( double x, double y ) {
        this.x = x;
        this.y = y;
    }

    public Vector getNormalVector( Point other ) {
        return new Vector( this.getX() - other.getX(), this.getY() - other.getY() );
    }

    public Vector getTangentialVector( Point other ) {
        Vector normal = this.getNormalVector( other );
        double x = -normal.getY();
        normal.setY( normal.getX() );
        normal.setX( x );
        return normal;
    }
}
