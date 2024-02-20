package net.simforge.refdata.airports;

import net.simforge.commons.io.Csv;
import net.simforge.commons.io.IOHelper;
import net.simforge.commons.misc.Geo;
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

        final Set<String> deletedIcaos = loadDeletedIcaos();

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

            fseAirport.ifPresent(airport -> builder.withRunwaySize(airport.getSize()));

            loadResource("/boundaries/%s.properties".replace("%s", paiAirport.getIcao())).ifPresent(content -> {
                final Properties properties = loadProperties(content);

                builder.withBoundaryType(BoundaryType.valueOf(properties.getProperty("type")))
                        .withBoundaryData(properties.getProperty("data"));
            });

            airports.addAirport(builder.build());
        }

        return airports;
    }

    private static Properties loadProperties(String content) {
        final Properties properties = new Properties();
        try {
            properties.load(new StringReader(content));
        } catch (IOException ignored) {
        }
        return properties;
    }

    private static Set<String> loadDeletedIcaos() {
        final Optional<String> content = loadResource("/deleted.csv");
        final Csv csv = content.map(Csv::fromContent).orElseGet(Csv::empty);
        final Set<String> deletedIcaos = new TreeSet<>();
        for (int row = 0; row < csv.rowCount(); row++) {
            deletedIcaos.add(csv.value(row, "icao"));
        }
        return deletedIcaos;
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
