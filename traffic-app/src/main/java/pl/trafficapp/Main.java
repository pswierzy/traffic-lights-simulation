package pl.trafficapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import pl.trafficapp.domain.Intersection;
import pl.trafficapp.managers.SimulationEngine;
import pl.trafficapp.managers.SimulationRequest;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        System.out.println("Building the intersection...");
        Intersection intersection = IntersectionFactory.createStandardIntersection();

        if (!intersection.isValid()) {
            throw new RuntimeException("Intersection is not valid");
        }

        SimulationEngine simulationEngine = new SimulationEngine(intersection);

        try {
            ObjectMapper mapper = new ObjectMapper();
            SimulationRequest request = mapper.readValue(new File("commands.json"), SimulationRequest.class);;

            System.out.println("Building finished!\n\nStarting the simulation...");
            simulationEngine.execute(request);

        } catch (Exception e) {
            System.err.println("Error loading the simulation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
