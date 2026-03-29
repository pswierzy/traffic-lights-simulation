package pl.trafficapp.managers;

import pl.trafficapp.domain.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrafficManager {
    private final Intersection intersection;
    public TrafficManager(Intersection intersection) {
        this.intersection = intersection;
    }

    public Intersection getIntersection() {
        return intersection;
    }

    /* logic:
    1) CAR GOES RIGHT:
        a) Full Green light -> just go; nothing fancy.
        b) Red light (WITH green arrow) -> Conditional turn:
            - 1st tick: Stop and check if possible to go.
            - 2nd tick: Go if intersection is still clear.
        c) Yellow / Red (without arrow) -> STOP.

    2) CAR GOES STRAIGHT:
        a) Green light -> just go.
        b) Yellow / Red -> STOP.

    3) CAR GOES LEFT:
        a) Directional Green Arrow -> just go.
        b) Full Green light -> Conditional turn:
            - Go if intersection is clear.
        c) Yellow / Red -> STOP.
    */

    private boolean isClearForRightTurn(Direction direction) {
        Road leftRoad = intersection.getRoad(Direction.getLeftDirection(direction));
        for (Lane lane : leftRoad.getLanes()) {
            if (!lane.isEmpty() &&
                    lane.getTrafficLight().getColor() == LightColor.GREEN &&
                    lane.peekVehicle().get().end() == Direction.getRightDirection(direction)) {
                return false;
            }
        }
        return true;
    }
    private boolean canTurnRight(Lane lane, Vehicle vehicle, LightColor color) {
        if (color == LightColor.GREEN) {
            return true;
        }
        if (color == LightColor.GREEN_ARROW_RIGHT) {
            if (!vehicle.hasStopped()) {
                vehicle.setStopped(true);
                return false;
            }
            return isClearForRightTurn(vehicle.start());
        }
        return false;
    }

    private boolean isClearForLeftTurn(Direction direction) {
        Road oppositeRoad = intersection.getRoad(Direction.getOppositeDirection(direction));
        for (Lane lane : oppositeRoad.getLanes()) {
            if (!lane.isEmpty() && lane.getTrafficLight().getColor() == LightColor.GREEN) {
                Direction oppositeIntent = lane.peekVehicle().get().end();
                if (oppositeIntent == Direction.getOppositeDirection(oppositeRoad.getIncomingDirection()) ||
                        oppositeIntent == Direction.getRightDirection(oppositeRoad.getIncomingDirection())) {
                    return false;
                }
            }
        }
        return true;
    }
    private boolean canTurnLeft(Lane lane, Vehicle vehicle, LightColor color) {
        if (color == LightColor.GREEN_ARROW_LEFT) {
            return true;
        }
        if (color == LightColor.GREEN) {
            return isClearForLeftTurn(vehicle.start());
        }
        return false;
    }

    private boolean canGoStraight(LightColor color) {
        return color == LightColor.GREEN;
    }

    private boolean canFirstVehicleGo(Lane lane) {
        if (lane.isEmpty()) {
            return false;
        }

        Vehicle vehicle = lane.peekVehicle().get();
        Direction intent = vehicle.end();
        Direction start = vehicle.start();
        LightColor color = lane.getTrafficLight().getColor();

        if (intent == Direction.getLeftDirection(start)) {
            return canTurnLeft(lane, vehicle, color);
        }
        if (intent == Direction.getRightDirection(start)) {
            return canTurnRight(lane, vehicle, color);
        }

        return canGoStraight(color);
    }

    public void processTraffic() {

        List<Lane> lanes = new ArrayList<>();
        for (Road road: intersection.getAllRoads()) {
            for (Lane lane : road.getLanes()) {
                if (canFirstVehicleGo(lane)) {
                    lanes.add(lane);
                }
            }
        }
        for (Lane lane : lanes) {
            Vehicle vehicle = lane.pollVehicle().get();
            System.out.printf("[leftVehicles] Vehicle %s left the intersection!\n", vehicle.id());
        }
    }
}
