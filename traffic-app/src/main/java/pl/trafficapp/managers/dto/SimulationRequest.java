package pl.trafficapp.managers.dto;

import java.util.List;

public record SimulationRequest(List<Command> commands) {}