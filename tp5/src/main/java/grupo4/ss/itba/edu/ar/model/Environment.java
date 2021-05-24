package grupo4.ss.itba.edu.ar.model;

import grupo4.ss.itba.edu.ar.utils.MathHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class Environment
{
    private final List<LinearWall> walls;
    private final List<EnvironmentState> states = new LinkedList<>();

    private Environment( Builder builder ) {
        this.walls = builder.walls;

    }

    public static class Builder
    {
        private final List<LinearWall> walls;
        private final List<Particle> particles;
        private Optional<Integer> seed;
        private Target firstTarget;
        private Target endTarget;

        private Builder() {
            this.walls = new LinkedList<>();

            final LinearWall leftWall = LinearWall.builder()
                                                  .withStart( 0, 0 )
                                                  .withEnd( 0, 20 )
                                                  .build();
            final LinearWall upperWall = LinearWall.builder()
                                                   .withStart( 0, 20 )
                                                   .withEnd( 20, 20 )
                                                   .build();
            final LinearWall rightWall = LinearWall.builder()
                                                   .withStart( 20, 20 )
                                                   .withEnd( 20, 0 )
                                                   .build();
            this.walls.add( leftWall );
            this.walls.add( upperWall );
            this.walls.add( rightWall );
            this.seed = Optional.empty();
            this.particles = new LinkedList<>();
        }

        public Builder withOpening( double opening ) {
            this.buildBottomWall( opening );
            this.buildTargets( opening );

            return this;
        }

        private void buildBottomWall( double opening ) {
            final LinearWall bottomLeftWall = LinearWall.builder()
                                                        .withStart( 0, 0 )
                                                        .withEnd( 10 - ( opening / 2.0 ), 0 )
                                                        .build();
            final LinearWall bottomRightWall = LinearWall.builder()
                                                         .withStart( 10 + ( opening / 2.0 ), 0 )
                                                         .withEnd( 20, 0 )
                                                         .build();
            this.walls.add( bottomLeftWall );
            this.walls.add( bottomRightWall );
        }

        private void buildTargets( double opening ) {
            this.firstTarget = Target.builder()
                                     .withStart( 10 - ( opening / 2.0 ) + 0.1, 0 )
                                     .withEnd( 10 + ( opening / 2.0 ) - 0.1, 0 )
                                     .build();
            this.endTarget = Target.builder()
                                   .withStart( 8.5, -10 )
                                   .withEnd( 11.5, -10 )
                                   .build();
        }

        public Builder withSeed( int seed ) {
            this.seed = Optional.of( seed );
            return this;
        }

        public Builder withParticlesQuantity( int quantity ) {
            Random random = seed.map( Random::new )
                                .orElseGet( Random::new );

            for ( int i = 0; i < quantity; i++ ) {
                Particle particle = Particle.builder()
                                            .withId( UUID.randomUUID() )
                                            .withMass( 80 )
                                            .withRadius( MathHelper.randBetween( random, 0.5, 0.58 ) )
                                            .withDesiredSpeed( 2 )
                                            .withTarget( 0, 10 )
                                            .withPosition( MathHelper.randBetween( random, 0, 20 ),
                                                           MathHelper.randBetween( random, 0, 20 ) )
                                            .withVelocity( 0, 0 )
                                            .build();
                this.particles.add( particle );
            }

            return this;
        }
    }
}
