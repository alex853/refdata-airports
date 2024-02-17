package net.simforge.refdata.airports;

import net.simforge.commons.misc.Geo;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class BoundariesTest {

    public static final Geo.Coords NORTHMOST_POINT = Geo.coords(35.805174, 140.378119);

    @Test
    public void testRJAA() {
        final Airport rjaaWithDefaultType = Airport.builder()
                .withIcao("RJAA")
                .withCoords(Geo.coords(35.746833, 140.385167))
                .build();

        final Airport rjaaWithCirclesType = Airport.builder()
                .withIcao("RJAA")
                .withCoords(Geo.coords(35.746833, 140.385167))
                .withBoundaryType(BoundaryType.Circles)
                .withBoundaryData("35.775667,140.384253,2.5")
                .build();

        assertFalse(rjaaWithDefaultType.isWithinBoundary(NORTHMOST_POINT));
        assertTrue(rjaaWithCirclesType.isWithinBoundary(NORTHMOST_POINT));
    }

    @Test
    public void testRJAAfromLoader() throws IOException {
        final Airports airports = AirportsLoader.load();
        final Airport rjaa = airports.getByIcao("RJAA");
        assertNotNull(rjaa);
        assertTrue(rjaa.isWithinBoundary(NORTHMOST_POINT));
    }
}
