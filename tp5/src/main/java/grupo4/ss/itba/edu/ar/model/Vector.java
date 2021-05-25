package grupo4.ss.itba.edu.ar.model;

import lombok.Getter;
import lombok.Setter;

public class Vector
{
    @Getter
    @Setter
    private double x;
    @Getter
    @Setter
    private double y;

    public Vector( double x, double y ) {
        this.x = x;
        this.y = y;
    }

    public double getLength() {
        return Math.sqrt( Math.pow( x, 2 ) + Math.pow( y, 2 ) );
    }

    public Vector getUnitVector() {
        return new Vector( x / this.getLength(), y / this.getLength() );
    }

    public static Vector multiply( Vector vector, double c ) {
        return new Vector( vector.x * c, vector.y * c );
    }

    public static double dot( Vector vector1, Vector vector2) {
        return vector1.x * vector2.x + vector1.y * vector2.y;
    }

    public static Vector sum( Vector vector1, Vector vector2 ) {
        return new Vector( vector1.x + vector2.x, vector1.y + vector2.y );
    }

    public static Vector minus( Vector vector1, Vector vector2 ) {
        return Vector.sum( vector1, Vector.multiply( vector2, -1 ) );
    }

    public static Vector getCopy( Vector vector ) {
        return new Vector( vector.x, vector.y );
    }

    public StringBuilder appendToStringBuilder( StringBuilder stringBuilder ) {
        return stringBuilder.append( " " ).append( this.x ).append( " " ).append( this.y );
    }

    @Override
    public String toString() {
        return "{" + "x=" + x + ", y=" + y + '}';
    }
}
