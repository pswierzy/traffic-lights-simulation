package pl.trafficapp.managers;

import pl.trafficapp.domain.Direction;
import pl.trafficapp.domain.Intersection;
import pl.trafficapp.domain.Vehicle;

public class SimulationEngine {
    private final TrafficManager trafficManager;
    private final TrafficLightManager trafficLightManager;
    private final Intersection intersection;

    public SimulationEngine(Intersection intersection) {
        this.intersection = intersection;
        this.trafficManager = new TrafficManager(intersection);
        this.trafficLightManager = new TrafficLightManager(intersection);
    }

    public void tick() {
        trafficLightManager.tick();
        trafficManager.processTraffic();
    }
    public boolean addVehicle(String id, Direction start, Direction end) {
        return intersection.addVehicle(new Vehicle(id, start, end));
    }

    public void execute(SimulationRequest request) {
        System.out.println("JSON executing started...\n");

        for (Command command : request.getCommands()) {
            switch (command.getType()) {
                case "addVehicle" -> handleAddVehicle(command);
                case "step" -> handleStep();
                default -> System.out.println("Unknown command: " + command.getType());
            }
        }

        System.out.println("\nJSON executing finished!");
    }

    private void handleAddVehicle(Command command) {
        Direction start = Direction.fromString(command.getStartRoad());
        Direction end = Direction.fromString(command.getEndRoad());

        if(addVehicle(command.getVehicleId(), start, end)) {
            System.out.printf("[addVehicle] Vehicle %s added: %s -> %s%n",
                    command.getVehicleId(), start, end);
        } else {
            System.out.println("ERROR - [addVehicle]");
        }
    }
    private void handleStep() {
        tick();
        System.out.println("[step] Simulation step finished!");
    }
}
