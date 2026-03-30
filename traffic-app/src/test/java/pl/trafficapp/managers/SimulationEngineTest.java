package pl.trafficapp.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.trafficapp.domain.*;
import pl.trafficapp.managers.dto.Command;
import pl.trafficapp.managers.dto.SimulationRequest;
import pl.trafficapp.managers.observer.SimulationObserver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimulationEngineTest {

    private SimulationEngine engine;
    private Intersection intersection;

    @Mock
    private SimulationObserver observer;

    @BeforeEach
    void setUp() {
        intersection = createTestIntersection();
        engine = new SimulationEngine(intersection);
        engine.addObserver(observer);
    }

    @Test
    void executeNextCommand_AddVehicle() {
        Command addCmd = new Command("addVehicle", "V1", "north", "south");
        SimulationRequest request = new SimulationRequest(List.of(addCmd));
        engine.loadCommands(request);

        engine.executeNextCommand();

        Lane straightLane = intersection.getRoad(Direction.NORTH).getLanes().get(1);
        assertEquals(1, straightLane.getQueueSize());
        assertEquals("V1", straightLane.peekVehicle().get().id());
        verifyNoInteractions(observer);
    }

    @Test
    void executeNextCommand_Step() {
        Command stepCmd = new Command("step", null, null, null);
        SimulationRequest request = new SimulationRequest(List.of(stepCmd));
        engine.loadCommands(request);

        engine.executeNextCommand();

        verify(observer, times(1)).onStepFinished(anyList());
        verify(observer, never()).onSimulationFinished();
    }

    @Test
    void executeNextCommand_EmptyQueue() {
        engine.executeNextCommand();

        verify(observer, times(1)).onSimulationFinished();
        verify(observer, never()).onStepFinished(anyList());
    }

    @Test
    void execute_BatchProcessing() {
        Command addCmd1 = new Command("addVehicle", "V1", "north", "south");
        Command addCmd2 = new Command("addVehicle", "V2", "east", "west");
        Command stepCmd = new Command("step", null, null, null);

        SimulationRequest request = new SimulationRequest(List.of(addCmd1, addCmd2, stepCmd));

        engine.execute(request);

        Lane northLane = intersection.getRoad(Direction.NORTH).getLanes().get(1);
        Lane eastLane = intersection.getRoad(Direction.EAST).getLanes().get(1);

        assertEquals(1, northLane.getQueueSize());
        assertEquals(1, eastLane.getQueueSize());

        verify(observer, times(1)).onStepFinished(anyList());
        verify(observer, times(1)).onSimulationFinished();
    }

    @Test
    void execute_UnknownCommandIsIgnored() {
        Command unknownCmd = new Command("jump", "V1", "north", "south");
        SimulationRequest request = new SimulationRequest(List.of(unknownCmd));

        engine.execute(request);

        verify(observer, times(1)).onSimulationFinished();
        verify(observer, never()).onStepFinished(anyList());
        assertTrue(intersection.getRoad(Direction.NORTH).getLanes().get(1).isEmpty());
    }

    private Intersection createTestIntersection() {
        return new Intersection(Arrays.asList(
                createRoad(Direction.NORTH),
                createRoad(Direction.SOUTH),
                createRoad(Direction.EAST),
                createRoad(Direction.WEST)
        ));
    }

    private Road createRoad(Direction dir) {
        Lane leftLane = new Lane(new HashSet<>(Arrays.asList(Direction.getLeftDirection(dir))), new TrafficLight(true));
        Lane straightRightLane = new Lane(new HashSet<>(Arrays.asList(
                Direction.getOppositeDirection(dir), Direction.getRightDirection(dir)
        )), new TrafficLight(false));
        return new Road(dir, Arrays.asList(leftLane, straightRightLane));
    }
}