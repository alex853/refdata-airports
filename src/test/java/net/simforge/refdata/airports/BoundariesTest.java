package net.simforge.refdata.airports;

import net.simforge.commons.misc.Geo;
import net.simforge.refdata.airports.boundary.Boundary;
import net.simforge.refdata.airports.boundary.BoundaryType;
import net.simforge.refdata.airports.boundary.CirclesBoundary;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

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
                .withBoundary(CirclesBoundary.fromProperties("35.775667,140.384253,2.5"))
                .build();

        assertFalse(rjaaWithDefaultType.isWithinBoundary(NORTHMOST_POINT));
        assertTrue(rjaaWithCirclesType.isWithinBoundary(NORTHMOST_POINT));
    }

    @Test
    public void testRJAAfromLoader() throws IOException {
        final Airports airports = AirportsLoader.load();
        final Optional<Airport> rjaa = airports.findByIcao("RJAA");
        assertTrue(rjaa.isPresent());
        assertTrue(rjaa.get().isWithinBoundary(NORTHMOST_POINT));
    }
}
