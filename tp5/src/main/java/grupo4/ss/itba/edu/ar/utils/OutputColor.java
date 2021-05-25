package grupo4.ss.itba.edu.ar.utils;

public enum OutputColor
{
    Green( " 0 1 0" ),
    Red( " 1 0 0" ),
    Orange( " 1 1 0" ),
    Blue( " 0 0 1" ),
    White( " 1 1 1" );

    private final String name;

    private OutputColor( String name ) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
