package pl.trafficapp.managers;

import java.util.List;

public class SimulationRequest {
    private List<Command> commands;

    public List<Command> getCommands() { return commands; }
    public void setCommands(List<Command> commands) { this.commands = commands; }
}

class Command {
    private String type;
    private String vehicleId;
    private String startRoad;
    private String endRoad;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
    public String getStartRoad() { return startRoad; }
    public void setStartRoad(String startRoad) { this.startRoad = startRoad; }
    public String getEndRoad() { return endRoad; }
    public void setEndRoad(String endRoad) { this.endRoad = endRoad; }
}