package net.simforge.refdata.airports.boundary;

import net.simforge.commons.misc.Geo;

import java.util.Properties;

public class CirclesBoundary implements Boundary {
    private Geo.Coords[] circlesBoundaryCoords;
    private double[] circlesBoundaryRadiuses;

    private CirclesBoundary() {
    }

    @Override
    public boolean isWithin(final Geo.Coords coords) {
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

    @Override
    public Properties asProperties() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < circlesBoundaryCoords.length; i++) {
            if (sb.length() > 0) {
                sb.append(';');
            }

            final Geo.Coords coords = circlesBoundaryCoords[i];
            final double radius = circlesBoundaryRadiuses[i];

            sb.append(coords.getLat()).append(',');
            sb.append(coords.getLon()).append(',');
            sb.append(radius);
        }

        final Properties properties = new Properties();
        properties.put("type", BoundaryType.Circles.name());
        properties.put("data", sb.toString());
        return properties;
    }

    public static CirclesBoundary fromProperties(final String data) {
        CirclesBoundary b = new CirclesBoundary();

        String[] lines = data.split("[;\n]");
        int length = lines.length;
        b.circlesBoundaryCoords = new Geo.Coords[length];
        b.circlesBoundaryRadiuses = new double[length];

        for (int i = 0; i < length; i++) {
            String line = lines[i];
            try {
                String[] each = line.split(",");

                String latStr = each[0];
                String lonStr = each[1];
                String radiusStr = each[2];

                double lat = Double.parseDouble(latStr);
                double lon = Double.parseDouble(lonStr);
                double radius = Double.parseDouble(radiusStr);

                b.circlesBoundaryCoords[i] = new Geo.Coords(lat, lon);
                b.circlesBoundaryRadiuses[i] = radius;
            } catch (Exception e) {
                throw new IllegalStateException("Unable to parse '" + line + "' line as Circles boundary data", e);
            }
        }
        return b;
    }
}
