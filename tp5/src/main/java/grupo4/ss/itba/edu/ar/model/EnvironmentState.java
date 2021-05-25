package grupo4.ss.itba.edu.ar.model;

import java.util.List;
import java.util.stream.Collectors;

public class EnvironmentState
{
    private final List<Particle> particles;
    private final double time;

    private EnvironmentState(Builder builder) {
        this.particles = builder.particles;
        this.time = builder.time;
    }

    public void appendToStringBuilder(StringBuilder stringBuilder) {
        stringBuilder.append( particles.size() ).append( System.lineSeparator() ).append( System.lineSeparator() );
        particles.forEach( x -> x.appendToStringBuilder( stringBuilder ) );
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<Particle> particles;
        private double time;

        private Builder() {
            this.time = 0;
        }

        public Builder withParticles(List<Particle> particles) {
            this.particles = particles.stream().map( Particle::getCopy ).collect( Collectors.toList());
            return this;
        }

        public Builder withTime(double time) {
            this.time = time;
            return this;
        }

        public EnvironmentState build() {
            return new EnvironmentState( this );
        }
    }
}
