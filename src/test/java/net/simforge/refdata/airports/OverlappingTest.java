package net.simforge.refdata.airports;

import net.simforge.commons.io.IOHelper;
import net.simforge.commons.misc.Geo;
import net.simforge.refdata.airports.boundary.DefaultBoundary;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class OverlappingTest {
    @Test
    @Disabled
    public void test_count_airports_having_overlapping_within_radius() {
        double radius = 2.5;

        Airports airports = Airports.get();

        AtomicInteger noOverlappingCounter = new AtomicInteger();
        AtomicInteger counter = new AtomicInteger();
        AtomicLong lastStatusTs = new AtomicLong(System.currentTimeMillis());

        File overlappingsFile = new File("./overlappings.txt");

        airports.getAllAirports().forEach(airport -> {
            final List<Airport> foundWithinRadius = airports.findAllWithinRadius(airport.getCoords(), 10);
            final List<Airport> overlappings = foundWithinRadius.stream()
                    .filter(each -> Geo.distance(airport.getCoords(), each.getCoords())
                            < DefaultBoundary.calcDefaultBoundaryRadius(airport.getRunwaySize())
                            + DefaultBoundary.calcDefaultBoundaryRadius(each.getRunwaySize()))
                    .collect(Collectors.toList());

            if (overlappings.size() == 1) {
                noOverlappingCounter.incrementAndGet();
            } else if (foundWithinRadius.size() > 1) {
                final String overlappingIcaos = overlappings.stream()
                        .filter(each -> each != airport)
                        .map(Airport::getIcao)
                        .collect(Collectors.joining(" "));
                try {
                    IOHelper.appendFile(overlappingsFile, airport.getIcao() + " -> " + overlappingIcaos + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // todo something wrong
            }

            final int current = counter.incrementAndGet();
            if (System.currentTimeMillis() - lastStatusTs.longValue() > 10000) {
                System.out.printf("Processed %d, no overlappings for %d\n", current, noOverlappingCounter.intValue());
                lastStatusTs.set(System.currentTimeMillis());
            }
        });

        System.out.printf("DONE, no overlappings for %d\n", noOverlappingCounter.intValue());
    }
}
