package net.simforge.refdata.airports;

import net.simforge.commons.misc.Geo;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Airports {

    private final Collection<Airport> airports = new LinkedList<>();

    @SuppressWarnings("unchecked")
    private final List<Airport>[][] gridLL9 = (List<Airport>[][]) new List[360][180];

    void addAirport(Airport airport) {
        airports.add(airport);

        putToGridLL9(airport);
    }

    public Collection<Airport> getAllAirports() {
        return Collections.unmodifiableCollection(airports);
    }

    public Optional<Airport> findByIcao(final String icao) {
        for (final Airport airport : airports) {
            if (airport.getIcao().equals(icao)) {
                return Optional.of(airport);
            }
        }
        return Optional.empty();
    }

    @Deprecated
    public Airport getByIcao(final String icao) {
        return findByIcao(icao).orElse(null);
    }

    public Airport findNearest(final Geo.Coords coords) {
        return findNearest(coords, DistanceType.GeoDistance);
    }

    public Airport findNearest(final Geo.Coords coords, DistanceType distanceType) {
        final int latIndex = getLatIndex(coords.getLat());
        final int lonIndex = getLonIndex(coords.getLon());

        final List<Airport> airports = gridLL9[lonIndex][latIndex];

        if (airports == null)
            return null;
        else
            return findNearest(coords, airports, distanceType);
    }

    public Airport findWithinBoundary(final Geo.Coords coords) {
        final int latIndex = getLatIndex(coords.getLat());
        final int lonIndex = getLonIndex(coords.getLon());

        final List<Airport> airports = gridLL9[lonIndex][latIndex];

        if (airports == null)
            return null;
        else
            return findWithinBoundary(coords, airports);
    }

    public List<Airport> findAllWithinRadius(final Geo.Coords coords, final double radius) {
        return airports.stream()
                .filter(airport -> Geo.distance(coords, airport.getCoords()) < radius)
                .collect(Collectors.toList());
    }

    private Airport findNearest(final Geo.Coords coords, final Collection<Airport> airports, final DistanceType distanceType) {
        double minDistance = Double.MAX_VALUE;
        Airport minDistanceAirport = null;

        for (final Airport airport : airports) {
            final double distance = Util.distance(coords, airport.getCoords(), distanceType);
            if (distance < minDistance) {
                minDistance = distance;
                minDistanceAirport = airport;
            }
        }

        return minDistanceAirport;
    }

    private Airport findWithinBoundary(final Geo.Coords coords, final List<Airport> airports) {
        return airports.stream().filter(airport -> airport.isWithinBoundary(coords)).findFirst().orElse(null);
    }

    private void putToGridLL9(final Airport airport) {
        final double lat = airport.getCoords().getLat();
        final double lon = airport.getCoords().getLon();

        for (int latI = -1; latI <= 1; latI++) {
            final double latC = lat + latI;

            if (latC < -90 || latC >= 90)
                continue;

            for (int lonI = -1; lonI <= 1; lonI++) {
                double lonC = lon + lonI;

                if (lonC < -180) {
                    lonC = 360 + lonC;
                } else if (lonC >= 180) {
                    lonC = 360 - lonC;
                }

                putToGridLL(latC, lonC, gridLL9, airport);
            }
        }
    }

    private void putToGridLL(final double lat, final double lon, final List<Airport>[][] gridLL, final Airport airport) {
        final int latIndex = getLatIndex(lat);
        final int lonIndex = getLonIndex(lon);

        List<Airport> airportsLL = gridLL[lonIndex][latIndex];
        if (airportsLL == null) {
            airportsLL = new ArrayList<>();
            gridLL[lonIndex][latIndex] = airportsLL;
        }

        airportsLL.add(airport);
    }

    private int getLatIndex(final double lat) {
        int latIndex = (int) (lat + 90);
        latIndex = latIndex % 180;
        return latIndex;
    }

    private int getLonIndex(final double lon) {
        int lonIndex = (int) (lon + 180);
        lonIndex = lonIndex % 360;
        return lonIndex;
    }

    private static Airports airportsInstance;

    public static synchronized Airports get() {
        if (airportsInstance == null) {
            try {
                airportsInstance = AirportsLoader.load();
            } catch (IOException e) {
                throw new RuntimeException("Unable to load airports data", e);
            }
        }
        return airportsInstance;
    }

    public static synchronized void reset() {
        airportsInstance = null;
    }
}
