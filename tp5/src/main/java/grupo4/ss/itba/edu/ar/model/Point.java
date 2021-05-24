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
}
