package net.simforge.refdata.airports;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AddedAirportTest {
    @Test
    public void test_LTFM() {
        Optional<Airport> airport = Airports.get().findByIcao("LTFM");
        assertTrue(airport.isPresent());
    }
}
