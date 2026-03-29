package pl.trafficapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import pl.trafficapp.domain.Direction;
import pl.trafficapp.domain.Intersection;
import pl.trafficapp.domain.Vehicle;
import pl.trafficapp.managers.SimulationEngine;
import pl.trafficapp.managers.dto.IntersectionState;
import pl.trafficapp.managers.dto.LaneState;
import pl.trafficapp.managers.dto.RoadState;
import pl.trafficapp.managers.dto.SimulationRequest;
import pl.trafficapp.managers.observer.JsonResultLogger;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static SimulationEngine engine;
    private static final Intersection intersection =
            IntersectionFactory.createTwoLaneIntersection();

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Must write 2 arguments: <input.json> <output.json>");
            System.exit(1);
        }

        String inputFile = args[0];
        String outputFile = args[1];
        if (!intersection.isValid()) {
            throw new RuntimeException("Intersection is not valid");
        }
        engine = new SimulationEngine(intersection);

        JsonResultLogger jsonLogger = new JsonResultLogger(outputFile);
        engine.addObserver(jsonLogger);

        try {
            ObjectMapper mapper = new ObjectMapper();
            SimulationRequest request = mapper.readValue(new File(inputFile), SimulationRequest.class);
            engine.loadCommands(request);
        } catch (Exception e) {
            System.err.println("Error loading commands: " + e.getMessage());
        }

        Javalin app = Javalin.create(config -> config.showJavalinBanner = false).start(8080);
        app.before(ctx -> ctx.header("Access-Control-Allow-Origin", "*"));

        // Endpoint 1: Pobranie pełnego stanu i metadanych
        app.get("/state", ctx -> {
            ctx.json(getIntersectionState());
        });

        // Endpoint 2: Wykonanie jednego kroku symulacji
        app.post("/next", ctx -> {
            engine.executeNextCommand();
            ctx.json(getIntersectionState());
        });

        System.out.println("Server works!");
    }

    private static IntersectionState getIntersectionState() {
        List<RoadState> roads = intersection.getAllRoads().stream()
                .map(road -> {
                    List<LaneState> lanes = road.getLanes().stream()
                            .map(lane -> {
                                List<String> vehicles = lane.getVehicles().stream()
                                        .map(Vehicle::id)
                                        .collect(Collectors.toList());

                                String color = lane.getTrafficLight().getColor().name();
                                List<String> directions = lane.getAllowedDirections().stream()
                                        .map(Direction::getVal)
                                        .collect(Collectors.toList());

                                return new LaneState(directions, color, vehicles);
                            })
                            .collect(Collectors.toList());
                    return new RoadState(road.getIncomingDirection().name(), lanes);
                })
                .collect(Collectors.toList());

        return new IntersectionState(roads);
    }
}
