package net.simforge.refdata.airports.fse;

import net.simforge.commons.misc.Geo;

public class FSEAirport {
    private String icao;
    private Geo.Coords coords;
    private Type type;
    private int size;
    private String name;
    private String city;
    private String state;
    private String country;

    private FSEAirport() {}

    public String getIcao() {
        return icao;
    }

    public Geo.Coords getCoords() {
        return coords;
    }

    public Type getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final FSEAirport delegate = new FSEAirport();

        private Builder() {}

        public Builder withIcao(final String icao) {
            delegate.icao = icao;
            return this;
        }

        public Builder withCoords(final Geo.Coords coords) {
            delegate.coords = coords;
            return this;
        }

        public Builder withType(final Type type) {
            delegate.type = type;
            return this;
        }

        public Builder withSize(final int size) {
            delegate.size = size;
            return this;
        }

        public Builder withName(final String name) {
            delegate.name = name;
            return this;
        }

        public Builder withCity(final String city) {
            delegate.city = city;
            return this;
        }

        public Builder withState(final String state) {
            delegate.state = state;
            return this;
        }

        public Builder withCountry(final String country) {
            delegate.country = country;
            return this;
        }

        public FSEAirport build() {
            return delegate;
        }
    }

    public enum Type {
        civil,
        water,
        military
    }
}
