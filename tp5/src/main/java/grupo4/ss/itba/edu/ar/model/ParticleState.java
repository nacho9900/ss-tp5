package grupo4.ss.itba.edu.ar.model;

import lombok.Getter;

@Getter
public class ParticleState
{
    private final Particle particle;
    private final double time;

    public ParticleState( Builder builder ) {
        this.particle = builder.particle;
        this.time = builder.time;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder
    {
        private Particle particle;
        private double time;

        public Builder withParticle( Particle particle ) {
            this.particle = particle.getCopy();
            return this;
        }

        public Builder withTime( double time ) {
            this.time = time;
            return this;
        }

        public ParticleState build() {
            return new ParticleState( this );
        }
    }
}
