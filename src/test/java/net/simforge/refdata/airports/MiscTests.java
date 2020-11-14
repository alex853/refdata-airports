package net.simforge.refdata.airports;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MiscTests {
    @Test
    public void test_1Y5_exists() {
        Airports airports = Airports.get();
        Airport airport = airports.getByIcao("1Y5");
        assertNotNull(airport);
    }

    @Test
    public void test_K17_exists() {
        Airports airports = Airports.get();
        Airport airport = airports.getByIcao("K17");
        assertNotNull(airport);
    }
}
