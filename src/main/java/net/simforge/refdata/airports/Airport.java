package net.simforge.refdata.airports;

import net.simforge.commons.misc.Geo;
import net.simforge.refdata.airports.boundary.Boundary;
import net.simforge.refdata.airports.boundary.DefaultBoundary;

public class Airport {
    private String icao;
    private Geo.Coords coords;
    private int elevation;

    private int runwaySize = 3000;
    private Boundary boundary;

    private Airport() {}

    public String getIcao() {
        return icao;
    }

    public Geo.Coords getCoords() {
        return coords;
    }

    public int getElevation() {
        return elevation;
    }

    public int getRunwaySize() {
        return runwaySize;
    }

    public Boundary getBoundary() {
        return boundary;
    }

    public boolean isWithinBoundary(Geo.Coords coords) {
        return boundary.isWithin(coords);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Airport delegate = new Airport();

        private Builder() {}

        public Builder withIcao(final String icao) {
            delegate.icao = icao;
            return this;
        }

        public Builder withCoords(final Geo.Coords coords) {
            delegate.coords = coords;
            return this;
        }

        public Builder withElevation(final int elevation) {
            delegate.elevation = elevation;
            return this;
        }

        public Builder withRunwaySize(final int runwaySize) {
            delegate.runwaySize = runwaySize;
            if (delegate.boundary == null || delegate.boundary instanceof DefaultBoundary) {
                delegate.boundary = DefaultBoundary.forRunwaySize(delegate.coords, runwaySize);
            }
            return this;
        }

        public Builder withBoundary(final Boundary boundary) {
            delegate.boundary = boundary;
            return this;
        }

        public Airport build() {
            if (delegate.boundary == null) {
                delegate.boundary = DefaultBoundary.forRunwaySize(delegate.coords, delegate.runwaySize);
            }
            return delegate;
        }
    }
}
