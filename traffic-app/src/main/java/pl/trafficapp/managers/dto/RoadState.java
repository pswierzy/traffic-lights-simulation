package pl.trafficapp.managers.dto;

import java.util.List;

public record RoadState(String incomingDirection, List<LaneState> lanes) {}
