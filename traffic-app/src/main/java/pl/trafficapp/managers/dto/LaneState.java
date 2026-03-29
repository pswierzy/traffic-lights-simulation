package pl.trafficapp.managers.dto;

import java.util.List;

public record LaneState(List<String> laneDirection, String lightColor, List<String> vehicles) {}