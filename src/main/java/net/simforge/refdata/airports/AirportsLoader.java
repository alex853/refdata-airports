package net.simforge.refdata.airports;

import net.simforge.commons.io.Csv;
import net.simforge.commons.io.IOHelper;
import net.simforge.commons.misc.Geo;
import net.simforge.refdata.airports.boundary.Boundary;
import net.simforge.refdata.airports.fse.FSEAirport;
import net.simforge.refdata.airports.fse.FSEAirports;
import net.simforge.refdata.airports.pai.PAIAirport;
import net.simforge.refdata.airports.pai.PAIAirports;
import net.simforge.refdata.airports.pai.PAIAirportsLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.*;

class AirportsLoader {
    static Airports load() throws IOException {
        final Airports airports = new Airports();

        final PAIAirports paiAirports = PAIAirportsLoader.load();
        final FSEAirports fseAirports = FSEAirports.get();
        final Collection<PAIAirport> paiAirportsCollection = paiAirports.getAirports();

        final Set<String> addedIcaos = loadIcaoList("/added.csv");
        for (final String icao : addedIcaos) {
            loadResource("/added/%s.properties".replace("%s", icao))
                    .ifPresent(addedAirportContent -> {
                        try {
                            final Properties properties = new Properties();
                            properties.load(new StringReader(addedAirportContent));

                            final String iata = properties.getProperty("iata");
                            final String name = properties.getProperty("name");
                            final float latitude = Float.parseFloat(properties.getProperty("coords.latitude"));
                            final float longitude = Float.parseFloat(properties.getProperty("coords.longitude"));
                            final int elevation = Integer.parseInt(properties.getProperty("elevation"));
                            final int runwaySize = Integer.parseInt(properties.getProperty("runway.size"));

                            final Airport.Builder builder = Airport.builder()
                                    .withIcao(icao)
                                    .withIata(iata)
                                    .withName(name)
                                    .withCoords(Geo.coords(latitude, longitude))
                                    .withElevation(elevation)
                                    .withRunwaySize(runwaySize);

                            loadBoundary(builder, icao);

                            airports.addAirport(builder.build());
                        } catch (IOException e) {
                            throw new RuntimeException("unable to load '" + icao + "' airport data", e);
                        }
                    });
        }

        final Set<String> deletedIcaos = loadIcaoList("/deleted.csv");

        for (PAIAirport paiAirport : paiAirportsCollection) {
            if (deletedIcaos.contains(paiAirport.getIcao())) {
                continue;
            }

            final Optional<FSEAirport> fseAirport = fseAirports.findByIcao(paiAirport.getIcao());

            final Geo.Coords coords = fseAirport.isPresent()
                    ? fseAirport.get().getCoords()
                    : Geo.coords(paiAirport.getLatitude(), paiAirport.getLongitude());

            final Airport.Builder builder = Airport.builder()
                    .withIcao(paiAirport.getIcao())
                    .withCoords(coords)
                    .withElevation(paiAirport.getAltitude());

            fseAirport.ifPresent(airport -> builder
                    .withName(airport.getName())
                    .withRunwaySize(airport.getSize()));

            loadBoundary(builder, paiAirport.getIcao());

            airports.addAirport(builder.build());
        }

        return airports;
    }

    private static void loadBoundary(final Airport.Builder builder, final String icao) throws IOException {
        final Optional<Boundary> boundary = AirportsStorage.loadBoundary(icao);

        if (boundary.isPresent()) {
            builder.withBoundary(boundary.get());
        } else {
            loadResource("/boundaries/%s.properties".replace("%s", icao))
                    .ifPresent(content -> builder.withBoundary(AirportsStorage.loadBoundaryFromProperties(content)));
        }
    }

    private static Set<String> loadIcaoList(final String icaoListFilename) {
        final Optional<String> content = loadResource(icaoListFilename);
        final Csv csv = content.map(Csv::fromContent).orElseGet(Csv::empty);
        final Set<String> icaos = new TreeSet<>();
        for (int row = 0; row < csv.rowCount(); row++) {
            icaos.add(csv.value(row, "icao"));
        }
        return icaos;
    }

    private static Optional<String> loadResource(final String resourceName) {
        final InputStream is = Airports.class.getResourceAsStream(resourceName);
        if (is == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(IOHelper.readInputStream(is));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
