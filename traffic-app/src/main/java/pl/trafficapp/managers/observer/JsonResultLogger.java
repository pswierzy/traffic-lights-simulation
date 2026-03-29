package pl.trafficapp.managers.observer;

import com.fasterxml.jackson.databind.ObjectMapper;
import pl.trafficapp.managers.dto.SimulationResult;
import pl.trafficapp.managers.dto.StepStatus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonResultLogger implements SimulationObserver {
    private final String filename;
    private final List<StepStatus> stepStatuses = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonResultLogger(String filename) {
        this.filename = filename;
    }

    @Override
    public void onStepFinished(List<String> leftVehicles) {
        stepStatuses.add(new StepStatus(leftVehicles));
    }

    @Override
    public void onSimulationFinished() {
        SimulationResult result = new SimulationResult(stepStatuses);
        try {
            objectMapper.writeValue(new File(filename), result);
        } catch (IOException e) {
            System.err.println("Error writing result to file: " + e.getMessage());
        }
    }
}
