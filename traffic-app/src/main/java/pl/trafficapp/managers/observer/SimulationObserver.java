package pl.trafficapp.managers.observer;

import java.util.List;

public interface SimulationObserver {
    void onStepFinished(List<String> leftVehicles);
    void onSimulationFinished();
}
