package pl.trafficapp.domain.managers;

import pl.trafficapp.domain.Intersection;

public class TrafficManager {
    private final Intersection intersection;
    public TrafficManager(Intersection intersection) {
        this.intersection = intersection;
    }

    public Intersection getIntersection() {
        return intersection;
    }

    // TODO - logika aut w korkach itd
}
