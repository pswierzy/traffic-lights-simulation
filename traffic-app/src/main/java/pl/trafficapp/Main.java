package pl.trafficapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import pl.trafficapp.domain.Intersection;
import pl.trafficapp.managers.SimulationEngine;
import pl.trafficapp.managers.dto.SimulationRequest;
import pl.trafficapp.managers.observer.JsonResultLogger;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Must write 2 arguments: <input.json> <output.json>");
            System.exit(1);
        }

        String inputFile = args[0];
        String outputFile = args[1];

        System.out.println("Building the intersection...");
        Intersection intersection = IntersectionFactory.createStandardIntersection();

        if (!intersection.isValid()) {
            throw new RuntimeException("Intersection is not valid");
        }

        SimulationEngine simulationEngine = new SimulationEngine(intersection);
        JsonResultLogger jsonLogger = new JsonResultLogger(outputFile);
        simulationEngine.addObserver(jsonLogger);

        try {
            ObjectMapper mapper = new ObjectMapper();
            SimulationRequest request = mapper.readValue(new File(inputFile), SimulationRequest.class);

            System.out.println("Building finished!\n\nStarting the simulation...");
            simulationEngine.execute(request);

        } catch (Exception e) {
            System.err.println("Error loading the simulation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
