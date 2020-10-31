package net.simforge.refdata.airports;

import net.simforge.commons.misc.Geo;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class BoundariesTest {

    public static final Geo.Coords NORTHMOST_POINT = new Geo.Coords(35.805174, 140.378119);

    @Test
    public void testRJAA() {
        AirportBuilder builder = new AirportBuilder();

        builder.setIcao("RJAA");
        builder.setCoords(new Geo.Coords(35.746833, 140.385167));

        Airport rjaaWithDefaultType = builder.create();

        builder.setBoundaryType(BoundaryType.Circles);
        builder.setBoundaryData("35.775667,140.384253,2.5");

        Airport rjaaWithCirclesType = builder.create();

        assertFalse(rjaaWithDefaultType.isWithinBoundary(NORTHMOST_POINT));
        assertTrue(rjaaWithCirclesType.isWithinBoundary(NORTHMOST_POINT));
    }

    @Test
    public void testRJAAfromLoader() throws IOException {
        Airports airports = AirportsLoader.load();
        Airport rjaa = airports.getByIcao("RJAA");
        assertNotNull(rjaa);
        assertTrue(rjaa.isWithinBoundary(NORTHMOST_POINT));
    }
}
