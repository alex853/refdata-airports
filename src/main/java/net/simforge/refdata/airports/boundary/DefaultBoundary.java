package net.simforge.refdata.airports.boundary;

import net.simforge.commons.misc.Geo;

import java.util.Properties;

public class DefaultBoundary implements Boundary {
    private Geo.Coords coords;
    private final float radius;

    public static Boundary forRunwaySize(final Geo.Coords coords, final int runwaySize) {
        return new DefaultBoundary(coords, calcDefaultBoundaryRadius(runwaySize));
    }

    public static DefaultBoundary fromProperties(final String data) {
        return new DefaultBoundary(Float.parseFloat(data));
    }

    private DefaultBoundary(final float radius) {
        this.radius = radius;
    }

    private DefaultBoundary(final Geo.Coords coords, final float radius) {
        this.coords = coords;
        this.radius = radius;
    }

    @Override
    public boolean isWithin(Geo.Coords coords) {
        return Geo.distance(this.coords, coords) <= radius;
    }

    @Override
    public Properties asProperties() {
        final Properties properties = new Properties();
        properties.put("type", BoundaryType.Default.name());
        properties.put("data", String.valueOf(radius));
        return properties;
    }

    public static float calcDefaultBoundaryRadius(int runwaySize) {
        final int lowestRunwaySize = 1000;
        final float lowestRadius = 0.5f;
        final int highestRunwaySize = 9000;
        final float highestRadius = 2.0f;

        if (runwaySize < lowestRunwaySize) {
            return lowestRadius;
        } else if (runwaySize > highestRunwaySize) {
            return highestRadius;
        }

        return (highestRadius - lowestRadius) * (runwaySize - lowestRunwaySize) / (highestRunwaySize - lowestRunwaySize) + lowestRadius;
    }
}
