package net.simforge.refdata.airports;

import net.simforge.commons.misc.Geo;
import net.simforge.refdata.airports.fse.FSEAirport;
import net.simforge.refdata.airports.fse.FSEAirports;
import net.simforge.refdata.airports.pai.PAIAirport;
import net.simforge.refdata.airports.pai.PAIAirports;
import net.simforge.refdata.airports.pai.PAIAirportsLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Optional;
import java.util.Properties;

class AirportsLoader {
    static Airports load() throws IOException {
        final Airports airports = new Airports();

        final PAIAirports paiAirports = PAIAirportsLoader.load();
        final FSEAirports fseAirports = FSEAirports.get();
        final Collection<PAIAirport> paiAirportsCollection = paiAirports.getAirports();

        for (PAIAirport paiAirport : paiAirportsCollection) {
            final Optional<FSEAirport> fseAirport = fseAirports.findByIcao(paiAirport.getIcao());

            final Geo.Coords coords = fseAirport.isPresent()
                    ? fseAirport.get().getCoords()
                    : Geo.coords(paiAirport.getLatitude(), paiAirport.getLongitude());

            final Airport.Builder builder = Airport.builder()
                    .withIcao(paiAirport.getIcao())
                    .withCoords(coords)
                    .withElevation(paiAirport.getAltitude());

            final String resourceNameTemplate = "/boundaries/%s.properties";
            final String resourceName = resourceNameTemplate.replace("%s", paiAirport.getIcao());
            final InputStream resourceInputStream = Airports.class.getResourceAsStream(resourceName);

            if (resourceInputStream != null) {
                final Properties properties = new Properties();
                properties.load(resourceInputStream);

                builder.withBoundaryType(BoundaryType.valueOf(properties.getProperty("type")))
                        .withBoundaryData(properties.getProperty("data"));
            }

            airports.addAirport(builder.build());
        }

        return airports;
    }
}
