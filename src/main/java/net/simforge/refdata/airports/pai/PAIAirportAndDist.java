package net.simforge.refdata.airports.pai;

public class PAIAirportAndDist {
    private PAIAirport airport;
    private double distance;

    public PAIAirportAndDist(PAIAirport airport, double distance) {
        this.airport = airport;
        this.distance = distance;
    }

    public PAIAirport getAirport() {
        return airport;
    }

    public double getDistance() {
        return distance;
    }
}
