package net.simforge.refdata.airports;

import net.simforge.commons.misc.Geo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DegreeDiffTest {
    private static final Airports airports = Airports.get();

    @Test
    public void test_53N_51E() {
        final Geo.Coords coords = Geo.coords(53, 51);

        Airport nearestByDist = airports.findNearest(coords, DistanceType.GeoDistance);
        Airport nearestByDiff = airports.findNearest(coords, DistanceType.DegreeDifference);

        assertNotNull(nearestByDist);
        assertNotNull(nearestByDiff);
        assertEquals(nearestByDist.getIcao(), nearestByDiff.getIcao());
    }
}
