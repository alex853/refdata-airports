package net.simforge.refdata.airports.fse;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FSEAirportsTest {
    @Test
    public void test_it_loads() {
        FSEAirports airports = FSEAirports.get();
        Optional<FSEAirport> airport = airports.findByIcao("EGLL");
        assertTrue(airport.isPresent());
    }
}
