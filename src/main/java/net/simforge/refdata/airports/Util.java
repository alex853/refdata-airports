package net.simforge.refdata.airports;

import net.simforge.commons.misc.Geo;

class Util {
    public static double distance(Geo.Coords p1, Geo.Coords p2, DistanceType distanceType) {
        switch (distanceType) {
            case GeoDistance:
                return Geo.distance(p1, p2);
            case DegreeDifference:
                return Geo.degreeDifference(p1, p2);
            default:
                throw new IllegalArgumentException("unknown distance type " + distanceType);
        }
    }
}
