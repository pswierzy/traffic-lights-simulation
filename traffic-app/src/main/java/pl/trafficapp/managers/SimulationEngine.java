package pl.trafficapp.managers;

public class SimulationEngine {
    private final TrafficManager trafficManager;
    private final TrafficLightManager trafficLightManager;

    public SimulationEngine(TrafficManager trafficManager, TrafficLightManager trafficLightManager) {
        this.trafficManager = trafficManager;
        this.trafficLightManager = trafficLightManager;
    }

    // TODO - symulacja start/stop/tick i zarządzanie managerami
}
