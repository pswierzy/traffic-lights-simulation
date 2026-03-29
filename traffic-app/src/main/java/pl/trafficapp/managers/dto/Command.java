package pl.trafficapp.managers.dto;

public record Command(
        String type,
        String vehicleId,
        String startRoad,
        String endRoad
) {}