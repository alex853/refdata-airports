package net.simforge.refdata.airports;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MiscTests {
    @Test
    public void test_1Y5_exists() {
        Optional<Airport> airport = Airports.get().findByIcao("1Y5");
        assertTrue(airport.isPresent());
    }

    @Test
    public void test_K17_exists() {
        Optional<Airport> airport = Airports.get().findByIcao("K17");
        assertTrue(airport.isPresent());
    }

    @Test
    public void test_FLU_is_deleted() {
        Optional<Airport> airport = Airports.get().findByIcao("FLU");
        assertFalse(airport.isPresent());
    }
}
