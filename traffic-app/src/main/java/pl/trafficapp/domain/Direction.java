package pl.trafficapp.domain;

public enum Direction {
    NORTH("north"),
    SOUTH("south"),
    EAST("east"),
    WEST("west");

    private final String val;

    Direction(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    public static Direction fromString(String val) {
        for (Direction d : Direction.values()) {
            if (d.getVal().equalsIgnoreCase(val)) {
                return d;
            }
        }
        throw new IllegalArgumentException("Unknown direction: " + val);
    }
}
