package pl.trafficapp.managers;

import pl.trafficapp.domain.*;
import pl.trafficapp.managers.dto.*;
import pl.trafficapp.managers.observer.SimulationObserver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SimulationEngine {
    private final TrafficManager trafficManager;
    private final TrafficLightManager trafficLightManager;
    private final Intersection intersection;

    private final Queue<Command> commandQueue = new LinkedList<>();

    private final List<SimulationObserver> observers = new ArrayList<>();

    public SimulationEngine(Intersection intersection) {
        this.intersection = intersection;
        this.trafficManager = new TrafficManager(intersection);
        this.trafficLightManager = new TrafficLightManager(intersection);
    }

    public void addObserver(SimulationObserver observer) {
        observers.add(observer);
    }

    public List<String> tick() {
        trafficLightManager.tick();
        return trafficManager.processTraffic();
    }
    public boolean addVehicle(String id, Direction start, Direction end) {
        return intersection.addVehicle(new Vehicle(id, start, end));
    }

    public void executeNextCommand() {
        if (commandQueue.isEmpty()) {
            System.out.println("Command queue is empty.");
            for (SimulationObserver observer : observers) {
                observer.onSimulationFinished();
            }
            return;
        }

        Command command = commandQueue.poll();
        switch (command.type()) {
            case "addVehicle" -> handleAddVehicle(command);
            case "step" -> handleStep();
            default -> System.out.println("Unknown command: " + command.type());
        }
    }

    public void loadCommands(SimulationRequest request) {
        this.commandQueue.addAll(request.commands());
        System.out.println("Loaded " + commandQueue.size() + " commands to a queue.");
    }

    public void execute(SimulationRequest request) {
        System.out.println("JSON executing started...\n");

        for (Command command : request.commands()) {
            switch (command.type()) {
                case "addVehicle" -> handleAddVehicle(command);
                case "step" -> handleStep();
                default -> System.out.println("Unknown command: " + command.type());
            }
        }

        for (SimulationObserver observer : observers) {
            observer.onSimulationFinished();
        }

        System.out.println("\nJSON executing finished!");
    }

    private void handleAddVehicle(Command command) {
        Direction start = Direction.fromString(command.startRoad());
        Direction end = Direction.fromString(command.endRoad());

        if(addVehicle(command.vehicleId(), start, end)) {
            System.out.printf("[addVehicle] Vehicle %s added: %s -> %s%n",
                    command.vehicleId(), start, end);
        } else {
            System.out.println("ERROR - [addVehicle]");
        }
    }

    private void handleStep() {
        List<String> leftInThisStep = tick();

        for (SimulationObserver observer : observers) {
            observer.onStepFinished(leftInThisStep);
        }

        System.out.println("[step] Simulation step finished!");
    }
}
