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

    public static Direction getRightDirection(Direction dir) {
        if(dir == Direction.NORTH) {
            return Direction.WEST;
        } else if(dir == Direction.WEST) {
            return Direction.SOUTH;
        } else if(dir == Direction.SOUTH) {
            return Direction.EAST;
        } else {
            return Direction.NORTH;
        }
    }

    public static Direction getOppositeDirection(Direction dir) {
        if(dir == Direction.NORTH) {
            return Direction.SOUTH;
        } else if(dir == Direction.SOUTH) {
            return Direction.NORTH;
        } else if(dir == Direction.EAST) {
            return Direction.WEST;
        } else {
            return Direction.EAST;
        }
    }

    public static Direction getLeftDirection(Direction dir) {
        if(dir == Direction.NORTH) {
            return Direction.EAST;
        } else if(dir == Direction.EAST) {
            return Direction.SOUTH;
        } else if(dir == Direction.SOUTH) {
            return Direction.WEST;
        } else {
            return Direction.NORTH;
        }
    }
}
