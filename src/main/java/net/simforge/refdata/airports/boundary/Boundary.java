package net.simforge.refdata.airports.boundary;

import net.simforge.commons.misc.Geo;

import java.util.Properties;

public interface Boundary {
    static Boundary fromProperties(final BoundaryType type, final String data) {
        switch (type) {
            case Default:
                return DefaultBoundary.fromProperties(data);
            case Circles:
                return CirclesBoundary.fromProperties(data);
            case Polygon:
                throw new UnsupportedOperationException();
        }
        return null;
    }

    boolean isWithin(final Geo.Coords coords);

    Properties asProperties();
}
