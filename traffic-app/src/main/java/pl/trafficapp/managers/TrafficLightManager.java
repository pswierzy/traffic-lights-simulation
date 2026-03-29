package pl.trafficapp.managers;

import pl.trafficapp.domain.*;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class TrafficLightManager {
    private final Intersection intersection;
    private final Map<Phase, List<Lane>> phaseLanes = new EnumMap<>(Phase.class);
    private final Map<Phase, Integer> phaseWaitTicks = new EnumMap<>(Phase.class);

    private int timer = 0;
    private Phase currentPhase = Phase.NS;
    private State currentState = State.RED_ALL;

    private final static int MIN_GREEN_LENGTH = 2;
    private final static int MAX_GREEN_LENGTH = 10;


    private enum Phase {NS_LEFT, EW_LEFT, NS, EW}
    private enum State {GREEN, YELLOW, RED_ALL, RED_YELLOW}

    public TrafficLightManager(Intersection intersection) {
        this.intersection = intersection;
        initPhaseLanes();
        for (Phase p : Phase.values()) phaseWaitTicks.put(p, 0);
    }

    private void initPhaseLanes() {
        for (Phase p : Phase.values()) phaseLanes.put(p, new ArrayList<>());

        // NORTH
        for (Lane lane: intersection.getRoad(Direction.NORTH).getLanes()) {
            if (lane.getTrafficLight().isDirectional()) {
                phaseLanes.get(Phase.NS_LEFT).add(lane);
            } else
                phaseLanes.get(Phase.NS).add(lane);
        }
        // SOUTH
        for (Lane lane: intersection.getRoad(Direction.SOUTH).getLanes()) {
            if (lane.getTrafficLight().isDirectional()) {
                phaseLanes.get(Phase.NS_LEFT).add(lane);
            } else
                phaseLanes.get(Phase.NS).add(lane);
        }
        // EAST
        for (Lane lane: intersection.getRoad(Direction.EAST).getLanes()) {
            if (lane.getTrafficLight().isDirectional()) {
                phaseLanes.get(Phase.EW_LEFT).add(lane);
            } else
                phaseLanes.get(Phase.EW).add(lane);
        }
        // WEST
        for (Lane lane: intersection.getRoad(Direction.WEST).getLanes()) {
            if (lane.getTrafficLight().isDirectional()) {
                phaseLanes.get(Phase.EW_LEFT).add(lane);
            } else
                phaseLanes.get(Phase.EW).add(lane);
        }
    }

    // We are adding a tick per a waiting car in waiting groups
    private void updateWaitTicks() {
        for (Phase p : Phase.values()) {
            if (p != currentPhase || currentState != State.GREEN) {
                for (Lane lane : phaseLanes.get(p)) {
                    phaseWaitTicks.put(p, phaseWaitTicks.get(p) + lane.getQueueSize());
                }
            }
        }
    }

    // We are choosing the group that collectively has waited the most
    private Phase getNextPhase() {
        int maxTicks = 0;
        Phase nextPhase = null;
        for (Phase p : Phase.values()) {
            if (phaseWaitTicks.get(p) > maxTicks && p != currentPhase) {
                maxTicks = phaseWaitTicks.get(p);
                nextPhase = p;
            }
        }

        if (nextPhase == null) {
            Phase[] phases = Phase.values();
            int nextIndex = (currentPhase.ordinal() + 1) % phases.length;
            return phases[nextIndex];
        }

        return nextPhase;
    }

    private int calculateGreenDuration() {
        int cars = 0;
        for (Lane lane : phaseLanes.get(currentPhase)) {
            cars += lane.getQueueSize();
        }
        // TODO - smarted algorithm for this function
        return Math.min(Math.max(cars/2, MIN_GREEN_LENGTH), MAX_GREEN_LENGTH);
    }

    private void changeState() {
        switch (currentState) {
            case GREEN -> {
                currentState = State.YELLOW;
                timer = 1;
            }
            case YELLOW -> {
                currentState = State.RED_ALL;
                timer = 1;
            }
            case RED_ALL -> {
                currentState = State.RED_YELLOW;
                currentPhase = getNextPhase();
                timer = 1;
            }
            case RED_YELLOW -> {
                currentState = State.GREEN;
                timer = calculateGreenDuration();
                phaseWaitTicks.put(currentPhase, 0);
            }
        }
    }

    private void applyColors() {
        switch (currentState) {
            case GREEN -> {
                // We are changing our next phase lines color to green
                for (Lane lane : phaseLanes.get(currentPhase)) {
                    lane.getTrafficLight().setColor(LightColor.GREEN);
                }
                // Where we can we turn on green arrows (bcs they are smart and non-blocking)
                for (Road road : intersection.getAllRoads()) {
                    for (Lane lane : road.getLanes()) {
                        if (lane.getTrafficLight().getColor() == LightColor.RED &&
                                lane.isDirectionAllowed(Direction.getRightDirection(road.getIncomingDirection()))) {
                            lane.getTrafficLight().setColor(LightColor.GREEN_ARROW_RIGHT);
                        }
                    }
                }
            }
            case YELLOW -> {
                // Green light changes to yellow and green arrows change to red
                for (Lane lane : phaseLanes.get(currentPhase)) {
                    lane.getTrafficLight().setColor(LightColor.YELLOW);
                }
                // Where we can we turn on green arrows (bcs they are smart and non-blocking)
                for (Road road : intersection.getAllRoads()) {
                    for (Lane lane : road.getLanes()) {
                        if (lane.getTrafficLight().getColor() == LightColor.GREEN_ARROW_RIGHT) {
                            lane.getTrafficLight().setColor(LightColor.RED);
                        }
                    }
                }
            }
            case RED_ALL -> {
                // Yellow light changes to red
                for (Lane lane : phaseLanes.get(currentPhase)) {
                    lane.getTrafficLight().setColor(LightColor.RED);
                }
            }
            case RED_YELLOW -> {
                // Red changes to yellow for a new phase
                for (Lane lane : phaseLanes.get(currentPhase)) {
                    lane.getTrafficLight().setColor(LightColor.YELLOW);
                }
            }
        }
    }

    public void tick() {
        timer--;
        updateWaitTicks();

        if (timer <= 0) {
            changeState();
            applyColors();
        }
    }

}
