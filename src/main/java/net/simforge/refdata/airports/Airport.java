package net.simforge.refdata.airports;

import net.simforge.commons.misc.Geo;

public class Airport {
    private String icao;
    private Geo.Coords coords;
    private int elevation;

    private int runwaySize = 3000;
    private float defaultBoundaryRadius = calcDefaultBoundaryRadius(runwaySize);

    private BoundaryType boundaryType = BoundaryType.Default;
    private String boundaryData;

    private Geo.Coords[] circlesBoundaryCoords;
    private double[] circlesBoundaryRadiuses;

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

    public float getDefaultBoundaryRadius() {
        return defaultBoundaryRadius;
    }

    public boolean isWithinBoundary(Geo.Coords coords) {
        switch (boundaryType) {
            case Circles:
                return checkCirclesBoundary(coords);
            case Polygon:
                throw new UnsupportedOperationException("Polygon type of boundary is not supported");
            default:
                return checkDefaultBoundary(coords);
        }
    }

    private boolean checkDefaultBoundary(Geo.Coords coords) {
        return Geo.distance(this.coords, coords) <= defaultBoundaryRadius;
    }

    private static float calcDefaultBoundaryRadius(int runwaySize) {
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

    private boolean checkCirclesBoundary(Geo.Coords coords) {
        parseCirclesBoundaryData();

        for (int i = 0; i < circlesBoundaryCoords.length; i++) {
            Geo.Coords center = circlesBoundaryCoords[i];
            double radius = circlesBoundaryRadiuses[i];
            double distance = Geo.distance(center, coords);
            if (distance <= radius) {
                return true;
            }
        }

        return false;
    }

    private void parseCirclesBoundaryData() {
        if (circlesBoundaryCoords != null) {
            return;
        }

        String[] lines = boundaryData.split("[;\n]");
        int length = lines.length;
        circlesBoundaryCoords = new Geo.Coords[length];
        circlesBoundaryRadiuses = new double[length];

        for (int i = 0; i < length; i++) {
            String line = lines[i];
            try {
                String[] data = line.split(",");

                String latStr = data[0];
                String lonStr = data[1];
                String radiusStr = data[2];

                double lat = Double.parseDouble(latStr);
                double lon = Double.parseDouble(lonStr);
                double radius = Double.parseDouble(radiusStr);

                circlesBoundaryCoords[i] = new Geo.Coords(lat, lon);
                circlesBoundaryRadiuses[i] = radius;
            } catch (Exception e) {
                throw new IllegalStateException("Unable to parse '" + line + "' line as Circles boundary data in " + icao + " airport", e);
            }
        }
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
            delegate.defaultBoundaryRadius = calcDefaultBoundaryRadius(runwaySize);
            return this;
        }

        public Builder withBoundaryType(final BoundaryType boundaryType) {
            delegate.boundaryType = boundaryType != null ? boundaryType : BoundaryType.Default;
            return this;
        }

        public Builder withBoundaryData(final String boundaryData) {
            delegate.boundaryData = boundaryData;
            return this;
        }

        public Airport build() {
            return delegate;
        }
    }
}
