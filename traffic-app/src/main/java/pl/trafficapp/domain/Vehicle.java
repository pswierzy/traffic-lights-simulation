package pl.trafficapp.domain;

public class Vehicle {
    private final String id;
    private final Direction start;
    private final Direction end;
    private boolean hasStopped; // before a green arrow u have to stop

    public Vehicle(String id, Direction start, Direction end) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.hasStopped = false;
    }

    public String id() { return id; }
    public Direction start() { return start; }
    public Direction end() { return end; }

    public boolean hasStopped() {
        return hasStopped;
    }
    public void setStopped(boolean hasStopped) {
        this.hasStopped = hasStopped;
    }
}