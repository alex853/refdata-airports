package net.simforge.refdata.airports.fse;

import net.simforge.commons.io.Csv;
import net.simforge.commons.io.IOHelper;
import net.simforge.commons.misc.Geo;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FSEAirports {

    private final Map<String, FSEAirport> data = new HashMap<>();

    private FSEAirports() {}

    private static FSEAirports instance;

    public synchronized static FSEAirports get() {
        if (instance != null) {
            return instance;
        }

        final InputStream in = FSEAirports.class.getResourceAsStream("/fseconomy-icaodata.csv");
        final String content;
        try {
            content = IOHelper.readInputStream(in);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read airport data", e);
        }
        final Csv csv = Csv.fromContent(content);

        final FSEAirports _instance = new FSEAirports();
        for (int row = 0; row < csv.rowCount(); row++) {
            final String icao = csv.value(row, 0);
            final String lat = csv.value(row, 1);
            final String lon = csv.value(row, 2);
            final String type = csv.value(row, 3);
            final String size = csv.value(row, 4);
            final String name = csv.value(row, 5);
            final String city = csv.value(row, 6);
            final String state = csv.value(row, 7);
            final String country = csv.value(row, 8);

            final FSEAirport airport = FSEAirport.builder()
                    .withIcao(icao)
                    .withCoords(Geo.coords(Double.parseDouble(lat), Double.parseDouble(lon)))
                    .withType(FSEAirport.Type.valueOf(type))
                    .withSize(Integer.parseInt(size))
                    .withName(name)
                    .withCity(city)
                    .withState(state)
                    .withCountry(country)
                    .build();

            _instance.data.put(icao, airport);
        }

        instance = _instance;

        return instance;
    }

    public Optional<FSEAirport> findByIcao(final String icao) {
        return Optional.ofNullable(data.get(icao));
    }

}
